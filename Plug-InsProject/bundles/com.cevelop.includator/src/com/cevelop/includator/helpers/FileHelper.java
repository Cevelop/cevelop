/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.dom.parser.AbstractCLikeLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.LanguageManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.stores.RecursiveIndexIncludeStore;


public class FileHelper {

    public static final String NL                     = System.getProperty("line.separator");
    public static final int    NL_LENGTH              = NL.length();
    public static final char   PATH_SEGMENT_SEPARATOR = File.separatorChar;

    private static Map<IFile, IDocumentProvider> connectedDocuments = new LinkedHashMap<>();

    public static IPath uriToPath(URI fileUri) {
        return new Path(fileUri.getPath());
    }

    public static IPath stringToPath(String strPath) {
        return new Path(strPath);
    }

    public static URI stringToUri(String fileString) {
        return new File(fileString).toURI();
    }

    public static String uriToStringPath(URI fileUri) {
        return pathToStringPath(uriToPath(fileUri));
    }

    public static String pathToStringPath(IPath filePath) {
        return filePath.toOSString();
    }

    public static URI pathToUri(IPath path) {
        return stringToUri(pathToStringPath(path));
    }

    public static boolean isPartOfProject(URI fileUri, IncludatorProject project) {
        return isPartOfProject(getIFile(fileUri), project);
    }

    public static boolean isPartOfProject(IFile file, IncludatorProject project) {
        if (file == null) {
            return false;
        }
        return file.getProject().equals(project.getCProject().getProject());
    }

    public static IFile getIFile(URI fileURI) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        root.getFileForLocation(new Path(fileURI.getPath()));
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

    public static IncludatorFile getNameCorrelatingHeaderFile(IncludatorFile file) {
        String fileNameWithoutFileExtension = new Path(file.getProjectRelativePath()).removeFileExtension().lastSegment();
        for (IASTPreprocessorIncludeStatement curInclude : file.getIncludes()) {
            if (curInclude.isSystemInclude()) {
                continue;
            }
            String includeFileNameWithoutExtension = new Path(curInclude.getName().getRawSignature()).removeFileExtension().lastSegment();
            if (includeFileNameWithoutExtension.equals(fileNameWithoutFileExtension)) {
                return IncludeHelper.findIncludedFile(curInclude, file.getProject());
            }
        }
        return null;
    }

    public static String getPositionString(IASTFileLocation fileLocation) {
        if (fileLocation == null) {
            return "[file location missigng]";
        }

        DocumentLocation location = new DocumentLocation(fileLocation);
        return location.getDocumentLocationString();
    }

    public static String getExtendedPositionString(IASTNode node) {
        IASTFileLocation fileLocation = getNodeFileLocation(node);
        String positionString = getPositionString(fileLocation);
        if (node.getFileLocation() == null) {
            positionString += " (parent fileLocation)";
        }
        return positionString;
    }

    public static String makeProjectRelativePath(String absolutePathStr, IncludatorProject project) {
        return makeProjectRelativePath(absolutePathStr, project.getCProject());
    }

    public static String makeProjectRelativePath(String absolutePathStr, IProject project) {
        IPath projectPath = project.getLocation();
        IPath absolutePath = new Path(absolutePathStr);
        return absolutePath.makeRelativeTo(projectPath).toOSString();
    }

    public static String makeProjectRelativePath(String absolutePathStr, ICProject project) {
        return makeProjectRelativePath(absolutePathStr, project.getProject());
    }

    public static IIndexFile getIndexFile(IncludatorFile file) throws CoreException {
        return getIndexFile(file.getFilePath(), file.getProject());
    }

    public static IIndexFile getIndexFile(String filePath, IncludatorProject project) throws CoreException {
        return getIndexFile(IndexLocationFactory.getIFLExpensive(project.getCProject(), filePath), project.getIndex());
    }

    public static IIndexFile getIndexFile(String fileName, IIndex index, IASTTranslationUnit translationUnit) {
        int linkageID = translationUnit.getLinkage().getLinkageID();
        IIndexFileLocation indexFileLocation = IndexLocationFactory.getIFLExpensive(fileName);
        try {
            return index.getFile(linkageID, indexFileLocation, translationUnit.getSignificantMacros());
        } catch (CoreException e) {
            return null;
        }
    }

    public static IIndexFile getIndexFile(IIndexFileLocation fileLocation, IIndex index) throws CoreException {
        IIndexFile[] files = index.getFiles(fileLocation);
        if (files.length == 0) {
            return null;
        } else if (files.length == 1) {
            return files[0];
        } else {
            return pickPreferredIndexFile(files, fileLocation);
        }
    }

    private static IIndexFile pickPreferredIndexFile(IIndexFile[] files, IIndexFileLocation fileLocation) {
        int preferredLinkageID = IncludatorPlugin.getPreferredLinkageID();
        for (IIndexFile curFile : files) {
            try {
                if (curFile.getLinkageID() == preferredLinkageID) {
                    return curFile;
                }
            } catch (CoreException e) {
                throw new IncludatorException("Failed to get linkage-id from index-file '" + FileHelper.uriToStringPath(fileLocation.getURI()), e);
            }
        }
        // should never happen since 2 files means each one with c and c++ linkage meaning one will always match the default linkage.
        return files[0];
    }

