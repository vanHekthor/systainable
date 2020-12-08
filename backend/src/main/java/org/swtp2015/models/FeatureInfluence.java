package org.swtp2015.models;

import java.util.Map;
import java.util.Set;

/**
 * Class to  provide a data structure for the Performance-Influence-Model.
 * It's instances describe one line of the "model.csv" file.
 */
public class FeatureInfluence {
    private final Set<Feature> activeFeatures;
    private final Map<Property, Double> propertyInfluence;

    /**
     * @param activeFeatures    the Features that are selected in current line of csv-file (meaning the
     *                          column in the csv-file contains a 1)
     * @param propertyInfluence a Map to map effects (represented by doubles) to the effected {@link Property}
     */
    public FeatureInfluence(Set<Feature> activeFeatures, Map<Property, Double> propertyInfluence) {
        this.activeFeatures = activeFeatures;
        this.propertyInfluence = propertyInfluence;
    }

    public Set<Feature> getActiveFeatures() {
        return activeFeatures;
    }

    public Map<Property, Double> getPropertyInfluence() {
        return propertyInfluence;
    }
}
