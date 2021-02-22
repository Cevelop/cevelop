package com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.utils.ASTHelper;


@SuppressWarnings("restriction")
public class C48PreferInClassInitializerToCtorsQuickFix extends RuleQuickFix {

    private ICPPASTFunctionDeclarator          ctor;
    private List<IASTSimpleDeclaration>        memVars;
    private ICPPASTConstructorChainInitializer ctorInits[];

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C48.getId())) {
            return Rule.C48 + ": In-class initialize member variables from constructor";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        ctor = (ICPPASTFunctionDeclarator) markedNode;
        memVars = ASTHelper.collectMemberVariables(ASTHelper.getCompositeTypeSpecifier(markedNode));

        final IASTNode parent = ctor.getParent();
        if (parent instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition funcDef = (ICPPASTFunctionDefinition) parent;
            ctorInits = funcDef.getMemberInitializers();
        }

        for (final IASTSimpleDeclaration var : memVars) {
            replaceInitializerList(var, hRewrite);
        }

        removeDefaultValuesInCtor(hRewrite);
    }

    private void replaceInitializerList(final IASTSimpleDeclaration var, final ASTRewrite mainRewrite) {
        final IASTEqualsInitializer eqInit = getDefaultInitializer(var);
        final IASTInitializer newInitList = createInitList(eqInit);
        for (final IASTDeclarator dec : var.getDeclarators()) {
            final ICPPASTDeclarator newDecl = createNewDeclarator(dec, newInitList);

            mainRewrite.replace(dec, newDecl, null);
        }
    }

    private IASTEqualsInitializer getDefaultInitializer(final IASTSimpleDeclaration var) {
        final IASTInitializer init = getCtorInitializer(var);
        return init instanceof IASTEqualsInitializer ? (IASTEqualsInitializer) init : null;
    }

    private IASTInitializer getCtorInitializer(final IASTSimpleDeclaration var) {
        for (final ICPPASTConstructorChainInitializer init : ctorInits) {
            for (final IASTDeclarator dec : var.getDeclarators()) {
                if (ASTHelper.isSameName(init.getMemberInitializerId(), dec.getName())) {
                    return getEqualInitializer(init);
                }
            }
        }
        return null;
    }

    private IASTInitializer getEqualInitializer(final ICPPASTConstructorChainInitializer init) {
        for (final ICPPASTParameterDeclaration param : ctor.getParameters()) {
            final ICPPASTDeclarator decl = param.getDeclarator();
            final IASTInitializer ctorInitList = init.getInitializer();
            final IASTNode node = ctorInitList.getChildren()[0];
            if (node instanceof IASTIdExpression) {
                final IASTIdExpression expr = (IASTIdExpression) node;
                if (ASTHelper.isSameName(expr.getName(), decl.getName())) {
                    return decl.getInitializer();
                }
            }
        }
        return null;
    }

    private IASTInitializer createInitList(final IASTEqualsInitializer eqInit) {
        IASTExpression expr = null;
        for (final IASTNode node : eqInit.getChildren()) {
            if (node instanceof IASTLiteralExpression) {
                expr = (IASTExpression) node.copy(CopyStyle.withLocations);
                break;
            } else if (node instanceof IASTInitializerList) {
                return (IASTInitializer) node.copy(CopyStyle.withLocations);
            }
        }
        final ICPPASTInitializerList initList = factory.newInitializerList();
        initList.addClause(expr);
        return initList;
    }

    private void removeDefaultValuesInCtor(final ASTRewrite mainRewrite) {
        for (final ICPPASTParameterDeclaration param : ctor.getParameters()) {
            mainRewrite.remove(param.getDeclarator().getInitializer(), null);
        }
    }

    private ICPPASTDeclarator createNewDeclarator(final IASTDeclarator dec, final IASTInitializer newInitList) {
        final ICPPASTDeclarator newDec = factory.newDeclarator(dec.getName().copy());
        newDec.setInitializer(newInitList);
        return newDec;
    }

}
