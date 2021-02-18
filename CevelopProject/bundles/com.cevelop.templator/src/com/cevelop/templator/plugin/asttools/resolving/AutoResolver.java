package com.cevelop.templator.plugin.asttools.resolving;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPPlaceholderType.PlaceholderKind;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.InstantiationContext;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalBinary;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalConditional;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalUnary;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.FindRelevantExpressionsInReturnVisitor;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.NameTypeKind;
import com.cevelop.templator.plugin.asttools.data.RelevantNameCache;
import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.asttools.data.UnresolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;


/**
 * Class to create a ResolvedName from an auto decl specifier. This only works
 * if the type is template dependent, else it is not important and null is
 * returned
 */
public class AutoResolver {

    private int               maxDepth; // max depth to guarantee termination
    private ASTAnalyzer       analyzer;
    private int               depth;
    private IASTDeclSpecifier declSpec;

    public AutoResolver(ASTAnalyzer analyzer) {
        this.analyzer = analyzer;
        maxDepth = 100;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Tests if a decl specifier is auto or decltype(auto)
     * @param declSpecifier
     * The decl specifier to check if it is auto
     * @return true for auto and decltype(auto), false otherwise
     */
    public static boolean isAuto(IASTDeclSpecifier declSpecifier) {
        if (declSpecifier == null) {
            return false;
        }
        PlaceholderKind placeholder = CPPVisitor.usesAuto(declSpecifier);
        return placeholder == PlaceholderKind.Auto || placeholder == PlaceholderKind.DecltypeAuto;
    }

    public IASTDeclSpecifier getAutoDeclSpec(IASTName name) throws TemplatorException {
        if (!ASTTools.isRelevantName(name)) {
            return null;
        }
        IASTName definitionName = analyzer.getTypeDeducer().getDefinitionForName(name);
        if (definitionName == null) {
            return null;
        }
        IASTSimpleDeclaration simpleDecl = ASTTools.findFirstAncestorByType(definitionName, IASTSimpleDeclaration.class, 2); // was 0
        if (simpleDecl == null) {
            return null;
        }
        IASTDeclSpecifier declSpec = simpleDecl.getDeclSpecifier();
        if (!(AutoResolver.isAuto(declSpec))) {
            return null;
        }
        return declSpec;
    }

    /**
     * Creates a ResolvedName from an auto decl specifier. This only works if the
     * type is template dependent, else it is not important and null is returned.
     *
     * @param declSpec A decl specifier
     * 
     * @param parent A {@link AbstractResolvedNameInfo}
     *
     * @return A ResolvedName which is created by searching a name and a definition
     * which represents the type auto resolves to or {@code null}
     * @throws TemplatorException
     * If {@link #createResolvedNameFromFunctionDefinition(ICPPASTFunctionDefinition, AbstractResolvedNameInfo, boolean)} throws
     */
    public ResolvedName createResolvedNameFromAuto(IASTDeclSpecifier declSpec, AbstractResolvedNameInfo parent) throws TemplatorException {
        if (!isAuto(declSpec)) {
            return null;
        }
        depth = 0;
        this.declSpec = declSpec;

        if (declSpec.getParent() instanceof ICPPASTFunctionDefinition) {
            return createResolvedNameFromFunctionDefinition((ICPPASTFunctionDefinition) declSpec.getParent(), parent, false);
        }

        IASTSimpleDeclaration simpleDecl = ASTTools.findFirstAncestorByType(declSpec, IASTSimpleDeclaration.class, 2);
        if (simpleDecl != null) {
            return createResolvedNameFromSimpleDeclaration(simpleDecl, parent, false);
        }
        return null;
    }

    private ResolvedName createResolvedNameFromSimpleDeclaration(IASTSimpleDeclaration simpleDecl, AbstractResolvedNameInfo parent,
            boolean checkDeclSpec) throws TemplatorException {
        // check if type name is in decl specifier but not if we already know that it is auto
        if (checkDeclSpec) {
            IASTDeclSpecifier declSpec = simpleDecl.getDeclSpecifier();
            if (!isAuto(declSpec)) {
                IASTName name = ASTTools.getName(declSpec);
                return createResolvedNameFromName(name, parent);
            }
        }

        IASTInitializerClause initializerClause = getInitializerClause(simpleDecl);
        if (initializerClause == null) {
            return null;
        } else if (initializerClause instanceof ICPPASTLambdaExpression) {
            return createResolvedNameWithLambda(simpleDecl, parent);
        } else if (initializerClause instanceof IASTExpression) {
            return createResolvedNameFromExpression((IASTExpression) initializerClause, parent);
        }
        return null;
    }

    private ResolvedName createResolvedNameFromExpression(IASTExpression expr, AbstractResolvedNameInfo parent) throws TemplatorException {
        ++depth;
        if (depth >= maxDepth) {
            return null;
        }
        if (expr instanceof ICPPASTCastExpression) {
            return createResolvedNameFromCastExpression((ICPPASTCastExpression) expr, parent);
        } else if (expr instanceof ICPPASTSimpleTypeConstructorExpression) {
            return createResolvedNameFromSimpleTypeConstructorExpression((ICPPASTSimpleTypeConstructorExpression) expr, parent);
        } else if (expr instanceof ICPPASTNewExpression) {
            return createResolvedNameFromNewExpression((ICPPASTNewExpression) expr, parent);
        } else if (expr instanceof ICPPASTFunctionCallExpression) {
            return createResolvedNameFromFunctionCallExpression((ICPPASTFunctionCallExpression) expr, parent);
        } else if (expr instanceof IASTConditionalExpression) {
            return createResolvedNameFromConditionalExpression((IASTConditionalExpression) expr, parent);
        } else if (expr instanceof ICPPASTBinaryExpression || expr instanceof ICPPASTUnaryExpression || expr instanceof ICPPASTLiteralExpression ||
                   expr instanceof IASTArraySubscriptExpression) {
            return createResolvedNameFromImplicitNameOwner((IASTImplicitNameOwner) expr, parent);
        } else if (expr instanceof IASTIdExpression) {
            return createResolvedNameFromIdExpression((IASTIdExpression) expr, parent);
        } else if (expr instanceof ICPPASTFieldReference) {
            return createResolvedNameFromFieldReference((ICPPASTFieldReference) expr, parent);
        }
        return null;
    }

    private ResolvedName createResolvedNameFromFunctionCallExpression(ICPPASTFunctionCallExpression funCallExpr, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        IASTName funCallExprName = ASTTools.getName(funCallExpr);
        AbstractResolvedNameInfo info = createAbstractResolvedNameInfoFromName(funCallExprName, parent);
        if (info == null) {
            return null;
        }
        if (info.getType() == NameTypeKind.CLASS) {
            return new ResolvedName(declSpec, info);
        }
        IASTDeclaration definition = info.getDefinition();
        if (definition instanceof ICPPASTFunctionDefinition) {
            return createResolvedNameFromFunctionDefinition((ICPPASTFunctionDefinition) info.getDefinition(), info, true);
        }
        return createResolvedNameFromFunctionDefinition(getFunctionDefinitionFromFunctionName(funCallExprName), info, true);
    }

    private ResolvedName createResolvedNameFromFunctionDefinition(ICPPASTFunctionDefinition funDef, AbstractResolvedNameInfo parent,
            boolean checkDeclSpec) throws TemplatorException {
        if (funDef == null) {
            return null;
        }

        // check if type name is in decl specifier but not if we already know that it is auto
        if (checkDeclSpec) {
            IASTDeclSpecifier declSpec = funDef.getDeclSpecifier();
            if (declSpec != null && !isAuto(declSpec)) {
                IASTName name = ASTTools.getName(declSpec);
                return createResolvedNameFromName(name, parent);
            }
        }

        // check if type name is given as trailing return type
        IASTTypeId typeId = getTrailingReturnType(funDef.getDeclarator());
        if (typeId != null) {
            return createResolvedNameFromTypeId(typeId, parent);
        }

        // follow the most relevant expression
        FindRelevantExpressionsInReturnVisitor findReturn = new FindRelevantExpressionsInReturnVisitor();
        funDef.accept(findReturn);
        IASTExpression expression = findReturn.getMostRelevantExpression();
        return createResolvedNameFromExpression(expression, parent);
    }

    private IASTTypeId getTrailingReturnType(IASTFunctionDeclarator funDecl) {
        if (!(funDecl instanceof ICPPASTFunctionDeclarator)) {
            return null;
        }
        ICPPASTFunctionDeclarator cppFunDecl = (ICPPASTFunctionDeclarator) funDecl;
        return cppFunDecl.getTrailingReturnType();
    }

    private IASTInitializerClause getInitializerClause(IASTSimpleDeclaration simpleDecl) {
        IASTInitializer initializer = simpleDecl.getDeclarators()[0].getInitializer();
        if (initializer == null) {
            return null;
        } else if (initializer instanceof ICPPASTInitializerList) {
            ICPPASTInitializerList initializerList = (ICPPASTInitializerList) initializer;
            if (initializerList.getClauses() != null) {
                return initializerList.getClauses()[0];
            }
        } else if (initializer instanceof IASTEqualsInitializer) {
            IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            return equalsInitializer.getInitializerClause();
        } else if (initializer instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) initializer;
            return constructorInitializer.getArguments()[0];
        }
        return null;
    }

