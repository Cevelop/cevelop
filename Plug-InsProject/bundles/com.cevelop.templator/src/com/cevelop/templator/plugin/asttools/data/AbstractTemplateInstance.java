package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;


public abstract class AbstractTemplateInstance extends AbstractResolvedNameInfo {

    protected TemplateArgumentMap        templateArgumentMap;
    protected ICPPASTTemplateParameter[] templateParameters;

    protected AbstractTemplateInstance(UnresolvedNameInfo unresolvedName, IASTDeclaration definition, AbstractResolvedNameInfo parent,
                                       ASTAnalyzer analyzer) throws TemplatorException {
        this(unresolvedName, definition, parent, analyzer, true);
        templateParameters = new ICPPASTTemplateParameter[0];
    }

    protected AbstractTemplateInstance(UnresolvedNameInfo unresolvedName, IASTDeclaration definition, AbstractResolvedNameInfo parent,
                                       ASTAnalyzer analyzer, boolean setArgumentMap) throws TemplatorException {
        super(unresolvedName, definition, parent, analyzer);

        if (setArgumentMap) {
            // binding is not a specialization in all cases (in an alias template for example)
            if (getBinding() instanceof ICPPSpecialization) {
                ICPPSpecialization specialization = (ICPPSpecialization) getBinding();
                this.templateArgumentMap = TemplateArgumentMap.copy(specialization.getTemplateParameterMap());
            }
        }
    }

    protected static AbstractTemplateInstance __create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        NameTypeKind type = unresolvedName.getType();

        AbstractTemplateInstance createdInstance = null;
        if (type == NameTypeKind.FUNCTION_TEMPLATE || type == NameTypeKind.CLASS_TEMPLATE || type == NameTypeKind.METHOD_TEMPLATE ||
            type == NameTypeKind.VARIABLE_TEMPLATE || type == NameTypeKind.ALIAS_TEMPLATE ||
            type == NameTypeKind.UNKNOWN_MEMBER_ALIAS_TEMPLATE_INSTANCE) {
            createdInstance = TemplateInstance.__create(unresolvedName, parent, analyzer);
        } else if (type == NameTypeKind.METHOD) {
            createdInstance = MemberFunctionInstance.__create(unresolvedName, parent, analyzer);
        }
        return createdInstance;
    }

    public ICPPASTTemplateParameter[] getTemplateParameters() {
        return templateParameters;
    }

    @Override
    public TemplateArgumentMap getTemplateArgumentMap() {
        return templateArgumentMap;
    }
}
