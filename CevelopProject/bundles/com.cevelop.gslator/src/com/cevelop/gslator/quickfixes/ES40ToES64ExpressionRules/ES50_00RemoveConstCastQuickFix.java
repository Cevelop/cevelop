package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.resources.IFile;

import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.ES50DontCastAwayConstVisitor;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.utils.ASTHelper;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


abstract public class ES50_00RemoveConstCastQuickFix extends BaseQuickFix {

    protected boolean isFunctionConst(IASTNode node) {
        return ES50DontCastAwayConstVisitor.isFunctionConst(node);
    }

    protected boolean isOperandMemberOfClass(IASTCastExpression markedNode) {
        return ES50DontCastAwayConstVisitor.isOperandMemberOfClass(markedNode, astRewriteStore);
    }

    protected ICPPASTCompositeTypeSpecifier getCompositeTypeDeclarationFromFunctionWithQualifiedName(ICPPASTFunctionDefinition funcdef) {
        return ES50DontCastAwayConstVisitor.getCompositeTypeDeclarationFromFunctionWithQualifiedName(funcdef, astRewriteStore);
    }

    protected boolean isFileWithDeclarationOfNameModifiable(IASTName iastname) {
        //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
        List<IASTNode> names = ASTHelper.findNames(iastname.resolveBinding(), CProjectUtil.getCProject(CProjectUtil.getProject(iastname)),
                IIndex.FIND_DECLARATIONS_DEFINITIONS);
        if (names.size() == 1) {
            IASTNode name = names.get(0);
            IFile file = name.getTranslationUnit().getOriginatingTranslationUnit().getFile();
            if (file != null) return !file.isReadOnly();
        }
        return true; // when in doubt assume writable
    }

    protected void removeConstCast(IASTCastExpression markedNode, ASTRewrite hRewrite) {
        hRewrite.replace(markedNode, markedNode.getOperand(), null);
    }
}
