/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.base;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FullIncludePath;
import com.cevelop.includator.helpers.AlgorithmScopeHelper;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Optimizer;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.FileStartingPoint;
import com.cevelop.includator.tests.helpers.ApplySuggestionRunnable;
import com.cevelop.includator.tests.helpers.StatusHelper;
import com.cevelop.includator.tests.mocks.FileMockSelection;
import com.cevelop.includator.tests.mocks.FirstFolderMockSelection;
import com.cevelop.includator.tests.mocks.IncludatorTestAction;
import com.cevelop.includator.tests.mocks.ProjectMockSelection;
import com.cevelop.includator.ui.ApplySuggestionsRunnable;
import com.cevelop.includator.ui.IncludatorResolutionGenerator;
import com.cevelop.includator.ui.actions.IncludatorAction;
import com.cevelop.includator.ui.actions.IncludatorJob;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.helpers.UIThreadSyncRunnable;


public abstract class IncludatorTest extends IncludatorBaseTest {

    public static final String NL = System.getProperty("line.separator");

    /*
     * (non-Javadoc)
     * 
     * Note that IncludatorTest.tearDown() can throw Exeptions (AssertionFailedError). Make sure, that whenever overloading teardown, super.tearDown()
     * should be encapsulated in a try block and following code should go to the finally block.
     * @see com.cevelop.includator.tests.base.IncludatorBaseTest#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        try {
            assertStatusOk();
        } finally {
            super.tearDown();
        }
    }

    protected void assertDeclarationReference(String expectedRefName, String expectedFileName, int expectedOffset, DeclarationReference reference)
            throws IOException {
        assertDeclRefName(expectedRefName, reference);
        Assert.assertEquals(expectedFileName, reference.getFile().getProjectRelativePath());
        final IASTFileLocation fileLocation = reference.getFileLocation();
        Assert.assertEquals(expectedOffset, adaptActualOffset(getCurrentProject().getFile(fileLocation.getFileName()), fileLocation.getNodeOffset()));
    }

    protected void assertDeclRefDependencyTargetFile(String expectedFileName, List<DeclarationReferenceDependency> dependencies) {
        assertDeclRefDependencyTargetFile(expectedFileName, dependencies, 1, 0);
    }

    protected void assertDeclRefDependencyTargetFile(String expectedFileName, List<DeclarationReferenceDependency> dependencies,
            int expectedDependenciesCount, int indexToTest) {
        Assert.assertEquals(expectedDependenciesCount, dependencies.size());
        Assert.assertEquals(expectedFileName, dependencies.get(indexToTest).getDeclaration().getFile().getProjectRelativePath());
    }

    protected void assertDeclRefName(String expectedName, DeclarationReference declRef) {
        Assert.assertEquals(expectedName, declRef.getName());
    }

    protected void assertNodeSignature(String expectedSignature, IASTNode nodeToCheck) {
        Assert.assertEquals(expectedSignature, nodeToCheck.getRawSignature());
    }

    protected void assertNotResolvable(DeclarationReference reference) {
        DeclarationReferenceDependency depencency = getRequiredDependency(reference);
        Assert.assertNull(depencency.getDeclaration());
    }

    public DeclarationReferenceDependency getRequiredDependency(DeclarationReference ref) {
        List<DeclarationReferenceDependency> list = ref.getRequiredDependencies();
        if (list.size() != 1) {
            fail("DeclarationReference contains " + list.size() + " dependencies instead of exactly 1.");
        }
        return list.get(0);
    }

    protected String makeOSPath(String path) {
        return new Path(path).toOSString();
    }

    protected void assertSuggestion(Suggestion<?> suggestionToTest, String expectedFileName, String expectedText, int expectedOffset,
            int expectedLength) throws IOException {
        Assert.assertEquals(expectedFileName, suggestionToTest.getProjectRelativePath());
        Assert.assertEquals(expectedText, suggestionToTest.getDescription());
        Assert.assertEquals(expectedOffset, adaptActualOffset(suggestionToTest.getIFile(), suggestionToTest.getStartOffset()));
        Assert.assertEquals(expectedLength, adaptActualLength(suggestionToTest.getIFile(), suggestionToTest.getLength(), suggestionToTest
                .getStartOffset()));
    }

    protected void assertDeclaration(Declaration declaration, String expectedFileName, String expectedCode, int expectedOffset) throws IOException {
        Assert.assertEquals(makeOSPath(expectedFileName), declaration.getFile().getProjectRelativePath());
        Assert.assertEquals(expectedCode, declaration.getName().toString());
        final IASTFileLocation fileLocation = declaration.getFileLocation();
        Assert.assertEquals(expectedOffset, adaptActualOffset(getCurrentProject().getFile(fileLocation.getFileName()), fileLocation.getNodeOffset()));
    }

    protected void assertFileLocation(IASTFileLocation fileLocation, String expectedProjectRelativeFileName, int expectedOffset, int expectedLength)
            throws IOException {
        Assert.assertEquals(expectedProjectRelativeFileName, makeProjectRelativePath(fileLocation.getFileName()));
        final IASTFileLocation fileLocation1 = fileLocation;
        Assert.assertEquals(expectedOffset, adaptActualOffset(getCurrentProject().getFile(fileLocation1.getFileName()), fileLocation1
                .getNodeOffset()));
        Assert.assertEquals(expectedLength, fileLocation.getNodeLength());
    }

    protected void runAction(IncludatorAction action, AlgorithmScope scope) throws InterruptedException {
        runActionNoStatusCheck(action, scope);
        assertStatusOk(action.getIncludatorAnalysationJob().getStatus());
    }

    private void runActionNoStatusCheck(IncludatorAction action, AlgorithmScope scope) throws InterruptedException {
        action.init(IncludatorPlugin.getActiveWorkbenchWindow());
        switch (scope) {
        case EDITOR_SCOPE:
            action.selectionChanged(null, new TextSelection(0, 0));
            break;
        case FILE_SCOPE:
            action.selectionChanged(null, new FileMockSelection(currentProjectHolder.getFile(getNameOfPrimaryTestFile())));
            break;
        case CONTAINER_SCOPE:
            action.selectionChanged(null, new FirstFolderMockSelection(getCurrentCProject()));
            break;
        case PROJECT_SCOPE:
            action.selectionChanged(null, new ProjectMockSelection(getCurrentCProject()));
            break;
        }
        action.run(null);
        IncludatorJob job = action.getIncludatorAnalysationJob();
        Assert.assertNotNull("IncludatorAction did not set the job field. It should do so. It should also not run sync.", job);
        job.join();
    }

    protected MultiStatus runAlgorithmsAsAction(Algorithm algorithm, AlgorithmScope scope) throws InterruptedException {
        IncludatorTestAction action = new IncludatorTestAction(algorithm);
        runActionNoStatusCheck(action, scope);
        return StatusHelper.unwrapStatus(action.getIncludatorAnalysationJob().getStatus());
    }

    protected List<Suggestion<?>> runAlgorithm(Algorithm algorithm) throws IOException, CoreException {
        Optimizer optimizer = new Optimizer(IncludatorPlugin.getWorkspace(), algorithm);
        IncludatorFile file = getActiveIncludatorFile();
        optimizer.run(new FileStartingPoint(IncludatorPlugin.getActiveWorkbenchWindow(), file), new NullProgressMonitor());
        List<Suggestion<?>> suggestions = optimizer.getOptimizationSuggestions();

        Collections.sort(suggestions, (o1, o2) -> {
            int relation = o1.getDescription().compareTo(o2.getDescription());
            if (relation != 0) return relation;
            relation = o1.getAbsoluteFilePath().compareTo(o2.getAbsoluteFilePath());
            if (relation != 0) return relation;
            relation = o1.getStartOffset() - o2.getStartOffset();
            if (relation != 0) return relation;
            relation = o1.getEndOffset() - o2.getEndOffset();
            return relation;
        });
        return suggestions;
    }

    protected List<DeclarationReference> getActiveFileDeclarationReferences() {
        return getDeclarationReferences(getNameOfPrimaryTestFile());
    }

    protected List<DeclarationReference> getDeclarationReferences(String relativeFilePath) {
        IncludatorFile file = getIncludatorFile(relativeFilePath);
        List<DeclarationReference> references = new ArrayList<>(file.getDeclarationReferences());
        return references;
    }

    protected void assertExternalIncludatorResourcePath(String expectedRelativePath, String actualAbsolutePath) {
        Assert.assertEquals(externalTestResourcesHolder.makeProjectAbsolutePath(expectedRelativePath).toOSString(), actualAbsolutePath);
    }

    protected void assertIncludePath(String expected, FullIncludePath includePath) throws CoreException {
        String[] parts = expected.split(" -> ");
        Assert.assertEquals(parts.length, includePath.length());
        for (int i = 0; i < parts.length; i++) {
            String relativePath = FileHelper.makeProjectRelativePath(FileHelper.uriToStringPath(includePath.getIncludeAt(i).getIncludesLocation()
                    .getURI()), getCurrentCProject());
            Assert.assertEquals(parts[i], relativePath);
        }
    }

    protected String makeProjectRelativePath(String absolutePath) {
        Path path = new Path(absolutePath);
        return path.makeRelativeTo(getCurrentProject().getLocation()).toOSString();
    }

    protected void assertNonCFile(IPath relativePath, String fileTypeToAdd) throws CoreException {
        boolean caugth = false;
        try {
            getExternalIndexFile(relativePath);
        } catch (IncludatorException e) {
            caugth = true;
            String expectedMsg = "CDT does not consider the file '" + relativePath.lastSegment() + "' as a C or C++ source or header file. " +
                                 "Please add '" + fileTypeToAdd +
                                 "' to the 'File Types' list under 'C/C++ General->File Types' in 'Eclipse Preferences' or the project's " +
                                 "'Project Properties'.";
            Assert.assertEquals(expectedMsg, e.getMessage());
        }
        Assert.assertTrue(caugth);
    }

    protected IIndexFile getExternalIndexFile(IPath relativePath) throws CoreException {
        IncludatorFile someCSourceFile = getActiveProject().getFile(externalTestResourcesHolder.makeProjectAbsolutePath(relativePath));
        return FileHelper.getIndexFile(someCSourceFile);
    }

    protected void assertFileLocation(DeclarationReferenceDependency declarationToTest, String expectedDeclRefFileName, int expectedDeclRefOffset,
            String expectedDeclFileName, int expectedDeclOffset, int expectedNodeLength) throws IOException {
        assertFileLocation(declarationToTest.getDeclarationReference().getFileLocation(), expectedDeclRefFileName, expectedDeclRefOffset,
                expectedNodeLength);
        assertFileLocation(declarationToTest.getDeclaration().getFileLocation(), expectedDeclFileName, expectedDeclOffset, expectedNodeLength);
    }

    protected void assertAreClassReferences(List<DeclarationReference> references, int... expectedIndexThatAreTypeSpecifiers) {
        for (int i = 0; i < references.size(); i++) {
            boolean expected = false;
            if ((expectedIndexThatAreTypeSpecifiers.length != 0) && (expectedIndexThatAreTypeSpecifiers[0] == i)) {
                expected = true;
                expectedIndexThatAreTypeSpecifiers = Arrays.copyOfRange(expectedIndexThatAreTypeSpecifiers, 1,
                        expectedIndexThatAreTypeSpecifiers.length);
            }
            Assert.assertEquals(expected, references.get(i).isClassReference());
        }
    }

    protected void assertDeclarationReference(String expectedRefName, String expectedFileName, DeclarationReference reference) throws IOException {
        final IASTFileLocation fileLocation = reference.getFileLocation();
        assertDeclarationReference(expectedRefName, expectedFileName, adaptActualOffset(getCurrentProject().getFile(fileLocation.getFileName()),
                fileLocation.getNodeOffset()), reference);
    }

    protected void assertStatus(int expectedAmount, int expectedType, String expectedStatusStr, MultiStatus status) {
        Assert.assertEquals(expectedAmount, status.getChildren().length);
        Assert.assertEquals(expectedType, status.getSeverity());
        Assert.assertEquals(expectedStatusStr, status.toString());
    }

    protected void assertStatus(int expectedType, String expectedStatusStr, IStatus statusToTest) {
        Assert.assertEquals(expectedType, statusToTest.getSeverity());
        Assert.assertEquals(expectedStatusStr, statusToTest.getMessage());
    }

    protected void assertStatusOk(IStatus status) {
        Assert.assertTrue(status.toString(), status.isOK());
        Assert.assertEquals(0, status.getChildren().length);
    }

    protected void assertQuickFix(String expectedDisplayString, int expectedStartOffset, int expectedEndOffset, Suggestion<?> suggestion)
            throws IOException {
        IncludatorQuickFix quickFix = suggestion.getQuickFixes()[0];
        Assert.assertEquals(expectedDisplayString, quickFix.getLabel());
        Assert.assertEquals(expectedStartOffset, adaptActualOffset(suggestion.getIFile(), quickFix.getStartOffset()));
        Assert.assertEquals(expectedEndOffset, adaptActualOffset(suggestion.getIFile(), quickFix.getEndOffset()));
    }

    protected void assertDeclRefDependencyExternalTargetFile(String expectedPath, List<DeclarationReferenceDependency> dependencies) {
        assertDeclRefDependencyExternalTargetFile(expectedPath, dependencies, 1, 0);
    }

    private void assertDeclRefDependencyExternalTargetFile(String expectedPath, List<DeclarationReferenceDependency> dependencies,
            int expectedReferencesCount, int indexToTest) {
        Assert.assertEquals(expectedReferencesCount, dependencies.size());
        String expectedAbsolutePath = externalTestResourcesHolder.makeProjectAbsolutePath(expectedPath).toOSString();
        Assert.assertEquals(expectedAbsolutePath, dependencies.get(indexToTest).getDeclaration().getFile().getFilePath());
    }

    /**
     * this method is supposed to be used when writing code. It prints a collection to system out which can then be used to past into the test. this
     * speeds up the development while arising the need to investigate the expected code if it really is the expected result.
     *
     * @param suggestions A {@link List} of suggestions
     * @throws IOException Could throw when accessing the actual files from the suggestions
     */
    protected void printSuggestionsToJavaCode(List<Suggestion<?>> suggestions) throws IOException {

        System.out.println("assertEquals(" + suggestions.size() + ", suggestions.size());\n");
        System.out.println("String notInUseText = \"This declaration is not in use through the file " + getNameOfPrimaryTestFile() + ".\";");
        System.out.println("String inUseText = \"This declaration is in use through the file " + getNameOfPrimaryTestFile() + ".\";");
        System.out.println("String implicitlyInUseText = \"This declaration is implicitly in use through the file " + getNameOfPrimaryTestFile() +
                           ".\";");
        System.out.println("int i = 0;");
        for (Suggestion<?> s : suggestions) {
            String descr = s.getDescription();
            String expectedDescrVarName;
            if (descr.startsWith("This declaration is in use")) {
                expectedDescrVarName = "inUseText";
            } else if (descr.startsWith("This declaration is implicitly in use")) {
                expectedDescrVarName = "implicitlyInUseText";
            } else {
                expectedDescrVarName = "notInUseText";
            }
            int offset = adaptActualOffset(s.getIFile(), s.getStartOffset());
            int length = adaptActualOffset(s.getIFile(), s.getEndOffset()) - adaptActualOffset(s.getIFile(), s.getStartOffset());
            String file = s.getProjectRelativePath();
            System.out.print("assertSuggestion(suggestions.get(i++), \"" + file + "\", " + expectedDescrVarName + ", " + offset + ", " + length +
                             "); // ");
            String expectedSource = testFiles.get(file).getSource().substring(s.getStartOffset(), s.getEndOffset());
            int breakOffset = expectedSource.indexOf(NL);
            if (breakOffset != -1) {
                expectedSource = expectedSource.substring(0, breakOffset);
            }
            System.out.println(expectedSource);
        }
    }

