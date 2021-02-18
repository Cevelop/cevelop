package com.cevelop.constificator.core.deciders.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.DecisionFactory;
import com.cevelop.constificator.core.deciders.decision.NullDecision;
import com.cevelop.constificator.core.deciders.rules.Arrays;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.functional.CachedUnaryPredicate;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.Truelean;


@SuppressWarnings("restriction")
public class Array {

    private static List<CachedUnaryPredicate<ICPPASTName>> rules = new ArrayList<>(8);

    static {
        rules.add((n, c) -> Arrays.isPassedToFunctionTakingArrayOfNonConst(n, c));
        rules.add((n, c) -> Arrays.elementIsModifiedInUnaryExpression(n, c));
        rules.add((n, c) -> Arrays.nonConstMemberFunctionCalledOnElement(n, c));
        rules.add((n, c) -> Arrays.nonConstMemberModifiedOnElement(n, c));
    }

    public static Decision decide(ICPPASTDeclarator declarator, ICPPASTName name, IType type, Class<? extends Decision> decisionType,
            ASTRewriteCache cache) {
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

        boolean canConstify = false;
        if (declarator instanceof ICPPASTArrayDeclarator || declarator.getParent() instanceof ICPPASTArrayDeclarator) {
            IType elementType = SemanticUtil.getNestedType(type, SemanticUtil.ARRAY | SemanticUtil.MPTR | SemanticUtil.PTR | SemanticUtil.TDEF |
                                                                 SemanticUtil.REF);
            canConstify = !SemanticUtil.getCVQualifier(elementType).isConst();
        } else if (type instanceof ICPPReferenceType || type instanceof IPointerType) {
            if (Type.arrayDimension(type) == 1) {
                canConstify = !Type.isConst(type, 1);
            } else {
                IArrayType innerType = Cast.as(IArrayType.class, ((ITypeContainer) type).getType());
                canConstify = innerType != null && !Type.isConst(innerType);
            }

        }

        Iterator<CachedUnaryPredicate<ICPPASTName>> ruleIterator = rules.iterator();
        while (ruleIterator.hasNext() && canConstify) {
            canConstify &= !ruleIterator.next().evaluate(name, cache);
        }

        IASTNode node = variableDeclaration != null ? variableDeclaration.getDeclSpecifier() : parameterDeclaration.getDeclSpecifier();
        return DecisionFactory.makeDecision(decisionType, node, canConstify ? Truelean.YES : Truelean.NO);
    }

}
