package com.cevelop.tdd.refactorings.argument;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        AddArgument_descWithSection;
    public static String        AddArgument_descWoSection;
    public static String        AddArgument_refactoringName;
    public static String        AddArgument_funCallNotFoundExcp;
    public static String        AddArgument_candidateNotFoundExcp;
    public static String        AddArgument_bindingNotFoundExcp;
    public static String        AddArgument_functionNameNotFoundExcp;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
