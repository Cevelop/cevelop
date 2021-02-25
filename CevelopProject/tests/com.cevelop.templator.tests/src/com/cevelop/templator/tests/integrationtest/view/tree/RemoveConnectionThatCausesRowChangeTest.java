package com.cevelop.templator.tests.integrationtest.view.tree;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.view.tree.TreeEntry;
import com.cevelop.templator.tests.RectangleStateCollection;
import com.cevelop.templator.tests.TreeViewTest;
import com.cevelop.templator.tests.TreeViewTestInfo;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RemoveConnectionThatCausesRowChangeTest extends TreeViewTest {

    private static TreeEntry entry0, entry1, entry2;

    //
    //   +-----+       +-----+      +-----+
    //   |     |       |     |      |     |
    //   |     +-------+  1  +---+--+  3  |
    //   |     |       |     |   |  |     |
    //   |  0  |       +-----+   |  +-----+
    //   |     +---+             |
    //   |     |   |   +-----+   |  +-----+
    //   +-----+   |   |     +------+     |
    //             +---+  2  |   |  |  4  |
    //                 |     +-- +  |     |
    //                 +-----+      +-----+
    //
    @Test
    public void test01_openAll() throws TemplatorException {
        createView();
        entry0 = getRootEntry();
        clickName(entry0, 0);
        entry1 = findChild(entry0, 0);
        clickName(entry1, 0);
        clickName(entry0, 1);
        entry2 = findChild(entry0, 1);
        clickName(entry2, 0);
        clickName(entry2, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test02_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test03_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry();
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //   +-----+       +-----+      +-----+
    //   |     |       |     |      |     |
    //   |     +-------+  1  |   +--|  4  |
    //   |     |       |     |   |  |     |
    //   |  0  |       +-----+   |  +-----+
    //   |     +---+             |
    //   |     |   |   +-----+   |  +-----+
    //   +-----+   |   |     +---+  +     |
    //             +---+  2  |      |  3  |
    //                 |     +------+     |
    //                 +-----+      +-----+
    //
    @Test
    public void test04_removeConnection() {
        clickName(entry1, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test05_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test06_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry();
        expected.addEntry();
        assertRectangleStates(expected);
    }

    @Test
    public void test07_removeAll() {
        closeAllEntries();

        List<TreeViewTestInfo> expected = new ArrayList<>();
        assertTreeViewTestInfo(expected);
    }
}
