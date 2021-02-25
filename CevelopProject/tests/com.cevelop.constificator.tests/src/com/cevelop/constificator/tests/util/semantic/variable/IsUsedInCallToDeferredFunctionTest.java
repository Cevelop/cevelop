package com.cevelop.constificator.tests.util.semantic.variable;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsUsedInCallToDeferredFunctionTest extends ASTBasedTest {

    // template<typename T>
    // void func(int, T) {
    //
    // }
    //
    // int main() {
    // int var{};
    // func(var, 12);
    // }
    public void testNotUsedInDeferredFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertFalse(Variable.isUsedInCallToDeferredFunction(variable, null));
    }

    // template<typename A>
    // void uknwn(A) {
    //
    // }
    //
    // template<typename B>
    // void templ(B var) {
    // uknwn(var);
    // }
    public void testUsedInDeferredFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertTrue(Variable.isUsedInCallToDeferredFunction(variable, null));
    }

}
