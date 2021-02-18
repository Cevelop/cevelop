package com.cevelop.templator.plugin.asttools.resolving;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.InstantiationContext;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPFunctionSet;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.NameTypeKind;
import com.cevelop.templator.plugin.asttools.data.UnresolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;


public final class ClassTemplateMemberResolver {

    private ClassTemplateMemberResolver() {}

    public static ICPPSpecialization resolveClassTemplateMember(UnresolvedNameInfo memberName, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        ICPPClassSpecialization resolvedOwner = resolveOwner(memberName, parent);
        NameTypeKind type = memberName.getType();

        ICPPSpecialization finalResolvedMember = null;

        if (type == NameTypeKind.DEFERRED_METHOD) {
            finalResolvedMember = resolveClassTemplateMemberFunction(memberName, resolvedOwner, parent);
        } else if (type == NameTypeKind.DEFERRED_MEMBER_CLASS_INSTANCE) {} else if (type == NameTypeKind.UNKNOWN_MEMBER_CLASS) {

        } else if (type == NameTypeKind.DEFERRED_MEMBER_CLASS_INSTANCE) {} else if (type == NameTypeKind.UNKNOWN_MEMBER_ALIAS_TEMPLATE_INSTANCE) {}

        return finalResolvedMember;
    }

    public static ICPPSpecialization resolveMemberAliasTemplateInstance(UnresolvedNameInfo aliasunresolvedName, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        IBinding originalBinding = aliasunresolvedName.getBinding();
        if (originalBinding instanceof ICPPAliasTemplateInstance) {
            ICPPAliasTemplateInstance aliasInstance = (ICPPAliasTemplateInstance) originalBinding;
            ICPPAliasTemplate aliasTemplate = aliasInstance.getTemplateDefinition();

            IASTName aliasDefinitionName = parent.getAnalyzer().getDefinition(aliasTemplate);
            IASTName aliasedTypeName = ASTTools.getUsingTypeFromDefinitionName(aliasDefinitionName);
            IBinding aliasedBinding = aliasedTypeName.resolveBinding();
            aliasunresolvedName.setResolvingName(aliasedTypeName);
            aliasunresolvedName.setBinding(aliasedBinding, true);

            PostResolver.resolveToFinalBinding(aliasunresolvedName, parent, parent.getAnalyzer());
        }

        IBinding finalBinding = aliasunresolvedName.getBinding();
        if (finalBinding instanceof ICPPSpecialization) {
            return (ICPPSpecialization) finalBinding;
        }

        return null;
    }

    public static ICPPClassSpecialization resolveOwner(UnresolvedNameInfo memberunresolvedName, AbstractResolvedNameInfo parent)
            throws TemplatorException {
        IASTName resolvingName = memberunresolvedName.getResolvingName();
        IBinding memberBinding = memberunresolvedName.getBinding();

        IBinding owner = memberBinding.getOwner();
        if (owner instanceof ICPPTemplateParameter) {
            // get the argument and instantiate it if necessary
        } else if (owner instanceof ICPPUnknownBinding) {
            ASTAnalyzer analyzer = parent.getAnalyzer();

            IASTName definitionName = analyzer.getTypeDeducer().getDefinitionForName(resolvingName);
            if (definitionName != null) {
                definitionName = analyzer.extractResolvingName(definitionName, true, false).getTypeName();
                AbstractResolvedNameInfo ownerInfo = analyzer.getTypeDeducer().createNewContext(definitionName, parent, true);
                if (ownerInfo != null) {
                    IBinding specialization = ownerInfo.getBinding();
                    if (specialization instanceof ICPPClassSpecialization) {
                        return (ICPPClassSpecialization) specialization;
                    }
                }
            }
        } else if (owner instanceof ICPPClassSpecialization) {
            return (ICPPClassSpecialization) owner;
        }

        return null;
    }

    public static ICPPSpecialization resolveClassTemplateMemberFunction(UnresolvedNameInfo unresolvedName, ICPPClassSpecialization resolvedOwner,
            AbstractResolvedNameInfo parent) throws TemplatorException {
        IASTName resolvingName = unresolvedName.getResolvingName();
        IBinding originalBinding = unresolvedName.getBinding();

        if (resolvedOwner != null) {
            IBinding specializedMemberFunction = ClassTemplateMemberResolver.instantiateMember(originalBinding, resolvedOwner, resolvingName, parent);
            if (specializedMemberFunction instanceof ICPPSpecialization && specializedMemberFunction instanceof ICPPMethod) {
                return (ICPPSpecialization) specializedMemberFunction;
            }
        }
        return null;
    }

    public static IBinding instantiateMember(IBinding deferredMemberBinding, ICPPClassSpecialization ownerInstance, IASTName point,
            AbstractResolvedNameInfo parent) throws TemplatorException {
        IBinding specializedMember = null;
        CPPSemantics.pushLookupPoint(point);
        try {
            /*
             * do NOT call fieldOwnerInstance.specializeMember(unresolvedName.getBinding(), resolvingName); this would
             * add the unknown binding (unresolvedName.getBinding()) with the newly created specialized member to a map
             * for caching. So our plug-in would maybe cause different behavior for CDT or other plug-ins. If
             * specializeMember gets called with the same unknown binding exactly this specialized member would be
             * returned even if the current context is different (for example the current context is <int> but in the
             * map it is saved for <double>). And our plug-in should not change the CDT behavior.
             * Also do not use CPPTemplates.createSpecialization(fieldOwnerInstance, originalBinding, resolvingName)
             * because then the function type is wrong and the specialized binding is set to the originalBinding which
             * is unknown and you have no possibility to get the function definition for this binding.
             * lost hours because of the last paragraph: 6
             */
            InstantiationContext context = new InstantiationContext(parent.getTemplateArgumentMap(), 0, ownerInstance);
            specializedMember = CPPTemplates.instantiateBinding(deferredMemberBinding, context, 255);
        } catch (DOMException e) {
            throw new TemplatorException("Could not instantiate the class template member " + deferredMemberBinding.getName(), e);
        } finally {
            CPPSemantics.popLookupPoint();
        }

        if (specializedMember instanceof CPPFunctionSet) {
            ICPPFunction[] candidateBindings = ((CPPFunctionSet) specializedMember).getBindings();
            specializedMember = FunctionCallResolver.resolveDeferredCall(point, candidateBindings, ownerInstance, parent, parent.getAnalyzer());
        }

        return specializedMember;
    }

}
