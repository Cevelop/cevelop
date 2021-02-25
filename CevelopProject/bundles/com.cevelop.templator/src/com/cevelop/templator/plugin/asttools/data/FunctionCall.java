package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IFunction;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;


public class FunctionCall extends AbstractResolvedNameInfo {

    private static final TemplateArgumentMap emptyMap = new TemplateArgumentMap();

    protected FunctionCall(UnresolvedNameInfo unresolvedName, IASTFunctionDefinition definition, AbstractResolvedNameInfo parent,
                           ASTAnalyzer analyzer) {
        super(unresolvedName, definition, parent, analyzer);
        type = NameTypeKind.FUNCTION;
    }

    protected static FunctionCall __create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        IASTFunctionDefinition definition = analyzer.getFunctionDefinition(unresolvedName.getBinding());
        return new FunctionCall(unresolvedName, definition, parent, analyzer);
    }

    @Override
    public IFunction getBinding() {
        return (IFunction) binding;
    }

    @Override
    public IASTFunctionDefinition getDefinition() {
        return (IASTFunctionDefinition) definition;
    }

    @Override
    public IASTFunctionDefinition getFormattedDefinition() {
        return (IASTFunctionDefinition) formattedDefinition;
    }

    @Override
    protected void doPostResolving(AbstractResolvedNameInfo subNameInfo) throws TemplatorException {
        // nothing to do for functions for now
    }

    @Override
    public void navigateTo() {
        EclipseUtil.navigateToNode(getDefinition().getDeclarator().getName());
    }

    /** FunctionCall should also implement this method for convenience. */
    @Override
    public TemplateArgumentMap getTemplateArgumentMap() {
        return emptyMap;
    }
}
