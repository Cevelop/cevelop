package com.cevelop.templator.plugin.view.components;

import java.util.LinkedList;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;


public class SearchBar {

    private Composite         parent;
    private StyledText        textWidget;
    private ScrolledComposite scrolledComposite;
    private Composite         searchComposite = null;

    private KeyListener toggleKeyListener;

    private Image arrowUpImage;
    private Image arrowDownImage;
    private Image closeImage;

    private Text  searchText;
    private Label occuranceLabel;

    private LinkedList<IRegion> foundRegionList;
    private int                 currentSelectedRegion = -1;

    private String lowerCaseText;

    public SearchBar(Composite parent, StyledText textWidget, ScrolledComposite scrolledComposite) {

        this.parent = parent;
        this.textWidget = textWidget;
        this.scrolledComposite = scrolledComposite;

        arrowUpImage = ImageCache.get(ImageID.ARROW_UP);
        arrowDownImage = ImageCache.get(ImageID.ARROW_DOWN);
        closeImage = ImageCache.get(ImageID.ICON_REMOVE);

        toggleKeyListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'f')) {
                    show();
                }
            }
        };
        textWidget.addKeyListener(toggleKeyListener);
    }

    private void performSearch() {
        String keyword = searchText.getText().toLowerCase();

        int cursor = -1;
        foundRegionList = new LinkedList<>();

        if (keyword.length() > 0) {
            while ((cursor = lowerCaseText.indexOf(keyword, cursor + 1)) >= 0) {
                Region region = new Region(cursor, keyword.length());
                foundRegionList.add(region);
            }
        }

        updateSearchResult();
    }

    private void updateSearchResult() {
        currentSelectedRegion = 0;
        updateOccuranceLabel();
        selectCurrentTextOccurance();
    }

    private void updateOccuranceLabel() {
        if (foundRegionList.size() == 0) {
            occuranceLabel.setText("0 of 0");
        } else {
            occuranceLabel.setText((currentSelectedRegion + 1) + " of " + foundRegionList.size());
        }
        searchComposite.layout();
    }

    private void selectCurrentTextOccurance() {
        if (foundRegionList.size() > 0) {
            int start = foundRegionList.get(currentSelectedRegion).getOffset();
            int length = foundRegionList.get(currentSelectedRegion).getLength();
            textWidget.setSelection(start, start + length);
            scrollToRegion(start, length);
        } else {
            textWidget.setSelection(0, 0);
        }
    }

    private void scrollToRegion(int offset, int length) {
        Point regionStartLocation = textWidget.getLocationAtOffset(offset);
        Point regionEndLocation = textWidget.getLocationAtOffset(offset + length);
        Rectangle viewBounds = new Rectangle(scrolledComposite.getOrigin().x, scrolledComposite.getOrigin().y, scrolledComposite
                .getClientArea().width, scrolledComposite.getClientArea().height);
        Point newOrigin = scrolledComposite.getOrigin();

        if (regionStartLocation.y < viewBounds.y || regionStartLocation.y > viewBounds.y + viewBounds.height) {
            newOrigin.y = regionStartLocation.y;
        }
        if (regionEndLocation.x < scrolledComposite.getClientArea().width) {
            newOrigin.x = 0;
        } else {
            newOrigin.x = regionEndLocation.x - scrolledComposite.getClientArea().width;
        }
        scrolledComposite.setOrigin(newOrigin);
    }

    private void changeSelectedRegion(int amount) {
        if (foundRegionList.size() > 0) {
            if (amount >= 0) {
                currentSelectedRegion += amount;
                currentSelectedRegion %= foundRegionList.size();
            } else {
                if (currentSelectedRegion == 0) {
                    currentSelectedRegion = foundRegionList.size();
                }
                currentSelectedRegion += amount;
            }
        }
        updateOccuranceLabel();
        selectCurrentTextOccurance();
    }

    private Composite createSearchBar() {
        Group bar = new Group(parent, SWT.NONE);
        bar.setText("Search");
        bar.setLayout(new GridLayout(5, false));
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        bar.setLayoutData(gridData);

        searchText = new Text(bar, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = SWT.CENTER;
        searchText.setLayoutData(gridData);
        searchText.addKeyListener(toggleKeyListener);
        searchText.addModifyListener(e -> performSearch());
        searchText.addTraverseListener(e -> {
            if (e.detail == SWT.TRAVERSE_RETURN) {
                changeSelectedRegion(1);
            }
            if (e.detail == SWT.TRAVERSE_ESCAPE) {
                hide();
            }
        });

        occuranceLabel = new Label(bar, SWT.NONE);
        occuranceLabel.setText("0 of 0");
        gridData = new GridData();
        gridData.verticalAlignment = SWT.CENTER;
        occuranceLabel.setLayoutData(gridData);

        ToolBar buttonBar = new ToolBar(bar, SWT.NONE);

        ToolItem previous = new ToolItem(buttonBar, SWT.PUSH);
        previous.setImage(arrowUpImage);
        previous.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeSelectedRegion(-1);
            }
        });

        ToolItem next = new ToolItem(buttonBar, SWT.NONE);
        next.setImage(arrowDownImage);
        next.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeSelectedRegion(1);
            }
        });

        ToolItem close = new ToolItem(buttonBar, SWT.NONE);
        close.setImage(closeImage);
        close.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                hide();
            }
        });

        bar.layout();

        return bar;
    }

    public void show() {
        if (searchComposite == null) {
            searchComposite = createSearchBar();
            Control[] children = parent.getChildren();
            searchComposite.moveAbove(children[1]);
            parent.layout();
            parent.getParent().redraw();
        }
        searchText.setFocus();
        searchText.selectAll();
    }

    public void hide() {
        searchComposite.dispose();
        searchComposite = null;
        parent.layout();
        parent.getParent().redraw();
        textWidget.setFocus();
    }

    public int getHeight() {
        return searchComposite == null ? 0 : searchComposite.getSize().y + 5;
    }

    public void setSearchText(String text) {
        lowerCaseText = text.toLowerCase();
    }
}
