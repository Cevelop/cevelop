package com.cevelop.conanator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;

import com.cevelop.conanator.preferences.PreferenceConstants;


public class ConanCliCommand {

    private final String[] arguments;
    private IPath          executable;
    private IPath          workingDirectory;
    private boolean        showCommand = false;

    public ConanCliCommand(final String[] arguments) {
        this.arguments = arguments;
        executable = new Path(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CONAN_PATH));

        if (!fileIsExecutable(executable.toFile())) {
            Optional<java.nio.file.Path> conanInPath = getConanInPath();

            if (conanInPath.isPresent()) {
                executable = new Path(conanInPath.get().toString());
            }
        }
    }

    public void setExecutable(IPath executable) {
        this.executable = executable;
    }

    public void setWorkingDirectory(IPath directory) {
        workingDirectory = directory;
    }

    public void showCommand(boolean show) {
        showCommand = show;
    }

    public boolean canExecute() {
        return fileIsExecutable(executable.toFile());
    }

    /**
     * Executes conan with the given arguments in the given working directory (optional).
     * All output is written to the corresponding streams.
     * If showCommand was set to true, the first line in the out-stream will be the command
     * with all it's parameters and the absolute path it was executed in.
     * <p>
     * Note: Call {@link #canExecute()} before calling this method to make sure that the
     * executable can be found. If not, you can set the path manually with {@link #setExecutable(IPath)}.
     * </p>
     *
     * @param out
     * @param err
     * @return true if the command was executed successfully, false otherwise
     */
    public boolean execute(final OutputStream out, final OutputStream err) {
        try {
            CommandLauncher launcher = new CommandLauncher();
            launcher.showCommand(showCommand);
            Process process = launcher.execute(executable, arguments, new String[0], workingDirectory, new NullProgressMonitor());

            if (process == null) {
                Activator.logError(launcher.getErrorMessage());
                return false;
            }

            process.getOutputStream().close();
            launcher.waitAndRead(out, err, new NullProgressMonitor());

            if (process.exitValue() == 0) return true;
        } catch (CoreException | IOException e) {
            Activator.log(e);
        }

        return false;
    }

    /**
     * Does the same as the {@link #execute(OutputStream, OutputStream)} method, but in a different thread.
     * Instead of returning whether the command was executed successfully,
     * the callback containing said value as a parameter is executed (on the UI thread).
     * <p>
     * Note: Call {@link #canExecute()} before calling this method to make sure that the
     * executable can be found. If not, you can set the path manually with {@link #setExecutable(IPath)}.
     * </p>
     *
     * @param out
     * @param err
     * @param callback
     */
    public void executeAsync(final OutputStream out, final OutputStream err, final Consumer<Boolean> callback) {
        Executors.newSingleThreadExecutor().submit(() -> {
            boolean successful = execute(out, err);

            Display.getDefault().asyncExec(() -> callback.accept(successful));
        });
    }

    private boolean fileIsExecutable(File file) {
        return file.exists() && !file.isDirectory() && file.canExecute();
    }

    private Optional<java.nio.file.Path> getConanInPath() {
        return Stream.of(System.getenv("PATH"), System.getenv("Path")).filter(p -> p != null).flatMap(Pattern.compile(":")::splitAsStream).flatMap(
                p -> Stream.of(Paths.get(p, "conan"), Paths.get(p, "conan.exe"))).filter(p -> Files.exists(p) && Files.isExecutable(p)).findFirst();
    }
}
