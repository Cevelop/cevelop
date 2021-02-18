package com.cevelop.ctylechecker.reporting;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;


public class GenerateReportHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        final List<ITranslationUnit> sourceFiles = getSelectedSourceFiles(selection);
        final String report = new Report(sourceFiles).print();
        openInEditor(report);
        return null;
    }

    private static void openInEditor(final String report) {
        try {
            final Path file = Files.createTempFile("report", null);
            file.toFile().deleteOnExit();
            try (final FileWriter fileWriter = new FileWriter(file.toFile())) {
                fileWriter.append(report);
            }
            final IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toUri());
            final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IDE.openEditorOnFileStore(page, fileStore);
        } catch (final PartInitException | IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<ITranslationUnit> getSelectedSourceFiles(final ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            final List<ICElement> list = ((IStructuredSelection) selection).toList();
            return list.stream().filter(is(ICElement.class)).flatMap(GenerateReportHandler::extractFile).collect(Collectors.toList());
        }
        return Collections.<ITranslationUnit>emptyList();
    }

    private static <T> Predicate<T> is(final Class<T> type) {
        return obj -> {
            return obj != null && type.isInstance(obj);
        };
    }

    private static Stream<ITranslationUnit> extractFile(final ICElement resource) {
        return isTranslationUnit(resource) ? Stream.of((ITranslationUnit) resource) : getChildren(resource);
    }

    private static Stream<ITranslationUnit> getChildren(final ICElement resource) {
        final List<ITranslationUnit> files = new ArrayList<>();
        try {
            resource.accept(child -> {
                if (isTranslationUnit(child)) {
                    files.add((ITranslationUnit) child);
                    return false;
                }
                return true;
            });
        } catch (final CoreException e) {
            e.printStackTrace();
        }
        return files.stream();
    }

    private static boolean isTranslationUnit(final ICElement resource) {
        return resource.getElementType() == ICElement.C_UNIT;
    }
}
