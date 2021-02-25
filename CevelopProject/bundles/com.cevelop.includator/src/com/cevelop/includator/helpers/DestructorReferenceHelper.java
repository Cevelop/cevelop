package com.cevelop.includator.helpers;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPClassSpecializationScope;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.ClassDeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.cxxelement.ImplicitDestructorDeclarationReference;
import com.cevelop.includator.resources.IncludatorFile;


@SuppressWarnings("restriction")
public class DestructorReferenceHelper {

    public static void addImplicitBaseClassDestructors(DestructorDeclarationReference destructorRef, RefMap refs) {
        try {
            ICPPClassType classBinding = destructorRef.getMethodBinding().getClassOwner();
            Collection<ICPPClassType> baseClasses = findBaseClasses(classBinding, destructorRef.getASTNode());
            addDestrRefs(baseClasses, destructorRef, refs);
        } catch (DOMException ignored) {} catch (IncludatorException ignored) {}
    }

    private static Collection<ICPPClassType> findBaseClasses(ICPPClassType classBinding, IASTNode point) throws DOMException {
        Collection<ICPPClassType> result = new ArrayList<>();
        for (ICPPBase curBase : classBinding.getBases()) {
            IBinding baseType = curBase.getBaseClass();
            if (baseType instanceof ICPPClassType) {
                result.add((ICPPClassType) baseType);
            }
        }
        return result;
    }

    public static void addImplicitMemberVarDestructors(DestructorDeclarationReference destructorRef, RefMap refs) {
        try {
            ICPPClassType classBinding = destructorRef.getMethodBinding().getClassOwner();
            Collection<ICPPClassType> fieldClasses = findFieldClasses(classBinding, destructorRef.getASTNode());
            addDestrRefs(fieldClasses, destructorRef, refs);
        } catch (DOMException e) {
            throw new IncludatorException("Error while adding implicit constructor references in constructor definition " + destructorRef.getName() +
                                          ".", e);
        }
    }

    private static Collection<ICPPClassType> findFieldClasses(ICPPClassType classBinding, IASTNode point) throws DOMException {
        Collection<ICPPClassType> result = new ArrayList<>();
        for (ICPPField curField : classBinding.getDeclaredFields()) {
            if (curField.getType() instanceof ICPPClassType) {
                result.add((ICPPClassType) curField.getType());
            }
        }
        return result;
    }

    private static void addDestrRefs(Collection<ICPPClassType> baseClasses, DestructorDeclarationReference initiatingRef, RefMap refs)
            throws DOMException {
        IncludatorFile file = initiatingRef.getFile();
        for (ICPPClassType curClass : baseClasses) {
            ICPPMethod typeDestructor = getDestructorBinding(curClass, file.getProject().getIndex(), initiatingRef.getASTNode());
            if (typeDestructor != null) {
                DestructorDeclarationReference addedRef = addImplicitDestrToMap(typeDestructor, (IASTName) initiatingRef.getASTNode(), file, refs);
                addedRef.addIsRefNameToMetaInfo();
            }
        }
    }

    public static ICPPMethod getDestructorBinding(ICPPClassType classType, IIndex index, IASTNode point) {
        for (ICPPMethod curMethod : classType.getDeclaredMethods()) {
            if (curMethod.isDestructor()) {
                return curMethod;
            }
        }
        return getImplicitDestructor(classType, index, point);
    }

    private static ICPPMethod getImplicitDestructor(ICPPClassType classType, IIndex index, IASTNode point) {

        ICPPClassScope classScope = getClassScope(classType);

        if (classScope != null) {
            for (ICPPMethod curImplicit : getImplicitClassScopeMethods(classScope, point)) {
                if (curImplicit.isDestructor()) {
                    return curImplicit;
                }
            }
        }
        return null;
    }

    private static ICPPMethod[] getImplicitClassScopeMethods(ICPPClassScope classScope, IASTNode point) {
        if (classScope instanceof ICPPClassSpecializationScope) {
            return ((ICPPClassSpecializationScope) classScope).getImplicitMethods();
        }
        return classScope.getImplicitMethods();
    }

    private static ICPPClassScope getClassScope(ICompositeType classType) {
        IScope compositeScope = classType.getCompositeScope();
        if (compositeScope instanceof ICPPClassScope) {
            return (ICPPClassScope) compositeScope;
        }
        if (classType instanceof ICPPTemplateInstance) {
            ICPPTemplateInstance teplInst = (ICPPTemplateInstance) classType;
            IBinding specializedBinding = teplInst.getSpecializedBinding();
            if (specializedBinding instanceof ICompositeType) {
                return getClassScope((ICompositeType) specializedBinding);
            }
        }
        return null;
    }

    public static void addImplicitDestrDefinition(ClassDeclarationReference classDeclRef, RefMap refs) {
        if (!hasExplicitDestructor(classDeclRef)) {
            IASTName helperNode = (IASTName) classDeclRef.getASTNode();
            IncludatorFile file = classDeclRef.getFile();
            ICPPClassType classBinding = classDeclRef.getClassBinding();
            ICPPMethod destrBinding = getDestructorBinding(classBinding, file.getProject().getIndex(), classDeclRef.getASTNode());
            if (destrBinding == null) {
                IncludatorPlugin.logStatus(new IncludatorStatus("Failed to add destructor reference of type " + classDeclRef.getName() + "."),
                        classDeclRef.getFile());
                return;
            }
            DestructorDeclarationReference destrRef = addImplicitDestrToMap(destrBinding, helperNode, file, refs);
            destrRef.setIsDefinitionName();
            addImplicitMemberVarDestructors(destrRef, refs);
            addImplicitBaseClassDestructors(destrRef, refs);
        }
    }

    private static DestructorDeclarationReference addImplicitDestrToMap(ICPPMethod destrBinding, IASTName helperNode, IncludatorFile file,
            RefMap refs) {
        if (refs.containsKey(destrBinding)) {
            DestructorDeclarationReference destrRef = (DestructorDeclarationReference) refs.get(destrBinding);
            return destrRef;
        } else {
            DestructorDeclarationReference refToAdd = new ImplicitDestructorDeclarationReference(destrBinding, helperNode, file);
            refs.put(destrBinding, refToAdd);
            return refToAdd;
        }
    }

    private static boolean hasExplicitDestructor(ClassDeclarationReference classDeclRef) {
        ICPPASTCompositeTypeSpecifier type = NodeHelper.findParentOfType(ICPPASTCompositeTypeSpecifier.class, classDeclRef.getASTNode());
        if (type == null) {
            return false;
        }
        for (IASTDeclaration curMember : type.getMembers()) {
            if (isDestructorMember(curMember)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDestructorMember(IASTDeclaration member) {
        IASTFunctionDeclarator declarator = null;
        if (member instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) member;
            IASTDeclarator[] declarators = simpleDeclaration.getDeclarators();
            if (declarators.length == 1 && declarators[0] instanceof IASTFunctionDeclarator) {
                declarator = (IASTFunctionDeclarator) declarators[0];
            }
        } else if (member instanceof ICPPASTFunctionDefinition) {
            ICPPASTFunctionDefinition def = (ICPPASTFunctionDefinition) member;
            declarator = def.getDeclarator();
        }
        if (declarator != null) {
            return declarator.getName().toString().startsWith("~");
        }
        return false;
    }
}
