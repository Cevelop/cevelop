package com.cevelop.templator.plugin.asttools.type.finding;

import org.eclipse.cdt.core.dom.ast.IASTName;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.logger.TemplatorException;


public class Parameter extends RelevantNameType {

    protected Parameter(IASTName definitionName) {
        super(definitionName);
    }

    @Override
    protected IASTName getTypeFromDefinition() throws TemplatorException {
        IASTName declSpecifier = ASTTools.getParameterTypeFromDefinitionName(definitionName);
        if (declSpecifier != null) {
            IASTName resolvingName = ASTTools.extractTemplateInstanceName(declSpecifier);
            if (ASTTools.isRelevantBindingIgnoreFunctions(resolvingName.resolveBinding())) {
                return declSpecifier;
            }
        }
        return null;
    }

}
