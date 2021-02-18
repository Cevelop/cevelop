package com.cevelop.charwars.utils;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;

import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.constants.StringType;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class ComplementaryNodeFactory {

    private static IBetterFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    public static IASTExpression newNposExpression(StringType stringType) {
        final ICPPASTQualifiedName npos = factory.newQualifiedName(factory.newName(StdString.STD));
        npos.addName(factory.newName(stringType.getClassName()));
        npos.addName(factory.newName(StdString.NPOS));
        return factory.newIdExpression(npos);
    }

}
