package com.cevelop.intwidthfixator.refactorings.inversion;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringTickProvider;
import org.eclipse.text.edits.TextEditGroup;

import com.cevelop.intwidthfixator.IntwidthfixatorPlugin;
import com.cevelop.intwidthfixator.helpers.AbstractHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;
import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;
import com.cevelop.intwidthfixator.helpers.InversionHelper;
import com.cevelop.intwidthfixator.helpers.PositionHelper;
import com.cevelop.intwidthfixator.preferences.PropAndPrefHelper;
import com.cevelop.intwidthfixator.refactorings.Messages;
import com.cevelop.intwidthfixator.visitors.VisitorArgs;
import com.cevelop.intwidthfixator.visitors.cstdintvisitors.CstdintVisitor;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.VisitorReport;
import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


/**
 * @author tstauber
 */
public class InversionRefactoring extends SelectionRefactoring<InversionRefactoringInfo> {

    public InversionRefactoring(final ICElement element, final InversionRefactoringInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.InvRef_name;
    }

    protected boolean isTypeSelectedForRefactoring(final IASTNode node) {
        final String nameString = node.toString();
        final int typeWidth = InversionHelper.getWidthFromCstdint(nameString);
        return isInSelectionConsideringSignedness(nameString) && info.isTypeSelectedForRefactoring(typeWidth);
    }

    private boolean isInSelectionConsideringSignedness(final String nameString) {
        return (InversionHelper.isUnsigned(nameString) && info.refactor_unsigned) || (InversionHelper.isSigned(nameString) && info.refactor_signed);
    }

    protected Collection<VisitorReport<ProblemId>> findExtractableNodes(final IProgressMonitor pm) {
        final SubMonitor subMonitor = SubMonitor.convert(pm, "Finding fixed integer types", 100);
        final Collection<VisitorReport<ProblemId>> results = new ArrayList<>();
        try {
            getAST(getTranslationUnit(), subMonitor.split(4)).accept(new CstdintVisitor((result, ignored) -> {
                if (isInSelection(result.getNode()) && isTypeSelectedForRefactoring(result.getNode())) {
                    results.add(result);
                }
                subMonitor.worked(1);
            }, VisitorArgs.REPORT_CASTS, VisitorArgs.REPORT_FUNCTIONS, VisitorArgs.REPORT_TEMPLATES, VisitorArgs.REPORT_TYPEDEFS,
                    VisitorArgs.REPORT_VARIABLES));
        } catch (final CoreException ignored) {}
        return results;
    }

