package com.cevelop.gslator.utils;

import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;


@SuppressWarnings("restriction")
public final class CCGlatorCPPNodeFactory {

    private final static CPPNodeFactory factory = CPPNodeFactory.getDefault();

    private CCGlatorCPPNodeFactory() {}

    public static CPPNodeFactory getCPPNodeFactory() {
        return factory;
    }
}
