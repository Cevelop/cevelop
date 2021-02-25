package com.cevelop.includator.optimizer.includestofwd;

import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;

import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.findunusedincludes.RemoveIncludeQuickFix;
import com.cevelop.includator.ui.helpers.PositionTrackingChange;


public class ReplaceIncludeWithFwdQuickFix extends IncludatorQuickFix {

    private final PositionTrackingChange insertFwdChange;

    public ReplaceIncludeWithFwdQuickFix(ReplaceIncludeWithFwdSuggestion suggestion, PositionTrackingChange insertFwdChange) {
        super(suggestion);
        this.insertFwdChange = insertFwdChange;
    }

    @Override
    public String getLabel() {
        return "Replace include with forward declaration(s).";
    }

    @Override
    public String getDescription() {
        return suggestion.getDescription();
    }

    @Override
    public Change getChange(IFile file) {
        CompositeChange result = new CompositeChange("Replace Include with forward declaration change");
        result.add(insertFwdChange.getWrappedChange());
        result.add(getRemoveIncludeChange(file));
        return new PositionTrackingChange(result, suggestion.getIFile());
    }

    private Change getRemoveIncludeChange(IFile file) {
        RemoveIncludeQuickFix removeIncludeQuickFix = new RemoveIncludeQuickFix(suggestion);
        return removeIncludeQuickFix.getChange(file);
    }
}
