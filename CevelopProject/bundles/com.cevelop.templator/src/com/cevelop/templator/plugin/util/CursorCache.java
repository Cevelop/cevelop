package com.cevelop.templator.plugin.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.cevelop.templator.plugin.util.ImageCache.ImageID;


public final class CursorCache {

    private static Map<CursorID, Cursor> cursors = null;

    private CursorCache() {}

    public enum CursorID {
        ARROW, RESIZE_WE, RESIZE_NS, RESIZE_NWSE, HAND_POINTER, PORTAL_HOVER, PORTAL_TRAVEL
    }

    public static Cursor get(CursorID id) {
        if (cursors == null) {
            initializeCursors();
        }
        return cursors.get(id);
    }

    private static void initializeCursors() {
        cursors = new HashMap<>();
        addCursorDefault(CursorID.RESIZE_WE, SWT.CURSOR_SIZEWE);
        addCursorDefault(CursorID.RESIZE_NS, SWT.CURSOR_SIZENS);
        addCursorDefault(CursorID.RESIZE_NWSE, SWT.CURSOR_SIZENWSE);
        addCursorDefault(CursorID.ARROW, SWT.CURSOR_ARROW);
        addCursorUser(CursorID.HAND_POINTER, ImageID.HAND_POINTER, 5, 5);
        addCursorUser(CursorID.PORTAL_HOVER, ImageID.PORTAL_HOVER, 5, 5);
        addCursorUser(CursorID.PORTAL_TRAVEL, ImageID.PORTAL_TRAVEL, 5, 5);
    }

    private static void addCursorDefault(CursorID cursorID, int style) {
        cursors.put(cursorID, new Cursor(Display.getDefault(), style));
    }

    private static void addCursorUser(CursorID cursorID, ImageID imageID, int hotPointX, int hotPointY) {
        ImageData imageData = ImageCache.get(imageID).getImageData();
        cursors.put(cursorID, new Cursor(Display.getDefault(), imageData, hotPointX, hotPointY));
    }

    public static void disposeCursors() {
        if (cursors == null) {
            return;
        }
        for (Cursor cursor : cursors.values()) {
            cursor.dispose();
        }
        cursors.clear();
        cursors = null;
    }

}
