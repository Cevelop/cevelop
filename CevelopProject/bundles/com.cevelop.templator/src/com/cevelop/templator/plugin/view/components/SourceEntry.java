package com.cevelop.templator.plugin.view.components;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.util.SettingsCache;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;
import com.cevelop.templator.plugin.view.interfaces.IActionButtonCallback;
import com.cevelop.templator.plugin.view.interfaces.ISubNameClickCallback;
import com.cevelop.templator.plugin.view.interfaces.ITreeViewController;


public class SourceEntry extends ResizableComposite {

    public static final Point  DEFAULT_SIZE  = new Point(600, 750);
    private static final Point SIZE_INCREASE = new Point(40, 30);

    private EntryHeader entryHeader;

    protected InfoBar infoBar;

    private SourceTextField sourceText;

    private ScrolledComposite scrolledComposite;
    protected SearchBar       searchBar;

    protected RectangleCollection rectangleCollection;

    public SourceEntry(Composite parent, ITreeViewController controller, int style) {
        super(parent, controller, style);
        setLayout(new GridLayout());
    }

    protected void createComponents() {

        entryHeader = new EntryHeader(this, SWT.NONE);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        entryHeader.setLayoutData(gridData);

        infoBar = new InfoBar(this, SWT.NONE);

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        infoBar.setLayoutData(gridData);

        scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        scrolledComposite.setLayoutData(gridData);

        sourceText = new SourceTextField(scrolledComposite);

        scrolledComposite.setLayout(new FillLayout());
        scrolledComposite.setContent(sourceText.getTextWidget());
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        scrolledComposite.getVerticalBar().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onScroll();
            }
        });

        searchBar = new SearchBar(this, sourceText.getTextWidget(), scrolledComposite);
    }

    protected void setDescription(String titleText, List<String> descriptions) {
        entryHeader.setDescription(titleText, descriptions);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        refreshSize();
    }

    @Override
    public void setSize(Point size) {
        setSize(size.x, size.y);
    }

    public void refreshSize() {
        if (entryHeader != null) {
            entryHeader.updateSize();
        }
    }

    protected void setSourceText(String text) {
        sourceText.setText(text);
        Point optimalTextSize = sourceText.getTextWidget().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        scrolledComposite.setMinSize(optimalTextSize);
        searchBar.setSearchText(text);
    }

    protected void setRectangleMap(Map<Integer, IRegion> rectMap, ISubNameClickCallback clickCallback) {
        rectangleCollection = new RectangleCollection(sourceText.getTextWidget(), rectMap, clickCallback);
    }

    protected void setActionButtonCallback(final IActionButtonCallback callback) {
        entryHeader.setActionButtonCallback(callback);
    }

    public int getRectOffset(int index) {
        TemplatorRectangle rect = rectangleCollection.get(index);
        int scrollSelection = scrolledComposite.getVerticalBar().getSelection();
        int headerHeight = getHeightOfCompleteHeader();
        return rect.getY() + rect.getHeigth() / 2 + headerHeight + 15 - scrollSelection;
    }

    public int getRectHeight(int index) {
        return rectangleCollection.get(index).getHeigth();
    }

    public void setRectangleState(int index, TemplatorRectangleState state) {
        rectangleCollection.get(index).setState(state);
    }

    public TemplatorRectangleState getRectangleState(int index) {
        return rectangleCollection.get(index).getState();
    }

    public int getRectangleCount() {
        return rectangleCollection.size();
    }

    protected Point calculateOptimalSize() {
        Point optimalTextSize = sourceText.getTextWidget().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        int width = optimalTextSize.x + SIZE_INCREASE.x;
        width = width > minSize.x ? width : minSize.x;
        int height = optimalTextSize.y + getHeightOfCompleteHeader() + SIZE_INCREASE.y;

        if (SettingsCache.hasMaxWidth()) {
            width = width < DEFAULT_SIZE.x ? width : DEFAULT_SIZE.x;
        }
        if (SettingsCache.hasMaxHeight()) {
            height = height < DEFAULT_SIZE.y ? height : DEFAULT_SIZE.y;
        }

        return new Point(width, height);
    }

    protected Point calculateMinimizedSize() {
        int width = minSize.x;
        int height = getHeightOfCompleteHeader() + 14;
        return new Point(width, height);
    }

    protected void onScroll() {}

    public void packSourcePart(boolean changed) {
        sourceText.packSourcePart(changed);
    }

    private int getHeightOfCompleteHeader() {
        return entryHeader.getSize().y + infoBar.getSize().y + 2 + searchBar.getHeight();
    }

    public String getTitle() {
        return entryHeader.getDescription();
    }

    public void updateRectangles() {
        if (rectangleCollection != null) {
            rectangleCollection.updateRectangles();
        }
    }
}
