package com.cevelop.constificator.core.deciders.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;

import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.DecisionFactory;
import com.cevelop.constificator.core.deciders.rules.Functions;
import com.cevelop.constificator.core.deciders.rules.NonPointerVariables;
import com.cevelop.constificator.core.deciders.rules.PointerVariables;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.functional.CachedBinaryPredicate;
import com.cevelop.constificator.core.util.functional.CachedUnaryPredicate;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.Truelean;


@SuppressWarnings("restriction")
public class Pointer {

    private static List<CachedBinaryPredicate<ICPPASTName, Integer>> pointeeRules = new ArrayList<>(6);
    private static List<CachedUnaryPredicate<ICPPASTName>>           pointerRules = new ArrayList<>(8);

    static {
        pointerRules.add((n, c) -> NonPointerVariables.isOperandInModifyingBinaryExpression(n, c));
        pointerRules.add((n, c) -> NonPointerVariables.isOperandInModifyingUnaryExpression(n, c));
        pointerRules.add((n, c) -> PointerVariables.isPassedAsLValueReferenceToNonConstPointer(n, c));
        pointerRules.add((n, c) -> PointerVariables.isUsedToBindLValueReferenceToNonConstPointer(n, c));
        pointerRules.add((n, c) -> PointerVariables.addressIsPassedAsPointerToNonConstPointer(n, c));
        pointerRules.add((n, c) -> PointerVariables.addressIsAssignedToPointerToNonConstPointer(n, c));
        pointerRules.add((n, c) -> PointerVariables.addressIsUsedToInitializePointerToNonConstPointer(n, c));
        pointerRules.add((n, c) -> PointerVariables.addressIsPassedAsReferenceToPointerToNonConstPointer(n, c));
        pointerRules.add((n, c) -> PointerVariables.addressIsUsedToBindReferenceToPointerToNonConstPointer(n, c));

        pointeeRules.add((n, i, c) -> PointerVariables.pointeeIsLeftHandSideInModifyingBinaryExpression(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.pointeeIsLeftHandSideInModifyingUnaryExpression(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.pointeeIsPassedAsReferenceToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.pointeeIsUsedToBindReferenceToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isPassedToFunctionTakingPointerToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isPassedToPlacementNew(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isAssignedToPointerToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isPassedToFunctionTakingReferenceToPointerToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isUsedToBindReferenceToPointerToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isUsedToInitializePointerToNonConst(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.isInitializedUsingLessConstPointer(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.nonConstMemberAccessedOnPointee(n, i, c));
        pointeeRules.add((n, i, c) -> PointerVariables.pointeeIsReturnedAsReferenceToNonConst(n, i, c));
    }

    public static List<Decision> decide(ICPPASTDeclarator declarator, ICPPASTName name, IType type, Class<? extends Decision> decisionType,
            ASTRewriteCache cache) {

        List<Decision> decisions = new ArrayList<>(declarator.getPointerOperators().length + 1);
        if (Variable.isUsedInCallToTemplateFunction(name, cache) || Variable.isUsedAsVariadicInVarargsFunction(name, cache) || Variable
                .isUsedInCallToDeferredFunction(name, cache)) {
            return decisions;
        }

        if (type instanceof ITypedef) {
            type = ((ITypedef) type).getType();
            if (type instanceof IPointerType && !((IPointerType) type).isConst() && declarator.getParent() instanceof IASTSimpleDeclaration) {
                boolean canConstifyPtr = true;
                Iterator<CachedUnaryPredicate<ICPPASTName>> it = pointerRules.iterator();
                while (it.hasNext() && canConstifyPtr) {
                    canConstifyPtr &= !it.next().evaluate(name, cache);
                }
                decisions.add(DecisionFactory.makeDecision(decisionType, ((IASTSimpleDeclaration) declarator.getParent()).getDeclSpecifier(),
                        canConstifyPtr ? Truelean.YES : Truelean.NO));
            }
        }

        final IASTPointerOperator[] pointerOps = declarator.getPointerOperators();
        final int nofPointerOps = pointerOps.length;

        if (!(declarator instanceof IASTArrayDeclarator) && nofPointerOps > 0 && pointerOps[nofPointerOps - 1] instanceof IASTPointer) {

            boolean typeIsConst = Type.isConst(type, 0);
            boolean pointerIsConst = ((IASTPointer) pointerOps[nofPointerOps - 1]).isConst();
            boolean canConstifyPtr = !typeIsConst || (typeIsConst && !pointerIsConst);

            Iterator<CachedUnaryPredicate<ICPPASTName>> it = pointerRules.iterator();
            while (it.hasNext() && canConstifyPtr) {
                canConstifyPtr &= !it.next().evaluate(name, cache);
            }

            decisions.add(DecisionFactory.makeDecision(decisionType, pointerOps[nofPointerOps - 1], canConstifyPtr ? Truelean.YES : Truelean.NO));
        }

        final int arrayDimension = declarator instanceof IASTArrayDeclarator ? ((IASTArrayDeclarator) declarator).getArrayModifiers().length : 0;

        int startLevel = arrayDimension > 0 ? 0 : 1;

        for (int level = startLevel; level <= nofPointerOps; ++level) {
            boolean canConstifyLevel;
            IASTNode currentNode = null;

            if (level < nofPointerOps) {
                currentNode = pointerOps[nofPointerOps - 1 - level];
                canConstifyLevel = !((IASTPointer) currentNode).isConst();
            } else if (declarator.getParent() instanceof IASTParameterDeclaration) {
                IASTParameterDeclaration decl = Cast.as(IASTParameterDeclaration.class, declarator.getParent());
                currentNode = decl.getDeclSpecifier();
                canConstifyLevel = !decl.getDeclSpecifier().isConst();
            } else if (declarator.getParent() instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration decl = Cast.as(IASTSimpleDeclaration.class, declarator.getParent());
                currentNode = decl.getDeclSpecifier();
                canConstifyLevel = !decl.getDeclSpecifier().isConst();
            } else {
                break;
            }

            if (nofPointerOps > 0 && pointerOps[nofPointerOps - 1] instanceof ICPPASTReferenceOperator && level > 1 && canConstifyLevel) {

                IASTInitializer initializer = declarator.getInitializer();

                IASTName initializerName = null;

                if (initializer instanceof IASTEqualsInitializer) {
                    initializerName = Node.resolveToName(((IASTEqualsInitializer) initializer).getInitializerClause());
                } else if (initializer instanceof ICPPASTInitializerList) {
                    initializerName = Node.resolveToName(((ICPPASTInitializerList) initializer).getClauses()[0]);
                }

                if (initializerName != null) {
                    IBinding initializerBinding = initializerName.getBinding();

                    if (initializerBinding instanceof CPPVariable) {
                        CPPVariable declared = (CPPVariable) name.resolveBinding();
                        canConstifyLevel = Type.isConst(((CPPVariable) initializerBinding).getType(), level - 1) || Type.isConst(declared.getType(),
                                level - 1);
                    }
                }
            }

            Iterator<CachedBinaryPredicate<ICPPASTName, Integer>> it2 = pointeeRules.iterator();
            while (it2.hasNext() && canConstifyLevel) {
                canConstifyLevel &= !it2.next().holdsFor(name, level + arrayDimension, cache);
            }

            if (canConstifyLevel && declarator.getParent() instanceof IASTParameterDeclaration) {
                IASTFunctionDeclarator function = Relation.getAncestorOf(IASTFunctionDeclarator.class, name);

                CPPParameter binding;
                if ((binding = Cast.as(CPPParameter.class, name.resolveBinding())) != null) {
                    binding.getParameterPosition();
                    canConstifyLevel &= !Functions.hasConstOverload((ICPPASTName) function.getName(), binding.getParameterPosition(), level, cache);
                }
            }

            decisions.add(DecisionFactory.makeDecision(decisionType, currentNode, canConstifyLevel ? Truelean.YES : Truelean.NO));
        }

        return decisions;
    }

}
