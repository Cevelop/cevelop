package com.cevelop.constificator.tests.util.semantic.memberfunction;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.constificator.core.util.semantic.MemberFunction;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class GetShadowedTest extends ASTBasedTest {

    // struct base {
    // void memfun(int) const;
    // void memfun(int);
    // void memfun() const;
    // };
    //
    // struct derived : base {
    // using base::memfun;
    // void memfun();
    // };
    public void testNoShadowedFunctionFoundBecauseOfUsingDeclaration() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function).isEmpty());
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived {
    // void memfun();
    // };
    public void testNoShadowedFunctionsFoundBecauseOfMissingInheritance() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function).isEmpty());
    }

    // struct base {
    // virtual void memfun() = 0;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testNoShadowedFunctionsFoundBecauseOfOverriding() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        assertTrue(MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function).isEmpty());
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived : base {
    // using base::memfun;
    // void memfun();
    // };
    public void testOneShadowedFunctionFoundDespiteUsingDeclaration() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName shadowed = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() ;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        Set<ICPPMethod> shadowedSet = MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function);
        assertEquals(1, shadowedSet.size());
        assertTrue(shadowedSet.contains(shadowed.resolveBinding()));
    }

    // struct base {
    // void memfun(int) const;
    // void memfun(int);
    // void memfun() const;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testSingleMultipleBaseFunctions() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstOverridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun(int) const;", 6));
        ICPPASTName secondOverridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun(int);", 6));
        ICPPASTName thirdOverridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() const;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        Set<ICPPMethod> shadowedSet = MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function);
        assertTrue(shadowedSet.contains(firstOverridden.resolveBinding()));
        assertTrue(shadowedSet.contains(secondOverridden.resolveBinding()));
        assertTrue(shadowedSet.contains(thirdOverridden.resolveBinding()));
        assertEquals(3, shadowedSet.size());
    }

    // struct base {
    // void memfun() const;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testSingleShadowedBaseFunctionWithDifferingConstSignature() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() const;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        Set<ICPPMethod> shadowedSet = MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function);
        assertTrue(shadowedSet.contains(overridden.resolveBinding()));
        assertEquals(1, shadowedSet.size());
    }

    // struct base {
    // void memfun(int);
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testSingleShadowedBaseFunctionWithDifferingParameterSignature() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun(int);", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        Set<ICPPMethod> shadowedSet = MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function);
        assertTrue(shadowedSet.contains(overridden.resolveBinding()));
        assertEquals(1, shadowedSet.size());
    }

    // struct base {
    // void memfun() ;
    // };
    //
    // struct derived : base {
    // void memfun();
    // };
    public void testSingleShadowedBaseFunctionWithIdenticalSignature() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName overridden = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun() ;", 6));
        ICPPASTName function = Cast.as(ICPPASTName.class, assertionHelper.findName("memfun();", 6));
        Set<ICPPMethod> shadowedSet = MemberFunction.getShadowed((ICPPMethod) function.resolveBinding(), function);
        assertTrue(shadowedSet.contains(overridden.resolveBinding()));
        assertEquals(1, shadowedSet.size());
    }

}
