package com.cevelop.intwidthfixator.refactorings.conversion;

import static ch.hsr.ifs.iltis.core.functional.Functional.as;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringTickProvider;
import org.eclipse.text.edits.TextEditGroup;

import com.cevelop.intwidthfixator.helpers.ConversionHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;
import com.cevelop.intwidthfixator.helpers.Includes;
import com.cevelop.intwidthfixator.helpers.PositionHelper;
import com.cevelop.intwidthfixator.refactorings.Messages;
import com.cevelop.intwidthfixator.visitors.VisitorArgs;
import com.cevelop.intwidthfixator.visitors.intvisitors.IntVisitor;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.VisitorReport;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeInsertionUtil;
import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


/**
 * @author tstauber
 */
public class ConversionRefactoring extends SelectionRefactoring<ConversionInfo> {

    public ConversionRefactoring(final ICElement element, final ConversionInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.ConvRef_name;
    }

    protected Collection<VisitorReport<ProblemId>> findExtractableNodes(final IProgressMonitor pm) {
        final SubMonitor subMonitor = SubMonitor.convert(pm, "Finding unfixed integer types", 100);
        final Collection<VisitorReport<ProblemId>> results = new ArrayList<>();
        try {
            getAST(getTranslationUnit(), subMonitor.split(4)).accept(new IntVisitor((result, ignored) -> {
                if (isInSelection(result.getNode()) && getRefactoringInfo().isTypeSelectedForRefactoring(as(result.getNode()))) {
                    results.add(result);
                }
                subMonitor.split(1).worked(1);
            }, VisitorArgs.REPORT_CASTS, VisitorArgs.REPORT_FUNCTIONS, VisitorArgs.REPORT_TEMPLATES, VisitorArgs.REPORT_TYPEDEFS,
                    VisitorArgs.REPORT_VARIABLES));
        } catch (final CoreException ignored) {}
        return results;
    }

    private void replaceWithFixedWidthTypes(final Collection<VisitorReport<ProblemId>> declSpecs, final ModificationCollector collector,
            final IProgressMonitor monitor) {

        final SubMonitor subMonitor = SubMonitor.convert(monitor, "Creating fixed width replacements", declSpecs.size() + 4);
        for (final VisitorReport<ProblemId> checkerResult : declSpecs) {
            subMonitor.worked(1);
            final ASTRewrite rewrite = collector.rewriterForTranslationUnit(checkerResult.getNode().getTranslationUnit());
            final TextEditGroup editGroup;

            switch (checkerResult.getProblemId()) {
            case CASTS:
                editGroup = new TextEditGroup("Casts"); //$NON-NLS-1$
                break;
            case FUNCTION:
                editGroup = new TextEditGroup("Functions"); //$NON-NLS-1$
                break;
            case TEMPLATE:
                editGroup = new TextEditGroup("Templates"); //$NON-NLS-1$
                break;
            case TYPEDEF:
                editGroup = new TextEditGroup("Typedefs & Usings"); //$NON-NLS-1$
                break;
            case VARIABLES:
                editGroup = new TextEditGroup("Variables"); //$NON-NLS-1$
                break;
            default:
                editGroup = null;
            }

            final ICPPASTNamedTypeSpecifier replacement = ConversionHelper.convertToNamedTypeSpecifier((ICPPASTSimpleDeclSpecifier) checkerResult
                    .getNode());
            rewrite.replace(checkerResult.getNode(), replacement, editGroup);
        }
    }

    @Override
    protected void collectModifications(final IProgressMonitor pm, final ModificationCollector collector) throws CoreException,
            OperationCanceledException {
        final IASTTranslationUnit ast = getAST(getTranslationUnit(), new NullProgressMonitor());

        IncludeInsertionUtil.createIncludeIfNotYetIncluded(ast, Includes.CSTDINT, true).ifPresent(collector::addChange);

        final SubMonitor subMonitor = SubMonitor.convert(pm, "Collecting modifications", 10);
        final Collection<VisitorReport<ProblemId>> declSpecsToReplace = findExtractableNodes(subMonitor.split(5));
        replaceWithFixedWidthTypes(declSpecsToReplace, collector, subMonitor.split(5));
    }

    @Override
    protected RefactoringTickProvider doGetRefactoringTickProvider() {
        return new RefactoringTickProvider(20, 20, 10, 0);
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.InvRef_descWithSection : Messages.InvRef_descWoSection;
        return new ConversionRefactoringDescriptor(project.getProject().getName(), Messages.InvRef_name, comment, getRefactoringInfo());
    }

    @Override
    protected boolean isInSelectionHook(final IASTNode node) {
        final int nodeStart = getNodeStart(node);
        final int nodeEnd = getNodeEnd(node);
        final int selStart = getSelectionStart();
        final int selEnd = getSelectionEnd();

        if (nodeStart >= selStart && nodeEnd <= selEnd) {
            return true;
        } else if (nodeEnd < selStart || nodeStart < selEnd) {
            return false;
        } else if (node instanceof ICPPASTSimpleDeclSpecifier) {
            final IRegion region = PositionHelper.getRegionOfTypeInFile((ICPPASTSimpleDeclSpecifier) node);
            return region.getOffset() >= selStart && region.getOffset() + region.getLength() <= selEnd;
        } else {
            return false;
        }
    }
}
