package org.swtp15.systemTests;

import org.junit.jupiter.api.Test;
import org.swtp15.system.SystemCache;
import org.swtp15.system.SystemCacheUpdater;

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
        try {
            updater.readSystemsFromDirectory("src/test/testFiles/modelsDirectories");
            assertTrue(updater.getSystemCache().getCurrentlyKnownSystems().keySet().containsAll(getExpectedSystems())
                       &&
                       getExpectedSystems().containsAll(updater.getSystemCache().getCurrentlyKnownSystems().keySet()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void nonExistingPath() {
        try {
            updater.readSystemsFromDirectory("test");
            fail("Exception not thrown");
        } catch (Exception e) {
            assertEquals("Given directory path does not exist.", e.getMessage());
        }
    }
}

