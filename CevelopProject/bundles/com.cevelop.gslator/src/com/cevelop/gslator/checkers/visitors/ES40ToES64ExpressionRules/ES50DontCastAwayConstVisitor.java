package com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.quickfixes.utils.ASTRewriteStore;
import com.cevelop.gslator.utils.ASTHelper;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


public class ES50DontCastAwayConstVisitor extends BaseVisitor {

    public ES50DontCastAwayConstVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {

        if (expression instanceof IASTCastExpression && isConstCast((IASTCastExpression) expression) && nodeHasNoIgnoreAttribute(this, expression)) {
            checker.reportProblem(ProblemId.P_ES50, expression);
        }
        return super.visit(expression);
    }

    public static boolean isConstCast(IASTCastExpression expression) {
        if (expression.getOperator() == ICPPASTCastExpression.op_const_cast) return true;
        if (expression.getOperator() == IASTCastExpression.op_cast) {
            if (isCCastRemovingConst(expression)) return true;
            if (isFunctionConst(expression) && isOperandMemberOfClass(expression, null)) return true;
        }
        return false;
    }

    private static boolean isCCastRemovingConst(IASTCastExpression expression) {
        List<IASTTypeId> intermediates = new ArrayList<>();
        IASTTypeId targetType = expression.getTypeId();
        IType sourceType = ASTHelper.getTypeFromExpressionElement(expression.getOperand(), true, intermediates, IASTTypeId.class);
        boolean isTargetConst = targetType.getDeclSpecifier().isConst();
        boolean isSourceConst = ASTHelper.isTypeConst(sourceType);
        if (!isTargetConst && isSourceConst) return true;
        if (!isTargetConst && !isSourceConst) {
            IASTTypeId toType = targetType;
            for (IASTTypeId fromType : intermediates) {
                if (!toType.getDeclSpecifier().isConst() && fromType.getDeclSpecifier().isConst()) return true;
                toType = fromType;
            }
        }
        return false;
    }

    public static boolean isFunctionConst(IASTNode node) {
        ICPPASTFunctionDefinition funcdef = ASTHelper.getFunctionDefinition(node);
        if (funcdef != null && funcdef.getDeclarator() instanceof ICPPASTFunctionDeclarator) {
            ICPPASTFunctionDeclarator funcdelc = (ICPPASTFunctionDeclarator) funcdef.getDeclarator();
            if (funcdelc != null) return funcdelc.isConst();
        }
        return false;
    }

    public static boolean isOperandMemberOfClass(IASTCastExpression markedNode, ASTRewriteStore astRewriteStore) {
        ICPPASTCompositeTypeSpecifier theClass = ASTHelper.getCompositeTypeSpecifier(markedNode);
        if (theClass == null) theClass = getCompositeTypeDeclarationFromFunctionWithQualifiedName(ASTHelper.getFunctionDefinition(markedNode),
                astRewriteStore);

        IASTExpression op = markedNode.getOperand();
        if (op instanceof IASTIdExpression) {
            return ASTHelper.isNameMemberOfClass(((IASTIdExpression) op).getName(), theClass);
        }
        return false;
    }

    public static ICPPASTCompositeTypeSpecifier getCompositeTypeDeclarationFromFunctionWithQualifiedName(ICPPASTFunctionDefinition funcdef,
            ASTRewriteStore astRewriteStore) {
        IASTName funcname = funcdef.getDeclarator().getName();
        if (funcname instanceof ICPPASTQualifiedName) {
            ICPPASTNameSpecifier[] qualifier = ((ICPPASTQualifiedName) funcname).getQualifier();
            ICPPASTNameSpecifier classnamequalifier = qualifier[qualifier.length - 1];
            if (classnamequalifier instanceof IASTName) {
                //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
                List<IASTNode> names = ASTHelper.findNames(astRewriteStore, classnamequalifier.resolveBinding(), CProjectUtil.getCProject(CProjectUtil
                        .getProject(classnamequalifier)), IIndex.FIND_DECLARATIONS_DEFINITIONS, null);
                return names.stream().map(ASTHelper::getCompositeTypeSpecifier).filter(n -> n != null).findFirst().orElse(null);
            }
        }
        return null;
    }
}