    private ResolvedName createResolvedNameFromIdExpression(IASTIdExpression idExpr, AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName name = analyzer.getDefinitionName(idExpr.getName(), true, false);
        if (name instanceof CPPASTTemplateId) {
            return createResolvedNameFromName(name, parent);
        }
        ICPPTemplateParameter templateParam = NameDeduction.getTemplateParameter(name);
        if (templateParam != null) {
            return createResolvedNameFromName(name, parent);
        }
        IASTSimpleDeclaration simpleDecl = ASTTools.findFirstAncestorByType(name, IASTSimpleDeclaration.class, 5);
        return createResolvedNameFromSimpleDeclaration(simpleDecl, parent, true);
    }

    private ResolvedName createResolvedNameFromSimpleTypeConstructorExpression(ICPPASTSimpleTypeConstructorExpression simpleTypeCtorExpr,
            AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName name = getNameFromTypeConstructor(simpleTypeCtorExpr);
        return createResolvedNameFromName(name, parent);
    }

    private ResolvedName createResolvedNameFromTypeId(IASTTypeId typeId, AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName name = ASTTools.getName(typeId);
        if (name != null) {
            return createResolvedNameFromName(name, parent);
        }
        IASTDeclSpecifier declSpec = typeId.getDeclSpecifier();
        if (declSpec.getChildren()[0] instanceof IASTExpression) {
            IASTExpression expr = (IASTExpression) declSpec.getChildren()[0];
            return createResolvedNameFromExpression(expr, parent);
        }
        return null;
    }

