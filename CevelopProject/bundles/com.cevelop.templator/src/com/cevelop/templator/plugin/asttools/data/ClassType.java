package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;


public class ClassType extends AbstractResolvedNameInfo {

    private static final TemplateArgumentMap emptyMap = new TemplateArgumentMap();

    protected ClassType(UnresolvedNameInfo unresolvedName, IASTDeclaration definition, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer) {
        super(unresolvedName, definition, parent, analyzer);
    }

    protected static ClassType __create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        IASTSimpleDeclaration definition = analyzer.getClassDefinition(unresolvedName.getBinding());
        return new ClassType(unresolvedName, definition, parent, analyzer);
    }

    @Override
    protected void doPostResolving(AbstractResolvedNameInfo subNameInfo) throws TemplatorException {}

    @Override
    public void navigateTo() {
        EclipseUtil.navigateToNode(getDefinition());
    }

    @Override
    public TemplateArgumentMap getTemplateArgumentMap() {
        return emptyMap;
    }
}
