package com.cevelop.elevator.ast.transformation;

import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;


/**
 * Converts a constructor chain initializer into a C++11 initializer list.
 */
public class ConstructorChainConverter {

    private final ICPPASTConstructorChainInitializer chainInitializer;

    public ConstructorChainConverter(ICPPASTConstructorChainInitializer chainInitializer) {
        this.chainInitializer = chainInitializer;
    }

    public ICPPASTConstructorChainInitializer convert() {
        ICPPASTConstructorChainInitializer newInitializer = chainInitializer.copy();
        IASTInitializer initializer = newInitializer.getInitializer();
        IASTInitializerList initList = chainInitializer.getTranslationUnit().getASTNodeFactory().newInitializerList();
        if (initializer instanceof ICPPASTConstructorInitializer) {
            for (IASTInitializerClause clause : ((ICPPASTConstructorInitializer) initializer).getArguments()) {
                initList.addClause(clause);
            }
        }
        newInitializer.setInitializer(initList);
        return newInitializer;
    }
}
