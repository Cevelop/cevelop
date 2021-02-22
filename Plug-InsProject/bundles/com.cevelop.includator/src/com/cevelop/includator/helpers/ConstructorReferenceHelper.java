package com.cevelop.includator.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.ClassDeclarationReference;
import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.SpecialMemberFunctionDeclarationReference;
import com.cevelop.includator.helpers.CDTInternalAccess.CPPSemantics;
import com.cevelop.includator.helpers.matchers.CtorMatcher;
import com.cevelop.includator.resources.IncludatorFile;


@SuppressWarnings("restriction")
public class ConstructorReferenceHelper {

    public static DefaultConstructorMatcher DEFAULT_CONSTRUCTOR_MATCHER = new DefaultConstructorMatcher();
    public static CopyConstructorMatcher    COPY_CONSTRUCTOR_MATCHER    = new CopyConstructorMatcher();

    public static void addImplicitBaseClassConstructors(ConstructorDeclarationReference constrRef, RefMap refs) {
        try {
            ICPPClassType classBinding = constrRef.getConstructorBinding().getClassOwner();
            Collection<String> initializedBaseConstrs = getInitializedBaseConstructorNames(constrRef);
            Collection<ICPPClassType> implicitlyInitializedBaseClasses = findImplicitlyInitializedBaseClasses(classBinding, initializedBaseConstrs,
                    constrRef.getASTNode());
            CtorMatcher matcher;
            if (constrRef.isImplicitDefinition() && constrRef.isCopyConstructor()) {
                matcher = ConstructorReferenceHelper.COPY_CONSTRUCTOR_MATCHER;
            } else {
                matcher = ConstructorReferenceHelper.DEFAULT_CONSTRUCTOR_MATCHER;
            }
            addConstrRefs(implicitlyInitializedBaseClasses, matcher, constrRef, refs);
        } catch (DOMException e) {
            // do nothing
        }
    }

    private static Collection<String> getInitializedBaseConstructorNames(SpecialMemberFunctionDeclarationReference constrRef) {
        Collection<String> result = new ArrayList<>();
        ICPPASTFunctionDefinition definitionNode = NodeHelper.findParentOfType(ICPPASTFunctionDefinition.class, constrRef.getASTNode());
        if (definitionNode != null) {
            for (ICPPASTConstructorChainInitializer curInitializer : definitionNode.getMemberInitializers()) {
                result.add(curInitializer.getMemberInitializerId().toString());
            }
        }
        return result;
    }

    private static Collection<ICPPClassType> findImplicitlyInitializedBaseClasses(ICPPClassType classBinding,
            Collection<String> initializedBaseConstrs, IASTNode point) throws DOMException {
        Collection<ICPPClassType> result = new ArrayList<>();
        for (ICPPBase curBase : classBinding.getBases()) {
            IBinding baseType = curBase.getBaseClass();
            if (baseType instanceof ICPPClassType && !initializedBaseConstrs.contains(baseType.getName())) {
                result.add((ICPPClassType) baseType);
            }
        }
        return result;
    }

    public static void addImplicitMemberVarConstructors(ConstructorDeclarationReference constrRef, RefMap refs) {
        try {
            ICPPClassType classBinding = constrRef.getConstructorBinding().getClassOwner();
            Collection<String> initializedFields = getInitializedFieldNames(constrRef);
            Collection<ICPPClassType> remainingFieldClasses = findImplicitlyInitializedFields(classBinding, initializedFields, constrRef
                    .getASTNode());
            CtorMatcher matcher;
            if (constrRef.isImplicitDefinition() && constrRef.isCopyConstructor()) {
                matcher = ConstructorReferenceHelper.COPY_CONSTRUCTOR_MATCHER;
            } else {
                matcher = ConstructorReferenceHelper.DEFAULT_CONSTRUCTOR_MATCHER;
            }
            addConstrRefs(remainingFieldClasses, matcher, constrRef, refs);
        } catch (DOMException e) {
            // do nothing
        }
    }

    private static void addConstrRefs(Collection<ICPPClassType> classes, CtorMatcher matcher, SpecialMemberFunctionDeclarationReference initiatingRef,
            RefMap refs) throws DOMException {
        for (ICPPClassType curClass : classes) {
            ConstructorDeclarationReference addedRef = addMatchingConstrRef(matcher, (IASTName) initiatingRef.getASTNode(), initiatingRef.getFile(),
                    refs, curClass);
            if (addedRef != null) {
                addedRef.addIsRefNameToMetaInfo();
            }
        }
    }

