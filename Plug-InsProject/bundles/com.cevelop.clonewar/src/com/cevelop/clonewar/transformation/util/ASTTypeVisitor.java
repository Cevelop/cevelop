package com.cevelop.clonewar.transformation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

import com.cevelop.clonewar.transformation.action.BodyTransformAction;
import com.cevelop.clonewar.transformation.action.NestedTransformAction;
import com.cevelop.clonewar.transformation.action.ParamTransformAction;
import com.cevelop.clonewar.transformation.action.ReturnTransformAction;
import com.cevelop.clonewar.transformation.action.TransformAction;


/**
 * A visitor to find all types in an AST. The same node structure has to be
 * traversed twice:
 *
 * <h1>First run:</h1>
 * <ul>
 * <li>Find types</li>
 * </ul>
 * 
 * <h1>Second run:</h1>
 * <ul>
 * <li>Create actions for the types</li>
 * <li>Set the copy of the node (no modification of frozen AST)</li>
 * </ul>
 *
 * Therefore this visitor is only available for two runs, otherwise an
 * {@link IllegalStateException} is thrown.
 *
 * @author ythrier(at)hsr.ch
 */

public class ASTTypeVisitor extends ASTVisitor {

    private final Map<ASTKeyPair, Class<? extends TransformAction>> registry = new HashMap<>();

    private final List<TypeInformation> typeInformations = new ArrayList<>();

    private Exception exception;

    /**
     * Create the type visitor and load lookup registry for the actions.
     */
    public ASTTypeVisitor() {
        this.shouldVisitDeclSpecifiers = true;
        registry.put(new ASTKeyPair(CPPASTParameterDeclaration.class, CPPASTSimpleDeclSpecifier.class), ParamTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTFunctionDefinition.class, CPPASTSimpleDeclSpecifier.class), ReturnTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTSimpleDeclaration.class, CPPASTSimpleDeclSpecifier.class), BodyTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTTypeId.class, CPPASTSimpleDeclSpecifier.class), NestedTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTParameterDeclaration.class, CPPASTNamedTypeSpecifier.class), ParamTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTFunctionDefinition.class, CPPASTNamedTypeSpecifier.class), ReturnTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTSimpleDeclaration.class, CPPASTNamedTypeSpecifier.class), BodyTransformAction.class);
        registry.put(new ASTKeyPair(CPPASTTypeId.class, CPPASTNamedTypeSpecifier.class), NestedTransformAction.class);
    }

    /**
     * Since we can not change the visitor we have to manually check whether an
     * exception occurred while traversing the AST.
     *
     * @return True if an exception occurred during the visit process, otherwise
     * false.
     */
    public boolean hasException() {
        return exception != null;
    }

    /**
     * Returns the exception or null if there were no exception.
     *
     * @return Exception.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        if (!hasType(declSpec)) {
            return PROCESS_CONTINUE;
        }

        performFirstRun(declSpec);
        return PROCESS_CONTINUE;
    }

    private boolean hasType(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof IASTSimpleDeclSpecifier) {
            IASTSimpleDeclSpecifier sDeclSpec = (IASTSimpleDeclSpecifier) declSpec;
            return sDeclSpec.getType() != IASTSimpleDeclSpecifier.t_unspecified || sDeclSpec.isLong() || sDeclSpec.isLongLong() || sDeclSpec
                    .isShort() || sDeclSpec.isSigned() || sDeclSpec.isUnsigned();
        }
        return true;
    }

    /**
     * Handle the first run visiting.
     *
     * @param declSpec
     * Declaration specifier.
     */
    private void performFirstRun(IASTDeclSpecifier declSpec) {
        if (hasAction(declSpec)) {
            typeInformations.add(createTypeInfo(declSpec));
        }
    }

    /**
     * Create an action for the declaration node specifier.
     *
     * @param declSpec
     * Declaration specifier.
     * @return Action.
     * @throws IllegalAccessException
     * Reflection.
     * @throws InstantiationException
     * Reflection.
     */
    private TransformAction createAction(IASTDeclSpecifier declSpec) throws InstantiationException, IllegalAccessException {
        TransformAction action = registry.get(new ASTKeyPair(declSpec)).newInstance();
        action.setNode(declSpec);
        return action;
    }

    /**
     * Create a type information for the declaration specifier.
     *
     * @param declSpec
     * Declaration specifier.
     * @return Type information.
     */
    private TypeInformation createTypeInfo(IASTDeclSpecifier declSpec) {
        return new TypeInformation(CPPVisitor.createType(declSpec));
    }

    /**
     * Check if an action could be created for the declaration specifier.
     *
     * @param declSpec
     * Declaration specifier.
     * @return True if an action could be created, otherwise false.
     */
    private boolean hasAction(IASTDeclSpecifier declSpec) {
        return registry.containsKey(new ASTKeyPair(declSpec));
    }

    public Map<TypeInformation, List<TransformAction>> findTypes(IASTNode originalNode, IASTNode copyNode) {
        originalNode.accept(this);
        ActionCreationVisitor actionCreator = new ActionCreationVisitor(typeInformations);
        copyNode.accept(actionCreator);
        return actionCreator.getActions();
    }

    private class ActionCreationVisitor extends ASTVisitor {

        {
            shouldVisitDeclSpecifiers = true;
        }

        private final Iterator<TypeInformation>                   typeInfoIterator;
        private final Map<TypeInformation, List<TransformAction>> actions = new TreeMap<>((o1, o2) -> o1.toString().compareTo(o2.toString()));

        public ActionCreationVisitor(List<TypeInformation> typeInformations) {
            typeInfoIterator = typeInformations.iterator();
        }

        public Map<TypeInformation, List<TransformAction>> getActions() {
            return actions;
        }

        @Override
        public int visit(IASTDeclSpecifier declSpec) {
            if (!hasType(declSpec)) {
                return PROCESS_CONTINUE;
            }
            try {
                if (!hasAction(declSpec)) {
                    return PROCESS_CONTINUE;
                }
                if (!typeInfoIterator.hasNext()) {
                    throw new IllegalArgumentException("Not traversing the same AST structure!");
                }
                TypeInformation typeInfo = typeInfoIterator.next();
                if (!actions.containsKey(typeInfo)) {
                    actions.put(typeInfo, new ArrayList<TransformAction>());
                }
                actions.get(typeInfo).add(createAction(declSpec));
            } catch (Exception e) {
                exception = e;
                return PROCESS_ABORT;
            }
            return PROCESS_CONTINUE;
        }

    }
}
