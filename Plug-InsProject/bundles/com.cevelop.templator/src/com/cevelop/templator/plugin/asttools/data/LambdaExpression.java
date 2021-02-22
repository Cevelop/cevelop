package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;


public class LambdaExpression extends AbstractResolvedNameInfo {

    protected LambdaExpression(IASTName resolvingName, IBinding binding, NameTypeKind type, IASTDeclaration definition,
                               AbstractResolvedNameInfo parent, ASTAnalyzer analyzer) {
        super(resolvingName, binding, type, definition, parent, analyzer);
    }

    private static final TemplateArgumentMap emptyMap = new TemplateArgumentMap();

    protected static LambdaExpression __create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        IASTName resolvingName = unresolvedName.getResolvingName();
        IASTSimpleDeclaration definition = ASTTools.findFirstAncestorByType(resolvingName, IASTSimpleDeclaration.class);
        return new LambdaExpression(resolvingName, unresolvedName.getBinding(), unresolvedName.getType(), definition, parent, analyzer);
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
