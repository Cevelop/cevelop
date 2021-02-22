package com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class C45_02UseInClassInitializerForConstValsQuickFix extends C45_01DefaultInClassInitializeQuickFix {

    @Override
    public String getLabel() {
        return Rule.C45 + ": Initialize member variables in-class with values from default constructor";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        super.isApplicable(marker); // run but ignore value of super.isApplicable()
        IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) {
            return false;
        }
        return markedNode instanceof IASTDeclarator;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTFunctionDefinition ctorDef = getCtorDef(markedNode, markedNode.getParent());
        final ICPPASTConstructorChainInitializer inits[] = ctorDef.getMemberInitializers();
        final List<IASTSimpleDeclaration> memVars = ASTHelper.collectMemberVariables(ASTHelper.getCompositeTypeSpecifier(markedNode));

        inclassInitializeMemVars(hRewrite, inits, memVars);
        inClassDefaultInitializeMemVarsIfNecessary(hRewrite, inits, memVars);

        final IASTName name = ctorDef.getDeclarator().getName();
        astRewriteStore.getASTRewrite(ctorDef).replace(ctorDef, ASTFactory.newInlineDefaultConstructor(name, ctorDef.getDeclSpecifier().isInline()),
                null);
    }

    private ICPPASTFunctionDefinition getCtorDef(final IASTNode markedNode, final IASTNode parent) {
        if (parent instanceof ICPPASTFunctionDefinition) {
            return (ICPPASTFunctionDefinition) markedNode.getParent();
        } else if (parent instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) parent);
        }
        return null;
    }

    private void inclassInitializeMemVars(final ASTRewrite hRewrite, final ICPPASTConstructorChainInitializer[] inits,
            final List<IASTSimpleDeclaration> memVars) {
        for (final ICPPASTConstructorChainInitializer init : inits) {
            final IASTName ctorName = (IASTName) init.getChildren()[0];
            final IASTSimpleDeclaration var = getVariable(ctorName, memVars);
            inClassInitializeWithCtorValue(hRewrite, init, var);
        }
    }

    private IASTSimpleDeclaration getVariable(final IASTName ctorName, final List<IASTSimpleDeclaration> memVars) {
        for (final IASTSimpleDeclaration memVar : memVars) {
            for (final IASTDeclarator decl : memVar.getDeclarators()) {
                if (isSameName(ctorName, decl.getName())) {
                    return memVar;
                }
            }
        }
        return null;
    }

    private void inClassInitializeWithCtorValue(final ASTRewrite hRewrite, final ICPPASTConstructorChainInitializer ctorInit,
            final IASTSimpleDeclaration var) {
        for (final IASTDeclarator decl : var.getDeclarators()) {
            if (ctorInit.getMemberInitializerId().toString().equals(decl.getName().toString())) {
                final IASTDeclarator newVar = decl.copy(CopyStyle.withLocations);
                newVar.setInitializer(ctorInit.getInitializer().copy(CopyStyle.withLocations));
                hRewrite.replace(decl, newVar, null);
            }
        }
    }

    private void inClassDefaultInitializeMemVarsIfNecessary(final ASTRewrite hRewrite, final ICPPASTConstructorChainInitializer[] inits,
            final List<IASTSimpleDeclaration> memVars) {
        if (inits.length != memVars.size()) {
            defaultInitUninitMemVars(hRewrite, getCtorUninitedMemVars(getUninitializedMemVars(memVars), inits));
        }
    }

    private List<IASTSimpleDeclaration> getCtorUninitedMemVars(final List<IASTSimpleDeclaration> memVars,
            final ICPPASTConstructorChainInitializer[] inits) {
        final List<IASTSimpleDeclaration> uninitedMemVars = new ArrayList<>();
        for (final IASTSimpleDeclaration var : memVars) {
            if (!isCtorInited(inits, var)) {
                uninitedMemVars.add(var);
            }
        }
        return uninitedMemVars;
    }

    private boolean isCtorInited(final ICPPASTConstructorChainInitializer[] inits, final IASTSimpleDeclaration var) {
        for (final ICPPASTConstructorChainInitializer init : inits) {
            if (isSameName(var.getDeclarators()[0].getName(), init.getMemberInitializerId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isSameName(final IASTName name1, final IASTName name2) {
        return ASTHelper.isSameName(name1, name2) ? true : name1.toString().equals(name2.toString());
    }

}
