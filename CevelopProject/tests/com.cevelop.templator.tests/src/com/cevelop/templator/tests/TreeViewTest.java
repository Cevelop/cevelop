package com.cevelop.templator.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.AfterClass;
import org.junit.Assert;

import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.handler.ViewOpener;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.view.components.Connection;
import com.cevelop.templator.plugin.view.components.ConnectionStart;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;
import com.cevelop.templator.plugin.view.interfaces.ISubNameClickCallback.ClickAction;
import com.cevelop.templator.plugin.view.tree.TreeEntry;
import com.cevelop.templator.plugin.view.tree.TreeEntryCollection;
import com.cevelop.templator.plugin.view.tree.TreeEntryCollectionNode;
import com.cevelop.templator.plugin.view.tree.TreeTemplateView;
import com.cevelop.templator.plugin.viewdata.ViewData;


public class TreeViewTest extends TemplatorMultiLevelResolutionTest {

    private static TreeTemplateView                templateView;
    protected static final TemplatorRectangleState ACTIVE = TemplatorRectangleState.ACTIVE;
    protected static final TemplatorRectangleState IDLE   = TemplatorRectangleState.IDLE;

    protected void createView() throws TemplatorException {
        TreeTemplateView.setAsyncLoading(false);
        templateView = ViewOpener.openView(TreeTemplateView.VIEW_ID);
        ResolvedName resolvedName = getMainAsResolvedName();
        resolvedName.getInfo().searchSubNames(new NullLoadingProgress());
        ViewData viewData = new ViewData(resolvedName);
        templateView.setRootData(viewData);
    }

    @AfterClass
    public static void clear() throws Exception {
        templateView.clear();
        templateView.dispose();
    }

    protected TreeEntry getRootEntry() {
        return templateView.getRootTreeEntry().getEntry();
    }

    protected void clickName(TreeEntry treeEntry, int index) {
        treeEntry.nameClicked(index, ClickAction.LEFT_CLICK);
    }

    private List<TreeViewTestInfo> getInformationOfEntries() {
        List<TreeViewTestInfo> actualList = new ArrayList<>();
        TreeEntryCollection entryCollection = templateView.getTreeEntryCollection();
        List<TreeSet<TreeEntry>> entries = entryCollection.getEntries();
        for (TreeSet<TreeEntry> column : entries) {
            for (TreeEntry entry : column) {
                TreeEntryCollectionNode node = entryCollection.getNode(entry);
                TreeViewTestInfo info = new TreeViewTestInfo(node);
                actualList.add(info);
            }
        }
        return actualList;
    }

    private RectangleStateCollection getRectangleStatesOfEntries() {
        RectangleStateCollection rectangleStates = new RectangleStateCollection();
        TreeEntryCollection entryCollection = templateView.getTreeEntryCollection();
        List<TreeSet<TreeEntry>> entries = entryCollection.getEntries();
        for (TreeSet<TreeEntry> column : entries) {
            for (TreeEntry entry : column) {
                rectangleStates.addEntry(entry);
            }
        }
        return rectangleStates;
    }

    protected TreeEntry findChild(TreeEntry treeEntry, int index) {
        TreeEntryCollection entryCollection = templateView.getTreeEntryCollection();
        TreeEntryCollectionNode node = entryCollection.getNode(treeEntry);
        for (Connection connection : node.getConnections()) {
            ConnectionStart start = connection.getStart();
            if (start.getEntry() == treeEntry && start.getNameIndex() == index) {
                return connection.getEnd().getEntry();
            }
        }
        return null;
    }

    protected void closeAllEntries() {
        templateView.clear();
    }

    protected void closeAllSubEntries(TreeEntry treeEntry) {
        templateView.closeAllSubEntries(treeEntry);
    }

    protected void closeEntry(TreeEntry treeEntry) {
        templateView.closeEntry(treeEntry);
    }

    protected void assertColors() {
        TreeEntryCollection entryCollection = templateView.getTreeEntryCollection();
        for (TreeSet<TreeEntry> column : entryCollection.getEntries()) {
            for (TreeEntry entry : column) {
                for (int i = 0; i < entry.getRectangleCount(); i++) {
                    assert shareSameColor(entry, i) : "colors of " + entry.getTitle() + " with index " + i + " is not the same";
                }
            }
        }
    }

    private boolean shareSameColor(TreeEntry start, int index) {
        Connection connection = findConnection(start, index);
        if (connection == null) {
            return true;
        }
        List<Integer> colors = new ArrayList<>();
        colors.add(start.getRectangleColorId(index));
        colors.add(connection.getEnd().getEntry().getColorId());
        colors.add(connection.getColorId());
        return colors.stream().distinct().count() <= 1;
    }

    private Connection findConnection(TreeEntry start, int index) {
        TreeEntryCollection entryCollection = templateView.getTreeEntryCollection();
        TreeEntryCollectionNode startNode = entryCollection.getNode(start);
        for (TreeEntryCollectionNode child : startNode.getChildren()) {
            for (Connection connection : child.getConnections()) {
                ConnectionStart conStart = connection.getStart();
                if (!connection.isReversed() && conStart.getEntry() == start && conStart.getNameIndex() == index) {
                    return connection;
                }
            }
        }
        return null;
    }

    protected void assertRectangleStates(RectangleStateCollection expected) {
        Assert.assertEquals(expected, getRectangleStatesOfEntries());
    }

    protected void assertTreeViewTestInfo(List<TreeViewTestInfo> expected) {
        Assert.assertEquals(expected, getInformationOfEntries());
    }
}
