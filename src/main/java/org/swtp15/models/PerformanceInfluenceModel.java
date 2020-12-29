package org.swtp15.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PerformanceInfluenceModel {
    @Getter
    private Set<Property> properties;

    @Getter
    private Set<FeatureInfluence> featureInfluences;

    /**
     * The Constructor.
     *
     * @param properties        The properties of the Model.
     * @param featureInfluences The featureInfluences of the Model.
     */
    public PerformanceInfluenceModel(Set<Property> properties, Set<FeatureInfluence> featureInfluences) {
        this.properties        = properties;
        this.featureInfluences = featureInfluences;
    }

    /**
     * Evaluates the property values for a list of active features. Iterates over the featureInfluences and adds their
     * value if their set of activeFeatures is contained in the given set of activeFeatures.
     *
     * @param activeFeatures Set of activeFeatures.
     *
     * @return The evaluated property values as a map.
     */
    public Map<Property, Double> evaluateConfiguration(Set<String> activeFeatures) {
        Map<Property, Double> evaluation = new HashMap<>();
        for (var property : properties) {
            evaluation.put(property, 0.0);
        }
        var relatedInfluences = featureInfluences.stream().parallel()
                .filter(x -> activeFeatures.containsAll(x.getActiveFeatures().stream().map(Feature::getName)
                                                                .collect(Collectors.toSet())))
                .collect(Collectors.toSet());
        for (var featureInfluence : relatedInfluences) {
            for (Map.Entry<Property, Double> entry : featureInfluence.getPropertyInfluence().entrySet()) {
                evaluation.put(entry.getKey(), evaluation.get(entry.getKey()) + entry.getValue());
            }
        }
        return evaluation;
    }
}
