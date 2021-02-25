package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;


public class MemberFunctionInstance extends AbstractTemplateInstance {

    private MemberFunctionInstance(UnresolvedNameInfo unresolvedName, IASTFunctionDefinition definition, AbstractResolvedNameInfo parent,
                                   ASTAnalyzer analyzer) throws TemplatorException {
        super(unresolvedName, definition, parent, analyzer);

        if (getParent() instanceof TemplateInstance) {
            templateParameters = ((TemplateInstance) getParent()).getDefinition().getTemplateParameters();
        }
    }

    protected static MemberFunctionInstance __create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        IBinding resolvedMemberInstance = unresolvedName.getBinding();

        IASTFunctionDefinition functionDefinition = analyzer.getFunctionDefinition(resolvedMemberInstance);

        parent = createParent(unresolvedName, parent, analyzer);

        return new MemberFunctionInstance(unresolvedName, functionDefinition, parent, analyzer);
    }

    @Override
    public IASTFunctionDefinition getDefinition() {
        return (IASTFunctionDefinition) definition;
    }

    @Override
    protected void doPostResolving(AbstractResolvedNameInfo subNameInfo) throws TemplatorException {}

    @Override
    public void navigateTo() {
        EclipseUtil.navigateToNode(getDefinition().getDeclarator().getName());
    }
}