    private void replaceWithVariableWidthTypes(final Collection<VisitorReport<ProblemId>> declSpecs, final ModificationCollector collector,
            final IProgressMonitor monitor) {

        final SubMonitor subMonitor = SubMonitor.convert(monitor, "Creating variable width replacements", declSpecs.size() + 4);
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

            /* Handle functional style casts */
            if (checkerResult.getNode() instanceof ICPPASTName) {
                final ICPPASTName node = (ICPPASTName) checkerResult.getNode();
                if (woldBeInvertedToMultiWordType(node.toString())) {
                    if (node.getParent().getParent() instanceof ICPPASTFunctionCallExpression) {
                        specialReplaceNode(rewrite, editGroup, node, (ICPPASTExpression) node.getParent().getParent());
                        continue;
                    }
                }
            }
            /* Handle other single word type occurrences */
            else if (onlySingleWordTypesAllowed(checkerResult.getNode())) {
                final ICPPASTNamedTypeSpecifier node = (ICPPASTNamedTypeSpecifier) checkerResult.getNode();
                if (woldBeInvertedToMultiWordType(node.getName().toString())) {
                    if (node.getParent() instanceof ICPPASTSimpleTypeConstructorExpression) {
                        specialReplaceNode(rewrite, editGroup, node, (ICPPASTExpression) node.getParent());
                        continue;
                    }
                }
            }
            /* Handle everything else */
            replaceNode(checkerResult, rewrite, editGroup);
        }
    }

    private void specialReplaceNode(final ASTRewrite rewrite, final TextEditGroup editGroup, final IASTNode node,
            final ICPPASTExpression expression) {
        final ICPPNodeFactory factory = (ICPPNodeFactory) expression.getTranslationUnit().getASTNodeFactory();

        final ICPPASTCastExpression replacement = factory.newCastExpression(ICPPASTCastExpression.op_static_cast, createTypeId(node, factory),
                createInitValue(expression, factory));
        rewrite.replace(expression, replacement, editGroup);
    }

    private ICPPASTLiteralExpression createInitValue(final ICPPASTExpression expression, final ICPPNodeFactory factory) {

        IASTNode[] initializer = IASTNode.EMPTY_NODE_ARRAY;
        if (expression instanceof ICPPASTSimpleTypeConstructorExpression) {
            initializer = ((ICPPASTSimpleTypeConstructorExpression) expression).getInitializer().getChildren();
        } else if (expression instanceof ICPPASTFunctionCallExpression) {
            initializer = ((IASTFunctionCallExpression) expression).getArguments();
        }

        if (initializer.length > 0 && initializer[0] instanceof ICPPASTLiteralExpression) {
            return (ICPPASTLiteralExpression) initializer[0].copy();
        } else {
            return createDefaultLiteralExpression(factory);
        }
    }

    private ICPPASTLiteralExpression createDefaultLiteralExpression(final ICPPNodeFactory factory) {
        return factory.newLiteralExpression(IASTLiteralExpression.lk_integer_constant, "0");
    }

    private boolean woldBeInvertedToMultiWordType(final String name) {
        return hasSignednessModifier(name, getProject().getProject()) || isLongLong(name, getProject().getProject());
    }

    private ICPPASTTypeId createTypeId(final IASTNode node, final ICPPNodeFactory factory) {
        return factory.newTypeId(InversionHelper.convertToSimpleDeclSpecifier(node), factory.newDeclarator(factory.newName()));
    }

    private void replaceNode(final VisitorReport<ProblemId> checkerResult, final ASTRewrite rewrite, final TextEditGroup editGroup) {
        final IASTSimpleDeclSpecifier replacementNode = InversionHelper.convertToSimpleDeclSpecifier(checkerResult.getNode());
        if (replacementNode == null) {
            IntwidthfixatorPlugin.log("No replacement node could be created. Maybe there is no mapping for this width."); //$NON-NLS-1$
        } else {
            rewrite.replace(checkerResult.getNode(), replacementNode, editGroup);
        }
    }

    private boolean onlySingleWordTypesAllowed(final IASTNode node) {
        if (node instanceof ICPPASTNamedTypeSpecifier) {
            final ICPPASTNamedTypeSpecifier namedTypeSpec = (ICPPASTNamedTypeSpecifier) node;
            return namedTypeSpec.getParent() instanceof ICPPASTSimpleTypeConstructorExpression;
        }
        return false;
    }

    private boolean isLongLong(final String name, final IProject project) {
        return (InversionHelper.getWidthIdFromCstdint(name) == AbstractHelper.getWidthIdFromPreferenceId(IdHelper.P_LONGLONG_MAPPING, project));
    }

    private boolean hasSignednessModifier(final String name, final IProject project) {
        final boolean isUnsigned = InversionHelper.isUnsigned(name);
        return (InversionHelper.getWidthIdFromCstdint(name) == WidthId.WIDTH_8 && PropAndPrefHelper.getInstance().getString(
                IdHelper.P_CHAR_PLATFORM_SIGNED_UNSIGNED, project).equals(IdHelper.V_CHAR_PLATFORM_UNSIGNED) ^ isUnsigned) || isUnsigned;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.InvRef_descWithSection : Messages.InvRef_descWoSection;
        return new InversionRefactoringDescriptor(project.getProject().getName(), Messages.InvRef_name, comment, info);
    }

    @Override
    protected void collectModifications(final IProgressMonitor pm, final ModificationCollector collector) throws CoreException,
            OperationCanceledException {
        final SubMonitor subMonitor = SubMonitor.convert(pm, "Collecting modifications", 10);
        final Collection<VisitorReport<ProblemId>> nodesToReplace = findExtractableNodes(subMonitor.split(5));
        replaceWithVariableWidthTypes(nodesToReplace, collector, subMonitor.split(5));
    }

    @Override
    protected RefactoringTickProvider doGetRefactoringTickProvider() {
        return new RefactoringTickProvider(20, 20, 10, 0);
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
