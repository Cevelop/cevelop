package com.cevelop.ctylechecker.service.factory;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";

    public static String ADD_PREFIX_RESOLUTION_TEXT;
    public static String ADD_SUFFIX_RESOLUTION_TEXT;
    public static String REPLACE_RESOLUTION_TEXT;
    public static String DEFAULT_RENAME_RESOLUTION_TEXT;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
