package com.cevelop.templator.plugin.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.cevelop.templator.plugin.TemplatorPlugin;
import com.cevelop.templator.plugin.preferences.TemplatorPreference;


public final class ColorPalette {

    private static Map<Integer, Color> colorTable       = null;
    private static Map<Integer, Color> brightColorTable = null;
    private static Color               hoverColor       = null;
    private static Color               rectangleColor   = null;

    private ColorPalette() {}

    static {
        addPropertyChangeListener();
    }

    public static Color getColor(int colorId) {
        if (colorTable == null) {
            initPalette();
        }
        int absoluteColorId = colorId % colorTable.size();
        return colorTable.get(absoluteColorId);
    }

    public static Color getBrightColor(int colorId) {
        if (brightColorTable == null) {
            initBrightPalette();
        }
        int absoluteColorId = colorId % brightColorTable.size();
        return brightColorTable.get(absoluteColorId);
    }

    public static Color getHoverColor() {
        if (hoverColor == null) {
            readHoverColorFromPreferences();
        }
        return hoverColor;
    }

    public static Color getRectangleColor() {
        if (rectangleColor == null) {
            readRectangleColorFromPreference();
        }
        return rectangleColor;
    }

    private static void initPalette() {
        colorTable = new HashMap<>();
        colorTable.put(0, new Color(Display.getCurrent(), new RGB(73, 0, 146)));
        colorTable.put(1, new Color(Display.getCurrent(), new RGB(146, 0, 0)));
        colorTable.put(2, new Color(Display.getCurrent(), new RGB(0, 146, 146)));
        colorTable.put(3, new Color(Display.getCurrent(), new RGB(255, 109, 182)));
        colorTable.put(4, new Color(Display.getCurrent(), new RGB(182, 109, 255)));
        colorTable.put(5, new Color(Display.getCurrent(), new RGB(36, 255, 36)));
        colorTable.put(6, new Color(Display.getCurrent(), new RGB(109, 182, 255)));
        colorTable.put(7, new Color(Display.getCurrent(), new RGB(0, 73, 73)));
    }

    private static void initBrightPalette() {
        brightColorTable = new HashMap<>();
        brightColorTable.put(0, new Color(Display.getCurrent(), new RGB(146, 73, 255)));
        brightColorTable.put(1, new Color(Display.getCurrent(), new RGB(220, 109, 109)));
        brightColorTable.put(2, new Color(Display.getCurrent(), new RGB(127, 220, 220)));
        brightColorTable.put(3, new Color(Display.getCurrent(), new RGB(255, 182, 220)));
        brightColorTable.put(4, new Color(Display.getCurrent(), new RGB(220, 182, 255)));
        brightColorTable.put(5, new Color(Display.getCurrent(), new RGB(182, 255, 182)));
        brightColorTable.put(6, new Color(Display.getCurrent(), new RGB(182, 220, 255)));
        brightColorTable.put(7, new Color(Display.getCurrent(), new RGB(73, 182, 182)));
    }

    private static void readHoverColorFromPreferences() {
        IPreferenceStore store = TemplatorPlugin.getDefault().getPreferenceStore();
        RGB rgb = PreferenceConverter.getColor(store, TemplatorPreference.HOVER_COLOR.getKey());
        hoverColor = new Color(Display.getCurrent(), rgb);
    }

    private static void readRectangleColorFromPreference() {
        IPreferenceStore store = TemplatorPlugin.getDefault().getPreferenceStore();
        RGB rgb = PreferenceConverter.getColor(store, TemplatorPreference.RECTANGLE_COLOR.getKey());
        rectangleColor = new Color(Display.getCurrent(), rgb);
    }

    private static void addPropertyChangeListener() {
        TemplatorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(event -> {
            if (event.getProperty() == TemplatorPreference.HOVER_COLOR.getKey()) {
                hoverColor.dispose();
                readHoverColorFromPreferences();
            } else if (event.getProperty() == TemplatorPreference.RECTANGLE_COLOR.getKey()) {
                rectangleColor.dispose();
                readRectangleColorFromPreference();
            }
        });
    }

    public static void disposePalette() {
        if (colorTable != null) {
            for (Color color : colorTable.values()) {
                color.dispose();
            }
            colorTable = null;
        }

        if (brightColorTable != null) {
            for (Color color : brightColorTable.values()) {
                color.dispose();
            }
            brightColorTable = null;
        }

        if (rectangleColor != null) {
            rectangleColor.dispose();
            rectangleColor = null;
        }

        if (hoverColor != null) {
            hoverColor.dispose();
            hoverColor = null;
        }
        //TODO property change listener
    }
}
