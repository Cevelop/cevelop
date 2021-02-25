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

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.rts.junit4.RunFor;


@RunFor(rtsFile = "/resources/com.cevelop.templator.tests/integrationtest/view/tree/ExampleWithoutPortals.rts")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RemoveEntryTest extends TreeViewTest {

    private static TreeEntry entry0, entry1, entry2, entry3, entry4, entry5, entry6;

    private void buildInitialSituation() throws TemplatorException {
        createView();
        entry0 = getRootEntry();
        clickName(entry0, 0);
        entry1 = findChild(entry0, 0);
        clickName(entry1, 0);
        entry3 = findChild(entry1, 0);
        clickName(entry3, 0);
        entry6 = findChild(entry3, 0);
        clickName(entry1, 1);
        entry4 = findChild(entry1, 1);
        clickName(entry4, 0);
        clickName(entry0, 1);
        clickName(entry0, 2);
        entry2 = findChild(entry0, 2);
        clickName(entry2, 0);
        entry5 = findChild(entry2, 0);
        clickName(entry5, 0);
        clickName(entry2, 1);
        clickName(entry0, 3);
    }

    //
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +----+---+  6  |
    //  |     |   |      |     +-----+ |  |     |    |   |     |
    //  |     |   |      +-----+     | |  +-----+    |   +-----+
    //  |     |   |                  | |             |
    //  |  0  +------------------------+  +-----+    |
    //  |     |   |                  |    |     |    |
    //  |     |   |      +-----+     +-+--+  4  +----+
    //  |     +----------+     +--+    |  |     |    |
    //  |     |   |      |  2  |  |    |  +-----+    |
    //  |     +---+      |     +-------+             |
    //  |     |          +-----+  |       +-----+    |
    //  +-----+                   |       |     |    |
    //                            +-------+  5  +----+
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test01_initialSituation() throws TemplatorException {
        buildInitialSituation();

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 3, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test02_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test03_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+                           +-----+
    //  |     |       +--+     |                           |     |
    //  |     |       |  |  3  +---------------------+-----+  6  |
    //  |     |       |  |     |                     |     |     |
    //  |     |       |  +-----+                     |     +-----+
    //  |     |       |                              |
    //  |  0  +-------+                   +-----+    |
    //  |     |                           |     |    |
    //  |     |          +-----+       +--+  5  +----+
    //  |     +----------+     +-------+  |     |    |
    //  |     |          |  2  |          +-----+    |
    //  |     |          |     +--+                  |
    //  |     |          +-----+  |       +-----+    |
    //  +-----+                   |       |     |    |
    //                            +-------+  4  +----+
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test04_removeEntry_1() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 3, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test05_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test06_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +----+---+  6  |
    //  |     |   |      |     +-----+ |  |     |    |   |     |
    //  |     |   |      +-----+     | |  +-----+    |   +-----+
    //  |     |   |                  | |             |
    //  |  0  +------------------------+  +-----+    |
    //  |     |   |                  |    |     |    |
    //  |     |   |                  +----+  4  +----+
    //  |     |   |                       |     |
    //  |     |   |                       +-----+
    //  |     +---+
    //  |     |
    //  +-----+
    //
    @Test
    public void test07_removeEntry_2() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test08_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test09_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, IDLE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+                         +-----+
    //  |     |          |     |                         |     |
    //  |     +---+------+  1  |                     +---+  6  |
    //  |     |   |      |     +-----+               |   |     |
    //  |     |   |      +-----+     |               |   +-----+
    //  |     |   |                  |               |
    //  |  0  |   |                  |    +-----+    |
    //  |     |   |                  |    |     |    |
    //  |     |   |      +-----+     +-+--+  4  +----+
    //  |     +----------+     +--+    |  |     |    |
    //  |     |   |      |  2  |  |    |  +-----+    |
    //  |     +---+      |     +-------+             |
    //  |     |          +-----+  |       +-----+    |
    //  +-----+                   |       |     |    |
    //                            +-------+  5  +----+
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test10_removeEntry_3() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry3);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test11_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test12_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +----+---+  6  |
    //  |     |   |      |     |       |  |     |    |   |     |
    //  |     |   |      +-----+       |  +-----+    |   +-----+
    //  |     |   |                    |             |
    //  |  0  +------------------------+             |
    //  |     |   |                                  |
    //  |     |   |      +-----+                     |
    //  |     +----------+     +--+                  |
    //  |     |   |      |  2  |  |                  |
    //  |     +---+      |     |  |                  |
    //  |     |          +-----+  |       +-----+    |
    //  +-----+                   |       |     |    |
    //                            +-------+  5  +----+
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test13_removeEntry_4() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry4);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test14_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test15_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +----+---+  6  |
    //  |     |   |      |     +-----+ |  |     |    |   |     |
    //  |     |   |      +-----+     | |  +-----+    |   +-----+
    //  |     |   |                  | |             |
    //  |  0  +------------------------+  +-----+    |
    //  |     |   |                  |    |     |    |
    //  |     |   |      +-----+     +-+--+  4  +----+
    //  |     +----------+     |       |  |     |
    //  |     |   |      |  2  |       |  +-----+
    //  |     +---+      |     +-------+
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test16_removeEntry_5() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry5);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test17_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test18_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +-------+--+     |
    //  |     +---+------+  1  |       |  |  3  |
    //  |     |   |      |     +-----+ |  |     |
    //  |     |   |      +-----+     | |  +-----+
    //  |     |   |                  | |
    //  |  0  +------------------------+  +-----+
    //  |     |   |                  |    |     |
    //  |     |   |      +-----+     +-+--+  4  |
    //  |     +----------+     +--+    |  |     |
    //  |     |   |      |  2  |  |    |  +-----+
    //  |     +---+      |     +-------+
    //  |     |          +-----+  |       +-----+
    //  +-----+                   |       |     |
    //                            +-------+  5  |
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test19_removeEntry_6() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry6);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test20_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test21_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    @Test
    public void test22_removeEntry_0() throws TemplatorException {
        buildInitialSituation();
        closeEntry(entry0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        assertTreeViewTestInfo(expected);
    }
}
