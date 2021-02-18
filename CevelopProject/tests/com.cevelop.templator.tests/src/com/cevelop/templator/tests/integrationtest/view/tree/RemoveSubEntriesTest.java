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
public class RemoveSubEntriesTest extends TreeViewTest {

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
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     |          |     |        |     |
    //  |     +---+------+  1  |     +----+  5  +--------+  6  |
    //  |     |   |      |     |     |    |     |        |     |
    //  |     |   |      +-----+     |    +-----+        +-----+
    //  |     |   |                  |
    //  |  0  |   |                  |
    //  |     |   |                  |
    //  |     |   |      +-----+     |
    //  |     +----------+     +-----+
    //  |     |   |      |  2  |
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test04_closeSubEntriesOfEntry_1() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test05_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test06_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +--------+  6  |
    //  |     |   |      |     |       |  |     |        |     |
    //  |     |   |      +-----+       |  +-----+        +-----+
    //  |     |   |                    |
    //  |  0  +------------------------+
    //  |     |   |
    //  |     |   |      +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test07_closeSubEntriesOfEntry_2() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test08_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test09_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
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
    public void test10_closeSubEntriesOfEntry_3() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry3);

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
    public void test11_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test12_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
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
    public void test13_closeSubEntriesOfEntry_4() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry4);

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
    public void test14_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test15_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
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
    public void test16_closeSubEntriesOfEntry_5() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry5);

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
    public void test17_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test18_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
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
    public void test19_closeSubEntriesOfEntry_6() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry6);

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
    public void test20_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test21_checkStates() {
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
    //  +-----+
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  |  0  |
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  +-----+
    //
    @Test
    public void test22_closeSubEntriesOfEntry_0() throws TemplatorException {
        buildInitialSituation();
        closeAllSubEntries(entry0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test23_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test24_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }
}
