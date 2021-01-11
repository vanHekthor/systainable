package org.swtp15.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureSystem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private PerformanceInfluenceModel performanceModel;

    @Getter
    @Setter
    private FeatureModel featureModel;


    /**
     * The constructor.
     *
     * @param name             The name of the FeatureSystem.
     * @param featureModel     The belonging {@link FeatureModel}.
     * @param performanceModel The belonging {@link PerformanceInfluenceModel}.
     */
    public FeatureSystem(String name, FeatureModel featureModel, PerformanceInfluenceModel performanceModel) {
        this.name             = name;
        this.featureModel     = featureModel;
        this.performanceModel = performanceModel;
    }


    /**
     * Tests, if feature configuration is valid for this system.
     *
     * @param featureConfiguration The {@link FeatureConfiguration} that should be tested
     *
     * @return Whether the given configuration is valid
     *
     * @throws InterruptedException     If the thread calculating was interrupted before it could finish gracefully
     * @throws IllegalArgumentException If the given configuration contains features not included in the feature model
     */
    public Boolean configurationIsValid(FeatureConfiguration featureConfiguration) throws InterruptedException,
                                                                                          IllegalArgumentException {
        return this.featureModel.isValidConfiguration(featureConfiguration);
    }

    /**
     * Evaluates a {@link FeatureConfiguration}. It sets the Map of the FeatureConfiguration to the evaluated values,
     * but also returns this Map because we will probably need this later, wenn trying to optimize a Config.
     *
     * @param featureConfiguration The Configuration to be evaluated.
     *
     * @return The evaluated Property values as a Map.
     */
    public Map<Property, Double> evaluateFeatureConfiguration(FeatureConfiguration featureConfiguration) {
        return performanceModel.evaluateConfiguration(featureConfiguration);
    }

    /**
     * Returns a List containing the names of this FeatureSystem as String.
     *
     * @return A Set of feature names
     */
    public List<String> getFeatureNames() {
        return featureModel.getFeatures().stream().parallel().map(Feature::getName).collect(
                Collectors.toList());
    }

    /**
     * Returns a Set containing the names of this FeatureSystem as String.
     *
     * @return A Set of feature names
     */
    public Set<Property> getProperties() {
        return performanceModel.getProperties();
    }
}
