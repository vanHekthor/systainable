package org.swtp15.system;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.models.FeatureModel;
import org.swtp15.models.FeatureSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides functionality to save and update every known {@link FeatureSystem}.
 */
@Component
public class SystemCache {

    @Getter
    @Setter
    private Map<String, FeatureSystem> currentlyKnownSystems;

    @Getter
    @Setter
    private SystemCacheUpdater systemCacheUpdater;

    @Getter
    private String pathToModels;

    /**
     * The constructor.
     */
    public SystemCache() {
        currentlyKnownSystems = new HashMap<>();
        pathToModels          = "src/main/resources/featureSystems";
    }

    /**
     * Checks, if given name is a known {@link FeatureSystem}.
     *
     * @param featureModelName The name of the {@link FeatureModel}
     *
     * @return Whether the model is known
     */
    public boolean hasSystem(String featureModelName) {
        return currentlyKnownSystems.containsKey(featureModelName);
    }

    /**
     * Returns the {@link FeatureSystem} with the given name.
     *
     * @param name The name of the {@link FeatureSystem}
     *
     * @return A {@link FeatureSystem} with given name
     */
    public FeatureSystem getFeatureSystemByName(String name) {
        return currentlyKnownSystems.get(name);
    }

    /**
     * @param featureConfiguration The configuration that should be checked for validity
     *
     * @return Whether the given configuration is valid
     *
     * @throws InterruptedException     If the thread calculating was interrupted before it could finish gracefully
     * @throws IllegalArgumentException If the given configuration contains features not included in the feature model
     */
    public Boolean configIsValid(FeatureConfiguration featureConfiguration) throws InterruptedException,
                                                                                   IllegalArgumentException {
        FeatureSystem featureSystem = getFeatureSystemForConfiguration(featureConfiguration);
        if (featureSystem == null) {
            throw SystemExceptions.NO_MATCHING_SYSTEM_FOR_CONFIGURATION;
        }
        return featureSystem.configurationIsValid(featureConfiguration);
    }

    /**
     * Looks for corresponding system of given {@link FeatureConfiguration} and finds (local) optimum for a specific
     * property in a given range. Range here describes the features that can be different from the given configuration.
     *
     * @param configToOptimize {@link FeatureConfiguration} that needs optimization
     * @param propertyName     name of property that should be optimized
     * @param maxDifference    number of features that can be different
     *
     * @return optimized {@link FeatureConfiguration} for property
     *
     * @throws IllegalArgumentException If system not found or configuration can't be or is already optimized
     * @throws InterruptedException     If the thread calculating was interrupted before it could finish gracefully
     */
    public FeatureConfiguration findLocalOptimumForConfiguration(FeatureConfiguration configToOptimize,
                                                                 String propertyName, int maxDifference)
    throws IllegalArgumentException, InterruptedException {
        FeatureSystem system = getFeatureSystemForConfiguration(configToOptimize);
        if (system == null) {
            throw SystemExceptions.NO_MATCHING_SYSTEM_FOR_CONFIGURATION;
        } else {
            return system.findLocalOptimum(configToOptimize, propertyName, maxDifference);
        }
    }

    /**
     * Searches for a {@link FeatureSystem} with same name as model name in given configuration. Its supposed that only
     * one or none {@link FeatureSystem} matches the given name.
     *
     * @param featureConfiguration The configuration for which the corresponding system is looked for.
     *
     * @return {@link FeatureSystem} for Configuration or NULL if not found
     */
    private FeatureSystem getFeatureSystemForConfiguration(FeatureConfiguration featureConfiguration)
    throws IllegalArgumentException {
        String modelNameInConfig = featureConfiguration.getFeatureModelName();
        if (currentlyKnownSystems.values().stream().parallel().filter(f -> f.getName().equals(modelNameInConfig))
                    .count() == 1) {
            return currentlyKnownSystems.values().stream().parallel().filter(f -> f.getName().equals(modelNameInConfig))
                    .collect(Collectors.toList()).get(0);
        } else if (currentlyKnownSystems.values().stream().parallel()
                .noneMatch(f -> f.getName().equals(modelNameInConfig))) {
            return null;
        } else {
            throw SystemExceptions.MORE_THAN_ONE_FEATURESYSTEM_MATCH_CONFIGURATION;
        }
    }
}
