package com.cevelop.codeanalysator.core.guideline;

public interface IGuideline {

    public String getName();

    public String getId();

    public ISuppressionStrategy getSuppressionStrategy();
}
