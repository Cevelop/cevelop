package com.cevelop.constificator.tests.util.semantic.function;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.Function;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class GetArgumentIndicesFor extends ASTBasedTest {

    // void func(int) { }
    // int main() {
    // int var{};
    // func(42);
    // }
    public void testVariableDoesNotOccurAsArgumentInCallToFunctionWithoutPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(42);", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        assertTrue(Function.getArgumentIndicesFor(arguments, variableReference, null).isEmpty());
    }

    // void func(int) { }
    // int main() {
    // int var{};
    // func(var);
    // }
    public void testVariableDoesOccurOnceAsArgumentInCallToFunctionWithoutPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var);", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, null);
        assertEquals(1, indices.size());
        assertEquals(((Integer) 0), indices.get(0));
    }

    // void func(int) { }
    // int main() {
    // int var{};
    // func(var);
    // }
    public void testVariableDoesOccurOnceAsArgumentInCallToFunctionButDoesNotFulfillPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var);", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, (n) -> false);
        assertTrue(indices.isEmpty());
    }

    // void func(int) { }
    // int main() {
    // int var{};
    // func(var);
    // }
    public void testVariableDoesOccurOnceAsArgumentInCallToFunctionAndDoesFulfillPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var);", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, (n) -> true);
        assertEquals(1, indices.size());
        assertEquals(((Integer) 0), indices.get(0));

    }

    // void func(int, char, int) { }
    // int main() {
    // int var{};
    // func(var, 'c', var);
    // }
    public void testVariableDoesOccurMultipleTimesAsArgumentInCallToFunctionWithoutPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, null);
        assertEquals(2, indices.size());
        assertEquals(((Integer) 0), indices.get(0));
        assertEquals(((Integer) 2), indices.get(1));
    }

    // void func(int, char, int) { }
    // int main() {
    // int var{};
    // func(var, 'c', var);
    // }
    public void testVariableDoesOccurMultipleTimesAsArgumentInCallToFunctionAndAllOccurencesFulfillPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, (n) -> true);
        assertEquals(2, indices.size());
        assertEquals(((Integer) 0), indices.get(0));
        assertEquals(((Integer) 2), indices.get(1));
    }

    // void func(int, char, int) { }
    // int main() {
    // int var{};
    // func(var, 'c', var);
    // }
    public void testVariableDoesOccurMultipleTimesAsArgumentInCallToFunctionButNoOccurenceFulfillsPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, (n) -> false);
        assertTrue(indices.isEmpty());
    }

    // void func(int, char, int) { }
    // int main() {
    // int var{};
    // func(var, 'c', var);
    // }
    public void testVariableDoesOccurMultipleTimesAsArgumentInCallToFunctionButOnlySecondOccurenceFulfillsPredicate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionReference = Cast.as(ICPPASTName.class, assertionHelper.findName("func(var", "func"));
        ICPPASTName variableReference = Cast.as(ICPPASTName.class, assertionHelper.findName("int var{};", "var"));
        ICPPASTFunctionCallExpression functionCall = Relation.getAncestorOf(ICPPASTFunctionCallExpression.class, functionReference);
        IASTInitializerClause[] arguments = functionCall.getArguments();
        boolean shouldAccept[] = { true };
        List<Integer> indices = Function.getArgumentIndicesFor(arguments, variableReference, (n) -> shouldAccept[0] = !shouldAccept[0]);
        assertEquals(1, indices.size());
        assertEquals(((Integer) 2), indices.get(0));
    }

}
