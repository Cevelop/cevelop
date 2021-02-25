package com.cevelop.tdd.refactorings.create.function.constructor;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompoundStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.tdd.helpers.FunctionCreationHelper;
import com.cevelop.tdd.helpers.ParameterHelper;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.ConstructorInfo;


public class CreateConstructorRefactoring extends SelectionRefactoring<ConstructorInfo> {

    public CreateConstructorRefactoring(final ICElement element, final ConstructorInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateConstructor_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateConstructor_descWithSection : Messages.CreateConstructor_descWoSection;
        return new CreateConstructorRefactoringDescriptor(project.getProject().getName(), Messages.CreateConstructor_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        if (!selection.isPresent()) {
            return;
        }
        IASTTranslationUnit localunit = refactoringContext.getAST(tu, pm);
        IASTName selectedNode = FunctionCreationHelper.getMostCloseSelectedNodeName(localunit, selection.get());
        if (selectedNode == null) {
            return;
        }
        ICPPASTCompositeTypeSpecifier type = getDefinitionScopeForName(localunit, selectedNode, refactoringContext);
        if (type == null) {
            IASTNode node = localunit.getNodeSelector(null).findEnclosingNodeInExpansion(selection.get().getOffset(), selection.get().getLength());
            if (node instanceof ICPPASTCompositeTypeSpecifier) {
                type = (ICPPASTCompositeTypeSpecifier) node;
            }
        }
        ICPPASTFunctionDefinition newFunction = getFunctionDefinition(selectedNode, info.typeName);
        if (newFunction == null) {
            return;
        }
        if (type == null) {
            final IASTNode parent = selectedNode.getParent();
            IASTNode insertionPoint;
            if (parent instanceof ICPPASTQualifiedName) {
                insertionPoint = TddHelper.getNestedInsertionPoint(localunit, (ICPPASTQualifiedName) parent, refactoringContext);
            } else {
                insertionPoint = localunit;
            }
            TddHelper.writeDefinitionTo(collector, insertionPoint, newFunction);
        } else {
            TddHelper.writeDefinitionTo(collector, type, newFunction);
        }
    }

    private ICPPASTFunctionDefinition getFunctionDefinition(IASTNode selectedName, String name) {
        CPPASTFunctionDeclarator funcdecl = new CPPASTFunctionDeclarator(new CPPASTName(name.toCharArray()));
        CPPASTDeclarator declarator = TddHelper.getAncestorOfType(selectedName, CPPASTDeclarator.class);
        if (declarator == null) {
            return null;
        }
        ParameterHelper.addTo(declarator, funcdecl);
        CPPASTNamedTypeSpecifier declspec = new CPPASTNamedTypeSpecifier();
        declspec.setName(new CPPASTName());
        CPPASTFunctionDefinition fd = new CPPASTFunctionDefinition(declspec, funcdecl, new CPPASTCompoundStatement());
        return fd;
    }

    private ICPPASTCompositeTypeSpecifier getDefinitionScopeForName(IASTTranslationUnit unit, IASTName name, CRefactoringContext context) {
        return TypeHelper.getTypeDefinitionOfVariable(unit, name, context);
    }
}
