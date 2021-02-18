package com.cevelop.ctylechecker.checker.includes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.infos.CtyleCheckerInfo;


public class SelfIncludeChecker extends AbstractStyleChecker {

    private static final Pattern CPP_FILE_NAME_PATTERN = Pattern.compile("(.*)\\.c(pp)?");

    @Override
    public void processAst(IASTTranslationUnit ast) {
        if (!ast.isHeaderUnit()) {
            IASTPreprocessorIncludeStatement[] includeDirectives = ast.getIncludeDirectives();
            final List<IFile> filesInProject = getProjectFiles();

            String fileName = getFile().getName();
            Matcher matcher = CPP_FILE_NAME_PATTERN.matcher(fileName);
            if (matcher.matches()) {
                String namePart = matcher.group(1);
                Pattern headerNamePattern = Pattern.compile(namePart + "\\.h(pp)?");
                Optional<IFile> matchingFile = filesInProject.stream().filter(f -> headerNamePattern.matcher(f.getName()).matches()).findFirst();
                if (matchingFile.isPresent()) {
                    Optional<IASTPreprocessorIncludeStatement> headerInclude = Arrays.stream(includeDirectives).filter(include -> headerNamePattern
                            .matcher(getIncludeFileName(include)).matches()).findFirst();
                    if (headerInclude.isPresent()) {
                        if (includeDirectives[0] != headerInclude.get()) {
                            reportProblem(ProblemId.SELF_INCLUDE_POSITION, headerInclude.get(), new CtyleCheckerInfo(headerInclude.get().getName()
                                    .toString()));
                        }
                    } else {
                        reportProblem(ProblemId.MISSING_SELF_INCLUDE, ast, new CtyleCheckerInfo(matchingFile.get().getName()));
                    }
                }
            }
        }
    }

    private List<IFile> getProjectFiles() {
        final List<IFile> filesInProject = new ArrayList<>();
        try {
            getProject().accept(resource -> {
                if (resource instanceof IFile) {
                    filesInProject.add((IFile) resource);
                }
                return true;
            });
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return filesInProject;
    }

    private static String getIncludeFileName(IASTPreprocessorIncludeStatement include) {
        String includeName = include.getName().toString();
        String pathSeparatorPattern = includeName.contains("/") ? "/" : "\\\\";
        String[] splitIncludes = includeName.split(pathSeparatorPattern);
        return splitIncludes[splitIncludes.length - 1];
    }
}
