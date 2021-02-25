package com.cevelop.templator.plugin.view.interfaces;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.ILoadingProgress;


public interface IEntryLoadCallback {

    void loadOperation(ILoadingProgress loadingProgress) throws TemplatorException;

    void loadComplete();

    void loadException(Throwable throwable);
}
