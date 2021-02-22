package com.cevelop.templator.plugin.asttools.resolving;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;


public class AutoCache {

    private Map<IASTDeclSpecifier, AbstractResolvedNameInfo> autoCache;

    public AutoCache() {
        autoCache = new HashMap<>();
    }

    public void put(IASTDeclSpecifier declSpec, AbstractResolvedNameInfo nameInfo) {
        autoCache.put(declSpec, nameInfo);
    }

    public AbstractResolvedNameInfo get(IASTDeclSpecifier declSpec) {
        return autoCache.get(declSpec);
    }
}
