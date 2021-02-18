package com.cevelop.templator.plugin.view.tree;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

import com.cevelop.templator.plugin.handler.ViewOpener;
import com.cevelop.templator.plugin.util.EclipseUtil;
import com.cevelop.templator.plugin.util.SettingsCache;
import com.cevelop.templator.plugin.view.components.BluePortalContextMenu;
import com.cevelop.templator.plugin.view.components.Connection;
import com.cevelop.templator.plugin.view.components.ConnectionCollection;
import com.cevelop.templator.plugin.view.components.DirectConnection;
import com.cevelop.templator.plugin.view.components.DirectConnectionContextMenu;
import com.cevelop.templator.plugin.view.components.GlobalToolBar;
import com.cevelop.templator.plugin.view.components.OrangePortalContextMenu;
import com.cevelop.templator.plugin.view.components.PortalConnection;
import com.cevelop.templator.plugin.view.components.ScrollAnimator;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;
import com.cevelop.templator.plugin.view.interfaces.IDirectConnectionClickCallback;
import com.cevelop.templator.plugin.view.interfaces.IDirectConnectionContextActionHandler;
import com.cevelop.templator.plugin.view.interfaces.INode;
import com.cevelop.templator.plugin.view.interfaces.IPortalClickCallback;
import com.cevelop.templator.plugin.view.interfaces.IPortalMenuActionHandler;
import com.cevelop.templator.plugin.view.interfaces.ITemplateView;
import com.cevelop.templator.plugin.view.interfaces.ITreeViewController;
import com.cevelop.templator.plugin.view.rendering.ConnectionCalculator;
import com.cevelop.templator.plugin.view.rendering.ConnectionRenderer;
import com.cevelop.templator.plugin.viewdata.ViewData;


