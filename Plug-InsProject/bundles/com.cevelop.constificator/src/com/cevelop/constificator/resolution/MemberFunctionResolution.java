package com.cevelop.constificator.resolution;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


public class MemberFunctionResolution implements Resolution<ICPPASTFunctionDefinition> {

    private final ConstificatorQuickFix fParent;

    public MemberFunctionResolution(ConstificatorQuickFix parent) {
        fParent = parent;
    }

    @Override
    public void handle(IASTNode node, IIndex index, ASTRewriteCache cache, ICPPASTFunctionDefinition ancestor) {
        ICPPASTFunctionDeclarator declarator = Cast.as(ICPPASTFunctionDeclarator.class, node);
        if (declarator == null) {
            return;
        }

        final IIndexBinding adapted = index.adaptBinding(declarator.getName().resolveBinding());
        final ICProject project = declarator.getTranslationUnit().getOriginatingTranslationUnit().getCProject();
        Set<ICPPASTFunctionDeclarator> declarators = new HashSet<>();

        try {
            IIndexName[] names = index.findNames(adapted, IIndex.FIND_DECLARATIONS_DEFINITIONS);

            for (IIndexName declaration : names) {
                IIndexFileLocation file = declaration.getFile().getLocation();
                ITranslationUnit tu = CoreModelUtil.findTranslationUnitForLocation(file, project);
                IASTTranslationUnit ast = cache.getASTTranslationUnit(tu);
                IASTName currentName = ast.getNodeSelector(null).findName(declaration.getNodeOffset(), declaration.getNodeLength());
                ICPPASTFunctionDeclarator currentNode;
                if ((currentNode = Relation.getAncestorOf(ICPPASTFunctionDeclarator.class, currentName)) != null) {
                    declarators.add(currentNode);
                }
            }
        } catch (CoreException e) {

        }

        fParent.setHasMutipleChanges(declarators.size() > 1);

        for (ICPPASTFunctionDeclarator functionDeclarator : declarators) {
            ASTRewrite rewrite = cache.getASTRewrite(functionDeclarator.getTranslationUnit().getOriginatingTranslationUnit());
            if (rewrite == null) {
                continue;
            }
            ICPPASTFunctionDeclarator copy = functionDeclarator.copy();
            copy.setConst(true);
            rewrite.replace(functionDeclarator, copy, null);
        }

    }

}
