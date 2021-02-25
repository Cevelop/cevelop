package com.cevelop.constificator.core.deciders.classmembers;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.MemberFunctionDecision;
import com.cevelop.constificator.core.deciders.decision.NullDecision;
import com.cevelop.constificator.core.deciders.rules.MemberFunctions;
import com.cevelop.constificator.core.deciders.rules.MemberVariables;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.Pair;
import com.cevelop.constificator.core.util.type.Truelean;


public class MemberFunctionDecider {

    public static Decision canConstify(ICPPASTFunctionDeclarator declarator) {
        ICPPASTFunctionDefinition definition;
        if (declarator == null || (definition = Cast.as(ICPPASTFunctionDefinition.class, declarator.getParent())) == null) {
            return new NullDecision();
        }

        ICPPMethod method;
        if ((method = Cast.as(ICPPMethod.class, declarator.getName().resolveBinding())) instanceof ICPPConstructor) {
            return new NullDecision();
        }

        ICPPField[] memberVariables = MemberVariables.memberVariablesForOwnerOf(method);
        ICPPMethod[] memberFunctions = MemberFunctions.memberFunctionsForOwnerOf(method);

        Decision decision = new MemberFunctionDecision(declarator);

        boolean canConstify = declarator != null && !declarator.isConst() && !method.isStatic();
        boolean mightBeConstifiable = false;

        if (canConstify) {
            canConstify = !MemberFunctions.isConstructorOrDestructor(declarator);
        }

        if (canConstify) {
            FunctionBodyVisitor visitor = new FunctionBodyVisitor(memberVariables, memberFunctions);
            if (!(definition.isDeleted() || definition.isDefaulted())) {
                definition.getBody().accept(visitor);
                canConstify = !visitor.modifiesDataMember();
            } else {
                canConstify = false;
            }
        }

        if (canConstify) {
            Pair<Boolean, Boolean> overloadDescriptor = MemberFunctions.constOverloadExists(declarator);
            if (overloadDescriptor.second()) {
                canConstify = false;
                mightBeConstifiable = true;
            } else {
                canConstify = !overloadDescriptor.first();
            }
        }

        decision.decide(canConstify ? Truelean.YES : mightBeConstifiable ? Truelean.MAYBE : Truelean.NO);
        return decision;
    }

}
