package com.cevelop.constificator.resolution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public class FunctionParameterResolution implements Resolution<ICPPASTParameterDeclaration> {

    private final ConstificatorQuickFix fParent;

    public FunctionParameterResolution(ConstificatorQuickFix parent) {
        fParent = parent;
    }

    @Override
    public void handle(IASTNode node, IIndex index, ASTRewriteCache cache, ICPPASTParameterDeclaration ancestor) {
        ICPPParameter parameter = Cast.as(ICPPParameter.class, ancestor.getDeclarator().getName().resolveBinding());
        handleFunctionParameter(node, cache, ancestor, parameter);
    }

    private void handleFunctionParameter(IASTNode node, ASTRewriteCache cache, ICPPASTParameterDeclaration ancestor, ICPPParameter parameter) {
        CPPParameter functionParameter = Cast.as(CPPParameter.class, parameter);
        if (functionParameter == null) {
            return;
        }

        IASTDeclarator declarator = Relation.getAncestorOf(ICPPASTFunctionDeclarator.class, node);
        if (declarator == null) {
            return;
        }

        IIndexBinding binding = cache.getIndex().adaptBinding(declarator.getName().resolveBinding());
        ICProject project = declarator.getTranslationUnit().getOriginatingTranslationUnit().getCProject();
        Set<ICPPASTFunctionDeclarator> decls = findDeclarations(cache, binding, project);
        int rewriteCount = rewriteDeclarators(node, cache, ancestor, functionParameter, decls);
        fParent.setHasMutipleChanges(rewriteCount > 1);
    }

    private int rewriteDeclarators(IASTNode node, ASTRewriteCache cache, ICPPASTParameterDeclaration ancestor, CPPParameter functionParameter,
            Set<ICPPASTFunctionDeclarator> decls) {
        int rewriteCount = 0;
        int parameterIndex = functionParameter.getParameterPosition();
        for (ICPPASTFunctionDeclarator decl : decls) {
            ASTRewrite rewrite = cache.getASTRewrite(decl.getTranslationUnit().getOriginatingTranslationUnit());
            if (rewrite == null) {
                continue;
            }
            ICPPASTParameterDeclaration currentParameter = decl.getParameters()[parameterIndex];

            IASTNode original = null;
            IASTNode replacement = null;

            if (node instanceof ICPPASTName && Relation.isDescendendOf(ICPPASTDeclSpecifier.class, node)) {
                node = Relation.getAncestorOf(ICPPASTDeclSpecifier.class, node);
            }

            if (node instanceof ICPPASTDeclSpecifier) {
                original = currentParameter.getDeclSpecifier();

                if (((IASTDeclSpecifier) original).isConst()) {
                    continue;
                }

                replacement = original.copy();
                ((IASTDeclSpecifier) replacement).setConst(true);
            } else if (node instanceof IASTPointer) {
                IASTDeclarator markedDeclarator = ancestor.getDeclarator();
                List<IASTPointerOperator> pointerOps = Arrays.asList(markedDeclarator.getPointerOperators());
                int pointerIndex = pointerOps.indexOf(node);
                original = currentParameter.getDeclarator().getPointerOperators()[pointerIndex];

                if (((IASTPointer) original).isConst()) {
                    continue;
                }

                replacement = original.copy();
                ((IASTPointer) replacement).setConst(true);
            }

            rewrite.replace(original, replacement, null);
            ++rewriteCount;
        }
        return rewriteCount;
    }

    private Set<ICPPASTFunctionDeclarator> findDeclarations(ASTRewriteCache cache, IIndexBinding binding, ICProject project) {
        Set<ICPPASTFunctionDeclarator> decls = new HashSet<>();

        try {
            IIndexName[] declarations = cache.getIndex().findNames(binding, IIndex.FIND_DECLARATIONS_DEFINITIONS);

            for (IIndexName declaration : declarations) {
                IIndexFileLocation file = declaration.getFile().getLocation();
                ITranslationUnit tu = CoreModelUtil.findTranslationUnitForLocation(file, project);
                IASTTranslationUnit ast = cache.getASTTranslationUnit(tu);
                IASTName currentName = ast.getNodeSelector(null).findName(declaration.getNodeOffset(), declaration.getNodeLength());
                ICPPASTFunctionDeclarator currentNode;
                if ((currentNode = Relation.getAncestorOf(ICPPASTFunctionDeclarator.class, currentName)) != null) {
                    decls.add(currentNode);
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return decls;
    }
}
