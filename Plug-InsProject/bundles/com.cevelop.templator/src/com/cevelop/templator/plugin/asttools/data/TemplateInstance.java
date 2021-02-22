package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.resolving.PostResolver;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;


public class TemplateInstance extends AbstractTemplateInstance {

    protected TemplateInstance(UnresolvedNameInfo unresolvedName, ICPPASTTemplateDeclaration templateDefinition, AbstractResolvedNameInfo parent,
                               ASTAnalyzer analyzer) throws TemplatorException {
        this(unresolvedName, templateDefinition, parent, analyzer, true);

        templateParameters = getDefinition().getTemplateParameters();
    }

    protected TemplateInstance(UnresolvedNameInfo unresolvedName, ICPPASTTemplateDeclaration templateDefinition, AbstractResolvedNameInfo parent,
                               ASTAnalyzer analyzer, boolean setArgumentMap) throws TemplatorException {
        super(unresolvedName, templateDefinition, parent, analyzer, setArgumentMap);
    }

    protected static TemplateInstance __create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        IBinding resolvedTemplateInstance = unresolvedName.getBinding();

        ICPPASTTemplateDeclaration templateDeclaration;
        if (unresolvedName.getType() == NameTypeKind.FUNCTION || unresolvedName.getType() == NameTypeKind.METHOD) {
            IASTFunctionDefinition functionDefinition = analyzer.getFunctionDefinition(resolvedTemplateInstance);
            templateDeclaration = ASTTools.findFirstAncestorByType(functionDefinition, ICPPASTTemplateDeclaration.class, 2);

        } else if ((unresolvedName.getType() == NameTypeKind.FUNCTION_TEMPLATE || unresolvedName.getType() == NameTypeKind.CLASS_TEMPLATE ||
                    unresolvedName.getType() == NameTypeKind.METHOD_TEMPLATE || unresolvedName.getType() == NameTypeKind.VARIABLE_TEMPLATE ||
                    unresolvedName.getType() == NameTypeKind.ALIAS_TEMPLATE) && resolvedTemplateInstance instanceof ICPPTemplateInstance) {
                        templateDeclaration = analyzer.getTemplateDeclaration(resolvedTemplateInstance);
                    } else {
                        return null;
                    }
        return new TemplateInstance(unresolvedName, templateDeclaration, parent, analyzer);
    }

    @Override
    protected void doPostResolving(AbstractResolvedNameInfo subNameInfo) throws TemplatorException {
        if (subNameInfo instanceof TemplateInstance) {
            PostResolver.replaceClassTemplateParameters((TemplateInstance) subNameInfo, this, analyzer);
        }
    }

    @Override
    public void navigateTo() {
        IASTDeclaration declaration = getDefinition().getDeclaration();
        if (declaration instanceof ICPPASTFunctionDefinition) {
            ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) declaration;
            EclipseUtil.navigateToNode(functionDefinition.getDeclarator().getName());
        } else if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
            IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
            if (declSpecifier instanceof IASTCompositeTypeSpecifier) {
                IASTCompositeTypeSpecifier typeSpecifier = (IASTCompositeTypeSpecifier) declSpecifier;
                EclipseUtil.navigateToNode(typeSpecifier.getName());
            }
        }
    }

    @Override
    public ICPPASTTemplateDeclaration getDefinition() {
        return (ICPPASTTemplateDeclaration) definition;
    }
}
