package com.cevelop.ctylechecker.checker.dynamic;

import java.util.Optional;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTOperatorName;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.common.FileUtil;
import com.cevelop.ctylechecker.common.ResourceType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.infos.DynamicStyleInfo;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


@SuppressWarnings("restriction")
public class DynamicStyleChecker extends AbstractStyleChecker {

    private final Pattern cppFilePattern = Pattern.compile(".([cC]|[hH])(p|pp|PP|xx|\\+\\+)?$");

    private IConfigurationService configService = CtylecheckerRuntime.getInstance().getRegistry().getConfigurationService();

    @Override
    public void processAst(IASTTranslationUnit ast) {
        IConfiguration config = configService.loadConfiguration(getProject());
        if (config.isEnabled()) {
            check(ast, config.getActiveStyleguide());
        } else {
            try {
                getFile().deleteMarkers(ProblemId.DYNAMIC.getId(), true, IResource.DEPTH_INFINITE);
                getFile().deleteMarkers(ProblemId.DYNAMIC_FOR_FILES.getId(), true, IResource.DEPTH_INFINITE);
            } catch (CoreException e) {
                CtylecheckerRuntime.log(e);
            }
        }
    }

    private void check(IASTTranslationUnit ast, IStyleguide activeStyleguide) {
        ast.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int visit(IASTName name) {
                // Operator's are treated like normal functions, thus
                // the name comes with operator keyword. As the feature to check
                // for UDL's isn't implemented, operator checking is deactivated for now
                if (!(name instanceof CPPASTOperatorName)) {
                    // IASTName.getLastName() return last part of a qualified name or this
                    // if it's not a qualified name, this way we can check qualified names
                    // as well like usual names, as we don't check the Scope part
                    if (shouldCheck(name.getLastName())) {
                        checkStyleguideCompliance(name.getLastName(), activeStyleguide);
                    }
                }
                return PROCESS_CONTINUE;
            }

            private Boolean shouldCheck(IASTName name) {
                return !name.isReference();
            }
        });

        if (cppFilePattern.matcher(getFile().getName()).find()) {
            checkFileStyleguideCompliance(getFile(), activeStyleguide, ast.getTranslationUnit().isHeaderUnit());
        }

        for (IASTPreprocessorMacroDefinition defintion : ast.getMacroDefinitions()) {
            checkStyleguideCompliance(defintion.getName().getLastName(), activeStyleguide);
        }
    }

    private void checkFileStyleguideCompliance(IFile pFile, IStyleguide pActiveStyleguide, Boolean pIsHeaderUnit) {
        Optional<String> oName = FileUtil.extractFileName(pFile);
        String fileNameEnding = pFile.getFileExtension();
        for (IGrouping grouping : pActiveStyleguide.getGroupings()) {
            for (IRule rule : grouping.getRules()) {
                checkRuleCompliance(oName, fileNameEnding, rule, pIsHeaderUnit);
            }
        }

        for (IRule rule : pActiveStyleguide.getRules()) {
            checkRuleCompliance(oName, fileNameEnding, rule, pIsHeaderUnit);
        }
    }

    private void checkRuleCompliance(Optional<String> oName, String fileNameEnding, IRule rule, Boolean pIsHeaderUnit) {
        if (oName.isPresent() && rule.isEnabled() && rule.isApplicableToFile(pIsHeaderUnit)) {
            if (!rule.matches(oName.get())) {
                reportFileProblem(rule, ResourceType.FILE);
            }
        }
        if (!fileNameEnding.isEmpty() && rule.isEnabled() && rule.isApplicableToFileEnding(pIsHeaderUnit)) {
            if (!rule.matches(fileNameEnding)) {
                reportFileProblem(rule, ResourceType.FILE_ENDING);
            }
        }
    }

    private void checkStyleguideCompliance(IASTName name, IStyleguide activeStyleguide) {
        for (IGrouping grouping : activeStyleguide.getGroupings()) {
            for (IRule rule : grouping.getRules()) {
                checkRuleCompliance(name, rule);
            }
        }

        for (IRule rule : activeStyleguide.getRules()) {
            checkRuleCompliance(name, rule);
        }
    }

    private void checkRuleCompliance(IASTName name, IRule rule) {
        if (rule.isEnabled() && rule.isApplicable(name)) {
            if (!rule.matches(name)) {
                reportProblem(name, rule);
            }
        }
    }

    private void reportFileProblem(IRule pRule, ResourceType pType) {
        reportProblem(ProblemId.DYNAMIC_FOR_FILES, createProblemLocation(getFile(), 0, 0), new DynamicStyleInfo(pRule, pType));
    }

    private void reportProblem(IASTName name, IRule pRule) {
        reportProblem(ProblemId.DYNAMIC, name.getOriginalNode(), new DynamicStyleInfo(pRule));
    }
}
