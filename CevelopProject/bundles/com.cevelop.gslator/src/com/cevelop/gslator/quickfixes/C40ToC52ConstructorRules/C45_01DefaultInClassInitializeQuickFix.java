package com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.utils.ASTHelper;


public class C45_01DefaultInClassInitializeQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        return Rule.C45 + ": In-class default-initialize variables";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) {
            return false;
        }
        IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) {
            return false;
        }
        return markedNode instanceof IASTName;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final List<IASTSimpleDeclaration> memVars = ASTHelper.collectMemberVariables(ASTHelper.getCompositeTypeSpecifier(markedNode));

        defaultInitUninitMemVars(hRewrite, getUninitializedMemVars(memVars));
    }

    @SuppressWarnings("restriction")
    protected void defaultInitUninitMemVars(final ASTRewrite hRewrite, final List<IASTSimpleDeclaration> uninitMemVars) {
        for (final IASTSimpleDeclaration var : uninitMemVars) {
            for (final IASTDeclarator dec : var.getDeclarators()) {
                if (dec.getInitializer() == null) {
                    final IASTDeclarator newDec = dec.copy(CopyStyle.withLocations);
                    newDec.setInitializer(factory.newInitializerList());
                    hRewrite.replace(dec, newDec, null);
                }
            }
        }
    }

    protected List<IASTSimpleDeclaration> getUninitializedMemVars(final List<IASTSimpleDeclaration> memVars) {
        final List<IASTSimpleDeclaration> unInitVars = new ArrayList<>();
        for (final IASTSimpleDeclaration var : memVars) {
            for (final IASTDeclarator decl : var.getDeclarators()) {
                if (decl.getInitializer() == null) {
                    unInitVars.add(var);
                }
            }
        }
        return unInitVars;
    }

}
