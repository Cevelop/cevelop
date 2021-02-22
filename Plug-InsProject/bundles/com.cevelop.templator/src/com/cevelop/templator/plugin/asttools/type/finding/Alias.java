package com.cevelop.templator.plugin.asttools.type.finding;

import org.eclipse.cdt.core.dom.ast.IASTName;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.logger.TemplatorException;


public class Alias extends RelevantNameType {

    protected Alias(IASTName definitionName) {
        super(definitionName);
    }

    @Override
    protected IASTName getTypeFromDefinition() throws TemplatorException {
        return ASTTools.getAliasedTypeFromDefinitionName(definitionName);
    }

}
