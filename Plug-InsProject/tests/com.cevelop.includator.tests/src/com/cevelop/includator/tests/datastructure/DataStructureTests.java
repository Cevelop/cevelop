/*******************************************************************************
 * Copyright (c) 20114 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.includator.tests.datastructure.ambiguouslookup.AmbiguousLookupTests;
import com.cevelop.includator.tests.datastructure.auto.AutoTests;
import com.cevelop.includator.tests.datastructure.constructorrefs.ConstructorRefTests;
import com.cevelop.includator.tests.datastructure.decltype.DecltypeTests;
import com.cevelop.includator.tests.datastructure.destructorrefs.DestructorRefTests;
import com.cevelop.includator.tests.datastructure.multipledefinition.MultipleDefinitionTests;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			DataStructureFileTest1ExternalFile.class,
			DataStructureFileTest2EmptyFileNoTu.class,
			DataStructureFileTest3AsmFile.class,
			DataStructureFileTest4AsmFileStartingPointFileAlg.class,
			DataStructureFileTest5AsmFileStartingPointProjAlg.class,
			DataStructureFileTest6CProjectIndexFileTest.class,
			DataStructureFileTest7CCProjectIndexFileTest.class,
			DataStructureIncludeStatementTest1.class,
			DataStructureIncludeStatementTest2.class,
			DataStructureIncludeStatementTestConditionalMacros.class,
			DataStructureReferenceTest10Operators.class,
			DataStructureReferenceTest11Indexer.class,
			DataStructureReferenceTest12UnresolvedReferenceTest.class,
			DataStructureReferenceTest13IfMacro.class,
			DataStructureReferenceTest14ManyMacros.class,
			DataStructureReferenceTest15UndefinedMacroSymbol.class,
			DataStructureReferenceTest16ComplexMacro.class,
			DataStructureReferenceTest17NewDeleteKeyword.class,
			DataStructureReferenceTest18NewOperatorOverload.class,
			DataStructureReferenceTest19ComplexMacroManyFiles.class,
			DataStructureReferenceTest1FunctionReference.class,
			DataStructureReferenceTest20ASTBuiltinName.class,
			DataStructureReferenceTest21GotoTest.class,
			DataStructureReferenceTest22UnnamedFunctionParam.class,
			DataStructureReferenceTest23UnnamedTypes.class,
			DataStructureReferenceTest24IfDefWithoutName.class,
			DataStructureReferenceTest25CassImplToClassDecl.class,
			DataStructureReferenceTest26NamespaceFunction.class,
			DataStructureReferenceTest27NamespaceFunction2.class,
			DataStructureReferenceTest28ConstructorResolutionTest.class,
			DataStructureReferenceTest29ConstructorResolutionTest2.class,
			DataStructureReferenceTest2ClassReference.class,
			DataStructureReferenceTest30TypeDef.class,
			DataStructureReferenceTest31ParentClassMethod.class,
			DataStructureReferenceTest32StaticMemberFunctionReference.class,
			DataStructureReferenceTest33HiddenFile.class,
			DataStructureReferenceTest3MethodReference.class,
			DataStructureReferenceTest4ClassFieldReference.class,
			DataStructureReferenceTest5Keywords.class,
			DataStructureReferenceTest6Templates.class,
			DataStructureReferenceTest7Macros.class,
			DataStructureReferenceTest8Namespaces.class,
			DataStructureReferenceTest9Templates2.class,
			DataStructureReferenceTest34ExcludedFile.class,
			DataStructureReferenceTest35ExcludedFolder.class,
			DataStructureReferenceTest36TypedefToClass.class,
			DataStructureReferenceTest37TypedefToClassFwdDecl.class,
			DataStructureReferenceTest38TypedefToClassFwdDeclExtern.class,
			DataStructureReferenceTest39FwdInTypeDef.class,
			DataStructureReferenceTest40TypedefToFunction.class,
			DataStructureReferenceTest41TypedefToMemberFunction.class,
			DataStructureReferenceTest42ReferencesInNamespaceAlias.class,
			DataStructureReferenceTest43OverloadedOperator.class,
			DataStructureReferenceTest44NamespaceMember.class,
			DataStructureReferenceTest45TemplateParameterTypeReference.class,
			DataStructureReferenceTest46TemplateParameterDependentType.class,
			DataStructureReferenceTest47ArrayTypeMemberAccess.class,
			DataStructureReferenceTest48FunctionTemplateReference.class,
			DataStructureReferenceTest49ExcludedSymbolReference.class,
			DataStructureReferenceTest50NonIncludedMacroRefs.class,
			DataStructureReferenceTest51UndefMacro.class,
			DataStructureReferenceTest52MinimalNamespaceAlias.class,
			DataStructureReferenceTest53DefNamespaceAliasAndUsagePart1.class,
			DataStructureReferenceTest53DefNamespaceAliasAndUsagePart2.class,
			DataStructureReferenceTest54TemplIdOfTemplSpecialization.class,
			DataStructureReferenceTest55FwdInFunctionParameter.class,
			ConstructorRefTests.class,
			DestructorRefTests.class,
			AmbiguousLookupTests.class,
			MultipleDefinitionTests.class,
			DecltypeTests.class,
			AutoTests.class
			//@formatter:on
})
public class DataStructureTests {}
