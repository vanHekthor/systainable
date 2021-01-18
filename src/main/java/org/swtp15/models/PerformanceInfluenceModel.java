package org.swtp15.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class PerformanceInfluenceModel {

    @Getter
    private final Set<Property> properties;

    @Getter
    private final Set<FeatureInfluence> featureInfluences;

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
     * @param featureConfiguration the {@link FeatureConfiguration} to be evaluated.
     *
     * @return The evaluated property values as a map.
     */
    public Map<Property, Double> evaluateConfiguration(FeatureConfiguration featureConfiguration) {
        Set<String> activeFeatures = featureConfiguration.getActiveFeatures();
        Set<String> numericFeatures = featureConfiguration.getNumericFeatures().keySet();
        activeFeatures.addAll(numericFeatures); //numericFeatures are always active
        Map<Property, Double> returnEvaluation = new HashMap<>();
        Map<String, Double> evaluation = new HashMap<>();
        for (Property property : properties) {
            returnEvaluation.put(property, 0.0);
            evaluation.put(property.getName(), 0.0);
        }
        Set<FeatureInfluence> relatedInfluences = featureInfluences.stream().parallel()
                .filter(x -> activeFeatures.containsAll(x.getActiveFeatures().stream().map(Feature::getName)
                                                                .collect(Collectors.toSet())))
                .collect(Collectors.toSet());
        for (FeatureInfluence featureInfluence : relatedInfluences) {
            Set<String> activeNumericFeatures =
                    featureInfluence.getActiveFeatures().stream().map(Feature::getName)
                            .filter(numericFeatures::contains).collect(Collectors.toSet());
            int factor = 1;
            for(String feature : activeNumericFeatures){
                factor *= featureConfiguration.getNumericFeatures().get(feature);
            }
            for (Map.Entry<Property, Double> entry : featureInfluence.getPropertyInfluence().entrySet()) {
                returnEvaluation.put(entry.getKey(), returnEvaluation.get(entry.getKey()) + factor * entry.getValue());
                evaluation.put(entry.getKey().getName(),
                               evaluation.get(entry.getKey().getName()) + factor * entry.getValue());
            }
        }
        featureConfiguration.setPropertyValues(evaluation);
        return returnEvaluation;
    }

    /**
     * Creates a Map of the property names and initializes their values with 0.0.
     *
     * @return The map of the property names.
     */
    public Map<String, Double> getInitialPropertyMap() {
        Map<String, Double> propertyMap = new HashMap<>();
        properties.forEach(p -> propertyMap.put(p.getName(), 0.0));
        return propertyMap;
    }
}
