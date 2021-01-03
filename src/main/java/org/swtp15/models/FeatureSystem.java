package org.swtp15.models;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

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
     * @param featureModel     The belonging FeatureModel.
     * @param performanceModel The belonging PerformanceModel.
     */
    public FeatureSystem(String name, FeatureModel featureModel, PerformanceInfluenceModel performanceModel) {
        this.name             = name;
        this.featureModel     = featureModel;
        this.performanceModel = performanceModel;
    }


    /**
     * Tests, if feature configuration is valid for this system.
     *
     * @param featureConfiguration The configuration that should be tested
     *
     * @return Whether the given configuration is valid
     *
     * @throws InterruptedException If the thread calculating was interrupted before it could finish gracefully
     * @throws IllegalArgumentException If the given configuration contains features not included in the feature model
     */
    public Boolean configurationIsValid(FeatureConfiguration featureConfiguration) throws InterruptedException,
            IllegalArgumentException {
        return this.featureModel.isValidConfiguration(featureConfiguration);
    }

    /**
     * Evaluates a FeatureConfiguration.
     *
     * @param featureConfiguration The Configuration to be evaluated.
     *
     * @return The evaluated Property values as a Map.
     */
    public Map<Property, Double> evaluateFeatureConfiguration(FeatureConfiguration featureConfiguration) {
        return performanceModel.evaluateConfiguration(featureConfiguration.getActiveFeatures());
    }
}
