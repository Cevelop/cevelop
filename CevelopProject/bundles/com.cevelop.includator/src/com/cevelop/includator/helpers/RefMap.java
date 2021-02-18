package com.cevelop.includator.helpers;

import java.util.LinkedHashMap;

import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.includator.cxxelement.DeclarationReference;


public class RefMap extends LinkedHashMap<IBinding, DeclarationReference> {

    private static final long serialVersionUID = 8840159214188746738L;
}
