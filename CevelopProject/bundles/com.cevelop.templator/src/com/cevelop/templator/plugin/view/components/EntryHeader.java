package com.cevelop.templator.plugin.view.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.cevelop.templator.plugin.view.interfaces.IActionButtonCallback;
import com.cevelop.templator.plugin.view.interfaces.IActionButtonCallback.ButtonAction;


public class EntryHeader extends Composite {

    private Composite parent;

    private Composite descriptionArea;

    private Label       titleLabel;
    private List<Label> descLabels;

    private ActionButtons actionButtons;

    public EntryHeader(Composite parent, int style) {
        super(parent, style);

        this.parent = parent;

        setLayout(new GridLayout(3, false));

        descriptionArea = new Composite(this, SWT.NONE);
        GridData descriptionGridData = new GridData();
        descriptionGridData.horizontalAlignment = SWT.FILL;
        descriptionGridData.grabExcessHorizontalSpace = true;
        descriptionArea.setLayoutData(descriptionGridData);
        descriptionArea.setLayout(new GridLayout());
    }

    protected void setDescription(String titleText, List<String> descriptions) {

        titleLabel = new Label(descriptionArea, SWT.NONE);
        titleLabel.setText(titleText);

        descLabels = new ArrayList<>();
        for (int i = 0; i < descriptions.size(); i++) {

            Label descLabel = new Label(descriptionArea, SWT.NONE);
            descLabel.setText(descriptions.get(i));

            if (i == 0) {
                GridData gridData = new GridData();
                gridData.verticalIndent = 10;
                descLabel.setLayoutData(gridData);
            }
            descLabels.add(descLabel);
        }
    }

    public String getDescription() {
        return titleLabel.getText();
    }

    protected void setActionButtonCallback(final IActionButtonCallback callback) {
        actionButtons = new ActionButtons(this, SWT.NONE, callback);
        GridData gridData = new GridData();
        gridData.verticalAlignment = SWT.TOP;
        gridData.horizontalAlignment = SWT.RIGHT;
        actionButtons.setLayoutData(gridData);

        MouseAdapter maximizeMouseListener = new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                callback.actionPerformed(ButtonAction.MAXIMIZE);
            }
        };
        parent.addMouseListener(maximizeMouseListener);
        addMouseListener(maximizeMouseListener);
        descriptionArea.addMouseListener(maximizeMouseListener);
    }

    public void updateSize() {
        layout();
    }
}
