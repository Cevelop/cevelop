package com.cevelop.templator.plugin.asttools.resolving;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.RelevantNameCache;
import com.cevelop.templator.plugin.asttools.data.UnresolvedNameInfo;
import com.cevelop.templator.plugin.asttools.type.finding.RelevantNameType;
import com.cevelop.templator.plugin.logger.TemplatorException;


public final class NameDeduction {

    private NameDeduction() {}

    public static UnresolvedNameInfo deduceName(IASTName originalName, boolean acceptUnknownBindings, boolean isStartingPoint,
            AbstractResolvedNameInfo parent, RelevantNameCache cache) throws TemplatorException {
        ICPPTemplateParameter templateParam = getTemplateParameter(originalName);
        if (templateParam != null) {
            ICPPTemplateArgument arg = parent.getTemplateArgumentMap().getArgument(templateParam);
            if (arg != null && arg.getTypeValue() instanceof CPPClassInstance) {
                CPPClassInstance ci = (CPPClassInstance) arg.getTypeValue();
                UnresolvedNameInfo unresolvedName = new UnresolvedNameInfo(originalName);
                unresolvedName.setBinding(ci, true);
                unresolvedName.setResolvingName(originalName);
                return unresolvedName;
            }
            return null;
        }
        return deduceName(originalName, acceptUnknownBindings, isStartingPoint, parent.getAnalyzer(), cache);
    }

    public static UnresolvedNameInfo deduceName(IASTName originalName, boolean acceptUnknownBindings, boolean isStartingPoint, ASTAnalyzer analyzer,
            RelevantNameCache cache) throws TemplatorException {
        RelevantNameType nameType = extractRelevantNameTypeFromName(originalName, acceptUnknownBindings, isStartingPoint, analyzer, cache);
        UnresolvedNameInfo unresolvedInfo = createUnresolvedNameInfo(originalName, nameType);
        return unresolvedInfo;
    }

    public static ICPPTemplateParameter getTemplateParameter(IASTName originalName) {
        IBinding binding = originalName.resolveBinding();
        ICPPTemplateParameter templateParam = null;
        if (binding instanceof ICPPTemplateParameter) {
            templateParam = (ICPPTemplateParameter) binding;
        } else if (binding instanceof ICPPParameter) {
            ICPPParameter param = (ICPPParameter) binding;
            if (param.getType() instanceof ICPPTemplateParameter) {
                templateParam = (ICPPTemplateParameter) param.getType();
            }
        } else if (binding instanceof ICPPVariable) {
            ICPPVariable var = (ICPPVariable) binding;
            if (var.getType() instanceof ICPPTemplateParameter) {
                templateParam = (ICPPTemplateParameter) var.getType();
            }
        }
        return templateParam;
    }

    private static RelevantNameType extractRelevantNameTypeFromName(IASTName originalName, boolean acceptUnknownBindings, boolean isStartingPoint,
            ASTAnalyzer analyzer, RelevantNameCache cache) throws TemplatorException {
        if (!ASTTools.isRelevantName(originalName)) {
            return null;
        }

        // first check if this exact binding has already been resolved and is cached
        RelevantNameType nameType = cache.getFor(originalName);
        if (nameType == null) {
            nameType = analyzer.extractResolvingName(originalName, acceptUnknownBindings, isStartingPoint);
            if (nameType != null && nameType.getTypeName() != null) {
                IBinding typeBinding = nameType.getTypeBinding();
                RelevantNameType cachedDeclaration = cache.getFor(typeBinding);
                if (cachedDeclaration != null) {
                    nameType = cachedDeclaration;
                }
            }
            cache.put(originalName.resolveBinding(), nameType);
        }
        return nameType;
    }

    private static UnresolvedNameInfo createUnresolvedNameInfo(IASTName originalName, RelevantNameType nameType) {
        UnresolvedNameInfo unresolvedNameInfo = new UnresolvedNameInfo(originalName);
        if (nameType == null) {
            return null;
        }
        IASTName resolvingName = nameType.getTypeName();
        if (resolvingName != null) {
            IBinding resolvedType = resolvingName.resolveBinding();
            if (resolvedType instanceof IVariable) {
                IType ultimateType = SemanticUtil.getUltimateType(((IVariable) resolvedType).getType(), false);
                if (ultimateType instanceof ICPPSpecialization) {
                    resolvedType = (IBinding) ultimateType;
                }
            }

            IBinding unwrappedResolvedType = null;
            // do not unwrap the type if it is an alias template
            if (!(resolvedType instanceof ICPPAliasTemplateInstance)) {
                unwrappedResolvedType = ASTTools.unwrapTypedef(resolvedType);
            } else {
                unwrappedResolvedType = resolvedType;
            }

            // determine if the binding is relevant for us, sets the type if relevant for us. If not == null
            unresolvedNameInfo.setResolvingName(resolvingName);
            unresolvedNameInfo.setNameType(nameType);
            unresolvedNameInfo.setBinding(unwrappedResolvedType, true);
            if (unresolvedNameInfo.isRelevant()) {
                return unresolvedNameInfo;
            }
        }
        return null;
    }
}
