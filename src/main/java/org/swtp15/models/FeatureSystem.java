package org.swtp15.models;

import lombok.Getter;

import java.util.Map;

public class FeatureSystem {
    @Getter
    private String name;

    @Getter
    private FeatureModel featureModel;

    @Getter
    private PerformanceInfluenceModel performanceModel;

    /**
     * The constructor.
     * @param name The name of the FeatureSystem.
     * @param featureModel The belonging FeatureModel.
     * @param performanceModel The belonging PerformanceModel.
     */
    public FeatureSystem(String name, FeatureModel featureModel, PerformanceInfluenceModel performanceModel) {
        this.name = name;
        this.featureModel = featureModel;
        this.performanceModel = performanceModel;
    }

    /**
     * Evaluates a FeatureConfiguration.
     * @param featureConfiguration The Configuration to be evaluated.
     * @return The evaluated Property values as a Map.
     */
    public Map<Property, Double> evaluateFeatureConfiguration(FeatureConfiguration featureConfiguration){
        return performanceModel.evaluateConfiguration(featureConfiguration.getActiveFeatures());
    }
}
