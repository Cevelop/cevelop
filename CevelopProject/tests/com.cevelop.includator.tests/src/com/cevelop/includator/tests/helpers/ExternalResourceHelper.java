/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.tests.IncludatorTestsPlugin;


public class ExternalResourceHelper {

    private static boolean      isLoaded               = false;
    private static final String EXTERNAL_TEST_RESOURCE = "externalTestResource/"; // do not use system separator since also on windows a "/" is used
    // in manifest.

    public static void copyPluginResourcesToTestingWorkspace() {
        if (!isLoaded) {
            URI rootUri = ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
            IPath rootPath = FileHelper.uriToPath(rootUri);
            String baseFolderPath = getTargetFilePath(EXTERNAL_TEST_RESOURCE, rootPath);
            deleteFolder(baseFolderPath);
            createFolder(baseFolderPath);
            Enumeration<?> enumeration = IncludatorTestsPlugin.getDefault().getBundle().findEntries(EXTERNAL_TEST_RESOURCE, "*", true);
            while (enumeration.hasMoreElements()) {
                URL url = (URL) enumeration.nextElement();
                String targetFilePath = getTargetFilePath(url.getPath(), rootPath);
                if (isFolderURL(targetFilePath)) {
                    createFolder(targetFilePath);
                } else {
                    createFile(url, targetFilePath);
                }
            }
            isLoaded = true;
        }
    }

    private static void deleteFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            return;
        }
        if (!recursiveDeleteDirContenteleteDir(file)) {
            System.err.println("Failed to clean up old resources in " + folderPath + " while setting up additional test resources.");
        }
    }

    public static boolean recursiveDeleteDirContenteleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                boolean success = recursiveDeleteDirContenteleteDir(new File(dir, element));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private static void createFile(URL url, String targetFilePath) {
        File file = new File(targetFilePath);
        try {
            if (!file.createNewFile()) {
                System.err.println("Failed to create file " + targetFilePath + " while setting up additional test resources.");
                return;
            }
            addFileContent(file, url);
        } catch (IOException e) {
            System.err.println("Failed to create file " + targetFilePath + " while setting up additional test resources.");
        }
    }

    private static void addFileContent(File fileToWrite, URL sourceUrl) {
        try {
            BufferedReader sourceReader = new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
            BufferedWriter targetWriter = new BufferedWriter(new FileWriter(fileToWrite));
            String line;
            while ((line = sourceReader.readLine()) != null) {
                targetWriter.write(line);
                targetWriter.write(FileHelper.NL);
            }
            targetWriter.close();
        } catch (IOException e) {
            System.err.println("Failed to read plugin resource stream " + sourceUrl + " while setting up additional test resources.");
        }
    }

    private static boolean isFolderURL(String targetFilePath) {
        return targetFilePath.endsWith(Character.toString(FileHelper.PATH_SEGMENT_SEPARATOR));
    }

    private static void createFolder(String targetFilePath) {
        File file = new File(targetFilePath);
        if (!file.mkdir()) {
            System.err.println("Failed to create folder " + targetFilePath + " while setting up additional test resources.");
        }
    }

    private static String getTargetFilePath(String postfix, IPath prefix) {
        return prefix.append(postfix).toOSString();
    }

    public static String makeExternalResourceAbsolutePath(String relativePath) {
        URI rootUri = ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
        IPath rootPath = FileHelper.uriToPath(rootUri);
        String baseFolderPath = getTargetFilePath(EXTERNAL_TEST_RESOURCE, rootPath);
        return getTargetFilePath(relativePath, FileHelper.stringToPath(baseFolderPath));
    }

}
