package com.cevelop.templator.plugin.asttools.resolving;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethodInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.InstantiationContext;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.AbstractTemplateInstance;
import com.cevelop.templator.plugin.asttools.data.NameTypeKind;
import com.cevelop.templator.plugin.asttools.data.TemplateInstance;
import com.cevelop.templator.plugin.asttools.data.UnresolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;


/**
 * Resolving and instantiating deferred bindings based on the template argument
 * map from the surrounding {@code AbstractResolvedNameInfo}s. This happens
 * after the normal binding resolution process.
 */
public final class PostResolver {

    private PostResolver() {}

    public static UnresolvedNameInfo resolveToFinalBinding(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {

        IBinding resolvedTemplateInstance = resolveDeferredBinding(unresolvedName, parent, analyzer);
        unresolvedName.setBinding(resolvedTemplateInstance, true);

        return unresolvedName;
    }

    public static IBinding resolveDeferredBinding(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        NameTypeKind type = unresolvedName.getType();
        IASTName resolvingName = unresolvedName.getResolvingName();

        if (type == null || !type.isDeferred()) {
            return unresolvedName.getBinding();
        }
        IBinding finalResolvedBinding = null;
        try {
            if (type == NameTypeKind.DEFERRED_FUNCTION && parent instanceof AbstractResolvedNameInfo) {
                finalResolvedBinding = resolveFunction(resolvingName, parent, analyzer);
            } else if (type == NameTypeKind.DEFERRED_CLASS_TEMPLATE || type == NameTypeKind.DEFERRED_VARIABLE_TEMPLATE ||
                       type == NameTypeKind.UNKNOWN_MEMBER_ALIAS_TEMPLATE_INSTANCE) {
                IBinding classInstance = resolveClassTemplate(resolvingName, parent, analyzer);
                if (classInstance != null) {
                    finalResolvedBinding = classInstance;
                }
            } else if (type.isMember()) {
                finalResolvedBinding = ClassTemplateMemberResolver.resolveClassTemplateMember(unresolvedName, parent);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new TemplatorException(e);
        }
        return finalResolvedBinding;
    }

    public static IFunction resolveFunction(IASTName resolvingName, AbstractResolvedNameInfo parentInstance, ASTAnalyzer analyzer)
            throws TemplatorException, DOMException {
        ICPPASTFunctionCallExpression functionCall = ASTTools.findFirstAncestorByType(resolvingName, ICPPASTFunctionCallExpression.class);
        IFunction resolvedCall = FunctionCallResolver.resolveCall(functionCall, parentInstance, analyzer);
        if (resolvedCall instanceof ICPPFunctionTemplate) {
            ICPPSpecialization instantiateForFunctionCall = InstantiateForFunctionCallHelper.instantiateForFunctionCall(
                    (ICPPFunctionTemplate) resolvedCall, functionCall, parentInstance.getTemplateArgumentMap());
            if (instantiateForFunctionCall instanceof IFunction) {
                resolvedCall = (IFunction) instantiateForFunctionCall;
            }
        }
        return resolvedCall;
    }

    public static IBinding resolveClassTemplate(IASTName resolvingName, AbstractResolvedNameInfo parentResolvedName, ASTAnalyzer analyzer)
            throws TemplatorException, DOMException {
        IBinding specialization = null;
        IASTName originalResolvingName = resolvingName;
        ICPPASTTemplateId id = getTemplateId(originalResolvingName, parentResolvedName, analyzer);
        if (id != null) {
            IBinding classInstance = ClassTemplateResolver.instantiateClassTemplate(id, parentResolvedName);
            specialization = classInstance;
        } else { // there could not be a templateId
            IBinding binding = resolvingName.getBinding();
            InstantiationContext context = new InstantiationContext(parentResolvedName.getTemplateArgumentMap());
            specialization = CPPTemplates.instantiateBinding(binding, context, 100);
        }
        return specialization;
    }

    public static ICPPASTTemplateId getTemplateId(IASTName originalResolvingName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        ICPPASTTemplateId id = null;
        if (originalResolvingName instanceof ICPPASTTemplateId) {
            id = (ICPPASTTemplateId) originalResolvingName;
        } else {
            IBinding resolvedBinding = originalResolvingName.resolveBinding();
            // TODO: move to ASTAnalyzer (typedef could also be in index?)
            if (resolvedBinding instanceof ITypedef) {
                IASTName[] names = analyzer.getAst().getDefinitionsInAST(resolvedBinding);
                for (IASTName name : names) {
                    if (name.getParent() instanceof ICPPASTAliasDeclaration) {
                        ICPPASTAliasDeclaration aliasDecl = (ICPPASTAliasDeclaration) name.getParent();
                        IASTName mappingName = ASTTools.getName(aliasDecl.getMappingTypeId());
                        if (mappingName instanceof ICPPASTTemplateId) {
                            return (ICPPASTTemplateId) mappingName;
                        }
                        return null;
                    }
                }
            }
            if (resolvedBinding instanceof ICPPSpecialization) {
                IBinding specializedBinding = ((ICPPSpecialization) resolvedBinding).getSpecializedBinding();

                // could be inside a nested class template, so get the first parent we find
                // where the name resolves to the same class template and then get the resolving name of this parent
                AbstractResolvedNameInfo currentParent = parent;
                IBinding parentSpecializedBinding;
                boolean foundTemplateId = false;
                while (currentParent != null) {
                    if (currentParent instanceof AbstractTemplateInstance) {
                        AbstractTemplateInstance abstractTemplateInstance = (AbstractTemplateInstance) currentParent;
                        // binding can also be something other than a ICPPSpecialization for example in
                        // an alias template
                        if (abstractTemplateInstance.getBinding() instanceof ICPPSpecialization) {
                            ICPPSpecialization specialization = (ICPPSpecialization) abstractTemplateInstance.getBinding();
                            parentSpecializedBinding = specialization.getSpecializedBinding();
                            if (specializedBinding == parentSpecializedBinding) {
                                foundTemplateId = true;
                                break;
                            }
                        }
                    }
                    currentParent = currentParent.getParent();
                }
                if (foundTemplateId && currentParent != null) {
                    if (currentParent.getResolvingName() instanceof ICPPASTTemplateId) {
                        id = (ICPPASTTemplateId) currentParent.getResolvingName();
                    }
                }
            }
        }
        return id;
    }

    /**
     * Consider the following code
     *
     * <pre>
     * template&lt;typename T&gt; struct Foo { void start() { newTemplateParam(1, 'c'); /* &lt;double,char&gt; *&#47; }
     *
     * template&lt;typename F&gt; void newTemplateParam(T first, F second) {} };
     *
     * int main() { Foo&lt;double&gt; doubleFoo {}; doubleFoo.start(); }
     * </pre>
     *
     * The call {@code newTemplateParam(1, 'c');} should have
     * {@code<double,char>} as template argument map. But resolving the binding for
     * the {@link IASTName} {@code newTemplateParam} results in {@code <int, char>}
     * which is correct at first. Because if there is a function declaration
     * {@code void newTemplateParam(int, char)}, then this one will be chosen
     * instead of the member function template because it is a better match. So if
     * the name resolved to a member function template we need to check if one
     * argument depends on any class template argument and if so replace it with the
     * chosen class template argument.
     * 
     * @param templateInfo A {@link TemplateInstance} 
     * @param parent A {@link AbstractResolvedNameInfo}
     * @param analyzer A {@link ASTAnalyzer}
     * @throws TemplatorException
     * If {@link ASTAnalyzer#getFunctionDefinition(IBinding)} throws
     */
    public static void replaceClassTemplateParameters(TemplateInstance templateInfo, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        IBinding binding = templateInfo.getBinding();
        // it can only happen with class template member functions, that after the
        // resolving, the template argument map
        // needs to be changed
        if (binding instanceof CPPMethodInstance) {
            IASTFunctionDefinition functionDefinition = analyzer.getFunctionDefinition(binding);
            IASTFunctionDeclarator declarator = functionDefinition.getDeclarator();
            if (declarator instanceof ICPPASTFunctionDeclarator) {
                ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) functionDefinition.getDeclarator();
                ICPPASTParameterDeclaration[] parameters = functionDeclarator.getParameters();
                for (ICPPASTParameterDeclaration functionParameter : parameters) {
                    IBinding parameterBinding = functionParameter.getDeclarator().getName().resolveBinding();
                    if (parameterBinding instanceof IVariable) {
                        ICPPTemplateParameter correspondingTemplateParameter = getTemplateParameter(((IVariable) parameterBinding).getType());
                        ICPPTemplateArgument argument = parent.getArgument(correspondingTemplateParameter);
                        if (argument != null) {
                            templateInfo.getTemplateArgumentMap().put(correspondingTemplateParameter, argument);
                        }
                    }
                }
            }
        }
    }

    private static ICPPTemplateParameter getTemplateParameter(IType parameterType) {
        ICPPTemplateParameter param = null;

        IType type = SemanticUtil.getUltimateType(parameterType, false);
        if (type instanceof ICPPTemplateParameter) {
            param = (ICPPTemplateParameter) type;
        }

        return param;
    }
}
