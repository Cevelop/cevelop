package com.cevelop.codeanalysator.core.helper;

import java.util.ResourceBundle;


public class TranslationHelper {

    private static ResourceBundle bundle = null;

    private static void ensureIsLoaded() {
        if (bundle != null) {
            return;
        }

        bundle = ResourceBundle.getBundle("translations");
    }

    public static String get(String name) {
        ensureIsLoaded();

        if (bundle.containsKey(name)) {
            return bundle.getString(name);
        }

        return "MISSING_" + name;
    }
}
