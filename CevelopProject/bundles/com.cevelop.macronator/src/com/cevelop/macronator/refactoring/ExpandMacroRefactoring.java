package com.cevelop.macronator.refactoring;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.cevelop.macronator.common.LocalExpansion;
import com.cevelop.macronator.quickassist.SelectionResolver;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class ExpandMacroRefactoring extends CRefactoring {

    public static final String             ID = "com.cevelop.macronator.plugin.ExpandMacroRefactoring";
    private HashMap<IFile, TextFileChange> changeMap;

    public ExpandMacroRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);

    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        if (!isMacroDefinitionSelected()) {
            return RefactoringStatus.createFatalErrorStatus("No macro definition selected");
        }
        return super.checkInitialConditions(pm);
    }

    private boolean isMacroDefinitionSelected() {
        IASTName selectedName = new SelectionResolver(getAST(), selectedRegion).getSelectedName();
        return selectedName != null && selectedName.getBinding() instanceof IMacroBinding && selectedName.isDefinition();
    }

    @Override
    protected RefactoringStatus checkFinalConditions(IProgressMonitor subProgressMonitor, CheckConditionsContext checkContext) throws CoreException,
            OperationCanceledException {
        return super.checkFinalConditions(subProgressMonitor, checkContext);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        // do nothing
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        IASTName macroName = new SelectionResolver(getAST(), selectedRegion).getSelectedName();
        IASTNode macroNode = macroName.getParent();
        IIndexBinding binding = getIndex().findBinding(macroName);
        IIndexName[] references = getIndex().findReferences(binding);
        changeMap = new HashMap<>();
        createMacroDefintitionRemovalChange(macroNode, binding);
        for (IIndexName reference : references) {
            createMacroExpansionChange(reference);
        }
        return createCompositeChange(macroName.toString());
    }

    private CompositeChange createCompositeChange(String macroName) {
        CompositeChange result = new CompositeChange("Expand " + macroName);
        for (Change change : changeMap.values()) {
            result.add(change);
        }
        return result;
    }

    private IASTTranslationUnit getAST() {
        try {
            return getAST(tu, new NullProgressMonitor());
        } catch (OperationCanceledException | CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMacroExpansionChange(IIndexName reference) throws CoreException {
        IFile file = getIFile(reference.getFile().getLocation().getURI());
        ITranslationUnit translationUnit = getAST(file);
        IASTPreprocessorMacroExpansion macroExpansion = getMacoExpansion(reference, translationUnit);
        String localExpansion = new LocalExpansion(macroExpansion).getExpansion();
        if (!changeMap.containsKey(file)) {
            changeMap.put(file, createFileChange(file));
        }
        ReplaceEdit referenceEdit = new ReplaceEdit(macroExpansion.getFileLocation().getNodeOffset(), macroExpansion.getFileLocation()
                .getNodeLength(), localExpansion);
        changeMap.get(file).addEdit(referenceEdit);
    }

    private IASTPreprocessorMacroExpansion getMacoExpansion(IIndexName reference, ITranslationUnit translationUnit) throws CoreException {
        return translationUnit.getAST(getIndex(), ITranslationUnit.AST_PARSE_INACTIVE_CODE | ITranslationUnit.AST_SKIP_INDEXED_HEADERS)
                .getNodeSelector(null).findEnclosingMacroExpansion(reference.getNodeOffset(), reference.getNodeLength());
    }

    private void createMacroDefintitionRemovalChange(IASTNode macroNode, IIndexBinding binding) throws CoreException {
        IIndexName[] selectedNodeReference = getIndex().findNames(binding, IIndex.FIND_DEFINITIONS);
        IFile file = getIFile(selectedNodeReference[0].getFile().getLocation().getURI());
        if (!changeMap.containsKey(file)) {
            changeMap.put(file, createFileChange(file));
        }
        ReplaceEdit edit = new ReplaceEdit(macroNode.getFileLocation().getNodeOffset(), macroNode.getFileLocation().getNodeLength(), "");
        changeMap.get(file).addEdit(edit);

    }

    private TextFileChange createFileChange(IFile file) {
        TextFileChange fileChange = new TextFileChange(file.getName(), file);
        fileChange.setEdit(new MultiTextEdit());
        return fileChange;
    }

    private ITranslationUnit getAST(IFile referenceFile) {
        ITranslationUnit translationUnit = CoreModelUtil.findTranslationUnit(referenceFile);
        if (translationUnit == null) {
            translationUnit = CoreModel.getDefault().createTranslationUnitFrom(project, referenceFile.getLocationURI());
        }
        return translationUnit;
    }

    private static IFile getIFile(URI fileURI) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile[] files = root.findFilesForLocationURI(fileURI);
        if (files.length == 1) {
            return files[0];
        }
        for (IFile curFile : files) {
            if (fileURI.getPath().endsWith(curFile.getFullPath().toString())) {
                return curFile;
            }
        }
        return null;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        return null;
    }
}
