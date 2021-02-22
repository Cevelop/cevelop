/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.resources;

import org.eclipse.osgi.util.NLS;


public final class Labels extends NLS {

    /*
     * 1. copy&paste content from Labels.properties 2. search for ^(.*)=.*$ and replace all by public static String $1;
     */
    public static String IU_SysNode;
    public static String IU_NoUsingSelected;
    public static String IUDEC_NoUDECSelected;
    public static String IUDIR_NoUDIRSelected;
    public static String QUN_NoNameSelected;
    public static String QUN_DeclaratorNameSelected;
    public static String QUN_SelectedNameAlreadyQualified;
    public static String No_QName_Selected;
    public static String No_Using_Name;
    public static String No_Using_Name_Built;
    public static String No_NS_IN_QNAME;
    public static String No_Selection_In_Type_Decl;
    public static String No_ExtractInTypeWithoutCorrectInheritance;
    public static String IUDEC_ImplicitOperatorCall;
    public static String IUDEC_TemplateArgument;
    public static String TD2A_NoNameSelected;
    public static String TD2A_NoTypedefSelected;

    static {
        NLS.initializeMessages(Labels.class.getName(), Labels.class);
    }

    private Labels() {

    }
}