    private ResolvedName createResolvedNameFromCastExpression(IASTCastExpression castExpr, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        IASTTypeId typeId = castExpr.getTypeId();
        return createResolvedNameFromTypeId(typeId, parent);
    }

    private ResolvedName createResolvedNameFromNewExpression(ICPPASTNewExpression newExpr, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        IASTTypeId typeId = newExpr.getTypeId();
        return createResolvedNameFromTypeId(typeId, parent);
    }

    private ResolvedName createResolvedNameFromConditionalExpression(IASTConditionalExpression condExpr, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        IASTExpression expr = condExpr.getPositiveResultExpression();
        return createResolvedNameFromExpression(expr, parent);
    }

    private ResolvedName createResolvedNameFromImplicitNameOwner(IASTImplicitNameOwner expr, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        if (expr.getImplicitNames().length == 0) {
            if (expr instanceof ICPPASTExpression) {
                ICPPASTExpression cppExpr = (ICPPASTExpression) expr;
                ICPPEvaluation eval = cppExpr.getEvaluation();
                InstantiationContext context = new InstantiationContext(parent.getTemplateArgumentMap());
                ICPPEvaluation resEval = eval.instantiate(context, maxDepth);
                CPPSemantics.pushLookupPoint(expr);
                try {
                    ICPPFunction overload = null;
                    if (resEval instanceof EvalBinary) {
                        EvalBinary evalBinary = (EvalBinary) resEval;
                        overload = evalBinary.getOverload();
                    } else if (resEval instanceof EvalUnary) {
                        EvalUnary evalUnary = (EvalUnary) resEval;
                        overload = evalUnary.getOverload();
                    } else if (resEval instanceof EvalConditional) {
                        EvalConditional evalCon = (EvalConditional) resEval;
                        overload = evalCon.getOverload();
                    }
                    if (overload != null && overload instanceof CPPFunction) {
                        CPPFunction cppFunction = (CPPFunction) overload;
                        IASTFunctionDeclarator functionDecl = cppFunction.getDefinition();
                        if (functionDecl.getParent() instanceof ICPPASTFunctionDefinition) {
                            ICPPASTFunctionDefinition funDef = (ICPPASTFunctionDefinition) functionDecl.getParent();
                            return createResolvedNameFromFunctionDefinition(funDef, parent, true);
                        }
                    }
                } finally {
                    CPPSemantics.popLookupPoint();
                }
            }
            return null;
        }
        IASTName name = expr.getImplicitNames()[0];
        AbstractResolvedNameInfo info = createAbstractResolvedNameInfoFromName(name, parent);
        return createResolvedNameFromFunctionDefinition(getFunctionDefinitionFromFunctionName(name), info, true);
    }

