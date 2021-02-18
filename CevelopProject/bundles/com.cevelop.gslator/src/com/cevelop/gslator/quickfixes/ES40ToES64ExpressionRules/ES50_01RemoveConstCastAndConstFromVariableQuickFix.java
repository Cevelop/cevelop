package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationListOwner;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.utils.ASTHelper;


public class ES50_01RemoveConstCastAndConstFromVariableQuickFix extends ES50_00RemoveConstCastQuickFix {

    @Override
    public boolean isApplicable(IMarker marker) {
        if (super.isApplicable(marker)) {
            IASTCastExpression markedNode = (IASTCastExpression) getMarkedNode(marker);
            if(markedNode == null) {
            	return false;
            }
            
            IASTTranslationUnit tu = markedNode.getTranslationUnit();
            if (tu == null) {
            	return false;
            }
            
            IIndex index = tu.getIndex();
            try {
                index.acquireReadLock();
                IType type = ASTHelper.getTypeFromExpressionElement(markedNode.getOperand());
                if (!ASTHelper.isTypeConst(type) && (!isFunctionConst(markedNode) || !isOperandMemberOfClass(markedNode))) {// Case "const_cast not needed"
                    return true;
                } else if (!ASTHelper.isTypeConst(type) && isOperandMemberOfClass(markedNode) && isFunctionConst(markedNode)) { // Case "func is const" (membervar can be mutable)
                    ICPPASTFunctionDefinition func = ASTHelper.getFunctionDefinition(markedNode);
                    if (isFileWithDeclarationOfNameModifiable(func.getDeclarator().getName())) {
                        return true;
                    }
                } else if (ASTHelper.isTypeConst(type)) {
                    IASTExpression op = markedNode.getOperand();
                    if (op instanceof IASTIdExpression && isFileWithDeclarationOfNameModifiable(((IASTIdExpression) op).getName())) {
                        return true; // Case "var can be made non-const"
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                index.releaseReadLock();
            }
        }
        return false;
    }

    @Override
    public String getLabel() {
        String additional = "";
        IASTCastExpression markedNode = (IASTCastExpression) getMarkedNode(marker);
        IIndex index = markedNode.getTranslationUnit().getIndex();
        try {
            index.acquireReadLock();

            IType type = ASTHelper.getTypeFromExpressionElement(markedNode.getOperand());

            if (!ASTHelper.isTypeConst(type) && isFunctionConst(markedNode) && isOperandMemberOfClass(markedNode)) {
                additional = "make function non-const and ";
            } else if (ASTHelper.isTypeConst(type)) {
                additional = "make variable non-const and ";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            index.releaseReadLock();
        }
        return Rule.ES50 + ": " + additional + "remove const cast";
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTCastExpression constcast = (IASTCastExpression) markedNode;
        IType type = ASTHelper.getTypeFromExpressionElement(constcast.getOperand());
        if (!ASTHelper.isTypeConst(type) && isFunctionConst(constcast) && isOperandMemberOfClass(constcast)) { // Case "func is const" (membervar can be mutable)
            ICPPASTFunctionDefinition func = ASTHelper.getFunctionDefinition(constcast);
            removeConstFromFunction(func, hRewrite);
        } else if (ASTHelper.isTypeConst(type)) {
            removeConstFromVariable((IASTIdExpression) constcast.getOperand(), hRewrite);
        }
        removeConstCast(constcast, hRewrite);
    }

    private void removeConstFromFunction(ICPPASTFunctionDefinition func, ASTRewrite hRewrite) {
        removeConstFromDeclarator((ICPPASTFunctionDeclarator) func.getDeclarator(), hRewrite);

        ICPPASTCompositeTypeSpecifier theClass = getCompositeTypeDeclarationFromFunctionWithQualifiedName(func);
        if (theClass != null) {
            ICPPASTFunctionDeclarator declarator = getFunctionDeclaratorWithNameFromClass(theClass, func.getDeclarator().getName());
            ASTRewrite rewr = hRewrite;
            if (!func.getTranslationUnit().equals(declarator.getTranslationUnit())) {
                rewr = astRewriteStore.getASTRewrite(declarator);
            }
            if (declarator != null) removeConstFromDeclarator(declarator, rewr);
        }
    }

    private ICPPASTFunctionDeclarator getFunctionDeclaratorWithNameFromClass(IASTDeclarationListOwner theClass, IASTName name) {
        IASTDeclaration[] decls = theClass.getDeclarations(false);
        for (IASTDeclaration iastDeclaration : decls) {
            for (IASTDeclarator declarator : ASTHelper.getDeclarators(iastDeclaration)) {
                if (declarator instanceof ICPPASTFunctionDeclarator) {
                    if (ASTHelper.namesEqual(declarator.getName(), name)) {
                        return (ICPPASTFunctionDeclarator) declarator;
                    }
                }
            }
        }
        return null;
    }

    private void removeConstFromDeclarator(ICPPASTFunctionDeclarator funcdecl, ASTRewrite hRewrite) {
        ICPPASTFunctionDeclarator newFuncdecl = funcdecl.copy();
        newFuncdecl.setConst(false);
        hRewrite.replace(funcdecl, newFuncdecl, null);
    }

    private void removeConstFromVariable(IASTIdExpression var, ASTRewrite hRewrite) {
        IASTDeclarator declarator = ASTHelper.findDeclaratorToName(var.getName(), astRewriteStore);
        if (declarator != null) {
            IASTDeclaration declaration = ASTHelper.getDeclaration(declarator);
            if (declaration != null) {
                IASTDeclSpecifier declspec = ASTHelper.getDeclSpecifierFromDeclaration(declaration);
                if (declspec != null) {
                    IASTDeclSpecifier newdeclspec = declspec.copy();
                    newdeclspec.setConst(false);
                    ASTRewrite rewr = hRewrite;
                    if (!var.getTranslationUnit().equals(declspec.getTranslationUnit())) {
                        rewr = astRewriteStore.getASTRewrite(declspec);
                    }
                    rewr.replace(declspec, newdeclspec, null);
                }
            }
        }
    }

}
