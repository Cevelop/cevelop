/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.refactorings.extract;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTLiteralNode;
import org.eclipse.cdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.cdt.internal.ui.util.EditorUtility;
import org.eclipse.cdt.internal.ui.wizards.filewizard.NewSourceFileGenerator;
import org.eclipse.cdt.ui.CodeGeneration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.UIPlugin;

import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.ExtractToHeaderInfo;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


@SuppressWarnings("restriction")
public class ExtractToHeaderRefactoring extends SelectionRefactoring<ExtractToHeaderInfo> {

    private static final String           STRING         = "\"";
    private static final String           LINE_SEPARATOR = "line.separator";
    private static final String           INCLUDE        = "#include \"";
    private static final String           H              = ".h";
    private ICPPASTCompositeTypeSpecifier type;

    public ExtractToHeaderRefactoring(ICElement element, ExtractToHeaderInfo info) {
        super(element, info);
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.Extract_descWithSection : Messages.Extract_descWoSection;
        return new ExtractToHeaderRefactoringDescriptor(project.getProject().getName(), Messages.Extract_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        IASTTranslationUnit localunit = refactoringContext.getAST(tu, pm);
        IASTNode selectedNode = getSelectedNode(localunit);

        if (isTypeSelected(selectedNode)) {
            extractType(collector, localunit);
        } else if (isFreeFunctionSelected(selectedNode, localunit)) {
            extractFreeFunction(collector, localunit, selectedNode);
        } else {
            // TODO: Inform the user about the problem
            // status.addFatalError(Messages.ExtractRefactoring_NothingExtractableSelected);
        }
    }

    private boolean isTypeSelected(IASTNode selectedNode) {
        return getTypeAtSelection(selectedNode) != null;
    }

    private ICPPASTCompositeTypeSpecifier getTypeAtSelection(IASTNode selectedNode) {
        ICPPASTCompositeTypeSpecifier typeAncestor = TddHelper.getAncestorOfType(selectedNode, ICPPASTCompositeTypeSpecifier.class);
        if (typeAncestor != null) {
            return typeAncestor;
        }
        ICPPASTCompositeTypeSpecifier typeChild = TddHelper.getChildofType(selectedNode, ICPPASTCompositeTypeSpecifier.class);
        return typeChild;
    }

    private boolean isFreeFunctionSelected(IASTNode node, IASTTranslationUnit localunit) {
        IASTNode functionAncestor = TddHelper.getAncestorOfType(node, ICPPASTFunctionDefinition.class);
        return functionAncestor != null;
    }

    private void extractFreeFunction(ModificationCollector collector, IASTTranslationUnit localunit, IASTNode selectedNode) throws CModelException,
            CoreException {
        IASTNode topNode = TddHelper.getTopAncestorOfType(selectedNode, ICPPASTTemplateDeclaration.class);
        if (topNode == null) {
            topNode = TddHelper.getAncestorOfType(selectedNode, ICPPASTFunctionDefinition.class);
        }
        ICPPASTFunctionDefinition funcNode = TddHelper.getChildofType(topNode, ICPPASTFunctionDefinition.class);
        IASTName nameNode = funcNode.getDeclarator().getName();
        String name = new String(nameNode.getSimpleID()) + H;

        ASTRewrite rewrite = collector.rewriterForTranslationUnit(localunit);
        ASTLiteralNode newInclude = new ASTLiteralNode((INCLUDE + name + STRING + System.getProperty(LINE_SEPARATOR)));
        if (createNewFile(topNode, name)) {
            moveCode(localunit, topNode, name, rewrite, newInclude);
        }
    }

    private void moveCode(IASTTranslationUnit localunit, IASTNode topNode, String name, ASTRewrite rewrite, ASTLiteralNode newInclude) {
        rewrite.remove(topNode, null);
        if (!hasIncludeStatement(localunit, name)) {
            IASTNode firstNodeOfUnit = localunit.getDeclarations()[0];
            rewrite.insertBefore(localunit, firstNodeOfUnit, newInclude, null);
        }
    }

    private void extractType(ModificationCollector collector, IASTTranslationUnit localunit) throws CoreException, CModelException {

        IASTNode enclosingTypeSpec = findTypeSpecifier(localunit);
        CPPASTSimpleDeclaration dec = null;
        if (enclosingTypeSpec instanceof CPPASTSimpleDeclaration) {
            dec = (CPPASTSimpleDeclaration) enclosingTypeSpec;
        } else {
            dec = (CPPASTSimpleDeclaration) ((ICPPASTTemplateDeclaration) enclosingTypeSpec).getDeclaration();
        }

        ASTRewrite rewrite = collector.rewriterForTranslationUnit(localunit);
        CPPASTCompositeTypeSpecifier typespec = (CPPASTCompositeTypeSpecifier) dec.getDeclSpecifier();
        String name = new String(typespec.getName().getSimpleID()) + H;
        ASTLiteralNode newInclude = new ASTLiteralNode((INCLUDE + name + STRING + System.getProperty(LINE_SEPARATOR)));
        // TODO
        //      CreateFileChange fileChange = null;
        //      collector.addFileChange(fileChange);
        //      IncludeInsertionUtil.createIncludeIfNotJetIncluded(ast, headerName, false);
        //      collector.addChange(change);
        if (createNewFile(enclosingTypeSpec, name)) {
            moveCode(localunit, enclosingTypeSpec, name, rewrite, newInclude);
            IPath newFilePath = new Path(getPath(tu.getPath(), name));
            EditorUtility.openInEditor(CoreModel.getDefault().create(newFilePath));
            EditorUtility.openInEditor(tu.getResource());
        }
    }

    private boolean hasIncludeStatement(IASTTranslationUnit localunit, String name) {
        IASTPreprocessorStatement[] prepstmts = localunit.getAllPreprocessorStatements();
        for (IASTPreprocessorStatement stmt : prepstmts) {
            if (stmt instanceof IASTPreprocessorIncludeStatement) {
                IASTName includename = ((IASTPreprocessorIncludeStatement) stmt).getName();
                if (name.equals(new String(includename.getSimpleID()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean createNewFile(IASTNode dec, final String filename) throws CoreException, CModelException {
        IPath newFilePath = new Path(getPath(tu.getPath(), filename));
        if (tu.getPath().equals(newFilePath)) {
            return false;
        }
        if (CoreModel.getDefault().create(newFilePath) != null) {
            if (shouldOverwriteOnUserRequest(filename)) {
                createFile(dec, newFilePath);
                return true;
            }
        } else {
            createFile(dec, newFilePath);
            return true;
        }
        return false;
    }

    private void createFile(IASTNode dec, IPath newFilePath) throws CoreException, CModelException {
        IFile newFile = NewSourceFileGenerator.createHeaderFile(newFilePath, true, new NullProgressMonitor());
        if (newFile == null) {
            return;
        }
        ITranslationUnit fNewFileTU = (ITranslationUnit) CoreModel.getDefault().create(newFile);
        if (fNewFileTU == null) {
            return;
        }
        String lineDelimiter = StubUtility.getLineDelimiterUsed(fNewFileTU);
        String content = CodeGeneration.getHeaderFileContent(getTemplate(), fNewFileTU, null, dec.getRawSignature(), lineDelimiter);
        if (content == null) {
            return;
        }
        fNewFileTU.getBuffer().setContents(content.toCharArray());
        fNewFileTU.save(new NullProgressMonitor(), true);
    }

    protected boolean shouldOverwriteOnUserRequest(final String name) {
        final Container<Boolean> answer = new Container<>();
        Runnable r = () -> {
            Shell shell = UIPlugin.getDefault().getWorkbench().getWorkbenchWindows()[0].getShell();
            boolean createnew = MessageDialog.openQuestion(shell, Messages.ExtractRefactoringQuestion_OverwriteFile, NLS.bind(
                    Messages.ExtractRefactoringFileExists, name));
            answer.setObject(createnew);
        };
        PlatformUI.getWorkbench().getDisplay().syncExec(r);
        return answer.getObject();
    }

    private IASTNode findTypeSpecifier(IASTTranslationUnit localunit) {
        IASTNode selectedNode = getSelectedNode(localunit);
        type = getTypeAtSelection(selectedNode);
        CPPASTCompositeTypeSpecifier outertype = TddHelper.getAncestorOfType(type.getParent(), CPPASTCompositeTypeSpecifier.class);
        while (outertype != null) {
            type = outertype;
            outertype = TddHelper.getAncestorOfType(type.getParent(), CPPASTCompositeTypeSpecifier.class);
        }
        if (type == null) {
            throw new OperationCanceledException(Messages.ExtractRefactoringErrorNoType);
        }
        IASTNode result = TddHelper.getAncestorOfType(type, ICPPASTTemplateDeclaration.class);
        if (result == null && type.getParent() instanceof IASTSimpleDeclaration) {
            result = type.getParent();
        }
        return result;
    }

    private IASTNode getSelectedNode(IASTTranslationUnit localunit) {
        int offset = selectedRegion.getOffset();
        int length = selectedRegion.getLength();
        IASTNode selectedNode = localunit.getNodeSelector(null).findNode(offset, length);
        if (selectedNode == null) {
            selectedNode = localunit.getNodeSelector(null).findEnclosingNode(offset, length);
        }
        if (selectedNode == null) {
            throw new OperationCanceledException(Messages.ExtractRefactoringErrorNoNodeSelected);
        }
        return selectedNode;
    }

    private Template getTemplate() {
        return StubUtility.getFileTemplatesForContentTypes(new String[] { CCorePlugin.CONTENT_TYPE_CXXHEADER }, null)[0];
    }

    private String getPath(IPath oldpath, String newFilename) {
        String oldpathstring = oldpath.toString();
        String newFilenamePath = oldpathstring.replaceAll("\\w*.\\w*$", "") + newFilename;
        return newFilenamePath;
    }
}
