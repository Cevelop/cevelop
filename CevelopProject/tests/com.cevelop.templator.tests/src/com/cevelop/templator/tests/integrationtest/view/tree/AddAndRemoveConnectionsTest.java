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
public class AddAndRemoveConnectionsTest extends TreeViewTest {

    private static TreeEntry entry0, entry1, entry2, entry3, entry4, entry5;

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
    public void test01_openRoot() throws TemplatorException {
        createView();
        entry0 = getRootEntry();

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test02_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test03_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     +----------+  1  |
    //  |     |          |     |
    //  |     |          +-----+
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
    public void test04_addConnection_0_1() throws TemplatorException {
        clickName(entry0, 0);
        entry1 = findChild(entry0, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test05_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test06_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, IDLE, IDLE);
        expected.addEntry(IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     +----------+  1  |
    //  |     |          |     |
    //  |     |          +-----+
    //  |     |
    //  |  0  |
    //  |     |
    //  |     |          +-----+
    //  |     +----------+     |
    //  |     |          |  2  |
    //  |     |          |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test07_addConnection_0_2() throws TemplatorException {
        clickName(entry0, 2);
        entry2 = findChild(entry0, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test08_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test09_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +----------+     |
    //  |     +----------+  1  |          |  3  |
    //  |     |          |     |          |     |
    //  |     |          +-----+          +-----+
    //  |     |
    //  |  0  |
    //  |     |
    //  |     |          +-----+
    //  |     +----------+     |
    //  |     |          |  2  |
    //  |     |          |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test10_addConnection_1_3() throws TemplatorException {
        clickName(entry1, 0);
        entry3 = findChild(entry1, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
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
        expected.addEntry(ACTIVE, IDLE, ACTIVE, IDLE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +----------+     |
    //  |     +---+------+  1  |          |  3  |
    //  |     |   |      |     |          |     |
    //  |     |   |      +-----+          +-----+
    //  |     |   |
    //  |  0  |   |
    //  |     |   |
    //  |     |   |      +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test13_addConnection_0_1() throws TemplatorException {
        clickName(entry0, 3);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
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
        expected.addEntry(ACTIVE, IDLE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +-------+--+     |
    //  |     +---+------+  1  |       |  |  3  |
    //  |     |   |      |     |       |  |     |
    //  |     |   |      +-----+       |  +-----+
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
    public void test16_addConnection_0_3() throws TemplatorException {
        clickName(entry0, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
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
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
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
    //  |     |   |      +-----+     +----+  4  |
    //  |     +----------+     |          |     |
    //  |     |   |      |  2  |          +-----+
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test19_addConnection_1_4() throws TemplatorException {
        clickName(entry1, 1);
        entry4 = findChild(entry1, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
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
        expected.addEntry(IDLE, IDLE);
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
    //  |     |   |      +-----+     +----+  4  |
    //  |     +----------+     +--+       |     |
    //  |     |   |      |  2  |  |       +-----+
    //  |     +---+      |     |  |
    //  |     |          +-----+  |       +-----+
    //  +-----+                   |       |     |
    //                            +-------+  5  |
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test22_addConnection_2_5() throws TemplatorException {
        clickName(entry2, 0);
        entry5 = findChild(entry2, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test23_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test24_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
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
    public void test25_addConnection_2_4() throws TemplatorException {
        clickName(entry2, 1);

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
    public void test26_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test27_checkStates() {
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
    //  |     +---+------+  1  |       |  |  3  +--------+  6  |
    //  |     |   |      |     +-----+ |  |     |        |     |
    //  |     |   |      +-----+     | |  +-----+        +-----+
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
    public void test28_addConnection_3_6() throws TemplatorException {
        clickName(entry3, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test29_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test30_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //   +-----+          +-----+          +-----+        +-----+
    //   |     |          |     +-------+--+     |        |     |
    //   |     +---+------+  1  |       |  |  3  +----+---+  6  |
    //   |     |   |      |     +-----+ |  |     |    |   |     |
    //   |     |   |      +-----+     | |  +-----+    |   +-----+
    //   |     |   |                  | |             |
    //   |  0  +------------------------+  +-----+    |
    //   |     |   |                  |    |     |    |
    //   |     |   |      +-----+     +-+--+  4  +----+
    //   |     +----------+     +--+    |  |     |
    //   |     |   |      |  2  |  |    |  +-----+
    //   |     +---+      |     +-------+
    //   |     |          +-----+  |       +-----+
    //   +-----+                   |       |     |
    //                             +-------+  5  |
    //                                     |     |
    //                                     +-----+
    //
    @Test
    public void test31_addConnection_4_6() throws TemplatorException {
        clickName(entry4, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(3, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test32_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test33_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(IDLE);
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
    public void test34_addConnection_5_6() throws TemplatorException {
        clickName(entry5, 0);

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
    public void test35_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test36_checkStates() {
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
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +----+---+  6  |
    //  |     |   |      |     +-----+ |  |     |    |   |     |
    //  |     |   |      +-----+     | |  +-----+    |   +-----+
    //  |     |   |                  | |             |
    //  |  0  +------------------------+  +-----+    |
    //  |     |   |                  |    |     |    |
    //  |     |   |      +-----+     +-+--+  4  +----+
    //  |     +----------+     +--+    |  |     |
    //  |     |   |      |  2  |  |    |  +-----+
    //  |     +---+      |     +-------+
    //  |     |          +-----+  |        +-----+
    //  +-----+                   |        |     |
    //                             +-------+  5  |
    //                                     |     |
    //                                     +-----+
    //
    @Test
    public void test37_removeConnection_5_6() throws TemplatorException {
        clickName(entry5, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(3, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test38_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test39_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+        +-----+
    //  |     |          |     +-------+--+     |        |     |
    //  |     +---+------+  1  |       |  |  3  +--------+  6  |
    //  |     |   |      |     +-----+ |  |     |        |     |
    //  |     |   |      +-----+     | |  +-----+        +-----+
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
    public void test40_removeConnection_4_6() throws TemplatorException {
        clickName(entry4, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 2, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test41_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test42_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
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
    public void test43_removeConnection_3_6() throws TemplatorException {
        clickName(entry3, 0);

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
    public void test44_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test45_checkStates() {
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
    //  |     |   |      +-----+     +----+  4  |
    //  |     +----------+     +--+       |     |
    //  |     |   |      |  2  |  |       +-----+
    //  |     +---+      |     |  |
    //  |     |          +-----+  |       +-----+
    //  +-----+                   |       |     |
    //                            +-------+  5  |
    //                                    |     |
    //                                    +-----+
    //
    @Test
    public void test46_removeConnection_2_4() throws TemplatorException {
        clickName(entry2, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test47_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test48_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
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
    //  |     |   |      +-----+     +----+  4  |
    //  |     +----------+     |          |     |
    //  |     |   |      |  2  |          +-----+
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test49_removeConnection_2_5() throws TemplatorException {
        clickName(entry2, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test50_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test51_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +-------+--+     |
    //  |     +---+------+  1  |       |  |  3  |
    //  |     |   |      |     |       |  |     |
    //  |     |   |      +-----+       |  +-----+
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
    public void test52_removeConnection_1_4() throws TemplatorException {
        clickName(entry1, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 4, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test53_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test54_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +----------+     |
    //  |     +---+------+  1  |          |  3  |
    //  |     |   |      |     |          |     |
    //  |     |   |      +-----+          +-----+
    //  |     |   |
    //  |  0  |   |
    //  |     |   |
    //  |     |   |      +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test55_removeConnection_0_3() throws TemplatorException {
        clickName(entry0, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test56_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test57_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+          +-----+
    //  |     |          |     +----------+     |
    //  |     +----------+  1  |          |  3  |
    //  |     |          |     |          |     |
    //  |     |          +-----+          +-----+
    //  |     |
    //  |  0  |
    //  |     |
    //  |     |          +-----+
    //  |     +----------+     |
    //  |     |          |  2  |
    //  |     |          |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test58_removeConnection_0_1() throws TemplatorException {
        clickName(entry0, 3);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(2, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test59_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test60_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE, IDLE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     +----------+  1  |
    //  |     |          |     |
    //  |     |          +-----+
    //  |     |
    //  |  0  |
    //  |     |
    //  |     |          +-----+
    //  |     +----------+     |
    //  |     |          |  2  |
    //  |     |          |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test61_removeConnection_1_3() throws TemplatorException {
        clickName(entry1, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test62_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test63_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     +----------+  1  |
    //  |     |          |     |
    //  |     |          +-----+
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
    public void test64_removeConnection_0_2() throws TemplatorException {
        clickName(entry0, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test65_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test66_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, IDLE, IDLE);
        expected.addEntry(IDLE, IDLE);
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
    public void test67_removeConnection_0_1() throws TemplatorException {
        clickName(entry0, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test68_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test69_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    @Test
    public void test70_removeAll() {
        closeAllEntries();

        List<TreeViewTestInfo> expected = new ArrayList<>();
        assertTreeViewTestInfo(expected);
    }
}
