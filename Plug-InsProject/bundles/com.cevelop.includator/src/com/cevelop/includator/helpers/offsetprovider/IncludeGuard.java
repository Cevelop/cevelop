package com.cevelop.includator.helpers.offsetprovider;

public class IncludeGuard {

    public final PreprocessorScope  scope;
    public final PreprocessorDefine define;

    public IncludeGuard(PreprocessorScope scope, PreprocessorDefine define) {
        this.scope = scope;
        this.define = define;
    }
}
