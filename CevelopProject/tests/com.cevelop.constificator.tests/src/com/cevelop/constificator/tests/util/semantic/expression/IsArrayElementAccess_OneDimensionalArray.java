package com.cevelop.constificator.tests.util.semantic.expression;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;

import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsArrayElementAccess_OneDimensionalArray extends ASTBasedTest {

    // void f() {
    // int arr[42]{};
    // arr;
    // }
    public void testReferenceOnlyMentionsArray() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("arr;", "arr"));
        assertFalse(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // &arr;
    // }
    public void testReferenceIsUsedToTakeAddressOfArray() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("&arr;", "arr"));
        assertFalse(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // *arr;
    // }
    public void testElementAccessViaPointerDereference() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("*arr;", "arr"));
        assertTrue(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // arr[41];
    // }
    public void testElementAccessViaArraySubscriptOperator() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("arr[41];", "arr"));
        assertTrue(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // &*arr;
    // }
    public void testAddressOfElementAccessedViaPointerDereference() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("&*arr;", "arr"));
        assertFalse(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // &arr[41];
    // }
    public void testAddressOfElementAccessedViaArraySubscriptOperator() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("&arr[41];", "arr"));
        assertFalse(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // *&*arr;
    // }
    public void testElementAccessViaAddressOfElementAccessedViaPointerDereference() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("*&*arr;", "arr"));
        assertTrue(Expression.isArrayElementAccess(occurrence));
    }

    // void f() {
    // int arr[42]{};
    // *&arr[41];
    // }
    public void testElementAccessViaAddressOfElementAccessedViaArraySubscriptOperator() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName occurrence = Cast.as(ICPPASTName.class, assertionHelper.findName("*&arr[41];", "arr"));
        assertTrue(Expression.isArrayElementAccess(occurrence));
    }

}
