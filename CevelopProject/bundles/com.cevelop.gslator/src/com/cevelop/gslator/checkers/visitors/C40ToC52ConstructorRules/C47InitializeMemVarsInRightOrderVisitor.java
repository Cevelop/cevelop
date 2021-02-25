package com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C47InitializeMemVarsInRightOrderVisitor extends BaseVisitor {

    public C47InitializeMemVarsInRightOrderVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(final IASTDeclarator declarator) {

        IASTName declName = declarator.getName();

        if (declName == null) {
            return super.visit(declarator);
        }

        IBinding declBinding = declName.resolveBinding();
        IASTNode parentNode = declarator.getParent();

        if (declBinding instanceof ICPPConstructor && parentNode instanceof ICPPASTFunctionDefinition && nodeHasNoIgnoreAttribute(this, declarator)) {

            final ICPPClassType classType = ((ICPPConstructor) declBinding).getClassOwner();
            final Iterator<ICPPField> fieldsItr = Arrays.asList(classType.getDeclaredFields()).iterator();
            final Iterator<ICPPBase> basesItr = Arrays.asList(classType.getBases()).iterator();

            final ICPPASTConstructorChainInitializer[] memberInitializers = ((ICPPASTFunctionDefinition) parentNode).getMemberInitializers();

            if (checkCtorDelegation(memberInitializers, classType)) {
                return super.visit(declarator);
            }

            boolean initializingMembers = false;

            final Set<ICPPClassType> initializedClasses = new HashSet<>();
            final Set<ICPPField> initializedFields = new HashSet<>();

            for (final ICPPASTConstructorChainInitializer memberInitializer : memberInitializers) {

                final IBinding originalInitializer = memberInitializer.getMemberInitializerId().resolveBinding();

                boolean hasError = false;
                boolean hasDuplicates = false;

                if (originalInitializer instanceof ICPPConstructor) {
                    ICPPClassType originalClass = ((ICPPConstructor) originalInitializer).getClassOwner();

                    hasDuplicates = checkDuplicates(initializedClasses, originalClass);
                    hasError = advanceInitializer(basesItr, base -> !originalClass.equals(base.next().getBaseClass())) || initializingMembers;
                }

                if (originalInitializer instanceof ICPPField) {
                    initializingMembers = true;
                    ICPPField originalMember = (ICPPField) originalInitializer;

                    hasDuplicates = checkDuplicates(initializedFields, originalMember);
                    hasError = advanceInitializer(fieldsItr, field -> !originalMember.equals(field.next()));
                }

                if (hasDuplicates) {
                    return super.visit(declarator);
                }

                if (hasError) {
                    checker.reportProblem(ProblemId.P_C47, declarator);
                    return super.visit(declarator);
                }
            }
        }

        return super.visit(declarator);
    }

    private <T> boolean checkDuplicates(final Set<T> checkedInitializers, T initializer) {
        if (checkedInitializers.contains(initializer)) {
            return true;
        }

        checkedInitializers.add(initializer);
        return false;
    }

    private <T> boolean advanceInitializer(final Iterator<T> memberIterator, final Predicate<Iterator<T>> condition) {

        do {
            if (!memberIterator.hasNext()) {
                return true;
            }
        } while (condition.test(memberIterator));

        return false;
    }

    private boolean checkCtorDelegation(final ICPPASTConstructorChainInitializer[] memberInitializers, final ICPPClassType originalClass) {

        if (memberInitializers.length > 0) {
            final IBinding firstMemberInitializer = memberInitializers[0].getMemberInitializerId().resolveBinding();

            if (firstMemberInitializer instanceof ICPPConstructor) {

                if (((ICPPConstructor) firstMemberInitializer).getClassOwner().equals(originalClass)) {
                    return true;
                }
            }
        }
        return false;
    }
}
