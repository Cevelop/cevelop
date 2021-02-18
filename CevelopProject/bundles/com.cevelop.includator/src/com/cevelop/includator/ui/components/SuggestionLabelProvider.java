package com.cevelop.includator.ui.components;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.ui.helpers.TooltipColumnListener;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class SuggestionLabelProvider extends CellLabelProvider implements TooltipColumnListener {

    private static final String CHECKED_KEY = "com.cevelop.includator.ui.suggestiontree.cell.checked";
    private static final String UNCHECK_KEY = "com.cevelop.includator.ui.suggestiontree.cell.unchecked";

    private List<SuggestionSolutionOperation> columnOperations;
    private int                               nextColumnId;

    public SuggestionLabelProvider(Control control) {
        if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
            JFaceResources.getImageRegistry().put(UNCHECK_KEY, makeShot(control, false));
            JFaceResources.getImageRegistry().put(CHECKED_KEY, makeShot(control, true));
        }
    }

    @Override
    public void update(ViewerCell cell) {
        if (cell.getColumnIndex() == 0) {
            cell.setText(cell.getElement().toString());
        } else {
            SuggestionTreeElement element = (SuggestionTreeElement) cell.getElement();
            SuggestionSolutionOperation operation = columnOperations.get(cell.getColumnIndex() - 1);
            if (element.hasOperation(operation)) {
                String imgKey = element.isChecked(operation) ? CHECKED_KEY : UNCHECK_KEY;
                cell.setImage(JFaceResources.getImageRegistry().get(imgKey));
            }
        }
    }

    @Override
    public String getToolTipText(Object element) {
        Suggestion<?> suggestion = ((SuggestionTreeElement) element).getSuggestion();
        if (suggestion == null) {
            return "Checks this option on all children.";
        }
        switch (nextColumnId) {
        case -1:
            return null;
        case 0:
            return suggestion.getDescription();
        default:
            return columnOperations.get(nextColumnId - 1).getToolTipFor(suggestion);
        }

    }

    /**
     * Note that due to swt bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=230246, the 'y' part of the point returned cannot be 0 (which
     * unfortunately is the default value).
     */
    @Override
    public Point getToolTipShift(Object object) {
        return new Point(15, 15);
    }

    private Image makeShot(Control control, boolean type) {
        Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);
        Button button = new Button(shell, SWT.RADIO);
        button.setSelection(type);
        button.setLocation(1, 1);
        Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
        bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
        button.setSize(bsize);
        shell.setSize(bsize);
        shell.open();
        control.getDisplay().update();
        GC gc = new GC(button);
        Image image = new Image(control.getDisplay(), bsize.x, bsize.y);
        gc.copyArea(image, 0, 0);
        gc.dispose();
        shell.close();

        //	    ImageData imageData = image.getImageData();
        //	    return new Image(control.getDisplay(), imageData);

        return image;
    }

    public void setColumnOperationList(List<SuggestionSolutionOperation> columnOperations) {
        this.columnOperations = columnOperations;
    }

    @Override
    public void nextColumnId(int nextId) {
        this.nextColumnId = nextId;
    }
}
