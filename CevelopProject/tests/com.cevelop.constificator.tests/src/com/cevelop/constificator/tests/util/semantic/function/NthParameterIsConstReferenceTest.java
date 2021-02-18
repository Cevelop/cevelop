package com.cevelop.constificator.tests.util.semantic.function;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;

import com.cevelop.constificator.core.util.semantic.Function;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class NthParameterIsConstReferenceTest extends ASTBasedTest {

    // void func(int, char &, bool const &) {
    // }
    public void testConstReferenceParameter() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 2));
    }

    // template<typename T>
    // struct cls {};
    // void func(cls<int> const &) {
    // }
    public void testConstReferenceParameter_ClassTemplateType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // template<typename T>
    // struct cls {};
    // using type = cls<int>;
    // void func(type const &) {
    // }
    public void testConstReferenceParameter_ClassTemplateTypedef() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // template<typename T>
    // struct cls {};
    // using type = cls<int> const;
    // void func(type &) {
    // }
    public void testConstReferenceParameter_ClassTemplateTypedefConst() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // template<typename T>
    // struct cls {};
    // using type = cls<int> const &;
    // void func(type) {
    // }
    public void testConstReferenceParameter_ClassTemplateTypedefConstReference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // struct cls {};
    // void func(cls const &) {
    // }
    public void testConstReferenceParameter_ClassType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // struct cls {};
    // using type = cls;
    // void func(type const &) {
    // }
    public void testConstReferenceParameter_ClassTypedef() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // struct cls {};
    // using type = cls const;
    // void func(type &) {
    // }
    public void testConstReferenceParameter_ClassTypedefConst() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // struct cls {};
    // using type = cls const &;
    // void func(type) {
    // }
    public void testConstReferenceParameter_ClassTypedefConstReference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // using type = int;
    // void func(type const &) {
    // }
    public void testConstReferenceParameter_Typedef() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // using type = int const;
    // void func(type &) {
    // }
    public void testConstReferenceParameter_TypedefConst() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // using type = int const &;
    // void func(type) {
    // }
    public void testConstReferenceParameter_TypedefConstReference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertTrue(Function.nthParameterIsConstReference(function, 0));
    }

    // void func(int, char &, bool const &) {
    // }
    public void testNonConstReferenceParameter() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertFalse(Function.nthParameterIsConstReference(function, 1));
    }

    // void func(int, char &, bool const &) {
    // }
    public void testNonReferenceParameter() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName functionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func"));
        ICPPFunction function = Cast.as(ICPPFunction.class, functionName.resolveBinding());
        assertFalse(Function.nthParameterIsConstReference(function, 0));
    }

}
