package com.cevelop.templator.plugin.util;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.cevelop.templator.plugin.TemplatorPlugin;


public final class ImageCache {

    private ImageCache() {}

    public enum ImageID {
        // @formatter:off
		HAND_OPEN("/icons/hand_open.png"),
		HAND_CLOSED("/icons/hand_closed.png"),
		HAND_POINTER("/icons/hand_pointer.png"),
		ICON_CON_MAX("/icons/icon_con_max.png"),
		ICON_CON_MIN("/icons/icon_con_min.png"),
		ICON_MENU("/icons/icon_menu_white.png"),
		ICON_REMOVE("/icons/icon_remove.png"),
		LOADING("/icons/loading.png"),
		PROGRESS_INDICATOR("/icons/progress_indicator.gif"),
		MINIMIZE_ALL("/icons/minimize_all.png"),
		MAXIMIZE_ALL("/icons/maximize_all.png"),
		REFRESH("/icons/icon_refresh.png"),
		CLOSE_ALL("/icons/close_all.png"),
		ARROW_UP("/icons/arrow_up.png"),
		ARROW_DOWN("/icons/arrow_down.png"),
		PORTAL_IN("/icons/portal_in_big.png"),
		PORTAL_OUT("/icons/portal_out.png"),
		PORTAL_HOVER("/icons/portal_hover.png"),
		PORTAL_TRAVEL("/icons/portal_travel.png"),
		CUT("/icons/cut.png"),
		TRAVEL_TO_START("/icons/jump_to_start.png"),
		TRAVEL_TO_END("/icons/jump_to_end.png"),
		MAGNIFIER("/icons/magnifier.png"),
		ARROW_RIGHT("/icons/arrow_right.png"),
		TYPE_ALIAS_TEMPLATE("/icons/type_alias_template.png"),
		TYPE_CLASS("/icons/type_class.png"),
		TYPE_CLASS_TEMPLATE("/icons/type_class_template.png"),
		TYPE_FUNCTION("/icons/type_function.png"),
		TYPE_MEMBER_FUNCTION("/icons/type_member_function.png"),
		TYPE_FUNCTION_TEMPLATE("/icons/type_function_template.png"),
		TYPE_VARIABLE_TEMPALTE("/icons/type_variable_template.png"),
		TYPE_LAMBDA("/icons/type_lambda.png"),
		TYPE_UNKNOWN("/icons/type_unknown.png");
		// @formatter:on

        private String location;

        private ImageID(String location) {
            this.location = location;
        }
    }

    private static ImageRegistry imageRegistry = TemplatorPlugin.getDefault().getImageRegistry();

    static {
        for (ImageID imageId : ImageID.values()) {
            addImage(imageId, imageId.location);
        }
    }

    public static Image get(ImageID id) {
        return imageRegistry.get(id.toString());
    }

    private static void addImage(ImageID id, String filename) {
        Image image = new Image(Display.getCurrent(), ImageCache.class.getResourceAsStream(filename));
        imageRegistry.put(id.toString(), image);
    }

    public static void resizeImage(ImageID id, int height) {
        imageRegistry.remove(id.toString());
        Image imageOriginal = new Image(Display.getCurrent(), ImageCache.class.getResourceAsStream(id.location));
        double scaleFactor = (double) height / imageOriginal.getBounds().height;
        int width = (int) ((imageOriginal.getBounds().width + 0.5) * scaleFactor);
        Image image = new Image(Display.getCurrent(), width, height);
        GC gc = new GC(image);
        gc.setAntialias(SWT.ON);
        gc.drawImage(imageOriginal, 0, 0, imageOriginal.getBounds().width, imageOriginal.getBounds().height, 0, 0, width, height);
        gc.dispose();
        imageOriginal.dispose();
        imageRegistry.put(id.toString(), image);
    }
}
