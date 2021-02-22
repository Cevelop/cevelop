package com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;


public class C47InitializeMemVarsInRightOrderQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C47.getId())) {
            return Rule.C47 + ": Fix orders of Initializerchain";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {

        final IASTNode parentNode = markedNode.getParent();

        if (markedNode instanceof ICPPASTFunctionDeclarator && parentNode instanceof ICPPASTFunctionDefinition) {

            final IBinding funcBinding = ((ICPPASTFunctionDeclarator) markedNode).getName().resolveBinding();
            final ICPPASTFunctionDefinition constructorDefinition = (ICPPASTFunctionDefinition) parentNode;

            if (funcBinding instanceof ICPPConstructor) {

                ICPPClassType classType = ((ICPPConstructor) funcBinding).getClassOwner();

                final ICPPField[] members = classType.getDeclaredFields();
                final ICPPBase[] bases = classType.getBases();

                final List<ICPPASTConstructorChainInitializer> initializerMembers = Arrays.asList(constructorDefinition.getMemberInitializers());

                final List<ICPPASTConstructorChainInitializer> orderedInitializers = new ArrayList<>();

                orderIntializers(bases, (m, base) -> m.getMemberInitializerId().resolveBinding().getOwner().equals(base.getBaseClass()),
                        initializerMembers, orderedInitializers);

                orderIntializers(members, (m, member) -> m.getMemberInitializerId().resolveBinding().equals(member), initializerMembers,
                        orderedInitializers);

                if (orderedInitializers.size() == initializerMembers.size()) {

                    final ASTRewrite astRewrite = astRewriteStore.getASTRewrite(markedNode);

                    for (int i = 0; i < orderedInitializers.size(); ++i) {
                        if (!orderedInitializers.get(i).equals(initializerMembers.get(i))) {
                            astRewrite.replace(initializerMembers.get(i), orderedInitializers.get(i), null);
                        }
                    }
                }
            }
        }
    }

    private <T> void orderIntializers(final T[] initializers, final BiFunction<ICPPASTConstructorChainInitializer, T, Boolean> filter,
            final List<ICPPASTConstructorChainInitializer> initializerMembers, final List<ICPPASTConstructorChainInitializer> orderedInitializers) {

        for (final T initializer : initializers) {

            Optional<ICPPASTConstructorChainInitializer> foundMember = initializerMembers.stream().filter(m -> filter.apply(m, initializer))
                    .findFirst();

            if (foundMember.isPresent()) {
                orderedInitializers.add(foundMember.get());
            }
        }
    }
}
