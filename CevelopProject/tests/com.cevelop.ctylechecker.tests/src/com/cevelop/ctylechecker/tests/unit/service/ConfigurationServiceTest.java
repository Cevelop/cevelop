package com.cevelop.ctylechecker.tests.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.ConfigurationType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Configuration;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class ConfigurationServiceTest {

    IConfigurationService configurationService = CtylecheckerRuntime.getInstance().getRegistry().getConfigurationService();

    @Test
    public void testDefaultConfigurationIsCreated() {
        IConfiguration config = configurationService.getDefaultConfiguration();
        assertTrue(!config.getStyleguides().isEmpty());
        assertEquals(IStyleguide.GOOGLE, config.getActiveStyleguide().getName());
        List<String> styleguideNames = Arrays.asList(IStyleguide.CANONICAL, IStyleguide.GEOSOFT, IStyleguide.GOOGLE);
        for (IStyleguide styleguide : config.getStyleguides()) {
            assertTrue(styleguideNames.contains(styleguide.getName()));
        }
    }

    @Test
    public void testDefaultConfigurationIsRestored() {
        IConfiguration config = new Configuration();
        assertEquals(config.getSetting(), ConfigurationType.WORKSPACE);
        assertEquals(0, config.getStyleguides().size());
        assertEquals("", config.getActiveStyleguide().getName());
        assertEquals(true, config.isEnabled());
        configurationService.restoreDefaults(config);
        assertTrue(!config.getStyleguides().isEmpty());
        assertEquals(IStyleguide.GOOGLE, config.getActiveStyleguide().getName());
        List<String> styleguideNames = Arrays.asList(IStyleguide.CANONICAL, IStyleguide.GEOSOFT, IStyleguide.GOOGLE);
        for (IStyleguide styleguide : config.getStyleguides()) {
            assertTrue(styleguideNames.contains(styleguide.getName()));
        }
    }
}
