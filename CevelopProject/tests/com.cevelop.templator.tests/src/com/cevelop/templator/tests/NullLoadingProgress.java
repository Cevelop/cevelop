package com.cevelop.templator.tests;

import com.cevelop.templator.plugin.util.ILoadingProgress;


/** For tests only. */
public class NullLoadingProgress implements ILoadingProgress {

    @Override
    public void setProgress(double d) {}

    @Override
    public void setStatus(String statusText) {}

}
