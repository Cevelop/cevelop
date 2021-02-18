package com.cevelop.constificator.tests.util.semantic.memberfunction;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.constificator.core.util.semantic.MemberFunction;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class GetOverriddenTest extends ASTBasedTest {

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct derived {
    // void memfun();
    // };
    public void testNoOverriddenFunctionFoundBecauseOfMissingInheritance() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertNull(MemberFunction.getOverridden((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testNoOverriddenFunctionFoundBecauseOfShadowing() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertNull(MemberFunction.getOverridden((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testOverridenBaseFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() = 0;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertEquals(overridden.resolveBinding(), MemberFunction.getOverridden((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct intermediate : base{
    // };
    //
    // struct derived : intermediate {
    // void memfun();
    // };
    public void testOverridenBaseFunctionWithIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() = 0;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertEquals(overridden.resolveBinding(), MemberFunction.getOverridden((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct intermediate : base{
    // void memfun() ;
    // };
    //
    // struct derived : intermediate {
    // void memfun();
    // };
    public void testOverridenBaseFunctionWithOverriddenFunctionInIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() = 0;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertEquals(overridden.resolveBinding(), MemberFunction.getOverridden((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    // struct intermediate : base {
    // void memfun(int);
    // };
    // struct derived : intermediate {
    // void memfun();
    // };
    public void testOverridenBaseFunctionWithHiddenFunctionInIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() = 0;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertEquals(overridden.resolveBinding(), MemberFunction.getOverridden((ICPPMethod) function.resolveBinding(), function));
    }

}
