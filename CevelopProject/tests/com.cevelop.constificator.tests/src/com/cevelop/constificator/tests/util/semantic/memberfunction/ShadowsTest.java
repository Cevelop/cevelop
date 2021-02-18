package com.cevelop.constificator.tests.util.semantic.memberfunction;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.constificator.core.util.semantic.MemberFunction;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class ShadowsTest extends ASTBasedTest {

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testDoesNotShadowButOverrides() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertFalse(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testDoesShadow() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // void memfun() const;
    // virtual void memfun() = 0;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testDoesShadowAndOverride() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
        assertTrue(MemberFunction.overrides((ICPPMethod) function.getBinding(), function));
    }

    // struct base {
    // template<typename T>
    // void memfun() ;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testDoesShadowTemplateFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // struct intermediate : base{
    // };
    //
    // struct derived : intermediate {
    // void memfun();
    // };
    public void testDoesShadowWithIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }

    // template<typename T>
    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived : base<int> {
    // void memfun();
    // };
    public void testDoesShadowWithTemplateBase() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }

    // template<typename T>
    // struct base {
    // void memfun() ;
    // };
    //
    // struct intermediate : base<int> {
    // }
    //
    // struct derived : intermediate {
    // void memfun();
    // };
    public void testDoesShadowWithTemplateBaseAndIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // template<typename T>
    // struct intermediate : base {
    // };
    //
    // struct derived : intermediate<int> {
    // void memfun();
    // };
    public void testDoesShadowWithTemplateIntermediate() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.shadows((ICPPMethod) function.resolveBinding(), function));
    }
}
