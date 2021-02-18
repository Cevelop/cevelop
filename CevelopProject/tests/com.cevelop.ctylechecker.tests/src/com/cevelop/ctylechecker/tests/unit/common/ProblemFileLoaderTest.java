package com.cevelop.ctylechecker.tests.unit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.junit.Before;
import org.junit.Test;

import com.cevelop.ctylechecker.problems.CtyleProblem;
import com.cevelop.ctylechecker.problems.ProblemFileLoader;


public class ProblemFileLoaderTest {

    private URL testProblemFolder;

    @Before
    public void setup() {
        testProblemFolder = getClass().getResource("/com.cevelop.ctylechecker.tests/problems");
        assertNotNull("Could not find problem file folder!", testProblemFolder);
    }

    @Test
    public void testLoadProblemDescriptionFromFile() throws Exception {
        final ProblemFileLoader problemLoader = new ProblemFileLoader(testProblemFolder);
        final CtyleProblem problem = problemLoader.load("some.random.prefix.problem");
        assertEquals(problem.getProblem(), "problem statement");
    }

    public Path getPath(final URL url) throws Exception {
        final URL resolvedUrl = FileLocator.resolve(url);
        if ("file".equals(resolvedUrl.getProtocol())) {
            return Paths.get(resolvedUrl.toURI());
        }
        if ("jar".equals(resolvedUrl.getProtocol())) {
            return extractFromJar(resolvedUrl);
        }
        throw new RuntimeException("error loading file");
    }

    private Path extractFromJar(final URL resolvedUrl) throws IOException, URISyntaxException {
        final Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        final FileSystem zipfs = FileSystems.newFileSystem(resolvedUrl.toURI(), env);
        return zipfs.provider().getPath(resolvedUrl.toURI());
    }
}
