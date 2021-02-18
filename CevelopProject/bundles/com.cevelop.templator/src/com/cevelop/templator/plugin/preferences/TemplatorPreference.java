package com.cevelop.templator.plugin.preferences;

public enum TemplatorPreference {
    HAS_MAX_WIDTH("HAS_MAX_WIDTH", "use a maximum width for new opened entries"), HAS_MAX_HEIGHT("HAS_MAX_HEIGHT",
            "use a maximum height for new opened entries"), RECTANGLE_ALWAYS_VISIBLE("RECTANGLE_ALWAYS_VISIBLE",
                    "always show rectangles around traceable nodes"), DISABLE_NORMAL_FUNCTIONS("DISABLE_NORMAL_FUNCTIONS",
                            "disable tracing normal functions"), DISABLE_NORMAL_CLASSES("DISABLE_NORMAL_CLASSES",
                                    "disable tracing normal classes"), DISABLE_AUTO_SPECIFIER("DISABLE_AUTO_SPECIFIER",
                                            "disable tracing auto specifiers"), HOVER_COLOR("HOVER_COLOR",
                                                    "color shown if an unopened name is hovered"), RECTANGLE_COLOR("RECTANGLE_COLOR",
                                                            "color for rectangles if entry is not opened (only occurs if rectangles are always shown");

    private String key;
    private String description;

    private TemplatorPreference(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