public class TreeTemplateView extends ViewPart implements ITemplateView, ITreeViewController, IShowInTarget, IDirectConnectionClickCallback,
        IPortalClickCallback, IDirectConnectionContextActionHandler, IPortalMenuActionHandler {

    public static final String VIEW_ID = "com.cevelop.templator.plugin.view.tree.TreeTemplateView";

    public static final int MARGIN        = 15;
    public static final int BORDER_MARGIN = 30;
    // multiplies the font size to create dynamic gap between entries
    public static final int CONNECTION_COLUMN_WIDTH_MULTIPLIER = 12;
    private static boolean  asyncLoading                       = true;

    private Composite    treeComposite;
    private ScrolledForm form;

    private Composite borderExtension;

    private TreeEntry           rootEntry;
    private TreeEntryCollection entryCollection = new TreeEntryCollection();

    private ScrollAnimator scrollAnimator;

    private ConnectionCalculator connectionCalculator = new ConnectionCalculator();
    private ConnectionCollection connectionCollection;

    @Override
    public void setRootData(ViewData data) {
        clear();
        rootEntry = new TreeEntry(treeComposite, this, this, data, asyncLoading);
        entryCollection.addRootEntry(rootEntry);
        reflow();
    }

    @Override
    public void addSubEntry(TreeEntry parent, int nameIndex, ViewData data) {
        // add entry if it is not possible to take it away (if it does not already exist)
        if (!entryCollection.removeConnection(parent, nameIndex)) {
            TreeEntryCollectionNode parentNode = entryCollection.getNode(parent);
            TreeEntryCollectionNode node = entryCollection.getCommonNodeInNextLevels(data, parent);
            if (node == null) {
                node = entryCollection.getCommonNodeInPreviousLevels(data, parent);
                if (node == null) {
                    // Node is not opened yet. A new node is created
                    TreeEntry newEntry = new TreeEntry(treeComposite, this, this, data, asyncLoading);
                    entryCollection.addEntry(newEntry, parent, nameIndex);
                } else {
                    // Node is already available in a previous level. A Portal is created.
                    entryCollection.addPortal(node, parentNode, nameIndex);
                }
            } else {
                // Node is already available in a following level. A connection is created.
                entryCollection.addConnection(node, parentNode, nameIndex);
            }
            parent.setRectangleState(nameIndex, TemplatorRectangleState.ACTIVE);
        }
        reflow();
    }

    @Override
    public void closeEntry(TreeEntry treeEntry) {
        entryCollection.removeEntry(treeEntry);
        reflow();
    }

    @Override
    public void createPartControl(final Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        form.getBody().setLayout(new FillLayout());

        form.setText(getErrorMessage());
        Font font = new Font(form.getDisplay(), new FontData(form.getFont().toString(), 12, SWT.NONE));
        form.setFont(font);
        form.setForeground(form.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        form.setBackground(form.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        treeComposite = new Composite(form.getBody(), SWT.DOUBLE_BUFFERED);
        treeComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        treeComposite.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                treeComposite.setFocus();
            }
        });

        borderExtension = new Composite(treeComposite, SWT.NONE);
        borderExtension.setSize(BORDER_MARGIN, BORDER_MARGIN);
        borderExtension.setVisible(false);

        scrollAnimator = new ScrollAnimator(form);
        SelectionListener stopScrollListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                scrollAnimator.stopScrolling();
            }
        };
        form.getHorizontalBar().addSelectionListener(stopScrollListener);
        form.getVerticalBar().addSelectionListener(stopScrollListener);

        connectionCollection = new ConnectionCollection(treeComposite, this, this);
        new ConnectionRenderer(treeComposite, connectionCollection);
        new GlobalToolBar(this, getViewSite());
    }

    @Override
    public void setFocus() {

    }

    @Override
    public void reflow() {
        entryCollection.sortTreeSetList();
        recalculateLayout();
        entryCollection.setPortalNumbers();
        connectionCollection.clear();
        for (INode node : entryCollection.getNodes()) {
            for (Connection connection : connectionCalculator.calculateConnections(node)) {
                connectionCollection.addConnection(connection);
            }
        }
        form.reflow(true);
        treeComposite.redraw();
    }

    @Override
    public void entryScrolled(TreeEntry treeEntry) {
        treeComposite.redraw();
    }

    public void recalculateLayout() {
        int currentLeft = BORDER_MARGIN;
        int maxTop = 0;
        final int CONNECTION_COLUMN_WIDTH = EclipseUtil.getTextFontSize() * CONNECTION_COLUMN_WIDTH_MULTIPLIER;

        if (entryCollection.getEntries().size() == 0) {
            form.setText(getErrorMessage());
        } else {
            form.setText(null);
        }

        for (TreeSet<TreeEntry> column : entryCollection.getEntries()) {
            int maxWidth = 0;
            int currentTop = BORDER_MARGIN;

            for (TreeEntry entry : column) {
                entry.setLocation(currentLeft, currentTop);
                currentTop += entry.getSize().y + MARGIN;

                if (entry.getSize().x > maxWidth) {
                    maxWidth = entry.getSize().x;
                }
                entry.updateRectangles();
            }
            currentLeft += maxWidth + CONNECTION_COLUMN_WIDTH;

            if (currentTop > maxTop) {
                maxTop = currentTop;
            }
        }
        borderExtension.setLocation(currentLeft, maxTop - MARGIN);
    }

    @Override
    public boolean show(ShowInContext context) {
        ViewOpener.showTemplateInfoUnderCursor();
        return true;
    }

    @Override
    public ScrolledForm getForm() {
        return form;
    }

    @Override
    public void minimizeAll() {
        for (TreeSet<TreeEntry> column : entryCollection.getEntries()) {
            for (TreeEntry entry : column) {
                entry.minimize();
            }
        }
        reflow();
    }

    @Override
    public void maximizeAll() {
        for (TreeSet<TreeEntry> column : entryCollection.getEntries()) {
            for (TreeEntry entry : column) {
                entry.maximize();
            }
        }
        reflow();
    }

    @Override
    public void refreshFromEditor() {
        ViewOpener.showTemplateInfoUnderCursor();
    }

    @Override
    public void clear() {
        entryCollection.clear();
        reflow();
    }

    @Override
    public void closeAllSubEntries(TreeEntry treeEntry) {
        List<TreeEntry> subEntries = entryCollection.getAllSubEntries(treeEntry);
        for (TreeEntry entry : subEntries) {
            entryCollection.removeEntry(entry);
        }
        reflow();
    }

    @Override
    public void minimizeAllSubEntries(TreeEntry treeEntry) {
        List<TreeEntry> subEntries = entryCollection.getAllSubEntries(treeEntry);
        for (TreeEntry entry : subEntries) {
            entry.minimize();
        }
        reflow();
    }

    @Override
    public void maximizeAllSubEntries(TreeEntry treeEntry) {
        List<TreeEntry> subEntries = entryCollection.getAllSubEntries(treeEntry);
        for (TreeEntry entry : subEntries) {
            entry.maximize();
        }
        reflow();
    }

    @Override
    public void scrollToEntry(TreeEntry treeEntry) {
        scrollAnimator.scrollTo(treeEntry);
    }

    @Override
    public void directConnectionClicked(DirectConnection directConnection) {
        connectionCollection.resetBezierCurveWidth();
        directConnection.setActive(true);
        reflow();
        new DirectConnectionContextMenu(treeComposite, directConnection, this);
    }

    public void removeConnection(Connection connection) {
        entryCollection.removeConnection(connection);
        reflow();
    }

    @Override
    public void bluePortalClicked(Set<PortalConnection> portalConnections) {
        if (portalConnections.size() == 1) {
            PortalConnection firstPortalConnection = portalConnections.iterator().next();
            scrollToEntry(firstPortalConnection.getEnd().getEntry());
        } else {
            new BluePortalContextMenu(treeComposite, portalConnections, this);
        }
    }

    @Override
    public void orangePortalClicked(TreeEntry treeEntry) {
        TreeEntryCollectionNode node = entryCollection.getNode(treeEntry);
        List<PortalConnection> portalConnections = node.getPortalConnections();
        if (portalConnections.size() == 1) {
            scrollToEntry(portalConnections.get(0).getStart().getEntry());
        } else {
            new OrangePortalContextMenu(treeComposite, portalConnections, this);
        }
    }

    @Override
    public void directConnectionContextActionPerformed(DirectConnection directConnection, DirectConnectionContextAction action) {
        switch (action) {
        case CUT:
            removeConnection(directConnection);
            break;
        default:
        case TRAVEL_TO_START:
            scrollToEntry(directConnection.getStart().getEntry());
            break;
        case TRAVEL_TO_END:
            scrollToEntry(directConnection.getEnd().getEntry());
            break;
        }
    }

    @Override
    public void contextActionPerformed(TreeEntry treeEntry, PortalAction action) {
        switch (action) {
        default:
        case TRAVEL_TO:
            scrollToEntry(treeEntry);
            break;
        }
    }

    public final TreeEntryCollectionNode getRootTreeEntry() {
        return entryCollection.getNode(rootEntry);
    }

    public final TreeEntryCollection getTreeEntryCollection() {
        return entryCollection;
    }

    public static void setAsyncLoading(boolean state) {
        asyncLoading = state;
    }

    private String getErrorMessage() {
        StringBuilder sb = new StringBuilder("\nNO TEMPLATE INFORMATION AVAILABLE");
        sb.append("\n\nPlease select one of the following types and refresh this view:\n");
        if (SettingsCache.isTracingNormalFunctions()) {
            sb.append("\n- Function");
        }
        if (SettingsCache.isTracingNormalClasses()) {
            sb.append("\n- Class");
        }
        sb.append("\n- Function Template");
        sb.append("\n- Class Template");
        sb.append("\n- Alias Template");
        sb.append("\n- Variable Template");
        return sb.toString();
    }
}
