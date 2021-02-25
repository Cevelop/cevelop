package com.cevelop.ctylechecker.quickfix.dynamic.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringStarter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.eclipse.ltk.ui.refactoring.resource.RenameResourceWizard;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.ctylechecker.common.FileUtil;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


@SuppressWarnings("restriction")
public class FileEndingRenameRefactoring extends AbstractRenameRefactoring {

    IFile file;

    public FileEndingRenameRefactoring(IFile pFile) {
        file = pFile;
    }

    private Boolean checkPreConditions() {
        return file != null;
    }

    public Boolean performFor(IRule pRule) throws BadLocationException {
        if (checkPreConditions()) {
            RenameResourceProcessor processor = new RenameResourceProcessor(file);
            processor.setNewResourceName(applyTransformations(pRule));
            processor.setUpdateReferences(true);
            Refactoring refactoring = new ProcessorBasedRefactoring(processor);
            if (file.getName().equals(processor.getNewResourceName()) || processor.getNewResourceName().isEmpty()) {
                RenameResourceWizard wizard = new RenameResourceWizard(file);
                RefactoringStarter starter = new RefactoringStarter();
                Boolean performedWithSucess = starter.activate(wizard, new Shell(), "Resource Rename", RefactoringSaveHelper.SAVE_ALL);
                reindexAndRefreshProject();
                return performedWithSucess;
            } else {
                processor.setNewResourceName(postProcessName(processor.getNewResourceName()));
                CheckConditionsOperation checkCondOp = new CheckConditionsOperation(refactoring, CheckConditionsOperation.ALL_CONDITIONS);
                CreateChangeOperation createChangeOp = new CreateChangeOperation(checkCondOp, RefactoringStatus.WARNING);
                PerformChangeOperation performChangeOp = new PerformChangeOperation(createChangeOp);
                try {
                    file.getProject().getWorkspace().run(performChangeOp, new NullProgressMonitor());
                    reindexAndRefreshProject();
                    return true;
                } catch (CoreException ex) {
                    CtylecheckerRuntime.showMessage("Info", "Resource Rename refactoring failed. Cause: " + ex.getMessage());
                }
            }
        }
        return false;
    }

    String postProcessName(String transformedName) {
        System.out.println(file.getName());
        Optional<String> oName = FileUtil.extractFileName(file);
        if (oName.isPresent()) {
            transformedName = oName.get() + "." + transformedName;
            return transformedName;
        }
        return file.getName();
    }

    private void reindexAndRefreshProject() {
        try {
            CCorePlugin.getIndexManager().reindex(CoreModel.getDefault().create(file.getProject()));
            file.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        } catch (CoreException e) {
            CtylecheckerRuntime.log(e);
        }
    }

    public Boolean performDefault(IFile pFile) throws BadLocationException {
        return performFor(null);
    }

    @Override
    protected Optional<String> getOriginalName() {
        return Optional.ofNullable(file.getFileExtension());
    }

}
