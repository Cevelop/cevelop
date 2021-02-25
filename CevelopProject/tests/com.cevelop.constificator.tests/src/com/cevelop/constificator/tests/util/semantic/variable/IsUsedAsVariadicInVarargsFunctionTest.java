package com.cevelop.constificator.tests.util.semantic.variable;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsUsedAsVariadicInVarargsFunctionTest extends ASTBasedTest {

    // void func(int, ...) {}
    //
    // int main() {
    // int var{};
    // func(var);
    // }
    public void testNotUsedAsVariadicInVarargsFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertFalse(Variable.isUsedAsVariadicInVarargsFunction(variable, null));
    }

    // void func(int) {}
    //
    // int main() {
    // int var{};
    // func(var);
    // }
    public void testNotUsedInVarargsFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertFalse(Variable.isUsedAsVariadicInVarargsFunction(variable, null));
    }

    // void func(int, ...) {}
    //
    // int main() {
    // int var{};
    // func(42, var);
    // }
    public void testUsedAsVariadicInVarargsFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertTrue(Variable.isUsedAsVariadicInVarargsFunction(variable, null));
    }

}
