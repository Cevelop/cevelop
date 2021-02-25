package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;


public class CPPASTTemplateIdPreservingOriginalNode extends CPPASTTemplateId {

    private IASTName originalTemplateName;

    public CPPASTTemplateIdPreservingOriginalNode(IASTName templateName) {
        super(deduceName(templateName));
        this.originalTemplateName = templateName;
    }

    private static IASTName deduceName(IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            return ((ICPPASTTemplateId) name).getTemplateName();
        } else {
            return name;
        }
    }

    @Override
    public IASTNode getOriginalNode() {
        return originalTemplateName.getOriginalNode();
    }
}