    protected DestructorDeclarationReference getDestructorReference(DeclarationReference ref) {
        if (ref instanceof DestructorDeclarationReference) {
            return (DestructorDeclarationReference) ref;
        }
        fail("declRef is no destructorDeclRef (as it was expected).");
        return null; // dummy return. will not be reached since fail above throws (runtime)exception
    }

    protected ConstructorDeclarationReference getConstructorReference(DeclarationReference ref) {
        if (ref instanceof ConstructorDeclarationReference) {
            return (ConstructorDeclarationReference) ref;
        }
        fail("declRef is no constructorDeclRef (as it was expected).");
        return null; // dummy return. will not be reached since fail above throws (runtime)exception
    }

    protected void assertName(String expectedFileName, String expectedName, int expectedOffset, IName name) throws IOException {
        Assert.assertEquals(expectedName, name.toString());
        final IASTFileLocation fileLocation = name.getFileLocation();
        Assert.assertEquals(expectedOffset, adaptActualOffset(getCurrentProject().getFile(fileLocation.getFileName()), fileLocation.getNodeOffset()));
        Assert.assertEquals(expectedFileName, makeProjectRelativePath(name.getFileLocation().getFileName()));
    }

    protected void assertFileLocation(int expected, IASTFileLocation fileLocation) throws IOException {
        final IASTFileLocation fileLocation1 = fileLocation;
        Assert.assertEquals(expected, adaptActualOffset(getCurrentProject().getFile(fileLocation1.getFileName()), fileLocation1.getNodeOffset()));
    }

