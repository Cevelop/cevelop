package com.cevelop.ctylechecker.problems;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProblemFileParser {

    private final String        input;
    private static final String newLine   = "\\r?\\n";
    private static final String paragraph = "(\\r?\\n\\s*\\r?\\n)";

    private static final Pattern link = Pattern.compile("\\[([^\\[]+)\\]\\(([^\\)]+)\\)");

    private ProblemFileParser(final String input) {
        this.input = input;
    }

    private void error(final String msg) {
        throw new RuntimeException(msg);
    }

    private final CtyleProblem parse() {
        try {
            final List<String> split = Arrays.asList(input.split(paragraph));
            validate(split);
            final String problem = split.get(0).trim().substring(2).trim();
            final String explanation = split.get(1).trim();
            final Map<String, URL> infoUrlMap = new HashMap<>();
            if (split.size() > 2) {
                final List<String> furtherInfos = Arrays.asList(split.get(2).split(newLine));
                extractUrls(infoUrlMap, furtherInfos);
            }
            return new CtyleProblem(problem, explanation, infoUrlMap);
        } catch (final Exception e) {
            throw new RuntimeException("parse error:" + e.getMessage());
        }
    }

    private void validate(final List<String> split) {
        if (split.size() > 3) {
            error("only 3 paragraphs allowed (description, explanation, urls)");
        }
        if (!firstLineContainsTitle(split)) {
            error("missing problem statement");
        }
        if (secondParagraphContainsURLs(split)) {
            error("missing problem explanation");
        }
    }

    private boolean secondParagraphContainsURLs(final List<String> split) {
        return link.matcher(split.get(1).split(newLine)[0]).matches();
    }

    private boolean firstLineContainsTitle(final List<String> split) {
        return split.get(0).trim().charAt(0) == '#';
    }

    private void extractUrls(final Map<String, URL> infoUrlMap, final List<String> furtherInfos) {
        furtherInfos.stream().forEach(info -> {
            extractUrl(infoUrlMap, info);
        });
    }

    private void extractUrl(final Map<String, URL> infoUrlMap, final String info) {
        final Matcher matcher = link.matcher(info);
        if (matcher.matches()) {
            try {
                final String name = matcher.group(1);
                final URL url = new URL(matcher.group(2));
                infoUrlMap.put(name, url);
            } catch (final Exception e) {
                throw new RuntimeException("invalid url", e);
            }
        }
    }

    public static CtyleProblem parse(final String input) {
        return new ProblemFileParser(input).parse();
    }
}
