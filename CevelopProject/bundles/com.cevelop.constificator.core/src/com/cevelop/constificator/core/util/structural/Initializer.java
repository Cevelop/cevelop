package com.cevelop.constificator.core.util.structural;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;


public class Initializer {

    public static Optional<IASTInitializerClause[]> getClauses(IASTInitializer initializer) {
        if (initializer instanceof ICPPASTConstructorInitializer) {
            return Optional.of(((ICPPASTConstructorInitializer) initializer).getArguments());
        } else if (initializer instanceof ICPPASTInitializerList) {
            return Optional.of(((ICPPASTInitializerList) initializer).getClauses());
        }

        return Optional.empty();
    }

}
