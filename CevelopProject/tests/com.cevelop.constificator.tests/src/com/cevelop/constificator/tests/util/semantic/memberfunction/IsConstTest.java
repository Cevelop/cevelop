package com.cevelop.constificator.tests.util.semantic.memberfunction;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.MemberFunction;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsConstTest extends ASTBasedTest {

    // struct type {
    // void memfun() const;
    // };
    public void testConstFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertTrue(MemberFunction.isConst(function.resolveBinding()));
    }

    // template<typename T>
    // struct type {
    // void memfun() const;
    // };
    public void testConstFunction_TemplateType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertTrue(MemberFunction.isConst(function.resolveBinding()));
    }

    // struct type {
    // template<typename T>
    // void memfun(T) const;
    // };
    public void testConstTemplateFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertTrue(MemberFunction.isConst(function.resolveBinding()));
    }

    // template<typename T>
    // struct type {
    // template<typename Q>
    // void memfun(Q) const;
    // };
    public void testConstTemplateFunction_TemplateType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertTrue(MemberFunction.isConst(function.resolveBinding()));
    }

    // struct type {
    // void memfun();
    // };
    public void testNonConstFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertFalse(MemberFunction.isConst(function.resolveBinding()));
    }

    // template<typename T>
    // struct type {
    // void memfun();
    // };
    public void testNonConstFunction_TemplateType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertFalse(MemberFunction.isConst(function.resolveBinding()));
    }

    // struct type {
    // template<typename T>
    // void memfun(T);
    // };
    public void testNonConstTemplateFunction() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertFalse(MemberFunction.isConst(function.resolveBinding()));
    }

    // template<typename T>
    // struct type {
    // template<typename Q>
    // void memfun(Q);
    // };
    public void testNonConstTemplateFunction_TemplateType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun"));
        assertFalse(MemberFunction.isConst(function.resolveBinding()));
    }

}