    public static IASTFileLocation getFileLocation(IName name) {
        if (name instanceof IASTNode) {
            return getNodeFileLocation((IASTNode) name);
        } else if (name instanceof IIndexName) {
            return getIndexNameFileLocation((IIndexName) name);
        }
        return name.getFileLocation();
    }

    public static IASTFileLocation getIndexNameFileLocation(IIndexName name) {
        try {
            return name.getFileLocation() != null ? name.getFileLocation() : getIndexNameFileLocation(name.getEnclosingDefinition());
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public static IASTFileLocation getNodeFileLocation(IASTNode node) {
        return (node.getFileLocation() != null) ? node.getFileLocation() : getNodeFileLocation(node.getParent());
    }

    public static String getLocationStrForIName(IName name, IncludatorProject project) {
        IASTFileLocation fileLocation = FileHelper.getFileLocation(name);
        String positionString = FileHelper.getPositionString(fileLocation);
        return getSmartFilePath(fileLocation.getFileName(), project) + positionString;
    }

    public static String getSmartFilePath(String absolutePath, IncludatorProject project) {
        return getSmartFilePath(absolutePath, project.getCProject());
    }

    public static String getSmartFilePath(String absolutePath, ICProject project) {
        return getSmartFilePath(absolutePath, project.getProject());
    }

    public static String getSmartFilePath(String absolutePath, IProject project) {
        String relativePath = makeProjectRelativePath(absolutePath, project);
        return getSmartFilePath(absolutePath, relativePath);
    }

    public static String getSmartFilePath(String absolutePath, String relativePath) {
        return relativePath.startsWith(".." + FileHelper.PATH_SEGMENT_SEPARATOR) ? absolutePath : relativePath;
    }

    public static IDocument getDocument(IFile file) {
        if (connectedDocuments.containsKey(file)) {
            return connectedDocuments.get(file).getDocument(file);
        }
        try {
            IDocumentProvider provider = new TextFileDocumentProvider();
            if (file == null) {
                return null;
            }
            provider.connect(file);
            connectedDocuments.put(file, provider);
            return provider.getDocument(file);
        } catch (CoreException e) {
            return null;
        }
    }

    public static IDocument getDocument(URI fileUri) {
        IFile file = FileHelper.getIFile(fileUri);
        return getDocument(file);
    }

    public static void clean() {
        for (Entry<IFile, IDocumentProvider> curEntry : connectedDocuments.entrySet()) {
            curEntry.getValue().disconnect(curEntry.getKey());
        }
        connectedDocuments.clear();
    }

    public static long getWeight(IncludatorFile file) {
        return getWeight(file.getFilePath(), file.getProject());
    }

    public static long getWeight(String absoluteFilePath, IncludatorProject project) {
        try {
            IIndexFile indexFile = getIndexFile(absoluteFilePath, project);
            if (indexFile == null) {
                return Long.MAX_VALUE;
            }
            RecursiveIndexIncludeStore store = IncludatorPlugin.getRecursiveIndexIncludeStore();
            long weight = 1 + store.getRecursiveIncludeRelations(indexFile, project.getIndex()).size();
            boolean shouldPrefer = new Path(absoluteFilePath).getFileExtension() == null;
            if (!shouldPrefer) {
                weight = weight * weight; // malus for files with extension. -> prefer extension-less header (like <string>, <vector>, etc.)
            }
            return weight;
        } catch (CoreException e) {
            throw new IncludatorException("Error while getting wight of file " + absoluteFilePath + ".", e);
        }
    }

    public static int adaptOffsetToIncludeNextNewline(int offset, IFile file) {
        IDocument document = getDocument(file);
        int docLength = document.getLength();
        if (document == null || docLength <= offset) {
            return offset;
        }
        char followingChar;
        try {
            do {
                followingChar = document.getChar(offset);
                offset++;
                if (followingChar == '\n' || docLength == offset) { // stop consuming (even if more whitespace would follow)
                    return offset;
                }
            } while (Character.isWhitespace(followingChar));
        } catch (BadLocationException e) {
            // ignore;
        }
        return --offset;
    }

    public static int getPreferredLinkageID(ICProject cproject) {
        try {
            IProjectNature cppNature = cproject.getProject().getNature(CCProjectNature.CC_NATURE_ID);
            if (cppNature != null) {
                return GPPLanguage.getDefault().getLinkageID();
            }
        } catch (CoreException e) {
            throw new IncludatorException("Failed to retrieve project nature of '" + cproject.getElementName() + "'.", e);
        }
        return GCCLanguage.getDefault().getLinkageID();
    }

    public static boolean isCLikeFile(IProject project, String filePath) {
        ILanguage language = getLanguageForFile(project, filePath);
        return language instanceof AbstractCLikeLanguage;
    }

    public static ILanguage getLanguageForFile(IProject project, String filePath) {
        IContentType contentType = CCorePlugin.getContentType(project, filePath);
        if (contentType == null) {
            return null;
        }
        ILanguage language = LanguageManager.getInstance().getLanguage(contentType);
        return language;
    }

    public static String readFile(File file) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        try {
            String tmp = in.readLine();
            final StringBuilder sb = new StringBuilder(tmp != null ? tmp : ""); //$NON-NLS-1$
            while ((tmp = in.readLine()) != null) {
                sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
                sb.append(tmp);
            }
            return sb.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
