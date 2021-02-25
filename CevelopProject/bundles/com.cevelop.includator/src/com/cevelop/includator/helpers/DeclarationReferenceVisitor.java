/*******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 * Thomas Corbat (IFS)
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceAlias;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.c.CImplicitFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPImplicitFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.LookupData;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.ClassDeclarationReference;
import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.cxxelement.FunctionDeclarationReference;
import com.cevelop.includator.cxxelement.MethodDeclarationReference;
import com.cevelop.includator.cxxelement.NamespaceDeclarationReference;
import com.cevelop.includator.cxxelement.TypeDefDeclarationReference;
import com.cevelop.includator.cxxelement.UsingDeclarationReference;
import com.cevelop.includator.cxxelement.VariableDeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.resources.IncludatorFile;


@SuppressWarnings("restriction")
public class DeclarationReferenceVisitor extends ASTVisitor {

    private final RefMap                   refs;
    private final IncludatorFile           file;
    private final ConstructorHack          constructorHack;
    private final DestructorHack           destructorHack;
    private final ParameterDeclarationHack parameterDeclarationHack;

    public DeclarationReferenceVisitor(IncludatorFile file, RefMap refs) {
        super(true);
        this.shouldVisitImplicitNames = true;
        this.shouldVisitImplicitNameAlternates = true;
        this.file = file;
        this.refs = refs;
        constructorHack = new ConstructorHack(refs, file);
        destructorHack = new DestructorHack(refs, file);
        parameterDeclarationHack = new ParameterDeclarationHack(refs, file);
    }

    @Override
    public int visit(IASTName name) {
        if (shouldConsiderName(name)) {
            addName(name);
            IASTNode parent = name.getParent();
            if (parent instanceof ICPPASTQualifiedName) {
                addNameQualifier((ICPPASTQualifiedName) parent);
            }
        }
        return super.visit(name);
    }

    private void addNameQualifier(ICPPASTQualifiedName qName) {
        ICPPASTNameSpecifier[] specifiers = qName.getQualifier();
        if (specifiers.length == 0) {
            return;
        }
        ICPPASTNameSpecifier lastSpecifier = specifiers[specifiers.length - 1];
        IBinding secondLastNameBinding = BindingHelper.unwrapProblemBinding(lastSpecifier.resolveBinding());
        if (secondLastNameBinding instanceof ICPPClassType && lastSpecifier instanceof IASTName) {
            IASTName secondLastName = (IASTName) lastSpecifier;
            addName(secondLastName);
        }
        IASTName lastName = qName.getLastName();
        if (secondLastNameBinding instanceof ICPPNamespace && !(lastName.getBinding() instanceof ICPPNamespace)) {
            IBinding lastNameBinding = BindingHelper.unwrapProblemBinding(lastName.resolveBinding());
            if (lastNameBinding instanceof IProblemBinding && ((IProblemBinding) lastNameBinding)
                    .getID() != IProblemBinding.SEMANTIC_AMBIGUOUS_LOOKUP) {
                lastNameBinding = tryLookupInIndex(lastName);
            }
            addNamespaceName(lastSpecifier, refs.get(lastNameBinding));
        }
    }

    private void addNamespaceName(ICPPASTNameSpecifier specifier, DeclarationReference hintRef) {
        IBinding binding = BindingHelper.unwrapProblemBinding(specifier.resolveBinding());
        if (refs.containsKey(binding)) {
            DeclarationReference declarationReference = refs.get(binding);
            if (declarationReference instanceof NamespaceDeclarationReference) {
                NamespaceDeclarationReference ref = (NamespaceDeclarationReference) declarationReference;
                addDefinitionLocationHints(ref, hintRef);
                declarationReference.combineMetaInfoOfNodeWith(specifier);
            } else {
                String msg = "Previously found reference to '" + specifier + "' is not a namespace reference, although it was expected to be.";
                IncludatorPlugin.logStatus(new IncludatorStatus(msg), declarationReference.getFile());
            }
        } else {
            NamespaceDeclarationReference declRef = new NamespaceDeclarationReference(binding, specifier, file);
            addDefinitionLocationHints(declRef, hintRef);
            refs.put(binding, declRef);
        }
    }

    private void addDefinitionLocationHints(NamespaceDeclarationReference declRef, DeclarationReference hintRef) {
        if (hintRef == null) {
            return;
        }
        for (DeclarationReferenceDependency curDependcency : hintRef.getRequiredDependencies()) {
            if (!curDependcency.isSelfDependency()) {
                declRef.addDefinitionLocationHint(curDependcency.getDeclaration().getFileLocation());
            }
        }
    }

    private void addName(IASTName name) {
        IBinding binding = BindingHelper.unwrapProblemBinding(name.resolveBinding());

        if (binding == null) {
            if (shouldLogNullBinding(name)) {
                String pos = file.getSmartPath() + FileHelper.getPositionString(name.getFileLocation());
                String msg = "Cannot resolve binding for '" + name + "' in " + pos + ".";
                IncludatorPlugin.logStatus(new IncludatorStatus(IStatus.WARNING, msg), file);
            }
            return;
        } else if (ownerIsTemplateParameter(binding)) {
            return;
        }
        if (binding instanceof IProblemBinding && ((IProblemBinding) binding).getID() != IProblemBinding.SEMANTIC_AMBIGUOUS_LOOKUP) {
            binding = tryLookupInIndex(name);
        }
        DeclarationReference declRef;
        if (refs.containsKey(binding)) {
            declRef = refs.get(binding);
            declRef.combineMetaInfoOfNameWith(name);
        } else {
            declRef = createNewRef(name, binding);
            refs.put(binding, declRef);
        }
        if (name.isDefinition()) {
            performAdditionalRefActions(declRef);
        }
        addDefRequirementOfVariableType(name, declRef);
    }

    private static boolean isNameInFunctionCall(IASTName name) {
        IASTIdExpression idExpression = ASTQueries.findAncestorWithType(name, IASTIdExpression.class);
        return idExpression != null && idExpression.getParent() instanceof IASTFunctionCallExpression;
    }

    private IBinding tryLookupInIndex(IASTName name) {
        char[] lookupName = name.getLookupKey();
        IASTTranslationUnit ast = name.getTranslationUnit();
        IIndex index = ast.getIndex();
        Optional<IBinding> result = Optional.empty();
        try {
            IBinding[] bindings = index.findBindings(lookupName, false, IndexFilter.ALL, null);
            if (isNameInFunctionCall(name)) {
                ICPPFunction[] functionBindings = Arrays.stream(bindings).filter(ICPPFunction.class::isInstance).map(ICPPFunction.class::cast)
                        .toArray(ICPPFunction[]::new);
                if (functionBindings.length > 0) {
                    LookupData lookupData = CPPSemantics.createLookupData(name);
                    try {
                        IBinding resolvedFunction = CPPSemantics.resolveFunction(lookupData, functionBindings, true, true);
                        if (resolvedFunction != null) {
                            return resolvedFunction;
                        }
                    } catch (DOMException e) {
                        IncludatorPlugin.log(e);
                    }
                }
            }
            result = Arrays.stream(bindings).findFirst();
        } catch (CoreException e) {}
        return result.orElse(name.resolveBinding());
    }

    /**
     * binding of name "i" in the code "void foo(void(bar)(int i));" has a null
     * binding even if there is nothing wrong with that code (so warning about
     * is not necessary).
     */
    private boolean shouldLogNullBinding(IASTName name) {
        IASTNode parent = name.getParent();
        if (!(parent instanceof IASTDeclarator)) {
            return true;
        }
        parent = parent.getParent();
        if (!(parent instanceof IASTParameterDeclaration)) {
            return true;
        }
        parent = parent.getParent();
        if (!(parent instanceof IASTFunctionDeclarator)) {
            return true;
        }
        parent = parent.getParent();
        return !(parent instanceof IASTParameterDeclaration);
    }

    private boolean ownerIsTemplateParameter(IBinding binding) {
        IBinding owner = binding.getOwner();
        return owner instanceof ICPPTemplateParameter || owner instanceof ICPPDeferredClassInstance;
    }

    private void addDefRequirementOfVariableType(IASTName name, DeclarationReference variableRef) {
        if (NodeHelper.findParentOfType(IASTFieldReference.class, name) != null) {
            IBinding nameBinding = variableRef.getBinding();
            if (nameBinding instanceof ICPPVariable) {
                IType type = ((ICPPVariable) nameBinding).getType();
                while (type instanceof IPointerType) {
                    type = ((IPointerType) type).getType();
                }
                if (type instanceof IBinding && refs.containsKey(type)) {
                    DeclarationReference typeRef = refs.get(type);
                    typeRef.setIsForwardDeclEnough(false);
                }
            }
        }

    }

    private void performAdditionalRefActions(DeclarationReference declRef) {
        if (declRef instanceof ClassDeclarationReference) {
            ClassDeclarationReference classDeclRef = (ClassDeclarationReference) declRef;
            ConstructorReferenceHelper.addImplicitConstrDefinitions(classDeclRef, refs);
            DestructorReferenceHelper.addImplicitDestrDefinition(classDeclRef, refs);
        } else if (declRef instanceof ConstructorDeclarationReference) {
            ConstructorReferenceHelper.addImplicitMemberVarConstructors((ConstructorDeclarationReference) declRef, refs);
            ConstructorReferenceHelper.addImplicitBaseClassConstructors((ConstructorDeclarationReference) declRef, refs);
        } else if (declRef instanceof DestructorDeclarationReference) {
            DestructorReferenceHelper.addImplicitMemberVarDestructors((DestructorDeclarationReference) declRef, refs);
            DestructorReferenceHelper.addImplicitBaseClassDestructors((DestructorDeclarationReference) declRef, refs);
        }
    }

    private DeclarationReference createNewRef(IASTNode node, IBinding binding) {
        DeclarationReference declRef; // template?
        if (binding instanceof ICPPClassType) {
            ICPPClassType classBinding = BindingHelper.getMostSpecificDefiningClassBinding((ICPPClassType) binding, file.getProject().getIndex());
            declRef = new ClassDeclarationReference(classBinding, node, file);
        } else if (binding instanceof ICPPConstructor) {
            declRef = new ConstructorDeclarationReference((ICPPConstructor) binding, node, file);
        } else if (BindingHelper.isDestructor(binding)) {
            declRef = new DestructorDeclarationReference((ICPPMethod) binding, node, file);
        } else if (binding instanceof ICPPMethod) {
            declRef = new MethodDeclarationReference((ICPPMethod) binding, node, file);
        } else if (binding instanceof IFunction) {
            declRef = new FunctionDeclarationReference((IFunction) binding, node, file);
        } else if (binding instanceof ICPPNamespace) {
            declRef = new NamespaceDeclarationReference(binding, node, file);
        } else if (binding instanceof ICPPUsingDeclaration) {
            declRef = new UsingDeclarationReference(binding, node, file);
        } else if (binding instanceof ITypedef) {
            declRef = new TypeDefDeclarationReference(binding, node, file);
        } else if (binding instanceof IVariable) {
            declRef = new VariableDeclarationReference(binding, node, file);
        } else {
            declRef = new DeclarationReference(binding, node, file);
        }
        return declRef;
    }

    private boolean shouldConsiderName(IASTName name) {
      // @formatter:off
		return !isQualifiedName(name) && isLastPartOfName(name) && !isNamespaceName(name) && !isUnnamedName(name)
				&& !isNonDeclaratorTemplateId(name) && !isImplicitFunction(name);
		// @formatter:on
    }

    private boolean isImplicitFunction(IASTName name) {
        IBinding binding = name.resolveBinding();
      // @formatter:off
		if (binding instanceof ICPPConstructor || BindingHelper.isDestructor(binding)
				|| name.toString().startsWith("operator new") || name.toString().startsWith("operator delete")) {
			return false;
		}
		// @formatter:on
        return binding instanceof CPPImplicitFunction || binding instanceof CImplicitFunction;
    }

    /**
     * we do not want a reference to be spawned in the case of "vector<int> v;",
     * but one is required in the case of a template specialization e.g.
     * "template<> int foo<int>() { }" which needs to know the template
     * "template<class T> T foo() { }".
     */
    private boolean isNonDeclaratorTemplateId(IASTName name) {
        boolean isTemplId = name.getParent() instanceof ICPPASTTemplateId;
        if (!isTemplId) {
            return false;
        }
        IASTFunctionDeclarator declarator = NodeHelper.findParentOfType(IASTFunctionDeclarator.class, name);
        return declarator == null;
    }

    private boolean isUnnamedName(IASTName name) {
        return name.getFileLocation() == null && "".equals(name.toString());
    }

    private boolean isQualifiedName(IASTName name) {
        return name instanceof ICPPASTQualifiedName;
    }

    private boolean isNamespaceName(IASTName name) {
        IASTNode parent = name.getParent();
        if (parent instanceof ICPPASTQualifiedName) {
            parent = parent.getParent();
        }
        return (parent instanceof ICPPASTNamespaceDefinition) || (parent instanceof ICPPASTUsingDirective) || isAliasNameOfNamespaceAlias(parent,
                name);
    }

    private boolean isAliasNameOfNamespaceAlias(IASTNode parentNode, IASTName name) {
        if (parentNode instanceof ICPPASTNamespaceAlias) {
            return ((ICPPASTNamespaceAlias) parentNode).getAlias().equals(name);
        }
        return false;
    }

    private boolean isLastPartOfName(IASTName name) {
        final IASTNode parent = name.getParent();
        if (parent instanceof ICPPASTQualifiedName) {
            return name.equals(((ICPPASTQualifiedName) parent).getLastName());
        }
        return true;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
            //constructorHack.process(simpleDeclaration);
            destructorHack.process(simpleDeclaration);
        }
        return super.visit(declaration);
    }

    @Override
    public int leave(IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
            if (!isTypedefDeclaration(simpleDeclaration) && !isMemberDeclaration(simpleDeclaration)) {
                constructorHack.process(simpleDeclaration);
            }
        }
        return super.visit(declaration);
    }

    public static boolean isMemberDeclaration(IASTSimpleDeclaration simpleDeclaration) {
        return simpleDeclaration.getPropertyInParent() == IASTCompositeTypeSpecifier.MEMBER_DECLARATION;
    }

    public static boolean isTypedefDeclaration(IASTSimpleDeclaration simpleDeclaration) {
        return simpleDeclaration.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef;
    }

    @Override
    public int visit(IASTParameterDeclaration parameterDeclaration) {
        destructorHack.process(parameterDeclaration);
        parameterDeclarationHack.process(parameterDeclaration);
        return super.visit(parameterDeclaration);
    }

    @Override
    public int visit(IASTExpression expression) {
        IType expressionType = expression.getExpressionType();
        IType unwrappedType = BindingHelper.unwrapProblemTypeBinding(expressionType);
        IType type = BindingHelper.unwrapType(unwrappedType);
        if (type instanceof IPointerType) {
            IPointerType pointerType = (IPointerType) type;
            type = pointerType.getType();
        }
        if (!(type instanceof IBinding) || type instanceof IProblemBinding) {
            return super.visit(expression);
        }
        IBinding binding = (IBinding) type;
        if (refs.containsKey(binding)) {
            DeclarationReference declRef = refs.get(binding);
            declRef.combineMetaInfoOfExpressionWith(expression);
        } else {
            refs.put(binding, createNewRef(expression, binding));
        }
        return super.visit(expression);
    }
}
