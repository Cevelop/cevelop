package com.cevelop.templator.plugin.asttools.resolving.nametype;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownMemberClass;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownMemberClassInstance;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.resolving.ClassTemplateMemberResolver;
import com.cevelop.templator.plugin.logger.TemplatorException;


public class QualifiedNameSegment extends TypeNameToType {

    protected QualifiedNameSegment lastSegment;

    public QualifiedNameSegment(IASTName segmentName) {
        super(segmentName);
    }

    public QualifiedNameSegment(IASTName typeName, AbstractResolvedNameInfo currentContext, QualifiedNameSegment lastSegment) {
        super(typeName, currentContext);
        this.lastSegment = lastSegment;
    }

    public QualifiedNameSegment(IASTName typeName, ICPPSpecialization specialization, AbstractResolvedNameInfo parent) {
        super(typeName, specialization, parent);
    }

    public QualifiedNameSegment resolve(NameTypeDeducer typeDeducer) throws TemplatorException {
        ICPPSpecialization lastNameSegmentSpecialization = null;

        if (lastSegment != null) {
            lastNameSegmentSpecialization = lastSegment.getSpecialization();
        }

        IBinding unresolvedCurrentSegmentType = ASTTools.getUltimateBindingType(typeName);
        if (unresolvedCurrentSegmentType instanceof ICPPDeferredClassInstance) {
            TypeNameToType currentSegment = typeDeducer.getType(new TypeNameToType(typeName), currentContext);
            setAll(currentSegment);
        } else if (unresolvedCurrentSegmentType instanceof ICPPUnknownMemberClassInstance &&
                   lastNameSegmentSpecialization instanceof ICPPClassSpecialization) {
            // pass parent and not currentContextParent. The member itself is defined in the currentContextParent
            // but the qualified name is still defined in parent and uses template arguments from parent
            IBinding memberClassInstance = ClassTemplateMemberResolver.instantiateMember(unresolvedCurrentSegmentType,
                    (ICPPClassSpecialization) lastNameSegmentSpecialization, typeName, currentContext);
            if (memberClassInstance instanceof ICPPAliasTemplateInstance) {
                TypeNameToType aliasType = getAliasTemplateInstanceType((ICPPAliasTemplateInstance) memberClassInstance, typeName, currentContext,
                        lastSegment, typeDeducer);
                memberClassInstance = aliasType.getSpecialization();
            }

            AbstractResolvedNameInfo newContext = typeDeducer.createNewContext(typeName, memberClassInstance, currentContext, true);
            setCurrentContext(newContext);
            TypeNameToType classMemberResult = typeDeducer.getMemberType(memberClassInstance, this, currentContext);
            setAll(classMemberResult);
        } else if (unresolvedCurrentSegmentType instanceof ICPPUnknownMemberClass &&
                   lastNameSegmentSpecialization instanceof ICPPClassSpecialization) {
            IBinding memberBinding = ClassTemplateMemberResolver.instantiateMember(unresolvedCurrentSegmentType,
                    (ICPPClassSpecialization) lastNameSegmentSpecialization, typeName, currentContext);
            TypeNameToType memberResult = typeDeducer.getMemberType(memberBinding, lastSegment, currentContext);
            setAll(memberResult);
        } else if (unresolvedCurrentSegmentType instanceof ICPPTemplateParameter) {
            ICPPTemplateArgument argument = currentContext.getArgument((ICPPTemplateParameter) unresolvedCurrentSegmentType);
            IType typeValue = argument.getTypeValue();
            setType(typeValue);
            setCompletelyResolved(true);
            if (typeValue instanceof IBinding) {
                AbstractResolvedNameInfo newContext = typeDeducer.createNewContext(typeName, (IBinding) typeValue, currentContext, true);
                setCurrentContext(newContext);
                if (typeValue instanceof ICPPSpecialization) {
                    setSpecialization((ICPPSpecialization) typeValue);
                }

            }
        }
        return this;
    }

    private TypeNameToType getAliasTemplateInstanceType(ICPPAliasTemplateInstance aliasInstance, IASTName typeName, AbstractResolvedNameInfo parent,
            QualifiedNameSegment lastSegment, NameTypeDeducer typeDeducer) throws TemplatorException {
        ICPPAliasTemplate aliasTemplate = aliasInstance.getTemplateDefinition();
        IASTName aliasDefinitionName = parent.getAnalyzer().getDefinition(aliasTemplate);
        IASTName aliasedTypeName = ASTTools.getUsingTypeFromDefinitionName(aliasDefinitionName);

        TypeNameToType result = null;
        if (aliasedTypeName.getParent() instanceof ICPPASTQualifiedName) {
            AbstractResolvedNameInfo contextFromLastSegment = lastSegment.getCurrentContext();

            AbstractResolvedNameInfo aliasContext = typeDeducer.createNewContext(typeName, aliasInstance, contextFromLastSegment, false);

            result = typeDeducer.getType((ICPPASTQualifiedName) aliasedTypeName.getParent(), aliasContext);
        }
        return result;
    }

    public QualifiedNameSegment getLastSegment() {
        return lastSegment;
    }
}
