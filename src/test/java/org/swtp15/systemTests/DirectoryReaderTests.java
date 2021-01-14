package org.swtp15.systemTests;

import org.junit.jupiter.api.Test;
import org.swtp15.models.FeatureSystem;
import org.swtp15.system.SystemCache;
import org.swtp15.system.SystemCacheUpdater;
import org.swtp15.system.SystemExceptions;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryReaderTests {
    private final SystemCacheUpdater updater = new SystemCacheUpdater(new SystemCache());

    private Set<String> getExpectedSystems() {
        Set<String> expectedSystems = new HashSet<>();
        expectedSystems.add("system1");
        expectedSystems.add("system2");
        return expectedSystems;
    }

    @Test
    void readCorrectFiles() {
        updater.readSystemsFromDirectory("src/test/testFiles/modelsDirectories");
        assertTrue(updater.getSystemCache().getCurrentlyKnownSystems().keySet().containsAll(getExpectedSystems()));
        assertTrue(getExpectedSystems().containsAll(updater.getSystemCache().getCurrentlyKnownSystems().keySet()));
    }

    @Test
    void nonExistingPath() {
        try {
            updater.readSystemsFromDirectory("test");
            fail("Exception not thrown");
        } catch (Exception e) {
            assertEquals(SystemExceptions.INVALID_DIRECTORY_PATH, e);
        }
    }


    @Test
    void parseAllExampleSystems() {
        updater.initialize();
        StringBuilder builder = new StringBuilder("Successfully read ");
        SystemCache cache = updater.getSystemCache();
        builder.append(cache.getCurrentlyKnownSystems().size()).append(" example systems:");
        for (FeatureSystem system : cache.getCurrentlyKnownSystems().values()) {
            builder.append("\n").append(system.getName());
        }
        System.out.println(builder);
    }
}

