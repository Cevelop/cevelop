package com.cevelop.tdd.refactorings.extract;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        Extract_descWithSection;
    public static String        Extract_descWoSection;
    public static String        Extract_name;
    public static String        ExtractRefactoringFileExists;
    public static String        ExtractRefactoringQuestion_OverwriteFile;
    public static String        ExtractRefactoringErrorNoType;
    public static String        ExtractRefactoringErrorNoNodeSelected;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
