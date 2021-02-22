package com.cevelop.constificator.tests.util.semantic.variable;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsUsedInCallToTemplateFunctionTest extends ASTBasedTest {

    // void func(int) {}
    //
    // int main() {
    // int var{};
    // func(var);
    // }
    public void testNotUsedInTemplateFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertFalse(Variable.isUsedInCallToTemplateFunction(variable, null));
    }

    // template<typename T>
    // void func(int, T) {
    //
    // }
    //
    // int main() {
    // int var{};
    // func(var, 12);
    // }
    public void testUsedInTemplateFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variable = Cast.as(ICPPASTName.class, assertionHelper.findName("var"));
        assertTrue(Variable.isUsedInCallToTemplateFunction(variable, null));
    }

}
