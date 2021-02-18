package com.cevelop.includator.ui.helpers;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

import com.cevelop.includator.ui.components.SuggestionTree;


/**
 * Need for this Class and also TooltipColumnListener arises because the method CellLabelProvider.getToolTipText(Object element) only receives the row
 * element of the table/tree instead of the ViewerCell (as in method update) which also contains the column index.
 *
 * Note that using this class would cause multi-threading issues when not used only in the ui-thread.
 */
public class ColumnNotifyingToolTipSupport extends ColumnViewerToolTipSupport {

    private final SuggestionTree        viewer;
    private final TooltipColumnListener tooltipColumnListener;

    public ColumnNotifyingToolTipSupport(SuggestionTree viewer, TooltipColumnListener tooltipColumnListener) {
        super(viewer, ToolTip.NO_RECREATE, false);
        this.viewer = viewer;
        this.tooltipColumnListener = tooltipColumnListener;
    }

    @Override
    protected boolean shouldCreateToolTip(Event event) {
        ViewerRow row = viewer.getViewerRow(new Point(event.x, event.y));
        viewer.getControl().setToolTipText(""); //$NON-NLS-1$
        Point point = new Point(event.x, event.y);
        if (row != null) {
            ViewerCell cell = row.getCell(point);
            if (cell != null) {
                tooltipColumnListener.nextColumnId(cell.getColumnIndex());
            } else {
                tooltipColumnListener.nextColumnId(-1);
            }
        } else {
            tooltipColumnListener.nextColumnId(-1);
        }
        return super.shouldCreateToolTip(event);
    }
}