    private ResolvedName createResolvedNameFromFieldReference(ICPPASTFieldReference fieldRef, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        return createResolvedNameFromName(fieldRef.getFieldName(), parent);
    }

    private ResolvedName createResolvedNameWithLambda(IASTSimpleDeclaration simpleDecl, AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName name = simpleDecl.getDeclarators()[0].getName();
        if (name == null) {
            return null;
        }
        UnresolvedNameInfo unresolvedName = new UnresolvedNameInfo(name);
        unresolvedName.setResolvingName(name);
        unresolvedName.setBinding(name.getBinding());
        unresolvedName.setType(NameTypeKind.LAMBDA);
        AbstractResolvedNameInfo info = AbstractResolvedNameInfo.create(unresolvedName, parent, analyzer, false);
        return new ResolvedName(declSpec, info);
    }

    private ICPPASTFunctionDefinition getFunctionDefinitionFromFunctionName(IASTName name) throws TemplatorException {
        IASTName funDefName = analyzer.getTypeDeducer().getDefinitionForName(name);
        ICPPASTFunctionDefinition funDef = ASTTools.findFirstAncestorByType(funDefName, ICPPASTFunctionDefinition.class, 4);
        return funDef;
    }

    private AbstractResolvedNameInfo createAbstractResolvedNameInfoFromName(IASTName name, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        if (name == null) {
            return null;
        }
        UnresolvedNameInfo unresolvedNameInfo = NameDeduction.deduceName(name, true, false, parent, RelevantNameCache.EMPTY_CACHE);
        if (unresolvedNameInfo == null) {
            return null;
        }
        return AbstractResolvedNameInfo.create(unresolvedNameInfo, parent, analyzer);
    }

    private ResolvedName createResolvedNameFromName(IASTName name, AbstractResolvedNameInfo parent) throws TemplatorException {
        AbstractResolvedNameInfo info = createAbstractResolvedNameInfoFromName(name, parent);
        if (info == null) {
            return null;
        }
        return new ResolvedName(declSpec, info);
    }

    private IASTName getNameFromTypeConstructor(ICPPASTSimpleTypeConstructorExpression simpleTypeCtorExpr) {
        return ASTTools.getName(simpleTypeCtorExpr.getDeclSpecifier());
    }
}
