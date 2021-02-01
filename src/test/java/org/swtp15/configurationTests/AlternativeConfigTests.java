package org.swtp15.configurationTests;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.parser.FeatureConfigurationParser;
import org.swtp15.system.SystemCache;
import org.swtp15.system.SystemCacheUpdater;
import org.swtp15.system.SystemExceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AlternativeConfigTests {

    private static SystemCache systemCache = new SystemCache();
    private static SystemCacheUpdater updater = new SystemCacheUpdater(systemCache);

    @BeforeAll
    static void readSystems() {
        updater.readSystemsFromDirectory("src/test/testFiles/modelsDirectories");
    }

    private FeatureConfiguration loadConfiguration(String path) {
        try {
            String json = Files.readString(Path.of(path));
            FeatureConfigurationParser.parseConfiguration(json);
            return FeatureConfigurationParser.parseConfiguration(json);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    void getAlternativeConfig() {
        FeatureConfiguration invalidConfig  = loadConfiguration("src/test/testFiles/jsons/alternativeConfigTests" +
                                                                "/invalidConfig");
        FeatureConfiguration alternativeConfig = loadConfiguration("src/test/testFiles/jsons/alternativeConfigTests" +
                                                                   "/alternativConfig");
        try {
            assertEquals(alternativeConfig, systemCache.getAlternativeConfiguration(invalidConfig));
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    void noSuchFeatureSystem() {
        FeatureConfiguration wrongSystemConfig = loadConfiguration("src/test/testFiles/jsons/alternativeConfigTests" +
                                                        "/wrongFeatureSystemConfig");
        try {
            systemCache.getAlternativeConfiguration(wrongSystemConfig);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(SystemExceptions.NO_MATCHING_SYSTEM_FOR_CONFIGURATION, e);
        } catch (InterruptedException e) {
            fail();
        }
    }


}
