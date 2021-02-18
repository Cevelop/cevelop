package com.cevelop.ctylechecker.problems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;


public class ProblemFileLoader {

    private final URL problemFolderURL;

    public ProblemFileLoader(final URL problemFolderURL) {
        this.problemFolderURL = problemFolderURL;
    }

    public boolean canLoad(final String problemId) {
        try {
            load(problemId);
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    public final CtyleProblem load(final String problemId) {
        try {
            final String filename = getFileName(problemId);
            final URL file = getFileURL(filename);
            final String fileContents = readFile(file);
            return ProblemFileParser.parse(fileContents);
        } catch (final Exception e) {
            throw new RuntimeException("error reading file", e);
        }
    }

    private URL getFileURL(final String filename) throws MalformedURLException {
        return new URL(problemFolderURL.toExternalForm() + "/" + filename);
    }

    private String readFile(final URL file) throws IOException {
        try (final InputStream inputStream = file.openConnection().getInputStream();
                final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")))) {
            return readFileContents(in);
        }
    }

    private String readFileContents(final BufferedReader in) throws IOException {
        String inputLine;
        final StringBuilder fileBuilder = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            fileBuilder.append(inputLine + System.getProperty("line.separator"));
        }
        return fileBuilder.toString();
    }

    private String getFileName(final String problemId) {
        final List<String> split = Arrays.asList(problemId.split("\\."));
        if (split.isEmpty()) {
            throw new RuntimeException("invalid problem id");
        }
        return split.get(split.size() - 1);
    }
}
