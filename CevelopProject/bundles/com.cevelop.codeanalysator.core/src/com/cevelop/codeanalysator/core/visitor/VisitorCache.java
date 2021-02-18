package com.cevelop.codeanalysator.core.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class VisitorCache {

    private Map<String, List<CodeAnalysatorVisitor>> cache = new HashMap<>();

    List<CodeAnalysatorVisitor> getCache(Class<?> cls) {
        List<CodeAnalysatorVisitor> visitors = cache.get(cls.getTypeName());
        if (visitors == null) {
            visitors = new ArrayList<CodeAnalysatorVisitor>();
        }
        return visitors;
    }

    void addCache(Class<?> cls, CodeAnalysatorVisitor visitor) {
        List<CodeAnalysatorVisitor> visitors = getCache(cls);
        visitors.add(visitor);
        cache.put(cls.getTypeName(), visitors);
    }

    void remove(CodeAnalysatorVisitor visitor) {
        for (Entry<String, List<CodeAnalysatorVisitor>> key : cache.entrySet()) {
            key.getValue().remove(visitor);
        }
    }
}
