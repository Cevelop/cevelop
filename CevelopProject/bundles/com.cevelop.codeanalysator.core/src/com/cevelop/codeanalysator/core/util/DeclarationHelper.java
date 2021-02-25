package com.cevelop.codeanalysator.core.util;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFieldDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArrayDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;


@SuppressWarnings("restriction")
public class DeclarationHelper {

    public static void makeDeclaratorAbstract(IASTDeclarator newdeclarator, ICPPNodeFactory factory) {
        // using ASTQueries works better.
        ASTQueries.findInnermostDeclarator(newdeclarator).setName(factory.newName(CharArrayUtils.EMPTY));
        IASTDeclarator lastrelevant = findTypeRelevantDeclarator(newdeclarator);
        lastrelevant.setNestedDeclarator(null); // avoid superfluous parentheses that might change the type!
    }

    public static IASTName findNameOfDeclarator(IASTDeclarator declarator) {
        IASTName name = ASTQueries.findInnermostDeclarator(declarator).getName();
        if (name.toString().length() == 0) {
            name = null; // abstract
        }
        return name;
    }

    public static boolean isPlainCPPASTDeclarator(IASTDeclarator declarator) {
        return (declarator instanceof CPPASTDeclarator) && !(declarator instanceof CPPASTArrayDeclarator) &&
               !(declarator instanceof CPPASTFieldDeclarator) && !(declarator instanceof CPPASTFunctionDeclarator);
    }

    // rid declarators from too much nesting, transfer pointeroperators to nested declarators
    public static IASTDeclarator optimizeDeclaratorNesting(IASTDeclarator declarator) {
        IASTDeclarator root = ASTQueries.findOutermostDeclarator(declarator);
        IASTDeclarator current = ASTQueries.findInnermostDeclarator(declarator);
        if (root.isFrozen()) {
            return declarator; // can not do anything
        }
        IASTNode parent = current.getParent();
        while (parent instanceof IASTDeclarator) {
            IASTDeclarator parentdeclarator = (IASTDeclarator) parent;
            if (isPlainCPPASTDeclarator(parentdeclarator)) {
                current = combineParentDeclaratorWithNested(parentdeclarator, current);
                // need to substitute parent with current in parent's parent if any.
            } else if (isPlainCPPASTDeclarator(current) && hasNoExtrasButARealName(current)) {
                current = moveNestedNameToParent(parentdeclarator, current);
            } else if (hasNoPointers(current) && bothAreArrays(parentdeclarator, current)) {
                current = combineArrayDeclarators(parentdeclarator, current);
            } else {
                if (parentdeclarator.getNestedDeclarator() != current) {
                    parentdeclarator.setNestedDeclarator(current); // keep hierarchy intact, in case we removed nodes
                }
                current = parentdeclarator;
            }
            parent = parent.getParent();
        }
        return current;
    }

    private static IASTDeclarator combineArrayDeclarators(IASTDeclarator parentdeclarator, IASTDeclarator current) {
        ICPPASTArrayDeclarator parent = (ICPPASTArrayDeclarator) parentdeclarator;
        ICPPASTArrayDeclarator cur = (ICPPASTArrayDeclarator) current;
        for (IASTArrayModifier am : parent.getArrayModifiers()) {
            cur.addArrayModifier(am);
        }
        // assume cur doesn't have pointers, otherwise we would have needed the parentheses
        movePointerOpsFromParent(parent, cur);
        if (parent.getNestedDeclarator() == cur) {
            parent.setNestedDeclarator(null);
        }
        cur.setParent(null);// abandon parent, in case we are put into a function declarator
        return cur;
    }

    private static boolean bothAreArrays(IASTDeclarator parentdeclarator, IASTDeclarator current) {
        boolean result = parentdeclarator instanceof ICPPASTArrayDeclarator;
        result = result && current instanceof ICPPASTArrayDeclarator;
        return result;
    }

    private static boolean hasNoPointers(IASTDeclarator current) {
        boolean result = current.getPointerOperators() == null || (current.getPointerOperators().length == 0 || current
                .getPointerOperators()[0] == null);
        return result;
    }

    private static IASTDeclarator moveNestedNameToParent(IASTDeclarator parentdeclarator, IASTDeclarator current) {
        // assert current has no extras but a real name that we transfer
        parentdeclarator.setName(current.getName());
        parentdeclarator.setNestedDeclarator(null);
        current.setParent(null);//abandon parent!
        return parentdeclarator;
    }

    private static boolean hasNoExtrasButARealName(IASTDeclarator current) {
        boolean result = hasNoPointers(current);
        result = result && current.getNestedDeclarator() == null;
        result = result && current.getName() != null && current.getName().toCharArray().length > 0;
        return result;
    }

    private static IASTDeclarator combineParentDeclaratorWithNested(IASTDeclarator parent, IASTDeclarator current) {
        combinePointerOperatorsWithParent(parent, current);
        // parent should not have attributes
        if (parent.getParent() instanceof IASTDeclarator) {
            IASTDeclarator enclosingdelcarator = (IASTDeclarator) parent.getParent();
            enclosingdelcarator.setNestedDeclarator(current);
        }
        return current;

    }

    public static void combinePointerOperatorsWithParent(IASTDeclarator parent, IASTDeclarator current) {
        IASTPointerOperator[] cpos = null;
        if (current.getPointerOperators() != null && current.getPointerOperators().length > 0) {
            cpos = new IASTPointerOperator[current.getPointerOperators().length];
            int i = 0;
            for (IASTPointerOperator cpo : current.getPointerOperators()) {
                cpos[i] = cpo;
                current.getPointerOperators()[i] = null;
                ++i;
            }
        }
        //insert parent pointer operators in the front
        movePointerOpsFromParent(parent, current);
        // add again, original pointer operators to the end
        if (cpos != null) {
            for (IASTPointerOperator cpo : cpos) {
                current.addPointerOperator(cpo);
            }
        }
    }

    public static void movePointerOpsFromParent(IASTDeclarator parent, IASTDeclarator current) {
        IASTPointerOperator[] pointerops = parent.getPointerOperators();
        for (IASTPointerOperator po : pointerops) { // incorrect sequence?
            current.addPointerOperator(po);
        }
    }

    /**
     * cloned and corrected from ASTQueries
     * Searches for the innermost declarator that contributes the the type declared.
     *
     * @param declarator
     * The {@link IASTDeclarator} for which the type relevant {@link IASTDeclarator} needs to be found.
     * @return The type relevant {@link IASTDeclarator}
     */
    public static IASTDeclarator findTypeRelevantDeclarator(IASTDeclarator declarator) {
        if (declarator == null) {
            return null;
        }

        IASTDeclarator result = ASTQueries.findInnermostDeclarator(declarator);
        while (result.getPointerOperators().length == 0 && !(result instanceof IASTFieldDeclarator) && !(result instanceof IASTFunctionDeclarator) &&
               !(result instanceof IASTArrayDeclarator)) { // <--- here was the bug. IMHO
            final IASTNode parent = result.getParent();
            if (parent instanceof IASTDeclarator) {
                result = (IASTDeclarator) parent;
            } else {
                return result;
            }
        }
        return result;
    }

}
