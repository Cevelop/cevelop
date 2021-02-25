package com.cevelop.constificator.tests.util.semantic.type;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;

import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.tests.util.ASTBasedTest;


public class AreSameTypeIgnoringConstTest extends ASTBasedTest {

    // void func(int);
    // void func(float);
    public void testFreeFunctions_DifferingInParameterTypes() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int)", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(float)", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // void func(int *);
    // void func(int const *);
    public void testFreeFunctions_DifferingPointerConstQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int *)", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int const *)", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // void func(int &);
    // void func(int const &);
    public void testFreeFunctions_DifferingReferenceConstQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int &)", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int const &)", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // void func(int);
    // void func(int const);
    public void testFreeFunctions_Redeclaration() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int)", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func(int const)", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // void funcA(int){};
    // char funcB(int const){};
    //
    // int main() {
    // void (*variableA)(int) = &funcA;
    // void (*variableB)(int const) = &funcB;
    // }
    public void testFunctionPointers_DifferingInArgumentConstQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // void funcA(int){};
    // void funcB(float){};
    //
    // int main() {
    // void (*variableA)(int) = &funcA;
    // void (*variableB)(float) = &funcB;
    // }
    public void testFunctionPointers_DifferingInParameterTypes() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // void funcA(){};
    // char funcB(){};
    //
    // int main() {
    // void (*variableA)() = &funcA;
    // void (*variableB)() = &funcB;
    // }
    public void testFunctionPointers_DifferingInReturnType() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // void func(){};
    //
    // int main() {
    // void (*variableA)() = &func;
    // void (*variableB)() = &func;
    // }
    public void testFunctionPointers_ExactMatch() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // struct s {
    // void func();
    // void func() const;
    // };
    public void testMemberFunctions_DifferingInConstQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func();", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func() const;", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // int * variableA;
    // int const * variableB;
    public void testPointers() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // int variableA[1];
    // int * variableB;
    public void testPointers_Array() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // int * * variableA;
    // int * variableB;
    public void testPointers_DifferentDimensions() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // int variableA[1][];
    // int * * variableB;
    public void testPointers_MultidimensionalArray() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // int * const variableA;
    // int const * variableB;
    public void testPointers_OutermostConst() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // struct s {
    // void func();
    // void func() const;
    // };
    //
    // int main() {
    // void (s::*       variableA)() = &s::func;
    // void (s::* const variableB)() = &s::func;
    // }
    public void testPointersToMemberFunctions_DifferingInConstQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // struct s {
    // void func();
    // };
    //
    // int main() {
    // void (s::* variableA)() = &s::func;
    // void (s::* variableB)() = &s::func;
    // }
    public void testPointersToMemberFunctions_ExactMatch() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // struct s {
    // void func();
    // };
    //
    // struct t {
    // void func();
    // };
    //
    // int main() {
    // void (s::* variableA)() = &s::func;
    // void (t::* variableB)() = &t::func;
    // }
    public void testPointersToMemberFunctions_MembersOfDifferentClassTypes() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA", 9));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB", 9));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // struct s {
    // void func() &;
    // void func() const &;
    // };
    public void testReferenceQualifiedMemberFunctions_DifferingInConstQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func() &;", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func() const &;", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // struct s {
    // void func() &;
    // void func() &&;
    // };
    public void testReferenceQualifiedMemberFunctions_DifferingInReferenceQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName firstFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func() &;", 4));
        ICPPFunction firstFunction = Cast.as(ICPPFunction.class, firstFunctionName.resolveBinding());
        ICPPASTName secondFunctionName = Cast.as(ICPPASTName.class, assertionHelper.findName("func() &&;", 4));
        ICPPFunction secondFunction = Cast.as(ICPPFunction.class, secondFunctionName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(firstFunction.getType(), secondFunction.getType()));
    }

    // int & variableA;
    // int const & variableB;
    public void testReferences() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // int variableA;
    // int variableB;
    public void testSimpleTypes() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // int variableA;
    // int const variableB;
    public void testSimpleTypes_DifferingQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // char variableA;
    // long variableB;
    public void testSimpleTypes_DifferingTypes() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // template<typename T>
    // struct cls {};
    //
    // cls<int> variableA;
    // cls<int> variableB;
    public void testTemplates() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // template<typename T>
    // struct cls {};
    //
    // cls<int> variableA;
    // cls<int const> variableB;
    public void testTemplates_DifferinglyQualifiedTypeArguments() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // template<int N>
    // struct cls {};
    //
    // cls<21> variableA;
    // cls<42> variableB;
    public void testTemplates_DifferingNonTypeArguments() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // template<typename T>
    // struct cla {};
    //
    // template<typename T>
    // struct clb {};
    //
    // cla<int> variableA;
    // clb<int> variableB;
    public void testTemplates_DifferingTemplateTypes() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // template<typename T>
    // struct cls {};
    //
    // cls<bool> variableA;
    // cls<char> variableB;
    public void testTemplates_DifferingTypeArguments() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertFalse(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // template<int N>
    // struct cls {};
    //
    // cls<21> variableA;
    // cls<21> variableB;
    public void testTemplates_NonTypeArguments() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // using Int = int const;
    //
    // int variableA;
    // Int variableB;
    public void testTypedefs_DifferingInsideQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

    // using Int = int;
    //
    // int variableA;
    // Int const variableB;
    public void testTypedefs_DifferingOutsideQualification() throws Exception {
        BindingAssertionHelper assertionHelper = getAssertionHelper();
        ICPPASTName variableAName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableA"));
        ICPPVariable variableA = Cast.as(ICPPVariable.class, variableAName.resolveBinding());
        ICPPASTName variableBName = Cast.as(ICPPASTName.class, assertionHelper.findName("variableB"));
        ICPPVariable variableB = Cast.as(ICPPVariable.class, variableBName.resolveBinding());

        assertTrue(Type.areSameTypeIgnoringConst(variableA.getType(), variableB.getType()));
    }

}
