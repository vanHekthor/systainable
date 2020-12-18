package org.swtp15.system;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides functionality to save and update all known FeatureSystems.
 */
public class SystemCache {
    //ToDo: will later become a Map when FeatureSystems will be implemented
    @Getter
    @Setter
    private Set<String> currentlyKnownSystems;

    /**
     * The constructor.
     */
    public SystemCache() {
        currentlyKnownSystems = new HashSet<>();
    }
}
