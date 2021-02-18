package com.cevelop.constificator.core.deciders.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IProblemType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.DecisionFactory;
import com.cevelop.constificator.core.deciders.decision.NullDecision;
import com.cevelop.constificator.core.deciders.rules.Functions;
import com.cevelop.constificator.core.deciders.rules.NonPointerVariables;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.functional.CachedUnaryPredicate;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Pair;
import com.cevelop.constificator.core.util.type.Truelean;


public class NonPointer {

    private static List<CachedUnaryPredicate<ICPPASTName>> definitive = new ArrayList<>(13);

    static {
        definitive.add((n, c) -> NonPointerVariables.isOperandInModifyingBinaryExpression(n, c));
        definitive.add((n, c) -> NonPointerVariables.isOperandInModifyingUnaryExpression(n, c));
        definitive.add((n, c) -> NonPointerVariables.isUsedToBindLValueReferenceToNonConst(n, c));
        definitive.add((n, c) -> NonPointerVariables.addressIsAssignedToPointerToNonConst(n, c));
        definitive.add((n, c) -> NonPointerVariables.addressIsPassedAsReferenceToPointerToNonConst(n, c));
        definitive.add((n, c) -> NonPointerVariables.addressIsUsedToBindReferenceToPointerToNonConst(n, c));
        definitive.add((n, c) -> NonPointerVariables.nonConstMemberFunctionCalledOn(n, c));
        definitive.add((n, c) -> NonPointerVariables.nonConstMemberIsModified(n, c));
        definitive.add((n, c) -> NonPointerVariables.isReturnedAsReferenceToNonConst(n, c));
        definitive.add((n, c) -> NonPointerVariables.isReturnedAsPointerToNonConst(n, c));
        definitive.add((n, c) -> NonPointerVariables.isReturnedAsCopy(n, c));
        definitive.add((n, c) -> NonPointerVariables.isOperandInModifyingSubscriptExpressions(n, c));
        definitive.add((n, c) -> NonPointerVariables.isUsedAsModifiableRangeBasedForInitializer(n, c));
    }

    public static Decision decide(ICPPASTDeclarator declarator, ICPPASTName name, IType type, Class<? extends Decision> decisionType,
            ASTRewriteCache cache) {

        if (type instanceof IProblemType) {
            return new NullDecision();
        }

        IASTSimpleDeclaration variableDeclaration = null;
        IASTParameterDeclaration parameterDeclaration = null;

        if ((parameterDeclaration = Relation.getAncestorOf(IASTParameterDeclaration.class, declarator)) == null && (variableDeclaration = Relation
                .getAncestorOf(IASTSimpleDeclaration.class, declarator)) == null) {
            return new NullDecision();
        }

        if (Variable.isUsedInCallToTemplateFunction(name, cache) || Variable.isUsedAsVariadicInVarargsFunction(name, cache) || Variable
                .isUsedInCallToDeferredFunction(name, cache)) {
            return new NullDecision();
        }

        boolean typeIsConst = declarator instanceof ICPPASTArrayDeclarator ? Type.isConst(type, 1) : Type.isConst(type, 0);
        boolean canConstifyNonPtr = !typeIsConst;

        if (parameterDeclaration != null) {
            boolean nonPointerIsConst = parameterDeclaration.getDeclSpecifier().isConst();
            canConstifyNonPtr = !typeIsConst || (typeIsConst && !nonPointerIsConst);
        }

        boolean mightBeConstifiable = false;

        Iterator<CachedUnaryPredicate<ICPPASTName>> it = definitive.iterator();
        while (it.hasNext() && canConstifyNonPtr) {
            canConstifyNonPtr &= !it.next().evaluate(name, cache);
        }

        if (canConstifyNonPtr) {
            Pair<Boolean, Pair<IASTName, Integer>> result = NonPointerVariables.isPassedAsLValueReferenceToNonConst(name, cache); // C3

            if (result.first()) {
                canConstifyNonPtr = false;
                IASTName function = result.second().first();
                Integer parameterIndex = result.second().second();
                mightBeConstifiable = Functions.hasConstOverload((ICPPASTName) function, parameterIndex, 1, cache);
            }
        }

        if (canConstifyNonPtr) {
            Pair<Boolean, Pair<IASTName, Integer>> result = NonPointerVariables.addressIsPassedAsPointerToNonConst(name, cache); // C5

            if (result.first()) {
                canConstifyNonPtr = false;
                IASTName function = result.second().first();
                Integer parameterIndex = result.second().second();
                mightBeConstifiable = Functions.hasConstOverload((ICPPASTName) function, parameterIndex, 1, cache);
            }
        }

        IASTNode node = variableDeclaration != null ? variableDeclaration.getDeclSpecifier() : parameterDeclaration.getDeclSpecifier();
        return DecisionFactory.makeDecision(decisionType, node, canConstifyNonPtr ? Truelean.YES : mightBeConstifiable ? Truelean.MAYBE
                                                                                                                       : Truelean.NO);
    }

}
