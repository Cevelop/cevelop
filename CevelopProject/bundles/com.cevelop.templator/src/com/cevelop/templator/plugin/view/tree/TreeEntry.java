package com.cevelop.templator.plugin.view.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.ILoadingProgress;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.components.AsyncEntryLoader;
import com.cevelop.templator.plugin.view.components.ProblemsDialog;
import com.cevelop.templator.plugin.view.components.RectangleContextMenu;
import com.cevelop.templator.plugin.view.components.SourceEntry;
import com.cevelop.templator.plugin.view.components.SyncEntryLoader;
import com.cevelop.templator.plugin.view.interfaces.IActionButtonCallback;
import com.cevelop.templator.plugin.view.interfaces.IEntryLoadCallback;
import com.cevelop.templator.plugin.view.interfaces.IEntryLoader;
import com.cevelop.templator.plugin.view.interfaces.IPortalClickCallback;
import com.cevelop.templator.plugin.view.interfaces.IRectangleContextMenuActionHandler;
import com.cevelop.templator.plugin.view.interfaces.ISubNameClickCallback;
import com.cevelop.templator.plugin.view.interfaces.ITreeViewController;
import com.cevelop.templator.plugin.view.listeners.OrangePortalListener;
import com.cevelop.templator.plugin.viewdata.ViewData;


public class TreeEntry extends SourceEntry implements ISubNameClickCallback, IActionButtonCallback, IRectangleContextMenuActionHandler,
        IEntryLoadCallback {

    private ITreeViewController  controller;
    private IPortalClickCallback portalCallback;
    private ViewData             data;
    private int                  portalNumber;
    private int                  colorId;
    private boolean              minimized;

    public TreeEntry(Composite parent, ITreeViewController controller, IPortalClickCallback portalCallback, ViewData data, boolean asyncLoading) {
        super(parent, controller, SWT.BORDER);

        this.controller = controller;
        this.data = data;
        this.portalCallback = portalCallback;
        minimized = false;
        IEntryLoader entryLoader;
        if (asyncLoading) {
            entryLoader = new AsyncEntryLoader(this, data.getTitle(), this);
        } else {
            entryLoader = new SyncEntryLoader(this, data.getTitle(), this);
        }
        entryLoader.start();
        controller.reflow();
    }

    @Override
    public void loadOperation(ILoadingProgress loadingProgress) throws TemplatorException {
        data.prepareForView(loadingProgress);
    }

    @Override
    public void loadComplete() {
        createComponents();

        setActionButtonCallback(this);
        setSourceText(data.getDataText());
        setDescription(data.getTitle(), data.getDescription());
        setRectangleMap(data.getSubSegments(), this);
        setTypeIcon(data.getTypeIcon());

        layout();
        setSize(calculateOptimalSize());

        minSize = calculateMinimizedSize();
        controller.reflow();
        controller.scrollToEntry(this);
        packSourcePart(true);
        infoBar.setOrangePortalListener(new OrangePortalListener(this, this, portalCallback));
    }

    @Override
    public void loadException(Throwable e) {
        controller.closeEntry(this);
        controller.reflow();
    }

    @Override
    public void nameClicked(int nameIndex, ClickAction clickAction) {
        switch (clickAction) {
        case CTRL_LEFT_CLICK:
            data.navigateToSubName(nameIndex);
            break;
        case LEFT_CLICK:
            openName(nameIndex);
            break;
        case RIGHT_CLICK:
            new RectangleContextMenu(this, nameIndex, this);
            break;
        default:
            break;
        }
    }

    @Override
    public void contextActionPerformed(int nameIndex, RectangleContextAction action) {
        switch (action) {
        case GO_TO_SOURCE:
            data.navigateToSubName(nameIndex);
            break;
        case OPEN_CLOSE:
            openName(nameIndex);
            break;
        default:
            break;
        }
    }

    @Override
    public void actionPerformed(ButtonAction action) {
        switch (action) {
        case MINIMIZE:
            minimize();
            controller.reflow();
            break;
        case MAXIMIZE:
            maximize();
            controller.reflow();
            break;
        case CLOSE:
            controller.closeEntry(this);
            break;
        case GO_TO_SOURCE:
            data.navigateToName();
            break;
        case CLOSE_ALL:
            controller.closeAllSubEntries(this);
            break;
        case MINIMIZE_ALL:
            controller.minimizeAllSubEntries(this);
            break;
        case MAXIMIZE_ALL:
            controller.maximizeAllSubEntries(this);
            break;
        case SEARCH:
            searchBar.show();
            break;
        case PROBLEMS:
            openProblemsDialog();
            break;
        default:
            break;
        }
    }

    private void openName(int nameIndex) {
        ViewData subData = data.getSubNameData(nameIndex);
        controller.addSubEntry(this, nameIndex, subData);
    }

    public void maximize() {
        setSize(calculateOptimalSize());
        minimized = false;
    }

    public void minimize() {
        setSize(calculateMinimizedSize());
        minimized = true;
    }

    private void openProblemsDialog() {
        ProblemsDialog problemsDialog = new ProblemsDialog(this.getShell(), data.getSubNameErrors());
        problemsDialog.open();
    }

    @Override
    protected void onScroll() {
        controller.entryScrolled(this);
    }

    @Override
    public boolean equals(Object obj) {
        return data.equals(((TreeEntry) obj).data);
    }

    public ViewData getViewData() {
        return data;
    }

    public void setTypeIcon(ImageID imageID) {
        infoBar.setTypeIcon(imageID);
    }

    public int getPortalNumber() {
        return portalNumber;
    }

    public void enablePortal(int id) {
        portalNumber = id;
        infoBar.enablePortal(id);
    }

    public void disablePortal() {
        if (infoBar != null) { // during loading of the data infoBar is not set yet
            infoBar.disablePortal();
        }
    }

    public void setRectangleColorId(int colorId, int nameIndex) {
        rectangleCollection.get(nameIndex).setColorId(colorId);
    }

    public int getRectangleColorId(int nameIndex) {
        return rectangleCollection.get(nameIndex).getColorId();
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getColorId() {
        return colorId;
    }

    public boolean isMinimized() {
        return minimized;
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TreeEntry\n");
        sb.append("\t title: " + getViewData().getTitle() + '\n');
        sb.append("\t viewData: \n" + getViewData().getDataText().replaceAll("(?m)^", "\t\t") + '\n');
        return sb.toString();
    }
}
