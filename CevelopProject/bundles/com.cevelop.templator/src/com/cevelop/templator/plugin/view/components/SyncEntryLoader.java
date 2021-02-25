package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.view.interfaces.IEntryLoadCallback;
import com.cevelop.templator.plugin.view.interfaces.IEntryLoader;


public class SyncEntryLoader implements IEntryLoader {

    private Composite          parent;
    private IEntryLoadCallback callback;
    private LoadingBar         loadingBar;

    public SyncEntryLoader(Composite parent, String title, IEntryLoadCallback callback) {
        this.parent = parent;
        this.callback = callback;

        loadingBar = new LoadingBar(parent, SWT.NONE, title);
        parent.setSize(loadingBar.getSize());
    }

    @Override
    public void start() {
        try {
            callback.loadOperation(loadingBar);
        } catch (TemplatorException e) {
            e.printStackTrace();
        }
        if (parent.isDisposed()) {
            return;
        }
        loadingBar.dispose();
        callback.loadComplete();
    }
}
