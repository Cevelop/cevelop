package com.cevelop.ctylechecker.service.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Styleguide;
import com.cevelop.ctylechecker.service.util.ConfigurationMapper;


public class StyleguideFactory {

    private static final String PREDEFINED_STYLEGUIDES = "/resources/predefined/";
    private static final String GOOGLE_STYLEGUIDE      = PREDEFINED_STYLEGUIDES + "google.ctyleguide";
    private static final String CANONICAL_STYLEGUIDE   = PREDEFINED_STYLEGUIDES + "canonical.ctyleguide";
    private static final String GEOSOFT_STYLEGUIDE     = PREDEFINED_STYLEGUIDES + "geosoft.ctyleguide";

    private static Optional<IStyleguide> loadPredefined(String path) {
        try (InputStream in = StyleguideFactory.class.getResourceAsStream(path)) {
            String ctyleguideString = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            return Optional.of(ConfigurationMapper.fromJson(ctyleguideString, IStyleguide.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static IStyleguide createGoogleStyleguide() {
        Optional<IStyleguide> guide = loadPredefined(GOOGLE_STYLEGUIDE);
        return guide.isPresent() ? guide.get() : new Styleguide("Failed to load Google Styleguide");
    }

    public static IStyleguide createCanonicalStyleguide() {
        Optional<IStyleguide> guide = loadPredefined(CANONICAL_STYLEGUIDE);
        return guide.isPresent() ? guide.get() : new Styleguide("Failed to load Canonical Styleguide");
    }

    public static IStyleguide createGeosoftStyleguide() {
        Optional<IStyleguide> guide = loadPredefined(GEOSOFT_STYLEGUIDE);
        return guide.isPresent() ? guide.get() : new Styleguide("Failed to load Geosoft Styleguide");
    }
}
