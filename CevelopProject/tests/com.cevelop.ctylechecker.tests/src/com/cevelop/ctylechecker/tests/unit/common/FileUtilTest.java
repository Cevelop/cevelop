package com.cevelop.ctylechecker.tests.unit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.common.FileUtil;


public class FileUtilTest {

    @Test
    public void testFileNameMainIsExtractedCorrectlyWithExtension() {

        Optional<String> oFileName = FileUtil.extractFileNameBody("Main.cpp", "cpp");
        assertTrue(oFileName.isPresent());
        assertTrue(oFileName.get().equals("Main"));
    }

    @Test
    public void testFileNameMainIsExtractedCorrectlyWithoutExtension() {
        Optional<String> oFileName = FileUtil.extractFileNameBody("Main", "");
        assertTrue(oFileName.isPresent());
        assertEquals("Main", oFileName.get());
    }

    @Test
    public void testFileNameMainIsExtractedCorrectlyWithoutExtension2() {
        Optional<String> oFileName = FileUtil.extractFileNameBody("Main", null);
        assertTrue(oFileName.isPresent());
        assertEquals("Main", oFileName.get());
    }

    @Test
    public void testFileNameComplexIsExtractedCorrectlyWithExtension() {
        Optional<String> oFileName = FileUtil.extractFileNameBody("Wabbalubba-dub-dub_file.txt", "txt");
        assertTrue(oFileName.isPresent());
        assertEquals("Wabbalubba-dub-dub_file", oFileName.get());
    }

    @Test
    public void testFileNameComplexIsExtractedCorrectlyWithoutExtension() {
        Optional<String> oFileName = FileUtil.extractFileNameBody("Wabbalubba-dub-dub_file", "");
        assertTrue(oFileName.isPresent());
        assertEquals("Wabbalubba-dub-dub_file", oFileName.get());
    }

    @Test
    public void testFileNameComplexIsExtractedCorrectlyWithoutExtension2() {
        Optional<String> oFileName = FileUtil.extractFileNameBody("Wabbalubba-dub-dub_file", null);
        assertTrue(oFileName.isPresent());
        assertEquals("Wabbalubba-dub-dub_file", oFileName.get());
    }

    @Test
    public void testFileNameWithMultipleEndingsIsExtractedCorrectlyWithExtension() {
        Optional<String> oFileName = FileUtil.extractFileNameBody("main.cpp.cpp.cpp", "cpp");
        assertTrue(oFileName.isPresent());
        assertEquals("main.cpp.cpp", oFileName.get());
    }

}
