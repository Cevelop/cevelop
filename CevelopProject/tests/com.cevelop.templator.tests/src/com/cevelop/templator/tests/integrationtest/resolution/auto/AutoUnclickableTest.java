package com.cevelop.templator.tests.integrationtest.resolution.auto;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.NullLoadingProgress;
import com.cevelop.templator.tests.TemplatorMultiLevelResolutionTest;


public class AutoUnclickableTest extends TemplatorMultiLevelResolutionTest {

    @Test
    public void shouldNotBeTraceable() throws TemplatorException {
        AbstractResolvedNameInfo info = getMainAsResolvedName().getInfo();
        info.setMaxAutoResolvingDepth(5);
        info.searchSubNames(new NullLoadingProgress());
        Assert.assertEquals(1, info.getSubNames().size()); // only function is traceable
    }
}
