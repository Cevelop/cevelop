package com.cevelop.templator.plugin.asttools.resolving.nametype;

import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.UnresolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;


/** Methods to decuce the type of an {@code IASTName} recursively. */
public class NameTypeDeducer {

    private ASTAnalyzer analyzer;

    public NameTypeDeducer(ASTAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public TypeNameToType getType(IASTName name, AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName definitionName = getDefinitionForName(name);
        if (definitionName != null) {
            return getTypeFromDefinition(definitionName, parent);
        }
        return null;
    }

    public TypeNameToType getTypeFromDefinition(IASTName definitionName, AbstractResolvedNameInfo parent) throws TemplatorException {
        return getType(new TypeNameToType(definitionName), parent);
    }

    public IASTName getDefinitionForName(IASTName originalName) throws TemplatorException {
        if (originalName instanceof ICPPASTTemplateId) {
            return originalName;
        }

        IASTNode parentOfOriginalName = originalName.getParent();
        IASTName definitionName = null;
        IBinding definitionBinding = null;
        if (parentOfOriginalName instanceof ICPPASTFieldReference) {
            ICPPASTExpression fieldOwner = ((ICPPASTFieldReference) parentOfOriginalName).getFieldOwner();
            if (fieldOwner instanceof IASTIdExpression) {
                IASTName ownerName = ((IASTIdExpression) fieldOwner).getName();
                IBinding fieldOwnerBinding = ownerName.resolveBinding();
                if (fieldOwnerBinding instanceof IVariable) {
                    definitionBinding = fieldOwnerBinding;
                }
            }
        } else {
            definitionBinding = originalName.resolveBinding();
        }
        try {
            definitionName = analyzer.getDefinition(getInnerMostBinding(definitionBinding));
        } catch (TemplatorException e) {
            // do nothing, it is another name than we expected
        }

        return definitionName;
    }

    public TypeNameToType getType(TypeNameToType original, AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName originalName = original.getTypeName();
        IBinding originalBinding = originalName.resolveBinding();

        TypeNameToType result = new TypeNameToType(original);

        if (originalName instanceof ICPPASTQualifiedName) {
            result = getType((ICPPASTQualifiedName) originalName, parent);
        } else if (originalBinding instanceof ITypedef) {
            result = getType(originalName, (ITypedef) originalBinding, parent);
        }

        result = resolveTypeName(result, parent);
        return result;
    }

    public TypeNameToType getType(IASTName originalName, ITypedef alias, AbstractResolvedNameInfo parent) throws TemplatorException {
        TypeNameToType result = new TypeNameToType(originalName);

        // typedef _Base::other mytype
        // aliasDefinition = mytype
        // aliasTypeLastName = other
        // aliasTypeParent = _Base::other
        IASTName aliasDefinition = analyzer.getDefinition(alias);

        IASTName aliasTypeLastName = ASTTools.getAliasedTypeFromDefinitionName(aliasDefinition);
        IASTNode aliasTypeParent = aliasTypeLastName.getParent();
        result.setTypeName(aliasTypeLastName);

        // If the aliased type is a qualified name, the other parts need to be instantiated in order to find out the
        // type for the last segment (for aliasTypeLastName)
        if (aliasTypeParent instanceof ICPPASTQualifiedName) {
            ICPPASTQualifiedName qualifiedAliasName = (ICPPASTQualifiedName) aliasTypeParent;
            result = getType(qualifiedAliasName, parent);
        } else {
            result = resolveTypeName(result, parent);
        }

        return result;
    }

    public TypeNameToType resolveTypeName(TypeNameToType typeNameToBinding, AbstractResolvedNameInfo parent) throws TemplatorException {
        if (!typeNameToBinding.isCompletelyResolved()) {
            // Does not matter what we pass to the ctor as originalName since this name is only used in the UI.
            IASTName typeName = typeNameToBinding.getTypeName();
            UnresolvedNameInfo unresolvedNameInfo = new UnresolvedNameInfo(typeName);
            unresolvedNameInfo.setResolvingName(typeName);
            unresolvedNameInfo.setBinding(typeName.resolveBinding(), true);

            if (unresolvedNameInfo.isRelevant() && !unresolvedNameInfo.getType().isReplacementNeeded()) {
                AbstractResolvedNameInfo newParentName = AbstractResolvedNameInfo.create(unresolvedNameInfo, parent, analyzer);
                typeNameToBinding.setCurrentContext(newParentName);
                if (newParentName.getBinding() instanceof ICPPSpecialization) {
                    typeNameToBinding.setSpecialization((ICPPSpecialization) newParentName.getBinding());
                    typeNameToBinding.setCompletelyResolved(true);
                }
            } else {
                IBinding binding = unresolvedNameInfo.getBinding();
                if (binding instanceof ICPPTemplateParameter) {
                    typeNameToBinding.setType(parent.getArgument((ICPPTemplateParameter) binding).getTypeValue());
                    typeNameToBinding.setCompletelyResolved(true);
                }
            }

        }

        return typeNameToBinding;
    }

    public TypeNameToType getType(ICPPASTQualifiedName qualifiedName, AbstractResolvedNameInfo parent) throws TemplatorException {

        ICPPASTNameSpecifier[] qualifierNameSegments = qualifiedName.getAllSegments();
        QualifiedNameSegment currentSegment = null;
        for (ICPPASTNameSpecifier qualifierNameSegment : qualifierNameSegments) {
            ICPPASTNameSpecifier nameSegmentSpecifier = qualifierNameSegment;

            if (!(nameSegmentSpecifier instanceof IASTName)) {
                throw new TemplatorException("Could not resolve " + nameSegmentSpecifier.getRawSignature() + " inside the qualified name " +
                                             qualifiedName.getRawSignature());
            }

            IASTName segmentName = (IASTName) nameSegmentSpecifier;
            currentSegment = new QualifiedNameSegment(segmentName, parent, currentSegment);
            currentSegment.resolve(this);
        }

        return currentSegment;
    }

    public TypeNameToType getMemberType(IBinding memberBinding, TypeNameToType ownerType, AbstractResolvedNameInfo parent) throws TemplatorException {
        if (memberBinding instanceof ICPPSpecialization) {
            ICPPSpecialization specializedMember = (ICPPSpecialization) memberBinding;
            IASTName memberDefinition = analyzer.getDefinition(getInnerMostBinding(specializedMember));
            // Since we instantiated a member in the ownerType.getSpecialization() (=fieldOwnerInstance)
            // the definition of this member is in the ownerType, so this is our new context
            // (= parent AbstractStatementInfo) since we are now in a new class
            TypeNameToType memberType = new TypeNameToType(memberDefinition, specializedMember);
            memberType.setCurrentContext(ownerType.getCurrentContext());
            memberType.setCompletelyResolved(true);
            TypeNameToType nestedType = getType(memberType, ownerType.getCurrentContext());
            return nestedType;
        }
        return null;
    }

    public static IBinding getInnerMostBinding(IBinding iBinding) {
        IBinding innerMost = iBinding;
        while (innerMost instanceof ICPPSpecialization) {
            innerMost = ((ICPPSpecialization) innerMost).getSpecializedBinding();
        }

        return innerMost;
    }

    public AbstractResolvedNameInfo createNewContext(IASTName resolvingName, AbstractResolvedNameInfo parent, boolean resolve)
            throws TemplatorException {
        return createNewContext(resolvingName, resolvingName.resolveBinding(), parent, resolve);
    }

    public AbstractResolvedNameInfo createNewContext(IASTName resolvingName, IBinding binding, AbstractResolvedNameInfo parent, boolean resolve)
            throws TemplatorException {
        UnresolvedNameInfo newContextunresolvedName = new UnresolvedNameInfo(resolvingName);
        newContextunresolvedName.setResolvingName(resolvingName);
        newContextunresolvedName.setBinding(binding, true);

        return AbstractResolvedNameInfo.create(newContextunresolvedName, parent, analyzer, resolve);
    }
}