    private static ConstructorDeclarationReference addMatchingConstrRef(CtorMatcher matcher, IASTName helperName, IncludatorFile file, RefMap refs,
            ICPPClassType classBinding) throws DOMException {
        for (ICPPConstructor curConstructor : classBinding.getConstructors()) {
            if (matcher.matches(curConstructor)) {
                return addImplicitConstrRefToRefMap(curConstructor, refs, helperName, file);
            }
        }
        return null;
    }

    public static ConstructorDeclarationReference addImplicitConstrRefToRefMap(ICPPConstructor constrToAdd, RefMap refs, IASTName astName,
            IncludatorFile file) {
        if (refs.containsKey(constrToAdd)) {
            ConstructorDeclarationReference constrRef = (ConstructorDeclarationReference) refs.get(constrToAdd);
            return constrRef;
        } else {
            ConstructorDeclarationReference refToAdd = new ConstructorDeclarationReference(constrToAdd, astName, file, true);
            refs.put(constrToAdd, refToAdd);
            return refToAdd;
        }
    }

    private static Collection<ICPPClassType> findImplicitlyInitializedFields(ICPPClassType classBinding, Collection<String> initializedFields,
            IASTNode point) throws DOMException {
        Collection<ICPPClassType> remainingFields = new ArrayList<>();
        for (ICPPField curField : classBinding.getDeclaredFields()) {
            if (!initializedFields.contains(curField.getName()) && !isPointerField(curField) && curField.getType() instanceof ICPPClassType) {
                remainingFields.add((ICPPClassType) curField.getType());
            }
        }
        return remainingFields;
    }

    public static boolean isPointerField(ICPPField field) throws DOMException {
        return field.getType() instanceof ICPPReferenceType;
    }

    private static Collection<String> getInitializedFieldNames(SpecialMemberFunctionDeclarationReference constrRef) {
        Collection<String> result = new ArrayList<>();
        ICPPASTFunctionDefinition definitionNode = NodeHelper.findParentOfType(ICPPASTFunctionDefinition.class, constrRef.getASTNode());
        if (definitionNode != null) {
            for (ICPPASTConstructorChainInitializer curInitializer : definitionNode.getMemberInitializers()) {
                result.add(curInitializer.getMemberInitializerId().toString());
            }
        }
        return result;
    }

