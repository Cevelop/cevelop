package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;
import com.cevelop.codeanalysator.core.util.DeclarationHelper;


public class DoNotUseTypedefQuickFix extends BaseQuickFix {

    public DoNotUseTypedefQuickFix(String label) {
        super(label);
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;

        String contextFlagsString = getProblemArgument(marker, ContextFlagsHelper.DoNotUseTypedefContextFlagsStringIndex);
        return !contextFlagsString.contains(ContextFlagsHelper.DoNotUseTypedefContextFlagContextFlagStruct);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof IASTSimpleDeclaration)) return;
        IASTSimpleDeclaration typedefDeclaration = (IASTSimpleDeclaration) markedNode;

        replaceTypedefWithUsing(typedefDeclaration, hRewrite);
    }

    private void replaceTypedefWithUsing(IASTSimpleDeclaration typedefDeclaration, ASTRewrite hRewrite) {
        IASTDeclSpecifier typedefDeclSpecifier = typedefDeclaration.getDeclSpecifier();
        IASTNode parent = typedefDeclaration.getParent();

        IASTDeclarator[] typedefDeclarators = typedefDeclaration.getDeclarators();
        /*
         * if there is more than one declarator, do not replace, but
         * insert and then remove, else replace to keep changes minimal
         */
        if (typedefDeclarators.length > 1) {
            for (IASTDeclarator declarator : typedefDeclarators) {
                if (declarator != null) {
                    ICPPASTAliasDeclaration aliasDeclaration = createAliasDeclaration(typedefDeclSpecifier, declarator);
                    hRewrite.insertBefore(parent, typedefDeclaration, aliasDeclaration, null);
                }
            }
            hRewrite.remove(typedefDeclaration, null);
        } else {
            IASTDeclarator declarator = typedefDeclarators[0];
            if (declarator != null) {
                ICPPASTAliasDeclaration aliasDeclaration = createAliasDeclaration(typedefDeclSpecifier, declarator);
                hRewrite.replace(typedefDeclaration, aliasDeclaration, null);
            }
        }
    }

    private ICPPASTAliasDeclaration createAliasDeclaration(IASTDeclSpecifier declspecifier, IASTDeclarator declarator) {
        IASTName name = DeclarationHelper.findNameOfDeclarator(declarator);
        if (name == null) return null; // OOPS, no name found
        IASTDeclarator newdeclarator = declarator.copy(CopyStyle.withLocations);
        // remove name selectedName from declarator
        DeclarationHelper.makeDeclaratorAbstract(newdeclarator, factory);
        newdeclarator = DeclarationHelper.optimizeDeclaratorNesting(newdeclarator);
        IASTDeclSpecifier newdeclspecifer = declspecifier.copy(CopyStyle.withLocations);
        newdeclspecifer.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        ICPPASTTypeId aliasedType = factory.newTypeId(newdeclspecifer, newdeclarator);
        ICPPASTAliasDeclaration alias = factory.newAliasDeclaration(name.copy(), aliasedType);
        return alias;
    }
}
