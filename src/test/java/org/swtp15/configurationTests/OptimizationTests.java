package org.swtp15.configurationTests;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.models.FeatureSystem;
import org.swtp15.models.ModelExceptions;
import org.swtp15.parser.FeatureConfigurationParser;
import org.swtp15.system.SystemCache;
import org.swtp15.system.SystemCacheUpdater;
import org.swtp15.system.SystemExceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class OptimizationTests {


    private static SystemCache systemCache = new SystemCache();
    private static SystemCacheUpdater updater = new SystemCacheUpdater(systemCache);
    private static FeatureSystem optimizationSystem;

    @BeforeAll
    static void readSystems() {
        updater.readSystemsFromDirectory("src/test/testFiles/modelsDirectories");
        optimizationSystem = systemCache.getFeatureSystemByName("system5");
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
    void noSuchFeatureSystem() {
        FeatureConfiguration config = loadConfiguration("src/test/testFiles/jsons" +
                                                        "/optimizationTests/invalidFeatureSystem");
        String property = (String) config.getPropertyValueMap().keySet().toArray()[0];
        try {
            systemCache.findLocalOptimumForConfiguration(config, property, 1);
            fail();
        } catch (InterruptedException | IllegalArgumentException e) {
            assertEquals(SystemExceptions.NO_MATCHING_SYSTEM_FOR_CONFIGURATION, e);
        }
    }

    @Test
    void invalidConfiguration() {
        FeatureConfiguration config = loadConfiguration("src/test/testFiles/jsons/optimizationTests" +
                                                        "/invalidConfiguration");
        String property = (String) config.getPropertyValueMap().keySet().toArray()[0];
        try {
            systemCache.findLocalOptimumForConfiguration(config, property, 1);
            fail();
        } catch (InterruptedException | IllegalArgumentException e) {
            assertEquals(ModelExceptions.CONFIGURATION_NOT_VALID, e);
        }
    }

    @Test
    void isAlreadyOptimum() {
        FeatureConfiguration config = loadConfiguration(
                "src/test/testFiles/jsons/optimizationTests/isAlreadyOptimum");
        String property = (String) config.getPropertyValueMap().keySet().toArray()[0];
        try {
            systemCache.findLocalOptimumForConfiguration(config, property, 1);
            fail();
        } catch (InterruptedException | IllegalArgumentException e) {
            assertEquals(SystemExceptions.IS_ALREADY_OPTIMUM, e);
        }
    }

    @Test
    void findOptimum() {
        FeatureConfiguration config = loadConfiguration("src/test/testFiles/jsons/optimizationTests/startConfig");
        FeatureConfiguration optimalConfig = loadConfiguration("src/test/testFiles/jsons/optimizationTests" +
                                                               "/optimalConfig");
        String property = (String) config.getPropertyValueMap().keySet().toArray()[0];
        try {
            config = systemCache.findLocalOptimumForConfiguration(config, property, 1);
            assertEquals(optimalConfig, config);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
