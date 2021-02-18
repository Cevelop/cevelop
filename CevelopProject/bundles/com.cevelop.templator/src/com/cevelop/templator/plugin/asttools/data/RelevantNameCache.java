package com.cevelop.templator.plugin.asttools.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.templator.plugin.asttools.type.finding.RelevantNameType;


public class RelevantNameCache {

    private Map<IASTDeclaration, RelevantNameType> declarationCache = new HashMap<>();
    private Map<IBinding, RelevantNameType>        bindingCache     = new HashMap<>();

    public static final RelevantNameCache EMPTY_CACHE = new RelevantNameCache();

    public RelevantNameType getFor(IASTName name) {
        if (name != null) {
            return (getFor(name.resolveBinding()));
        }
        return null;
    }

    public RelevantNameType getFor(IASTDeclaration declaration) {
        return declarationCache.get(declaration);
    }

    /** Can be used for variables, parameters and such.
     *  @param binding The binding to get the {@link RelevantNameType} for
     *  @return The {@link RelevantNameType}
     */
    public RelevantNameType getFor(IBinding binding) {
        return bindingCache.get(binding);
    }

    public void put(IASTDeclaration declaration, RelevantNameType name) {
        declarationCache.put(declaration, name);
    }

    public void put(IBinding binding, RelevantNameType name) {
        bindingCache.put(binding, name);
    }
}
