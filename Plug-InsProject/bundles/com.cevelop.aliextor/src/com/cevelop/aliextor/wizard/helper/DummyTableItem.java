package com.cevelop.aliextor.wizard.helper;

import java.util.ArrayList;

import org.eclipse.swt.widgets.TableItem;


public class DummyTableItem {

    private ArrayList<String>                                             text;
    private boolean                                                       checked;
    private Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> data;

    public DummyTableItem() {
        text = new ArrayList<>();
        checked = false;
        data = null;
    }

    public String getText(int index) {
        return text.get(index);
    }

    public void setText(int index, String text) {
        if (index == this.text.size()) {
            this.text.add(text);
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean check) {
        this.checked = check;
    }

    public Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> getData() {
        return data;
    }

    public void setData(Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return data.getLeft().hashCode() ^ data.getRight().hashCode();
    }

    @Override
    public boolean equals(Object dummy) {
        if (dummy instanceof DummyTableItem) {
            DummyTableItem item = (DummyTableItem) dummy;
            return this.equals(item);
        }
        return false;
    }

    public boolean equals(DummyTableItem other) {
        return this.data.getLeft().equals(other.getData().getLeft());
    }

    public boolean equalsTableItem(TableItem item) {
        return getPair().equals(getPair(item));
    }

    @SuppressWarnings("unchecked")
    private Pair<String, Integer> getPair(TableItem item) {
        return ((Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>>) item.getData()).getLeft();
    }

    private Pair<String, Integer> getPair() {
        return getData().getLeft();
    }
}
