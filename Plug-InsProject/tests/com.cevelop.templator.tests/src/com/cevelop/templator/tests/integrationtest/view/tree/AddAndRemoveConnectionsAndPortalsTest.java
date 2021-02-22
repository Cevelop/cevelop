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


@RunFor(rtsFile = "/resources/com.cevelop.templator.tests/integrationtest/view/tree/ExampleWithPortals.rts")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddAndRemoveConnectionsAndPortalsTest extends TreeViewTest {

    private static TreeEntry entry0, entry1, entry2, entry3, entry4;

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
        expected.addEntry(IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     +----------+  1  |
    //  |     |          |     |
    //  |     |          +-----+
    //  |     |
    //  |     |
    //  |  0  |
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
        expected.addEntry(ACTIVE, IDLE, IDLE);
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
    //  |     |
    //  |  0  |
    //  |     |          +-----+
    //  |     +----------+     |
    //  |     |          |  2  |
    //  |     |          |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test07_addConnection_0_2() throws TemplatorException {
        clickName(entry0, 1);
        entry2 = findChild(entry0, 1);

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
        expected.addEntry(ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     +---+------+  1  |
    //  |     |   |      |     |
    //  |     |   |      +-----+
    //  |     |   |
    //  |     |   |
    //  |  0  |   |
    //  |     |   |      +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test10_addConnection_0_1() throws TemplatorException {
        clickName(entry0, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 0, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test11_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test12_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, IDLE);
        expected.addEntry(IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     +-+2
    //  |     +---+------+  1  |
    //  |     |   |      |     |
    //  |     |   |      +-----+
    //  |     |   |
    //  |     |   |
    //  |  0  |   |
    //  |     |   |      +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     |
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test13_addPortal_1_2() throws TemplatorException {
        clickName(entry1, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 0, 0, 1));
        expected.add(new TreeViewTestInfo(1, 1, 0, 1, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test14_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test15_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     +-+2
    //  |     +---+------+  1  |
    //  |     |   |      |     |
    //  |     |   |      +-----+
    //  |     |   |
    //  |     |   |
    //  |  0  |   |
    //  |     |   |      +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test16_addPortal_2_1() throws TemplatorException {
        clickName(entry2, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 0, 1, 1));
        expected.add(new TreeViewTestInfo(1, 1, 0, 1, 1));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test17_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test18_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, IDLE);
        expected.addEntry(IDLE, ACTIVE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+
    //  |     |          |     +-+2     |     |
    //  |     +---+------+  1  |        |     |
    //  |     |   |      |     +--------+     |
    //  |     |   |      +-----+        |  3  |
    //  |     |   |                     |     |
    //  |     |   |                     |     |
    //  |  0  |   |                     |     |
    //  |     |   |      +-----+        +-----+
    //  |     +----------+     |
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test19_addConnection_1_3() throws TemplatorException {
        clickName(entry1, 1);
        entry3 = findChild(entry1, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 1, 1));
        expected.add(new TreeViewTestInfo(1, 1, 0, 1, 1));
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
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+
    //  |     |          |     +-+2     |     |
    //  |     +---+------+  1  |        |     |
    //  |     |   |      |     +----+---+     |
    //  |     |   |      +-----+    |   |  3  |
    //  |     |   |                 |   |     |
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test22_addConnection_2_3() throws TemplatorException {
        clickName(entry2, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 1, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 1, 1));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test23_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test24_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2
    //  |     +---+------+  1  |        |     |
    //  |     |   |      |     +----+---+     |
    //  |     |   |      +-----+    |   |  3  |
    //  |     |   |                 |   |     |
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test25_addPortal_3_2() throws TemplatorException {
        clickName(entry3, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 1, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 1));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 1));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test26_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test27_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1
    //  |     +---+------+  1  |        |     |
    //  |     |   |      |     +----+---+     |
    //  |     |   |      +-----+    |   |  3  |
    //  |     |   |                 |   |     |
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test28_addPortal_3_1() throws TemplatorException {
        clickName(entry3, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 2, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 1));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 2));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test29_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test30_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1
    //  |     +---+------+  1  |        |     |
    //  |     |   |      |     +----+---+     +-+1
    //  |     |   |      +-----+    |   |  3  |
    //  |     |   |                 |   |     |
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test31_addPortal_3_1() throws TemplatorException {
        clickName(entry3, 4);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 3, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 1));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 3));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test32_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test33_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1
    //  |     +---+------+  1  |        |     |
    //  |     |   |      |     +----+---+     +-+1,1
    //  |     |   |      +-----+    |   |  3  |
    //  |     |   |                 |   |     |
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test34_addPortal_3_1() throws TemplatorException {
        clickName(entry3, 5);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 1));
        expected.add(new TreeViewTestInfo(2, 2, 0, 0, 4));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test35_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test36_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     |
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     |
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test37_addConnection3_5() throws TemplatorException {
        clickName(entry3, 6);
        entry4 = findChild(entry3, 6);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 1));
        expected.add(new TreeViewTestInfo(2, 2, 1, 0, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test38_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test39_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     |
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     |
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     +---+
    //  |     |   |      +-----+    |   +-----+   |
    //  |     +----------+     +----+             |    +-----+
    //  |     |   |      |  2  |                  |    |     |
    //  |     +---+      |     +-+1               +----+  5  |
    //  |     |          +-----+                       |     |
    //  +-----+                                        +-----+
    //
    @Test
    public void test40_addConnection_3_5() throws TemplatorException {
        clickName(entry3, 7);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 1));
        expected.add(new TreeViewTestInfo(2, 2, 2, 0, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
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
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     +-+2
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     |
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     +---+
    //  |     |   |      +-----+    |   +-----+   |
    //  |     +----------+     +----+             |    +-----+
    //  |     |   |      |  2  |                  |    |     |
    //  |     +---+      |     +-+1               +----+  5  |
    //  |     |          +-----+                       |     |
    //  +-----+                                        +-----+
    //
    @Test
    public void test43_addPortal_4_2() throws TemplatorException {
        clickName(entry4, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 3, 1));
        expected.add(new TreeViewTestInfo(2, 2, 2, 0, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 1));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test44_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test45_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, IDLE, IDLE, IDLE, IDLE, IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     +-+2,2
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     |
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     +---+
    //  |     |   |      +-----+    |   +-----+   |
    //  |     +----------+     +----+             |    +-----+
    //  |     |   |      |  2  |                  |    |     |
    //  |     +---+      |     +-+1               +----+  5  |
    //  |     |          +-----+                       |     |
    //  +-----+                                        +-----+
    //
    @Test
    public void test46_addPortal_4_2() throws TemplatorException {
        clickName(entry4, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 4, 1));
        expected.add(new TreeViewTestInfo(2, 2, 2, 0, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 2));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test47_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test48_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, IDLE, IDLE, IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     +-+2,2,2
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     |
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     +---+
    //  |     |   |      +-----+    |   +-----+   |
    //  |     +----------+     +----+             |    +-----+
    //  |     |   |      |  2  |                  |    |     |
    //  |     +---+      |     +-+1               +----+  5  |
    //  |     |          +-----+                       |     |
    //  +-----+                                        +-----+
    //
    @Test
    public void test49_addPortal_4_2() throws TemplatorException {
        clickName(entry4, 3);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 5, 1));
        expected.add(new TreeViewTestInfo(2, 2, 2, 0, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 3));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test50_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test51_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE, IDLE, IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     +-+2,2,2
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     +-+3
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     +---+
    //  |     |   |      +-----+    |   +-----+   |
    //  |     +----------+     +----+             |    +-----+
    //  |     |   |      |  2  |                  |    |     |
    //  |     +---+      |     +-+1               +----+  5  |
    //  |     |          +-----+                       |     |
    //  +-----+                                        +-----+
    //
    @Test
    public void test52_addPortal_4_3() throws TemplatorException {
        clickName(entry4, 5);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 5, 1));
        expected.add(new TreeViewTestInfo(2, 2, 2, 1, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test53_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test54_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE, ACTIVE, IDLE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     +-+2,2,2
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     +-+3,5
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     +---+
    //  |     |   |      +-----+    |   +-----+   |
    //  |     +----------+     +----+             |    +-----+
    //  |     |   |      |  2  |                  |    |     |
    //  |     +---+      |     +-+1               +----+  5  |
    //  |     |          +-----+                       |     |
    //  +-----+                                        +-----+
    //
    @Test
    public void test55_addPortal_4_5() throws TemplatorException {
        clickName(entry4, 6);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 5, 1));
        expected.add(new TreeViewTestInfo(2, 2, 2, 1, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 5));
        expected.add(new TreeViewTestInfo(3, 1, 0, 1, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test56_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test57_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE);
        expected.addEntry();
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     +-+2     |     +-+2,1   |     +-+2,2,2
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +----+---+     +-+1,1 | |     +-+3
    //  |     |   |      +-----+    |   |  3  |      | +-----+
    //  |     |   |                 |   |     +------+
    //  |     |   |                 |   |     |
    //  |  0  |   |                 |   |     |
    //  |     |   |      +-----+    |   +-----+
    //  |     +----------+     +----+
    //  |     |   |      |  2  |
    //  |     +---+      |     +-+1
    //  |     |          +-----+
    //  +-----+
    //
    @Test
    public void test58_removeConnection_3_5() throws TemplatorException {
        clickName(entry3, 7);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 3, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 4, 1));
        expected.add(new TreeViewTestInfo(1, 1, 1, 5, 1));
        expected.add(new TreeViewTestInfo(2, 2, 1, 1, 4));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 4));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test59_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test60_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(ACTIVE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE, ACTIVE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     |        |     +-+1     |     |
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +--------+     +-+1,1 | |     +-+3
    //  |     |   |      +-----+        |  3  |      | +-----+
    //  |     |   |                     |     +------+
    //  |     |   |                     |     |
    //  |  0  |   |                     |     |
    //  |     |   |                     +-----+
    //  |     |   |
    //  |     |   |
    //  |     +---+
    //  |     |
    //  +-----+
    //
    @Test
    public void test61_removeConnection_0_2() throws TemplatorException {
        clickName(entry0, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 3, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 1, 3));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 1));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test62_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test63_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(IDLE, IDLE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, ACTIVE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     |        |     +-+1     |     |
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +--------+     +-+1,1 | |     |
    //  |     |   |      +-----+        |  3  |      | +-----+
    //  |     |   |                     |     +------+
    //  |     |   |                     |     |
    //  |  0  |   |                     |     |
    //  |     |   |                     +-----+
    //  |     |   |
    //  |     |   |
    //  |     +---+
    //  |     |
    //  +-----+
    //
    @Test
    public void test64_removePortal_4_3() throws TemplatorException {
        clickName(entry4, 5);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 3, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 3));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test65_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test66_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(IDLE, IDLE, ACTIVE, IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     |        |     |        |     |
    //  |     +---+------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +--------+     +-+1,1 | |     |
    //  |     |   |      +-----+        |  3  |      | +-----+
    //  |     |   |                     |     +------+
    //  |     |   |                     |     |
    //  |  0  |   |                     |     |
    //  |     |   |                     +-----+
    //  |     |   |
    //  |     |   |
    //  |     +---+
    //  |     |
    //  +-----+
    //
    @Test
    public void test67_removePortal_3_1() throws TemplatorException {
        clickName(entry3, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 2, 0, 0));
        expected.add(new TreeViewTestInfo(1, 2, 1, 2, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 2));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test68_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test69_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(ACTIVE, IDLE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+        +-----+        +-----+
    //  |     |          |     |        |     |        |     |
    //  |     |   +------+  1  |        |     |      +-+  4  |
    //  |     |   |      |     +--------+     +-+1,1 | |     |
    //  |     |   |      +-----+        |  3  |      | +-----+
    //  |     |   |                     |     +------+
    //  |     |   |                     |     |
    //  |  0  |   |                     |     |
    //  |     |   |                     +-----+
    //  |     |   |
    //  |     |   |
    //  |     +---+
    //  |     |
    //  +-----+
    //
    @Test
    public void test70_removeConnection_0_1() throws TemplatorException {
        clickName(entry0, 0);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 1, 2, 0));
        expected.add(new TreeViewTestInfo(2, 1, 1, 0, 2));
        expected.add(new TreeViewTestInfo(3, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test71_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test72_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, IDLE, ACTIVE);
        expected.addEntry(IDLE, ACTIVE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, ACTIVE, ACTIVE, ACTIVE, IDLE);
        expected.addEntry(IDLE, IDLE, IDLE, IDLE, IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    //
    //  +-----+          +-----+
    //  |     |          |     |
    //  |     |   +------+  1  |
    //  |     |   |      |     |
    //  |     |   |      +-----+
    //  |     |   |
    //  |     |   |
    //  |  0  |   |
    //  |     |   |
    //  |     |   |
    //  |     |   |
    //  |     +---+
    //  |     |
    //  +-----+
    //
    @Test
    public void test73_removeConnection_1_3() throws TemplatorException {
        clickName(entry1, 1);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 1, 0, 0));
        expected.add(new TreeViewTestInfo(1, 1, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test74_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test75_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, IDLE, ACTIVE);
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
    //  |     |
    //  |  0  |
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  |     |
    //  +-----+
    //
    @Test
    public void test76_removeConnection_0_1() throws TemplatorException {
        clickName(entry0, 2);

        List<TreeViewTestInfo> expected = new ArrayList<>();
        expected.add(new TreeViewTestInfo(0, 0, 0, 0, 0));
        assertTreeViewTestInfo(expected);
    }

    @Test
    public void test77_checkRectangleColors() {
        assertColors();
    }

    @Test
    public void test78_checkStates() {
        RectangleStateCollection expected = new RectangleStateCollection();
        expected.addEntry(IDLE, IDLE, IDLE);
        assertRectangleStates(expected);
    }

    @Test
    public void test79_removeAll() {
        closeAllEntries();

        List<TreeViewTestInfo> expected = new ArrayList<>();
        assertTreeViewTestInfo(expected);
    }
}
