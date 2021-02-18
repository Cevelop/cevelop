package com.cevelop.ctylechecker.quickfix.dynamic;

import java.util.Optional;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;

import com.cevelop.ctylechecker.common.ResourceType;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.quickfix.AbstractStyleResolver;
import com.cevelop.ctylechecker.quickfix.dynamic.refactoring.ASTRenameRefactoring;
import com.cevelop.ctylechecker.quickfix.dynamic.refactoring.FileEndingRenameRefactoring;
import com.cevelop.ctylechecker.quickfix.dynamic.refactoring.FileRenameRefactoring;
import com.cevelop.ctylechecker.quickfix.dynamic.util.MarkerArg;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.service.util.ConfigurationMapper;


public class DynamicStyleResolution extends AbstractStyleResolver {

    @Override
    public String getLabel() {
        return "Resolve rule violation: " + getInfo().ruleName;
    }

    @Override
    public void apply(IMarker marker, IDocument document) {
        Optional<IRule> oRule = getReportingRule(marker);
        if (oRule.isPresent()) {
            IRule rule = oRule.get();
            try {
                ResourceType checkType = ResourceType.valueOf(getProblemArgument(marker, MarkerArg.RESOURCE_TYPE.ordinal()));
                if (checkType.equals(ResourceType.AST)) {
                    applyFix(marker, document, rule);
                }
                if (checkType.equals(ResourceType.FILE) || checkType.equals(ResourceType.FILE_ENDING)) {
                    applyFix((IFile) marker.getResource(), rule, checkType);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                CtylecheckerRuntime.log(ex);
            }
        }
    }

    private Optional<IRule> getReportingRule(IMarker marker) {
        try {
            String ruleJson = getProblemArgument(marker, MarkerArg.RULE_JSON.ordinal());
            if (!ruleJson.isEmpty()) {
                return Optional.ofNullable(ConfigurationMapper.fromJson(ruleJson, IRule.class));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            CtylecheckerRuntime.log(ex);
        }
        return Optional.empty();
    }

    public IDocument getDocument(IMarker marker) {
        return openDocument(marker);
    }

    public void applyFix(IFile file, IRule pRule, ResourceType pType) {
        try {
            if (pType.equals(ResourceType.FILE)) {
                FileRenameRefactoring refactoring = new FileRenameRefactoring(file);
                if (!refactoring.performFor(pRule)) {
                    CtylecheckerRuntime.log("Refactoring failed");
                } else {
                    CodanRuntime.getInstance().getBuilder().processResource(file, new NullProgressMonitor());
                }
            } else {
                FileEndingRenameRefactoring refactoring = new FileEndingRenameRefactoring(file);
                if (!refactoring.performFor(pRule)) {
                    CtylecheckerRuntime.log("Refactoring failed");
                } else {
                    CodanRuntime.getInstance().getBuilder().processResource(file, new NullProgressMonitor());
                }
            }
        } catch (Exception e) {
            CtylecheckerRuntime.log(e);
        }
    }

    public void applyFix(IMarker pMarker, IDocument pDocument, IRule pRule) {
        try {
            ASTRenameRefactoring refactoring = new ASTRenameRefactoring(pMarker, pDocument);
            if (!refactoring.performFor(pRule)) {
                CtylecheckerRuntime.log("Refactoring failed");
            } else {
                CodanRuntime.getInstance().getBuilder().processResource(pMarker.getResource(), new NullProgressMonitor());
            }
        } catch (Exception e) {
            CtylecheckerRuntime.log(e);
        }
    }
}
