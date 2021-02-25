package com.cevelop.ctylechecker.domain.types;

public class DefaultRenameResolution extends AbstractRenameResolution {

    public DefaultRenameResolution() {
        super(null);
    }

    @Override
    public String transform(String pName) {
        return pName;
    }

}
