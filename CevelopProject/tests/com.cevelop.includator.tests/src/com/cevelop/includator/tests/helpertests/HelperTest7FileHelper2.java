package com.cevelop.includator.tests.helpertests;

import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;

import com.cevelop.includator.helpers.FileHelper;

import junit.framework.TestCase;


public class HelperTest7FileHelper2 extends TestCase {

    private static final String WIN_PATH   = "C:\\folder\\file.txt";
    private static final String LINUX_PATH = "/var/www/something.html";

    private static final String EXPECTED_URI_TO_STRING_LINUX = "file:/var/www/something.html";
    private static final String EXPECTED_URI_TO_STRING_WIN   = "file:/C:/folder/file.txt";

    @Test
    public void test1WinPath() {
        IPath path = FileHelper.stringToPath(getExpected());
        assertEquals(getExpected(), path.toOSString());
        assertEquals(getExpected(), FileHelper.pathToStringPath(path));
    }

    @Test
    public void test2WinURI() {
        URI uri = FileHelper.stringToUri(getExpected());
        assertEquals(getExpectedUriString(), uri.toString());
        assertEquals(getExpected(), FileHelper.uriToStringPath(uri));
    }

    @Test
    public void test3WinPathViaUri() {
        URI uri = FileHelper.stringToUri(getExpected());
        IPath path = FileHelper.uriToPath(uri);
        assertEquals(getExpected(), path.toOSString());
        assertEquals(getExpected(), FileHelper.pathToStringPath(path));
    }

    @Test
    public void test4WinUriViaPath() {
        IPath path = FileHelper.stringToPath(getExpected());
        URI uri = FileHelper.pathToUri(path);
        assertEquals(getExpectedUriString(), uri.toString());
        assertEquals(getExpected(), FileHelper.uriToStringPath(uri));
    }

    private String getExpected() {
        if (isWindows()) {
            return WIN_PATH;
        } else {
            return LINUX_PATH;
        }
    }

    private String getExpectedUriString() {
        if (isWindows()) {
            return EXPECTED_URI_TO_STRING_WIN;
        } else {
            return EXPECTED_URI_TO_STRING_LINUX;
        }
    }

    private boolean isWindows() {
        return FileHelper.PATH_SEGMENT_SEPARATOR == '\\';
    }
}
