package com.cevelop.ctylechecker.tests.checker.dynamic;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.cevelop.ctylechecker.Activator;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.types.Styleguide;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.service.util.ConfigurationMapper;
import com.cevelop.ctylechecker.tests.checker.CheckerTest;
import com.google.gson.JsonSyntaxException;


public class DynamicStyleCheckerTest extends CheckerTest {

    IConfiguration        config;
    IConfigurationService configService = CtylecheckerRuntime.getInstance().getRegistry().getConfigurationService();

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.DYNAMIC;
    }

    @Override
    protected void configureTest(Properties properties) {
        String valuePair = properties.get("activeStyleguide").toString();
        String[] values = valuePair.split(":", 2);
        if (values.length == 2) {
            if (values[0].equals("predefined")) {
                configurePredefinedStyleguide(values[1]);
            }
            if (values[0].equals("custom")) {
                configureCustomStyleguide(values[1]);
            }
            if (values[0].equals("file")) {
                configureReferenceStyleguide(values[1]);
            }
        }
        if (config == null) {
            config = configService.getDefaultConfiguration();
        }
        configService.saveConfiguration(Activator.getDefault().getPreferenceStore(), config);
        super.configureTest(properties);
    }

    private void configureReferenceStyleguide(String styleguideFileName) {
        if (styleguideFileName != null) {
            try {
                URL fileUrl = getClass().getResource("/styleguides/" + styleguideFileName + ".ctyleguide");
                String path = fileUrl.toURI().getRawPath();
                byte[] bytes = Files.readAllBytes(Paths.get(path.substring(1)));
                String styleguideJson = new String(bytes);
                configureCustomStyleguide(styleguideJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void configureCustomStyleguide(Object styleguideJson) {
        if (styleguideJson != null) {
            try {
                Styleguide activeStyleguide = ConfigurationMapper.fromJson(styleguideJson.toString(), Styleguide.class);
                config = configService.getDefaultConfiguration();
                config.setActiveStyleguide(activeStyleguide);
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void configurePredefinedStyleguide(Object styleguideName) {
        if (styleguideName != null) {
            config = configService.getDefaultConfiguration();
            config.getStyleguides().stream().forEach((styleguide) -> {
                if (styleguide.getName().equals(styleguideName + " Styleguide")) {
                    config.setActiveStyleguide(styleguide);
                }
            });
        }
    }
}
