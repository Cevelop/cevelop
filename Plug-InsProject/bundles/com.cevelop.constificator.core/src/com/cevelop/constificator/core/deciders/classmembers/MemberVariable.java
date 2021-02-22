package com.cevelop.constificator.core.deciders.classmembers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;

import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.MemberVariableDecision;
import com.cevelop.constificator.core.deciders.decision.NullDecision;
import com.cevelop.constificator.core.deciders.rules.MemberVariables;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.functional.CachedUnaryPredicate;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.Truelean;


public class MemberVariable {

    private static List<CachedUnaryPredicate<ICPPASTName>> rules = new ArrayList<>(11);

    static {
        rules.add((n, c) -> MemberVariables.isUninitialized(n, c));
        rules.add((n, c) -> MemberVariables.ownerTypeCanBeCopyAssigned(n, c));
    }

    public static Decision canConstify(ICPPASTDeclarator declarator, ASTRewriteCache cache) {

        ICPPASTName name = Cast.as(ICPPASTName.class, declarator.getName());
        if (name == null) {
            return new NullDecision();
        }

        ICPPVariable binding = Cast.as(ICPPVariable.class, name.resolveBinding());
        if (binding == null) {
            return new NullDecision();
        }

        boolean canConstify = true;
        Iterator<CachedUnaryPredicate<ICPPASTName>> ruleIterator = rules.iterator();
        MemberVariableDecision decision = new MemberVariableDecision(declarator);
        while (canConstify && ruleIterator.hasNext()) {
            canConstify &= !ruleIterator.next().evaluate(name, cache);
        }

        decision.decide(canConstify ? Truelean.YES : Truelean.NO);
        return decision;
    }

}
