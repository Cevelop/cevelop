package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules.utils;

import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;

import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;


@SuppressWarnings("restriction")
public class ES46QuickFixData {

    private IASTNode oldNode = null;
    private IASTNode subject = null;
    private IType    itype   = null;
    private String   type    = "";

    public void setOld(IASTNode node) {
        oldNode = node;
    }

    public IASTNode getOld() {
        return oldNode;
    }

    public void setSubject(IASTNode node) {
        subject = node.copy(CopyStyle.withLocations);
    }

    public IASTNode getSubject() {
        return subject;
    }

    public void setType(IType iType) {
        itype = iType;
        type = convertITypeToString(iType);
    }

    public void setType(String string) {
        itype = null;
        type = string;
    }

    public IType getIType() {
        return itype;
    }

    public String getTypeString() {
        return type;
    }

    public ICPPASTFunctionCallExpression getNew(String name) {
        return getNew(new String[] { "gsl" }, name);
    }

    public ICPPASTFunctionCallExpression getNew(String[] qualifiers, String name) {
        return ASTFactory.newQualifiedNamedFunctionCall(qualifiers, name + "<" + type + ">", new IASTInitializerClause[] {
                                                                                                                           (IASTInitializerClause) getSubject() });
    }

    public boolean allSet() {
        if (oldNode != null && subject != null && type != "") {
            return true;
        }
        return false;
    }

    private static String convertITypeToString(IType iType) {
        String type = iType.toString();
        if (iType instanceof CPPTypedef) {
            IASTNode[] decls = ((CPPTypedef) iType).getDeclarations();
            type = ((IASTName) decls[0]).toString();
        }
        return ES46LossyType.normalizeType(type);
    }
}
