
package com.cevelop.gslator.quickfixes.C30ToC37DestructorRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;

import com.cevelop.gslator.checkers.visitors.util.PointerFromDeclarationVisitor;
import com.cevelop.gslator.checkers.visitors.util.TypeIdVisitor;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;
import com.cevelop.gslator.utils.ASTHelper.SpecialFunction;
import com.cevelop.gslator.utils.OwnerInformation;


public abstract class C31_00DeleteOwnersInDestructorQuickFix extends RuleQuickFix {

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTCompositeTypeSpecifier struct = ASTHelper.getCompositeTypeSpecifier(markedNode);
        final List<IASTSimpleDeclaration> gslOwners = ASTHelper.collectGslOwners(ASTHelper.collectMemberVariables(struct));
        final List<OwnerInformation> ownerInformation = collectInformationOfOwners(struct, gslOwners);
        final IASTDeclaration destructor = getDestructor(struct);

        handleMarkedNode(hRewrite, struct, ownerInformation, destructor);
    }

    public List<OwnerInformation> collectInformationOfOwners(final ICPPASTCompositeTypeSpecifier struct,
            final List<IASTSimpleDeclaration> memberVars) {
        final List<OwnerInformation> result = new ArrayList<>();
        for (final IASTSimpleDeclaration memberVar : memberVars) {
            final OwnerInformation ownerInfo = new OwnerInformation(memberVar.getDeclarators()[0].getName());

            setOwningRef(memberVar, ownerInfo);

            //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
            final List<IASTNode> results = ASTHelper.findNames(astRewriteStore, ownerInfo.getName().resolveBinding(), CProjectUtil.getCProject(
                    CProjectUtil.getProject(ownerInfo.getName())), IIndex.FIND_ALL_OCCURRENCES, null);
            for (final IASTNode node : results) {
                findTypeId(ownerInfo, node);
            }

            result.add(ownerInfo);
        }
        return result;
    }

    private void findTypeId(final OwnerInformation ownerInfo, IASTNode node) {
        final TypeIdVisitor typeIdVisitor = new TypeIdVisitor();
        while (node != null && typeIdVisitor.getTypeIds().size() == 0) {
            node = node.getParent();
            if (node != null) {
                node.accept(typeIdVisitor);
                if (typeIdVisitor.getTypeIds().size() > 0) {
                    for (final IASTTypeId typeId : typeIdVisitor.getTypeIds()) {
                        if (typeId.getAbstractDeclarator() instanceof ICPPASTArrayDeclarator && isChildrenOfNewExpression(typeId)) {
                            ownerInfo.setOwnerOfArray(true);
                        }
                    }
                }
            }
        }
    }

    private void setOwningRef(final IASTSimpleDeclaration memberVar, final OwnerInformation ownerInfo) {
        final PointerFromDeclarationVisitor visitor = new PointerFromDeclarationVisitor();
        memberVar.accept(visitor);
        ownerInfo.setOwningRef(visitor.isOwningRef());
    }

    private IASTDeclaration getDestructor(final ICPPASTCompositeTypeSpecifier struct) {
        return ASTHelper.getFirstSpecialMemberFunction(struct, SpecialFunction.DefaultDestructor);
    }

    protected ICPPASTFunctionDefinition addMissingDeleteStatements(final ICPPASTFunctionDefinition destructor,
            final List<OwnerInformation> ownerInformation) {
        for (final OwnerInformation ownerInfo : ownerInformation) {
            ((ICPPASTCompoundStatement) destructor.getBody()).addStatement(ASTFactory.newDeleteStatement(ownerInfo.getName().copy(), ownerInfo
                    .isOwnerOfArray(), ownerInfo.isOwningRef()));
        }
        return destructor;
    }

    public boolean isChildrenOfNewExpression(IASTNode node) {
        while (node != null) {
            node = node.getParent();
            if (node instanceof ICPPASTNewExpression) {
                return true;
            }
        }
        return false;
    }

    abstract protected void handleMarkedNode(final ASTRewrite rewrite, ICPPASTCompositeTypeSpecifier struct, List<OwnerInformation> ownerInformation,
            IASTDeclaration destructor);

}
