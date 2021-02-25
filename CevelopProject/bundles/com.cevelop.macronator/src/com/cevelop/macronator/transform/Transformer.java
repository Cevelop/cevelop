package com.cevelop.macronator.transform;

public abstract class Transformer {

    public abstract String generateTransformationCode();

    public boolean isValid() {
        return true;
    }
}
