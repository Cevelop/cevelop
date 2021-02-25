package com.cevelop.templator.instancespy.views;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;
import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;


/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class InstanceView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "com.cevelop.templator.instancespy.views.InstanceView";

    private Tree       tree;
    private TreeViewer viewer;
    private Action     updateTemplateAction;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(Composite parent) {
        tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tree.setHeaderVisible(true);
        viewer = new TreeViewer(tree);
        viewer.setContentProvider(new TemplateInstanceContentProvider());
        viewer.setLabelProvider(new TemplateLabelProvider());
        tree.setLinesVisible(true);

        getSite().setSelectionProvider(viewer);
        makeActions();
        contributeToActionBars();
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(updateTemplateAction);
        manager.add(new Separator());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(updateTemplateAction);
    }

    private void updateTable() {
        IIndex index = EclipseUtil.getIndexFromCurrentCProject();
        try {
            IASTTranslationUnit ast = EclipseUtil.getASTFromIndex(index);
            IASTName selectedName = EclipseUtil.getNameUnderCursor(ast);
            ICPPTemplateDefinition selectedTemplate = findSelectedTemplate(selectedName);
            if (selectedTemplate == null) {
                showMessage("No template in selection");
                return;
            }
            IIndexBinding templateIndexBinding = index.adaptBinding(selectedTemplate);
            updateColumns(selectedTemplate);
            viewer.setInput(templateIndexBinding);
            updateColumnWidths();
        } catch (TemplatorException e) {

        }
    }

    private void updateColumnWidths() {
        for (TreeColumn column : tree.getColumns()) {
            column.pack();
        }
    }

    private void updateColumns(ICPPTemplateDefinition selectedTemplate) {
        tree.setRedraw(false);
        for (TreeColumn column : tree.getColumns()) {
            column.dispose();
        }
        TreeColumn templateColumn = new TreeColumn(tree, SWT.LEFT);
        templateColumn.setAlignment(SWT.LEFT);
        templateColumn.setText("Template ID");
        templateColumn.setWidth(100);
        Arrays.stream(selectedTemplate.getTemplateParameters()).forEach(parameter -> {
            TreeColumn parameterColumn = new TreeColumn(tree, SWT.LEFT);
            parameterColumn.setAlignment(SWT.LEFT);
            parameterColumn.setText(parameter.getName());
            parameterColumn.setWidth(60);
        });
        tree.setRedraw(true);
    }

    private ICPPTemplateDefinition findSelectedTemplate(IASTName name) {
        if (name == null) {
            return null;
        }
        IBinding nameBinding = name.resolveBinding();
        if (nameBinding instanceof ICPPTemplateDefinition) {
            return (ICPPTemplateDefinition) nameBinding;
        }
        if (nameBinding instanceof ICPPTemplateInstance) {
            ICPPTemplateInstance templateInstance = (ICPPTemplateInstance) nameBinding;
            return templateInstance.getTemplateDefinition();
        }
        return null;
    }

    private void makeActions() {
        updateTemplateAction = new Action() {

            @Override
            public void run() {
                updateTable();
            }

        };
        updateTemplateAction.setText("Show instances");
        updateTemplateAction.setToolTipText("Show instance of surrounding template");
        updateTemplateAction.setImageDescriptor(ImageDescriptor.createFromImage(ImageCache.get(ImageID.REFRESH)));

    }

    private void showMessage(String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), "Template Instance View", message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
}
