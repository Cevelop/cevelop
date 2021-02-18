package com.cevelop.conanator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.osgi.service.prefs.BackingStoreException;

import com.cevelop.conanator.buildinfomanager.ConanBuildInfoManager;
import com.cevelop.conanator.utility.ConanNotFoundDialog;
import com.cevelop.conanator.utility.ConanProfilePreferenceUtility;
import com.cevelop.conanator.utility.MessageConsoleUtility;


public class InstallHandler extends AbstractHandler {

    private final IWorkbenchWindow window;

    public InstallHandler() {
        this.window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IProject selectedProject = getSelectedProject();

        if (selectedProject == null) {
            Activator.logError("Unable to run 'conan install': No project selected");
            return null;
        }

        ConanCliCommand command = new ConanCliCommand(getCommandArguments(selectedProject));

        if (!command.canExecute()) {
            IPath executable = promptUserForExecutable();

            if (executable == null) return null;

            command.setExecutable(executable);
        }

        MessageConsole msgConsole = MessageConsoleUtility.getConsole(MessageConsoleUtility.CONSOLE_NAME);
        msgConsole.clearConsole();
        MessageConsoleUtility.showConsole(msgConsole);

        command.setWorkingDirectory(selectedProject.getLocation());
        command.showCommand(true);
        command.executeAsync(msgConsole.newMessageStream(), msgConsole.newMessageStream(), (successful) -> {
            if (!successful) return;

            File cbiFile = selectedProject.getLocation().append("conanbuildinfo.txt").toFile();

            if (cbiFile.exists()) {
                try {
                    ConanBuildInfoManager.install(selectedProject, cbiFile);
                } catch (IOException | BuildException | BackingStoreException e) {
                    Activator.log(e);
                }
            } else {
                Activator.logError("File does not exist: " + cbiFile.getAbsolutePath());
            }
        });

        return null;
    }

    private IProject getSelectedProject() {
        ISelection selection = window.getSelectionService().getSelection();

        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object firstElement = ((IStructuredSelection) selection).getFirstElement();
            IResource resource = Platform.getAdapterManager().getAdapter(firstElement, IResource.class);

            if (resource != null) {
                return resource.getProject();
            }
        }

        return null;
    }

    private String[] getCommandArguments(IProject project) {
        List<String> commandArguments = new ArrayList<>(Arrays.asList("install", "--generator", "txt", "--build=missing"));
        String conanProfile = ConanProfilePreferenceUtility.getActiveProfile(project);

        if (!conanProfile.isEmpty()) {
            commandArguments.add("--profile");
            commandArguments.add(conanProfile);
        }

        commandArguments.add(project.getLocation().toOSString());

        return commandArguments.toArray(new String[commandArguments.size()]);
    }

    private IPath promptUserForExecutable() {
        ConanNotFoundDialog dialog = new ConanNotFoundDialog(window.getShell());

        if (dialog.open() == Window.OK) {
            return dialog.getConanPath();
        }

        return null;
    }
}
