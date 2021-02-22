package com.cevelop.elevator.ast.analysis;

import static com.cevelop.elevator.ast.analysis.conditions.Condition.not;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;

import com.cevelop.elevator.ast.analysis.conditions.Condition;
import com.cevelop.elevator.ast.analysis.conditions.ContainsPackExpansion;
import com.cevelop.elevator.ast.analysis.conditions.HasInitializerListConstructor;
import com.cevelop.elevator.ast.analysis.conditions.HasNarrowingTypeConversion;
import com.cevelop.elevator.ast.analysis.conditions.IsElevated;
import com.cevelop.elevator.ast.analysis.conditions.IsReference;


/**
 * Collects all ConstructorChainInitializers that can be elevated.
 *
 */
public class InitializerCollector extends ASTVisitor {

    private final List<ICPPASTConstructorChainInitializer> initializers;
    private final Condition                                isElevationCandidate = not(new IsElevated().or(new HasNarrowingTypeConversion()).or(
            new IsReference()).or(new ContainsPackExpansion()).or(new HasInitializerListConstructor()));

    public InitializerCollector() {
        initializers = new ArrayList<>();
        shouldVisitInitializers = true;
    }

    @Override
    public int visit(final IASTInitializer initializer) {
        if (initializer instanceof ICPPASTConstructorChainInitializer) {
            collectIfElevationCandidate((ICPPASTConstructorChainInitializer) initializer);
        }
        return PROCESS_SKIP;
    }

    private void collectIfElevationCandidate(ICPPASTConstructorChainInitializer initializer) {
        if (isElevationCandidate.satifies(initializer)) {
            initializers.add(initializer);
        }
    }

    public List<ICPPASTConstructorChainInitializer> getInitializers() {
        return initializers;
    }
}
