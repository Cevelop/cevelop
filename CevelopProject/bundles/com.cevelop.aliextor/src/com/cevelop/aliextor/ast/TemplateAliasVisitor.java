package com.cevelop.aliextor.ast;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;

import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.ast.selection.IRefactorSelection;


public class TemplateAliasVisitor extends BaseASTVisitor {

    public TemplateAliasVisitor(IRefactorSelection refactorSelection) {
        super(refactorSelection);
        shouldVisitDeclSpecifiers = true;
        shouldVisitNames = true;
        shouldVisitTypeIds = true;
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        if (ASTHelper.isType(declSpec, Type.ICPPASTSimpleDeclSpecifier)) {
            occurrences.add(declSpec);
        }
        return super.visit(declSpec);
    }

    @Override
    public int visit(IASTName name) {
        if (ASTHelper.isType(name, Type.CPPASTName)) {
            occurrences.add(name);
        }
        return super.visit(name);
    }

    @Override
    public int visit(IASTTypeId typeId) {
        // Separator
        occurrences.add(null);
        return super.visit(typeId);
    }

}
