package com.cevelop.constificator.tests.util.semantic.expression;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;

import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class IsResolvedToInstance extends ASTBasedTest {

    // struct s {
    // void f() {
    // &(*this);
    // }
    // }
    public void testThisLiteralDereferenceInParensDoesNotResolveToInstanceWithAddressOf() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTLiteralExpression thisLiteral = Cast.as(ICPPASTLiteralExpression.class, assertionHelper.findLiteral("this"));
        assertFalse(Expression.isResolvedToInstance(thisLiteral));
    }

    // struct s {
    // void f() {
    // this;
    // }
    // }
    public void testThisLiteralDoesNotResolveToInstance() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTLiteralExpression thisLiteral = Cast.as(ICPPASTLiteralExpression.class, assertionHelper.findLiteral("this"));
        assertFalse(Expression.isResolvedToInstance(thisLiteral));
    }

    // struct s {
    // void f() {
    // (this);
    // }
    // }
    public void testThisLiteralInParensDoesNotResolveToInstance() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTLiteralExpression thisLiteral = Cast.as(ICPPASTLiteralExpression.class, assertionHelper.findLiteral("this"));
        assertFalse(Expression.isResolvedToInstance(thisLiteral));
    }

    // struct s {
    // void f() {
    // &*(this);
    // }
    // }
    public void testThisLiteralInParensDoesNotResolveToInstanceViaOuterDereferenceAndAddressOf() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTLiteralExpression thisLiteral = Cast.as(ICPPASTLiteralExpression.class, assertionHelper.findLiteral("this"));
        assertFalse(Expression.isResolvedToInstance(thisLiteral));
    }

    // struct s {
    // void f() {
    // *(this);
    // }
    // }
    public void testThisLiteralInParensResolvesToInstanceViaOuterDereference() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTLiteralExpression thisLiteral = Cast.as(ICPPASTLiteralExpression.class, assertionHelper.findLiteral("this"));
        assertTrue(Expression.isResolvedToInstance(thisLiteral));
    }

    // struct s {
    // void f() {
    // *this;
    // }
    // }
    public void testThisLiteralResolvesToInstanceViaSimpleDereference() throws Exception {
        AssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTLiteralExpression thisLiteral = Cast.as(ICPPASTLiteralExpression.class, assertionHelper.findLiteral("this"));
        assertTrue(Expression.isResolvedToInstance(thisLiteral));
    }

}
