package com.cevelop.constificator.core.util.structural;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.constificator.core.Activator;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.functional.BinaryPredicate;
import com.cevelop.constificator.core.util.type.Cast;


public class Node {

    public static <T> boolean anyOfDescendingFrom(Class<T> ancestorCls, ICPPASTName name, BinaryPredicate<T, IASTName> pred, ASTRewriteCache cache) {
        IASTName[] references = Node.getReferences(name, cache);

        for (IASTName reference : references) {

            IASTNodeLocation[] nodeLocations = reference.getNodeLocations();

            for (IASTNodeLocation location : nodeLocations) {
                if (location instanceof IASTMacroExpansionLocation) {
                    continue;
                }
            }

            if (Relation.isDescendendOf(ancestorCls, reference)) {
                T ancestor = Relation.getAncestorOf(ancestorCls, reference);

                if (ancestor instanceof IASTNode) {
                    if (pred.holdsFor(ancestor, reference)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean declaresLValueReferenceToObject(ICPPASTDeclarator declarator) {
        IASTPointerOperator[] pointerOperators = declarator.getPointerOperators();

        if (pointerOperators == null || pointerOperators.length != 1) {
            return false;
        }

        IASTPointerOperator lastOperator = pointerOperators[pointerOperators.length - 1];
        ICPPASTReferenceOperator referenceOperator = Cast.as(ICPPASTReferenceOperator.class, lastOperator);

        if (referenceOperator == null || referenceOperator.isRValueReference()) {
            return false;
        }

        IASTSimpleDeclaration declaration = Cast.as(IASTSimpleDeclaration.class, declarator.getParent());

        if (declaration != null) {
            ICPPASTDeclSpecifier specifier = Cast.as(ICPPASTDeclSpecifier.class, declaration.getDeclSpecifier());
            return specifier != null;
        }

        return false;
    }

    public static boolean declaresLValueReferenceToPointer(ICPPASTDeclarator declarator) {
        IASTPointerOperator[] pointerOperators = declarator.getPointerOperators();

        if (pointerOperators == null || pointerOperators.length <= 1) {
            return false;
        }

        IASTPointerOperator lastOperator = pointerOperators[pointerOperators.length - 1];
        ICPPASTReferenceOperator referenceOperator = Cast.as(ICPPASTReferenceOperator.class, lastOperator);

        if (referenceOperator != null && !referenceOperator.isRValueReference()) {
            return Cast.as(IASTPointer.class, pointerOperators[pointerOperators.length - 2]) != null;
        }

        return false;
    }

    public static ICPPASTName getNameForFunction(IASTExpression functionNameExpression) {
        if (functionNameExpression instanceof IASTIdExpression) {
            return Cast.as(ICPPASTName.class, ((IASTIdExpression) functionNameExpression).getName());
        } else if (functionNameExpression instanceof IASTFieldReference) {
            return Cast.as(ICPPASTName.class, ((IASTFieldReference) functionNameExpression).getFieldName());
        } else if (functionNameExpression instanceof IASTUnaryExpression) {
            return Node.getNameForFunction(((IASTUnaryExpression) functionNameExpression).getOperand());
        }

        return null;
    }

    public static ICPPFunction getBindingForFunction(IASTExpression functionNameExpression) {
        ICPPASTName functionName;
        if ((functionName = getNameForFunction(functionNameExpression)) == null) {
            return null;
        }
        return Cast.as(ICPPFunction.class, functionName.resolveBinding());
    }

    public static IASTName[] getReferences(ICPPASTName name, ASTRewriteCache cache) {
        ICPPASTTranslationUnit currentTu = Cast.as(ICPPASTTranslationUnit.class, name.getTranslationUnit());

        if (cache == null) {
            cache = new ASTRewriteCache(currentTu.getIndex());
        }

        Set<IASTName> foundReferences = new HashSet<>();
        IIndex index = currentTu.getIndex();
        IBinding binding = name.resolveBinding();

        foundReferences.addAll(java.util.Arrays.asList(name.getTranslationUnit().getReferences(binding)));

        if (index == null) {
            return foundReferences.toArray(new IASTName[foundReferences.size()]);
        }

        ICProject project = currentTu.getOriginatingTranslationUnit().getCProject();

        try {
            index.acquireReadLock();
            for (IIndexName reference : index.findNames(binding, IIndex.FIND_REFERENCES)) {
                IIndexFileLocation location = reference.getFile().getLocation();
                ITranslationUnit referenceTu = CoreModelUtil.findTranslationUnitForLocation(location, project);
                IASTTranslationUnit ast = cache.getASTTranslationUnit(referenceTu);
                IASTName[] references2 = ast.getReferences(binding);
                foundReferences.addAll(java.util.Arrays.asList(references2));
            }
        } catch (CoreException | InterruptedException e) {
            Activator.getDefault().logException("Failed to acquire references for " + name.toString(), e);
        } finally {
            index.releaseReadLock();
        }

        return foundReferences.toArray(new IASTName[foundReferences.size()]);
    }

    public static boolean isUsedToTakeAddressOf(IASTName reference) {

        boolean takesAddress = false;
        IASTUnaryExpression unary = Relation.getAncestorOf(IASTUnaryExpression.class, reference);
        while (unary != null) {
            int op = unary.getOperator();

            if (op == IASTUnaryExpression.op_amper) {
                takesAddress = true;
            } else if (op == IASTUnaryExpression.op_star) {
                takesAddress = false;
            }

            unary = Relation.getAncestorOf(IASTUnaryExpression.class, unary.getParent());
        }
        return takesAddress;
    }

    public static IASTName resolveToName(IASTNode node) {
        if (node instanceof IASTIdExpression) {
            return ((IASTIdExpression) node).getName();
        }

        return null;
    }

}
