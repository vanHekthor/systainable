package org.swtp15.models;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * Class to  provide a data structure for the {@link PerformanceInfluenceModel}. It's instances describe one line of the
 * "model.csv" file.
 */
public class FeatureInfluence {

    @Getter
    private final Set<Feature> activeFeatures;
    @Getter
    private final Map<Property, Double> propertyInfluence;

    /**
     * @param activeFeatures    the Features that are selected in current line of csv-file (meaning the column in the
     *                          csv-file contains a 1)
     * @param propertyInfluence a Map to map effects (represented by doubles) to the effected {@link Property}
     */
    public FeatureInfluence(Set<Feature> activeFeatures, Map<Property, Double> propertyInfluence) {
        this.activeFeatures    = activeFeatures;
        this.propertyInfluence = propertyInfluence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        activeFeatures.forEach(feature -> sb.append(feature.getName()).append(","));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("->");
        propertyInfluence.forEach((key, value) -> sb.append(key.getName()).append(":").append(value).append(";"));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
