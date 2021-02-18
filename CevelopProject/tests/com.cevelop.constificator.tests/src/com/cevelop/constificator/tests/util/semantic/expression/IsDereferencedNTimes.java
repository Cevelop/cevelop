package com.cevelop.constificator.tests.util.semantic.expression;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsDereferencedNTimes extends ASTBasedTest {

    // void f() {
    // int * * * ptr{};
    // ***ptr;
    // }
    public void testMultiStagePointerFullyDereferenced() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("***ptr;", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 3));
    }

    // void f() {
    // int * * * ptr{};
    // *(*(*ptr) + 2);
    // }
    public void testMultiStagePointerFullyDereferencedWithBinaryHalfWay() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(*(*ptr) + 2);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 3));
    }

    // void f() {
    // int * * * ptr{};
    // *(*(*ptr) += 2);
    // }
    public void testMultiStagePointerFullyDereferencedWithModifyingBinaryHalfWay() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(*(*ptr) += 2);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 3));
    }

    // void f() {
    // int * * * ptr{};
    // *(*(*ptr) += 2);
    // }
    public void testMultiStagePointerFullyDereferencedWithModifyingBinaryHalfWayCheckForPartialDereference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(*(*ptr) += 2);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 2));
    }

    // void f() {
    // int * * * ptr{};
    // *(*(*ptr)++);
    // }
    public void testMultiStagePointerFullyDereferencedWithModifyingUnaryHalfWay() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(*(*ptr)++);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 3));
    }

    // void f() {
    // int * * * ptr{};
    // *((*(*ptr))++);
    // }
    public void testMultiStagePointerFullyDereferencedWithModifyingUnaryHalfWayCheckForPartialDereference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*((*(*ptr))++);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 2));
    }

    // void f() {
    // int * * * ptr{};
    // ptr;
    // }
    public void testMultiStagePointerNotDereferenced() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("ptr;", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 0));
    }

    // void f() {
    // int * * * ptr{};
    // **ptr;
    // }
    public void testMultiStagePointerPartiallyDereferenced() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("**ptr;", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 2));
    }

    // void f() {
    // int * * * ptr{};
    // *((*ptr) + 2);
    // }
    public void testMultiStagePointerPartiallyDereferencedWithBinaryHalfWay() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*((*ptr) + 2);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 2));
    }

    // void f() {
    // int * * * ptr{};
    // *((*ptr) += 2);
    // }
    public void testMultiStagePointerPartiallyDereferencedWithModifyingBinaryHalfWay() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*((*ptr) += 2)", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 2));
    }

    // void f() {
    // int * * * ptr{};
    // *((*ptr)++);
    // }
    public void testMultiStagePointerPartiallyDereferencedWithModifyingUnaryHalfWay() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*((*ptr)++)", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 2));
    }

    // void f() {
    // int * ptr{};
    // *ptr;
    // }
    public void testSingleStagePointerDereferencedCheckForSingleDereference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*ptr;", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 1));
    }

    // void f() {
    // int * ptr{};
    // *ptr;
    // }
    public void testSingleStagePointerDereferencedCheckForZeroDereferences() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*ptr;", "ptr"));
        assertFalse(Expression.isDereferencedNTimes(pointer, 0));
    }

    // void f() {
    // int * ptr{};
    // ptr;
    // }
    public void testSingleStagePointerNoDereference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("ptr;", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 0));
    }

    // void f() {
    // int * ptr{};
    // *(ptr + 2);
    // }
    public void testSingleStagePointerWithOfsetDereferencedCheckForSingleDereference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(ptr + 2);", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 1));
    }

    // void f() {
    // int * ptr{};
    // *(ptr + 2);
    // }
    public void testSingleStagePointerWithOfsetDereferencedCheckForZeroDereferences() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(ptr + 2);", "ptr"));
        assertFalse(Expression.isDereferencedNTimes(pointer, 0));
    }

    // void f() {
    // int * ptr{};
    // ptr + 2;
    // }
    public void testSingleStagePointerWithOfsetNotDereferencedCheckForZeroDereferences() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("ptr + 2;", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 0));
    }

    // void f() {
    // int * ptr{};
    // *(((ptr)));
    // }
    public void testSingleStagePointerWrappedInParensDereferencedCheckForSingleDereference() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(((ptr)));", "ptr"));
        assertTrue(Expression.isDereferencedNTimes(pointer, 1));
    }

    // void f() {
    // int * ptr{};
    // *(((ptr)));
    // }
    public void testSingleStagePointerWrappedInParensDereferencedCheckForZeroDereferences() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName pointer = Cast.as(ICPPASTName.class, assertionHelper.findName("*(((ptr)));", "ptr"));
        assertFalse(Expression.isDereferencedNTimes(pointer, 0));
    }

}
