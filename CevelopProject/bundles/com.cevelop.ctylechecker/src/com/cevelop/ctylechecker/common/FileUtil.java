package com.cevelop.ctylechecker.common;

import java.util.Optional;

import org.eclipse.core.resources.IFile;


public class FileUtil {

    public static Optional<String> extractFileName(IFile pFile) {
        return FileUtil.extractFileNameBody(pFile.getName(), pFile.getFileExtension());

    }

    public static Optional<String> extractFileNameBody(String pAbsoluteFileName, String pFileExtension) {
        if (pFileExtension == null || pFileExtension.isEmpty()) {
            return Optional.ofNullable(pAbsoluteFileName);
        }
        int backIndex = (pAbsoluteFileName.length() - pFileExtension.length()) - 1;
        if (backIndex > 0) {
            return Optional.ofNullable(pAbsoluteFileName.substring(0, backIndex));
        }
        return Optional.empty();
    }
}
