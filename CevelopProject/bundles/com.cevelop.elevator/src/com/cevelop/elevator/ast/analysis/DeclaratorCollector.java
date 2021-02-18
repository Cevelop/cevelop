package com.cevelop.elevator.ast.analysis;

import static com.cevelop.elevator.ast.analysis.conditions.Condition.not;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;

import com.cevelop.elevator.ast.analysis.conditions.Condition;
import com.cevelop.elevator.ast.analysis.conditions.ContainsPackExpansion;
import com.cevelop.elevator.ast.analysis.conditions.HasEqualsInitializer;
import com.cevelop.elevator.ast.analysis.conditions.HasInitializerListConstructor;
import com.cevelop.elevator.ast.analysis.conditions.HasNarrowingTypeConversion;
import com.cevelop.elevator.ast.analysis.conditions.IsCatchParameter;
import com.cevelop.elevator.ast.analysis.conditions.IsElevated;
import com.cevelop.elevator.ast.analysis.conditions.IsElevatedNewExpression;
import com.cevelop.elevator.ast.analysis.conditions.IsExternDeclaration;
import com.cevelop.elevator.ast.analysis.conditions.IsInstanceOf;
import com.cevelop.elevator.ast.analysis.conditions.IsUninitializedReference;
import com.cevelop.elevator.ast.analysis.conditions.OtherDeclaratorElevationConditions;


/**
 * Collects all {@link IASTDeclarator}s that can be elevated.
 *
 */
public class DeclaratorCollector extends ASTVisitor {

    final private List<IASTDeclarator> declarators;

    private final Condition isAlreadyElevated = new IsInstanceOf(IASTDeclarator.class).and(new IsElevated().or(new IsElevatedNewExpression()));

    private final Condition isElevationCandidate;

    public DeclaratorCollector(boolean markEqualsInitializers) {
        declarators = new ArrayList<>();
        
        //@formatter:off
        Condition baseCondition = isAlreadyElevated.or(new HasNarrowingTypeConversion())
                                                   .or(new HasInitializerListConstructor())
                                                   .or(new ContainsPackExpansion())
                                                   .or(new IsUninitializedReference())
                                                   .or(new IsExternDeclaration())
                                                   .or(new IsCatchParameter());
        //@formatter:on
        isElevationCandidate = not(markEqualsInitializers ? baseCondition : baseCondition.or(new HasEqualsInitializer())).and(new OtherDeclaratorElevationConditions());
        
        this.shouldVisitDeclarators = true;
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        if (isFunctionDeclarator(declarator)) {
            return PROCESS_SKIP;
        }
        if (isElevationCandidate.satifies(declarator)) {
            declarators.add(declarator);
        }
        return PROCESS_CONTINUE;
    }

    public List<IASTDeclarator> getDeclarators() {
        return declarators;
    }

    private boolean isFunctionDeclarator(IASTDeclarator element) {
        return element instanceof IASTFunctionDeclarator;
    }
}
