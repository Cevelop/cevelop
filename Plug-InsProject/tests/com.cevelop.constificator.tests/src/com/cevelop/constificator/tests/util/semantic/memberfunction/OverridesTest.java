package com.cevelop.constificator.tests.util.semantic.memberfunction;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.constificator.core.util.semantic.MemberFunction;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class OverridesTest extends ASTBasedTest {

    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testDoesNotOverrideButShadowsNonVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertFalse(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun();
    // };
    //
    // struct derived : base {
    // void memfun() const;
    // };
    public void testDoesNotOverrideButShadowsVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() const;", 6));
        assertFalse(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // void memfun() = 0;
    // };
    //
    // struct intermediate : base {
    // };
    //
    // struct derived : intermediate {
    // void memfun() const;
    // };
    public void testDoesNotOverrideButShadowsWithIntermediateNonVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() const;", 6));
        assertFalse(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct intermediate : base {
    // };
    //
    // struct derived : intermediate {
    // void memfun() const;
    // };
    public void testDoesNotOverrideButShadowsWithIntermediateVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() const;", 6));
        assertFalse(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testDoesOverride() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct intermediate : base {
    // };
    //
    // struct derived : intermediate {
    // void memfun();
    // };
    public void testDoesOverrideWithIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // template<typename T>
    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct derived : base<int> {
    // void memfun();
    // };
    public void testDoesOverrideWithTemplateBase() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // template<typename T>
    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct intermediate : base<int> {
    // };
    //
    // struct intermediate2 : intermediate {
    // };
    //
    // struct derived : intermediate, intermediate2 {
    // void memfun();
    // };
    public void testDoesOverrideWithTemplateBaseAndIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // template<typename T>
    // struct intermediate : base {
    // };
    //
    // struct derived : intermediate<float> {
    // void memfun();
    // };
    public void testDoesOverrideWithTemplateIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.overrides((ICPPMethod) function.resolveBinding(), function));
    }

}
