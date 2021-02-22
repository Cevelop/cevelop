package com.cevelop.templator.plugin.view.interfaces;

public interface IActionButtonCallback {

    public enum ButtonAction {

        MINIMIZE("Minimize Entry"), MAXIMIZE("Maximize Entry"), CLOSE("Close Entry"), GO_TO_SOURCE("Go to Source"), CLOSE_ALL(
                "Close All Sub Entries"), MINIMIZE_ALL("Minimize All Sub Entries"), MAXIMIZE_ALL("Maximize All Sub Entries"), SEARCH(
                        "Search"), PROBLEMS("Problems");

        private String text;

        private ButtonAction(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    void actionPerformed(ButtonAction action);
}
