package com.cevelop.constificator.tests.util.semantic.type.inheritancegraph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;

import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.semantic.Type.InheritanceGraph.ApplicationPolicy;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class ApplyTest extends ASTBasedTest {

    // struct A {};
    // struct B : virtual A {};
    // struct C : virtual A {};
    // struct D : B, C {};
    public void testDiamondHierarchy_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName virtualBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType virtualBaseBinding = Cast.as(ICPPClassType.class, virtualBaseName.resolveBinding());
        assertNotNull(virtualBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(4, seenTypes.size());
        assertEquals(virtualBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));
        assertEquals(secondBaseBinding, seenTypes.get(2));
        assertEquals(derivedBinding, seenTypes.get(3));
    }

    // struct A {};
    // struct B : virtual A {};
    // struct C : virtual A {};
    // struct D : B, C {};
    public void testDiamondHierarchy_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName virtualBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType virtualBaseBinding = Cast.as(ICPPClassType.class, virtualBaseName.resolveBinding());
        assertNotNull(virtualBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(5, seenTypes.size());

        assertEquals(virtualBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));

        assertEquals(virtualBaseBinding, seenTypes.get(2));
        assertEquals(secondBaseBinding, seenTypes.get(3));

        assertEquals(derivedBinding, seenTypes.get(4));
    }

    // struct A {};
    // struct B : A {};
    // struct C : A {};
    // struct D : B, C {};
    public void testForkHierarchy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName commonBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType commonBaseBinding = Cast.as(ICPPClassType.class, commonBaseName.resolveBinding());
        assertNotNull(commonBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(5, seenTypes.size());

        assertEquals(commonBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));

        assertEquals(commonBaseBinding, seenTypes.get(2));
        assertEquals(secondBaseBinding, seenTypes.get(3));

        assertEquals(derivedBinding, seenTypes.get(4));
    }

    // struct A {};
    // struct B : virtual A {};
    // struct C : A {};
    // struct D : B, C {};
    public void testForkHierarchy_OneVirtualProng_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName commonBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType commonBaseBinding = Cast.as(ICPPClassType.class, commonBaseName.resolveBinding());
        assertNotNull(commonBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(5, seenTypes.size());

        assertEquals(commonBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));

        assertEquals(commonBaseBinding, seenTypes.get(2));
        assertEquals(secondBaseBinding, seenTypes.get(3));

        assertEquals(derivedBinding, seenTypes.get(4));
    }

    // struct A {};
    // struct B : virtual A {};
    // struct C : A {};
    // struct D : B, C {};
    public void testForkHierarchy_OneVirtualProng_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName commonBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType commonBaseBinding = Cast.as(ICPPClassType.class, commonBaseName.resolveBinding());
        assertNotNull(commonBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(5, seenTypes.size());

        assertEquals(commonBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));

        assertEquals(commonBaseBinding, seenTypes.get(2));
        assertEquals(secondBaseBinding, seenTypes.get(3));

        assertEquals(derivedBinding, seenTypes.get(4));
    }

    // struct A {};
    // struct B : virtual A {};
    // struct C : virtual A {};
    // struct D : A {};
    // struct E : B, C, D {};
    public void testISO14882DiamondForkHierarchy_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName commonBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType commonBaseBinding = Cast.as(ICPPClassType.class, commonBaseName.resolveBinding());
        assertNotNull(commonBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName forkBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType forkBaseBinding = Cast.as(ICPPClassType.class, forkBaseName.resolveBinding());
        assertNotNull(forkBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("E"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(6, seenTypes.size());

        assertEquals(commonBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));
        assertEquals(secondBaseBinding, seenTypes.get(2));

        assertEquals(commonBaseBinding, seenTypes.get(3));
        assertEquals(forkBaseBinding, seenTypes.get(4));

        assertEquals(derivedBinding, seenTypes.get(5));
    }

    // struct A {};
    // struct B : virtual A {};
    // struct C : virtual A {};
    // struct D : A {};
    // struct E : B, C, D {};
    public void testISO14882DiamondForkHierarchy_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName commonBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType commonBaseBinding = Cast.as(ICPPClassType.class, commonBaseName.resolveBinding());
        assertNotNull(commonBaseBinding);

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName forkBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType forkBaseBinding = Cast.as(ICPPClassType.class, forkBaseName.resolveBinding());
        assertNotNull(forkBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("E"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(7, seenTypes.size());

        assertEquals(commonBaseBinding, seenTypes.get(0));
        assertEquals(firstBaseBinding, seenTypes.get(1));

        assertEquals(commonBaseBinding, seenTypes.get(2));
        assertEquals(secondBaseBinding, seenTypes.get(3));

        assertEquals(commonBaseBinding, seenTypes.get(4));
        assertEquals(forkBaseBinding, seenTypes.get(5));

        assertEquals(derivedBinding, seenTypes.get(6));
    }

    // struct A {};
    // struct B {};
    // struct C {};
    // struct D : C, A {};
    public void testMultipleBaseClasses_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(3, seenTypes.size());
        assertEquals(firstBaseBinding, seenTypes.get(0));
        assertEquals(secondBaseBinding, seenTypes.get(1));
        assertEquals(derivedBinding, seenTypes.get(2));
    }

    // struct A {};
    // struct B {};
    // struct C {};
    // struct D : C, A {};
    public void testMultipleBaseClasses_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(3, seenTypes.size());
        assertEquals(firstBaseBinding, seenTypes.get(0));
        assertEquals(secondBaseBinding, seenTypes.get(1));
        assertEquals(derivedBinding, seenTypes.get(2));
    }

    // struct A {};
    // struct B {};
    // struct C {};
    // struct D : C, A {};
    public void testMultipleBaseClasses_PolicySkipMostDerived() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName firstBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("C"));
        ICPPClassType firstBaseBinding = Cast.as(ICPPClassType.class, firstBaseName.resolveBinding());
        assertNotNull(firstBaseBinding);

        ICPPASTName secondBaseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType secondBaseBinding = Cast.as(ICPPClassType.class, secondBaseName.resolveBinding());
        assertNotNull(secondBaseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("D"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.SKIP_MOST_DERIVED);

        assertEquals(2, seenTypes.size());
        assertEquals(firstBaseBinding, seenTypes.get(0));
        assertEquals(secondBaseBinding, seenTypes.get(1));
    }

    // struct A {};
    public void testNoBaseClasses_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName structName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType structBinding = Cast.as(ICPPClassType.class, structName.resolveBinding());

        assertNotNull(structBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(structBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(1, seenTypes.size());
        assertEquals(structBinding, seenTypes.get(0));
    }

    // struct A {};
    public void testNoBaseClasses_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName structName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType structBinding = Cast.as(ICPPClassType.class, structName.resolveBinding());

        assertNotNull(structBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(structBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(1, seenTypes.size());
        assertEquals(structBinding, seenTypes.get(0));
    }

    // struct A {};
    public void testNoBaseClasses_PolicySkipMostDerived() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName structName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType structBinding = Cast.as(ICPPClassType.class, structName.resolveBinding());

        assertNotNull(structBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(structBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.SKIP_MOST_DERIVED);

        assertTrue(seenTypes.isEmpty());
    }

    // struct A {};
    // struct B : A {};
    public void testSingleBaseClass_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName baseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType baseBinding = Cast.as(ICPPClassType.class, baseName.resolveBinding());
        assertNotNull(baseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(2, seenTypes.size());
        assertEquals(baseBinding, seenTypes.get(0));
        assertEquals(derivedBinding, seenTypes.get(1));
    }

    // struct A {};
    // struct B : A {};
    public void testSingleBaseClass_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName baseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType baseBinding = Cast.as(ICPPClassType.class, baseName.resolveBinding());
        assertNotNull(baseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(2, seenTypes.size());
        assertEquals(baseBinding, seenTypes.get(0));
        assertEquals(derivedBinding, seenTypes.get(1));
    }

    // struct A {};
    // struct B : A {};
    public void testSingleBaseClass_PolicySkipMostDerived() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName baseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType baseBinding = Cast.as(ICPPClassType.class, baseName.resolveBinding());
        assertNotNull(baseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.SKIP_MOST_DERIVED);

        assertEquals(1, seenTypes.size());
        assertEquals(baseBinding, seenTypes.get(0));
    }

    // struct A {};
    // struct B : virtual A {};
    public void testSingleVirtualBaseClass_NoPolicy() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName baseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType baseBinding = Cast.as(ICPPClassType.class, baseName.resolveBinding());
        assertNotNull(baseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        });

        assertEquals(2, seenTypes.size());
        assertEquals(baseBinding, seenTypes.get(0));
        assertEquals(derivedBinding, seenTypes.get(1));
    }

    // struct A {};
    // struct B : virtual A {};
    public void testSingleVirtualBaseClass_PolicyDontSkipVirtual() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName baseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType baseBinding = Cast.as(ICPPClassType.class, baseName.resolveBinding());
        assertNotNull(baseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.DONT_SKIP_VIRTUAL);

        assertEquals(2, seenTypes.size());
        assertEquals(baseBinding, seenTypes.get(0));
        assertEquals(derivedBinding, seenTypes.get(1));
    }

    // struct A {};
    // struct B : virtual A {};
    public void testSingleVirtualBaseClass_PolicySkipMostDerived() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();

        ICPPASTName baseName = Cast.as(ICPPASTName.class, assertionHelper.findName("A"));
        ICPPClassType baseBinding = Cast.as(ICPPClassType.class, baseName.resolveBinding());
        assertNotNull(baseBinding);

        ICPPASTName derivedName = Cast.as(ICPPASTName.class, assertionHelper.findName("B"));
        ICPPClassType derivedBinding = Cast.as(ICPPClassType.class, derivedName.resolveBinding());
        assertNotNull(derivedBinding);

        List<IType> seenTypes = new ArrayList<>(1);
        new Type.InheritanceGraph(derivedBinding).apply((ICPPClassType type) -> {
            seenTypes.add(type);
        }, ApplicationPolicy.SKIP_MOST_DERIVED);

        assertEquals(1, seenTypes.size());
        assertEquals(baseBinding, seenTypes.get(0));
    }

}
