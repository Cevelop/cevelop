package com.cevelop.codeanalysator.core.suppression;

public class SuppressionAttribute {

    private final String scope;
    private final String ignoreText;

    public SuppressionAttribute(final String scope, final String ignoreText) {
        this.scope = scope;
        this.ignoreText = ignoreText;
    }

    public String getIgnoreText() {
        return ignoreText;
    }

    public String getScope() {
        return scope;
    }
}