    public static boolean isCopyConstructor(ICPPConstructor constructor, ICPPClassType classType) {
        ICPPFunctionType constrType = constructor.getType();
        IType[] paramTypes = constrType.getParameterTypes();
        ICPPParameter[] params = constructor.getParameters();
        if (paramTypes.length == 0) {
            return false;
        }
        if (!isFirstCopyConstructorParam(paramTypes[0], classType)) {
            return false;
        }
        for (int i = 1; i < params.length; i++) {
            if (!params[i].hasDefaultValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFirstCopyConstructorParam(IType paramType, ICPPClassType classType) {
        if (!(paramType instanceof ICPPReferenceType)) {
            return false;
        }
        IType potentialClassType = ((ICPPReferenceType) paramType).getType();
        if (potentialClassType instanceof IQualifierType) {
            potentialClassType = ((IQualifierType) potentialClassType).getType();
        }
        if (potentialClassType instanceof ICPPDeferredClassInstance) {
            potentialClassType = ((ICPPDeferredClassInstance) potentialClassType).getClassTemplate();
        }
        if (potentialClassType == null) {
            IncludatorPlugin.log("Potential class type of " + paramType + " was null. Tried to match it to " + classType);
            return false;
        }
        return potentialClassType.isSameType(classType);
    }

    public static boolean isDefaultConstructor(ICPPConstructor constructor) {
        ICPPParameter[] parameters = constructor.getParameters();
        int length = parameters.length;
        return length == 0 || (length == 1 && isVoidParam(parameters[0])) || haveAllDefaultValue(parameters);
    }

    public static boolean isVoidParam(ICPPParameter param) {
        if (param.getType() instanceof IBasicType) {
            return ((IBasicType) param.getType()).getKind().equals(Kind.eVoid);
        }
        return false;
    }

    public static boolean haveAllDefaultValue(ICPPParameter[] parameters) {
        for (ICPPParameter curParam : parameters) {
            if (!curParam.hasDefaultValue()) {
                return false;
            }
        }
        return true;
    }

    public static void addImplicitConstrDefinitions(ClassDeclarationReference classDeclRef, RefMap refs) {
        List<ICPPConstructor> constrs = findExplicitConstructors(classDeclRef);
        try {
            if (constrs.isEmpty()) {
                addImplicitDefaultConstrRef(classDeclRef, refs);
            }
            if (!containsCopyConstructor(constrs, classDeclRef.getClassBinding())) {
                addImplicitCopyConstrRef(classDeclRef, refs);
            }
        } catch (DOMException e) {
            throw new IncludatorException("Error while adding implicit constructor declaration references.", e);
        }
    }

    private static boolean containsCopyConstructor(List<ICPPConstructor> constrs, ICPPClassType classBinding) throws DOMException {
        for (ICPPConstructor curconstrBinding : constrs) {
            if (isCopyConstructor(curconstrBinding, classBinding)) {
                return true;
            }
        }
        return false;
    }

    private static void addImplicitCopyConstrRef(ClassDeclarationReference classDeclRef, RefMap refs) throws DOMException {
        IASTName helperNode = (IASTName) classDeclRef.getASTNode();
        IncludatorFile file = classDeclRef.getFile();
        ICPPClassType binding = classDeclRef.getClassBinding();
        ConstructorDeclarationReference addedConstrRef = addMatchingConstrRef(COPY_CONSTRUCTOR_MATCHER, helperNode, file, refs, binding);
        if (addedConstrRef != null) {
            addedConstrRef.setIsDefinitionName();
            addImplicitMemberVarConstructors(addedConstrRef, refs);
            addImplicitBaseClassConstructors(addedConstrRef, refs);
        }
    }

    private static List<ICPPConstructor> findExplicitConstructors(ClassDeclarationReference classDeclRef) {
        List<ICPPConstructor> result = new ArrayList<>();
        ICPPASTCompositeTypeSpecifier type = NodeHelper.findParentOfType(ICPPASTCompositeTypeSpecifier.class, classDeclRef.getASTNode());
        if (type == null) {
            return result;
        }
        for (IASTDeclaration curMember : type.getMembers()) {
            ICPPConstructor curCtorBinding = getContructorBinding(curMember);
            if (curCtorBinding != null) {
                result.add(curCtorBinding);
            }
        }
        return result;
    }

    private static ICPPConstructor getContructorBinding(IASTDeclaration member) {
        if (member instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) member;
            IASTDeclarator[] declarators = simpleDeclaration.getDeclarators();
            if (declarators.length == 1 && declarators[0] instanceof ICPPASTFunctionDeclarator) {
                ICPPASTFunctionDeclarator declarator = (ICPPASTFunctionDeclarator) declarators[0];
                IBinding binding = declarator.getName().resolveBinding();
                if (binding instanceof ICPPConstructor) {
                    return (ICPPConstructor) binding;
                }
            }
        }
        if (member instanceof ICPPASTFunctionDefinition) {
            ICPPASTFunctionDefinition def = (ICPPASTFunctionDefinition) member;
            IBinding binding = def.getDeclarator().getName().resolveBinding();
            if (binding instanceof ICPPConstructor) {
                return (ICPPConstructor) binding;
            }
        }
        return null;
    }

    private static void addImplicitDefaultConstrRef(ClassDeclarationReference classDeclRef, RefMap refs) throws DOMException {
        IASTName helperNode = (IASTName) classDeclRef.getASTNode();
        IncludatorFile file = classDeclRef.getFile();
        ICPPClassType binding = classDeclRef.getClassBinding();
        ConstructorDeclarationReference addedConstrRef = addMatchingConstrRef(DEFAULT_CONSTRUCTOR_MATCHER, helperNode, file, refs, binding);
        addedConstrRef.setIsDefinitionName();
        addImplicitMemberVarConstructors(addedConstrRef, refs);
        addImplicitBaseClassConstructors(addedConstrRef, refs);
    }

    private static class DefaultConstructorMatcher implements CtorMatcher {

        @Override
        public boolean matches(ICPPConstructor ctor) throws DOMException {
            return isDefaultConstructor(ctor);
        }
    }

    private static class CopyConstructorMatcher implements CtorMatcher {

        @Override
        public boolean matches(ICPPConstructor ctor) throws DOMException {
            return isCopyConstructor(ctor, ctor.getClassOwner());
        }
    }

    public static ICPPConstructor getConstructorBinding(ICPPClassType classType, IIndex index, IASTDeclarator declarator) {
        return CPPSemantics.findImplicitlyCalledConstructor(classType, declarator.getInitializer(), declarator.getName());
    }
}
