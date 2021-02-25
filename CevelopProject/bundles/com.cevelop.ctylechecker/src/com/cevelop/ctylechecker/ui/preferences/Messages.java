package com.cevelop.ctylechecker.ui.preferences;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";

    public static String INFO;
    public static String FAILED_CONFIG_LOAD;
    public static String FAILED_SAVE_TRAY_AGAIN;
    public static String FAILED_PROJECT_LOAD;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
