package com.cevelop.templator.plugin.view.interfaces;

public interface ISubNameClickCallback {

    public enum ClickAction {
        LEFT_CLICK, RIGHT_CLICK, CTRL_LEFT_CLICK;
    }

    void nameClicked(int nameIndex, ClickAction clickAction);
}
