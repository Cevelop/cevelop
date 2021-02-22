package com.cevelop.templator.plugin.asttools;

import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.templator.plugin.logger.TemplatorException;


public class IndexAction {

    public static <T extends Object> T perform(IIndex index, IIndexAction action) throws TemplatorException {
        T result = null;
        try {
            index.acquireReadLock();
        } catch (InterruptedException e) {
            throw new TemplatorException("Interrupt while performing index operation", e);
        }

        try {
            result = action.<T>doAction(index);
        } catch (Exception e) {
            throw new TemplatorException(e);
        } finally {
            index.releaseReadLock();
        }

        return result;
    }
}
