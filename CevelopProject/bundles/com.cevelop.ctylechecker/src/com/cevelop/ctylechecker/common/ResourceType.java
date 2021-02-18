package com.cevelop.ctylechecker.common;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IStringifyable;


public enum ResourceType implements IStringifyable<ResourceType> {
    AST("AST"), //
    FILE("FILE"), //
    FILE_ENDING("FILE_ENDING"); //

    private final String string;

    ResourceType(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public static ResourceType of(String string) {
        for (final ResourceType resourceType : values()) {
            if (resourceType.toString().equals(string)) return resourceType;
        }
        throw new IllegalArgumentException("Illegal ResourceType: " + string);
    }

    @Override
    public String stringify() {
        return string;
    }

    @Override
    public ResourceType unstringify(String string) {
        return of(string);
    }
}
