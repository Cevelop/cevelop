package com.cevelop.includator.cxxelement;

//package com.cevelop.includator.cxxelement;
//
//import java.util.List;
//
//import org.eclipse.cdt.core.dom.IName;
//import org.eclipse.cdt.core.dom.ast.DOMException;
//import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
//import org.eclipse.cdt.core.index.IIndex;
//import org.eclipse.cdt.core.index.IIndexName;
//import org.eclipse.core.runtime.CoreException;
//
//import com.cevelop.includator.IncludatorPlugin;
//import com.cevelop.includator.dependency.DeclarationReferenceDependency;
//import com.cevelop.includator.helpers.BindingHelper;
//import com.cevelop.includator.helpers.DeclarationReferenceHelper;
//import com.cevelop.includator.helpers.IncludatorException;
//import com.cevelop.includator.resources.IncludatorFile;
//import com.cevelop.includator.resources.IncludatorProject;
//
//public class MethodDelRefDependencyResolver {
//
//	private final MethodDeclarationReference methodDeclRef;
//	private boolean resolvesToClass;
//
//	public MethodDelRefDependencyResolver(MethodDeclarationReference methodDeclRef) {
//		this.methodDeclRef = methodDeclRef;
//		resolvesToClass = false;
//	}
//
//	public List<DeclarationReferenceDependency> getAllDependencies() {
//		List<DeclarationReferenceDependency> dependencyList = DeclarationReferenceHelper.getDependencies(methodDeclRef);
//		handleIsEmpty(dependencyList);
//		resolvesToClass = resolvesToClass(dependencyList);
//		return dependencyList;
//	}
//
//	private boolean resolvesToClass(List<DeclarationReferenceDependency> dependencyList) {
//		if (dependencyList.isEmpty()) {
//			return false;
//		}
//		Declaration declaration = dependencyList.get(0).getDeclaration();
//		try {
//			return declaration.getFile().getProject().getIndex().findBinding(declaration.getName()) instanceof ICPPClassType;
//		} catch (CoreException e) {
//			return false;
//		}
//	}
//
//	private void handleIsEmpty(List<DeclarationReferenceDependency> dependencyList) {
//		if (dependencyList.isEmpty()) {
//			try {
//				addClassDefinitionDependency(dependencyList);
//			} catch (DOMException e) {
//				throw new IncludatorException(e);
//			}
//		}
//	}
//
//	private void addClassDefinitionDependency(List<DeclarationReferenceDependency> dependencies) throws DOMException {
//		ICPPClassType classType = methodDeclRef.getMethodBinding().getClassOwner();
//		classType = BindingHelper.getMostSpecificDefiningClassBinding(classType, methodDeclRef.getFile().getProject().getIndex());
//		IncludatorProject project = methodDeclRef.getFile().getProject();
//		IIndex index = project.getIndex();
//		try {
//			IIndexName[] declNames = index.findNames(classType, IIndex.FIND_DEFINITIONS | IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES);
//			for (IName curName : declNames) {
//				IncludatorFile file = project.getFile(curName.getFileLocation().getFileName());
//				Declaration declaration = IncludatorPlugin.getDeclarationStore().getDeclaration(curName, file);
//				dependencies.add(new DeclarationReferenceDependency(methodDeclRef, declaration));
//			}
//		} catch (CoreException e) {
//			throw new IncludatorException(e);
//		}
//	}
//
//	public void pickRequiredDependencies(List<DeclarationReferenceDependency> requiredDependencies, List<DeclarationReferenceDependency> definitions,
//			DeclarationReferenceDependency declaration) {
//		if (resolvesToClass) {
//			requiredDependencies.addAll(definitions);
//			return;
//		}
//		if (declaration != null) {
//			requiredDependencies.add(declaration);
//		} else if (!definitions.isEmpty()) {
//			methodDeclRef.handlePickRequiredFromDefs(definitions);
//		}
//	}
// }
