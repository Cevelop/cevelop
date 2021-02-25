package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import com.cevelop.templator.plugin.util.ILoadingProgress;


public class LoadingBar extends Composite implements ILoadingProgress {

    private int         maximum;
    private ProgressBar bar;
    private Label       statusLabel;

    public LoadingBar(Composite parent, int style, String titleText) {
        super(parent, style | SWT.DOUBLE_BUFFERED);

        Label titleLabel = new Label(this, SWT.NONE);
        titleLabel.setText("Loading: " + titleText);
        titleLabel.setSize(titleLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        titleLabel.setLocation(5, 5);

        bar = new ProgressBar(this, SWT.SMOOTH);
        bar.setBounds(5, 46, 280, 24);

        statusLabel = new Label(this, SWT.NONE);
        statusLabel.setText("Status...");
        statusLabel.setSize(statusLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        statusLabel.setLocation(5, 74);

        setSize(300, 120);

        maximum = bar.getMaximum();

        layout();
    }

    @Override
    public void setProgress(final double percentage) {
        if (isDisposed()) {
            return;
        }
        getDisplay().asyncExec(() -> {
            if (bar.isDisposed()) {
                return;
            }
            int percentageInt = (int) (percentage * maximum);
            bar.setSelection(percentageInt);
        });
    }

    @Override
    public void setStatus(final String statusText) {
        if (isDisposed()) {
            return;
        }
        getDisplay().asyncExec(() -> {
            if (statusLabel.isDisposed()) {
                return;
            }
            statusLabel.setText(statusText);
            statusLabel.setSize(statusLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        });
    }
}
