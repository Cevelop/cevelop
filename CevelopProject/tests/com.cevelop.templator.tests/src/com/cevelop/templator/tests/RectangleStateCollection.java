package com.cevelop.templator.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;
import com.cevelop.templator.plugin.view.tree.TreeEntry;


public class RectangleStateCollection {

    private List<List<TemplatorRectangleState>> rectangleStates = new ArrayList<>();

    public void addEntry(TemplatorRectangleState... states) {
        List<TemplatorRectangleState> stateList = new ArrayList<>();
        for (TemplatorRectangleState state : states) {
            stateList.add(state);
        }
        rectangleStates.add(stateList);
    }

    public void addEntry(TreeEntry treeEntry) {
        List<TemplatorRectangleState> stateList = new ArrayList<>();
        for (int i = 0; i < treeEntry.getRectangleCount(); i++) {
            stateList.add(treeEntry.getRectangleState(i));
        }
        rectangleStates.add(stateList);
    }

    public List<TemplatorRectangleState> getEntry(int index) {
        return rectangleStates.get(index);
    }

    public int getSize() {
        return rectangleStates.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<TemplatorRectangleState> list : rectangleStates) {
            sb.append(Arrays.toString(list.toArray()));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        RectangleStateCollection otherCollection = (RectangleStateCollection) obj;
        if (getSize() != otherCollection.getSize()) {
            return false;
        }
        for (int i = 0; i < getSize(); i++) {
            List<TemplatorRectangleState> thisList = getEntry(i);
            List<TemplatorRectangleState> otherList = otherCollection.getEntry(i);
            if (thisList.size() != otherList.size()) {
                return false;
            }
            for (int k = 0; k < thisList.size(); k++) {
                if (thisList.get(k) != otherList.get(k)) {
                    return false;
                }
            }
        }
        return true;
    }
}