    protected String makeLocationString(String relativeFileName, int offset, int length) throws IOException {
        int expectedStart = adaptExpectedOffset(currentProjectHolder.makeProjectAbsoluteURI(relativeFileName), offset);
        return "[" + expectedStart + "," + (expectedStart + length) + "]";
    }

    protected void assertStatusOk() {
        assertStatusOk(StatusHelper.collectStatus());
    }

    protected void assertStatus(String... expectedMsgs) {
        assertStatus(StatusHelper.collectStatus(), expectedMsgs);
    }

    protected void assertStatus(MultiStatus status, String... expectedMsgs) {
        IStatus[] childStati = status.getChildren();
        Assert.assertEquals(expectedMsgs.length, childStati.length);
        for (int i = 0; i < expectedMsgs.length; i++) {
            Assert.assertEquals(expectedMsgs[i], childStati[i].getMessage());
        }
    }

    protected void applySuggestions(List<Suggestion<?>> suggestions) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new ApplySuggestionsRunnable(suggestions));
    }

    protected void insertTextIntoActiveDocument(String text, int offset) throws Exception {
        URI absoluteFileName = currentProjectHolder.makeProjectAbsoluteURI(getNameOfPrimaryTestFile());
        int actualInsertOffset = adaptExpectedOffset(absoluteFileName, offset);
        applyEdit(new InsertEdit(actualInsertOffset, text));
    }

    protected void deleteFromActiveDocument(int offset, int length) throws Exception {
        URI absoluteFileName = currentProjectHolder.makeProjectAbsoluteURI(getNameOfPrimaryTestFile());
        int actualDeleteOffset = adaptExpectedOffset(absoluteFileName, offset);
        applyEdit(new DeleteEdit(actualDeleteOffset, length));
    }

    protected Algorithm wrapAsProjectAlgorithm(Algorithm algToWrap) {
        return AlgorithmScopeHelper.getAlgorithmForScope(AlgorithmScope.PROJECT_SCOPE, algToWrap);
    }

    protected void assertFileNames(List<IncludatorFile> files, String... expectedRelativePaths) {
        Assert.assertEquals(expectedRelativePaths.length, files.size());
        for (int i = 0; i < expectedRelativePaths.length; i++) {
            Assert.assertEquals(expectedRelativePaths[i], files.get(i).getProjectRelativePath());
        }
    }

    protected FullIncludePath getIncludePath(DeclarationReferenceDependency dependency) {
        if (dependency.getIncludePaths().size() == 1) {
            return dependency.getIncludePaths().get(0);
        }
        throw new IncludatorException("Multiple include paths exist. Call getIncludePaths() instead.");
    }

    protected void applySuggestion(Suggestion<?> suggestion) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new ApplySuggestionRunnable(suggestion));
    }

    protected void applyMarker(final IMarker marker) throws Exception {
        new UIThreadSyncRunnable() {

            @Override
            protected void runSave() throws Exception {
                IMarkerResolution[] resolutions = new IncludatorResolutionGenerator().getResolutions(marker);
                Assert.assertTrue(resolutions.length >= 1);
                resolutions[0].run(marker);
            }
        }.runSyncOnUIThread();
    }

    private void applyEdit(final TextEdit edit) throws Exception {
        new UIThreadSyncRunnable() {

            @Override
            protected void runSave() throws Exception {
                edit.apply(currentProjectHolder.getDocument(getCurrentIFile(getNameOfPrimaryTestFile())));
            }
        }.runSyncOnUIThread();
    }
}
