package org.swtp15.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureConfiguration implements Cloneable {
    @Getter
    @Setter
    private String featureModelName;

    @Getter
    private final Map<String, Boolean> binaryFeatures;

    @Getter
    private final Map<String, Integer> numericFeatures;

    @Getter
    @Setter
    private Map<String, Double> propertyValueMap;

    @Getter
    private final Map<FeatureInfluence, Integer> activeInfluences;


    /**
     * Constructor for the class. Instead of using instances of {@link Feature} or {@link Property}, Strings are used
     * here to (1) allow for quicker comparison for example inside {@link FeatureModel} as well as (2) avoid creating
     * useless instances of those classes.
     *
     * @param featureModelName Name of the FeatureModel this configuration should belong to.
     * @param binaryFeatures   Map of binaryFeatures with active status.
     * @param numericFeatures  Map of numericFeatures with belonging value.
     * @param properties       Map from the Properties of the FeatureSystem to the respective values.
     */
    public FeatureConfiguration(@NonNull String featureModelName, @NonNull Map<String, Boolean> binaryFeatures,
                                @NonNull Map<String, Integer> numericFeatures, Map<String, Double> properties) {
        this.featureModelName = featureModelName;
        this.binaryFeatures   = binaryFeatures;
        this.numericFeatures  = numericFeatures;
        this.propertyValueMap = properties;
        this.activeInfluences = new HashMap<>();
    }


    /**
     * Returns all features currently active in this configuration.
     *
     * @return Set of active features
     */
    public Set<String> getActiveFeatures() {
        return binaryFeatures.keySet().parallelStream().filter(this.binaryFeatures::get).collect(Collectors.toSet());
    }

    /**
     * Returns the value of the given property.
     *
     * @param propertyName Property name
     *
     * @return A Double value of property.
     */
    public Double getPropertyValue(String propertyName) {
        return this.propertyValueMap.get(propertyName);
    }


    /**
     * Creates deep copy of {@link FeatureConfiguration}.
     *
     * @return A deep copy instance of {@link FeatureConfiguration}
     */
    @SneakyThrows
    @Override
    public FeatureConfiguration clone() {
        String newFeatureModelName = this.featureModelName;
        Map<String, Boolean> newBinaryFeatures = new HashMap<>();
        Map<String, Integer> newNumericFeatures = new HashMap<>();
        Map<String, Double> newPropertyValueMap = new HashMap<>();

        this.binaryFeatures.forEach(newBinaryFeatures::put);
        this.numericFeatures.forEach(newNumericFeatures::put);
        this.propertyValueMap.forEach(newPropertyValueMap::put);

        return new FeatureConfiguration(newFeatureModelName, newBinaryFeatures, newNumericFeatures,
                                        newPropertyValueMap);
    }


    /**
     * Checks if given {@link FeatureConfiguration} has same configuration as this one.
     *
     * @param o other FeatureConfiguration
     *
     * @return True if same configuration, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeatureConfiguration))
            return false;
        FeatureConfiguration otherConfig = (FeatureConfiguration) o;

        // checks content of both configurations
        // checks if binary features are equal
        Set<String> otherBinaries = otherConfig.getBinaryFeatures().keySet();
        if (this.binaryFeatures.keySet().containsAll(otherBinaries) &&
            otherBinaries.containsAll(this.binaryFeatures.keySet())) {

            // checks if active binary features are equal
            if (this.getActiveFeatures().containsAll(otherConfig.getActiveFeatures()) &&
                otherConfig.getActiveFeatures().containsAll(this.getActiveFeatures())) {

                // checks if numeric features are equal
                Map<String, Integer> otherNumMap = otherConfig.getNumericFeatures();
                if (this.numericFeatures.keySet().containsAll(otherNumMap.keySet()) &&
                    otherNumMap.keySet().containsAll(this.numericFeatures.keySet())) {

                    // checks if numeric features are equal
                    for (String numFeature : this.numericFeatures.keySet()) {
                        if (!this.numericFeatures.get(numFeature).equals(otherNumMap.get(numFeature)))
                            return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Converts the current configuration into its respective JSON format.
     *
     * @return JSON representation of the configuration
     */
    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        final JSONObject root = new JSONObject();
        final JSONObject conf = new JSONObject();
        final JSONObject properties = new JSONObject();
        final JSONObject features = new JSONObject();
        final JSONObject dissectedProperties = new JSONObject();

        root.put("featureConfiguration", conf);
        conf.put("featureModel", this.featureModelName);
        conf.put("features", features);
        for (String featureName : this.binaryFeatures.keySet()) {
            features.put(featureName, this.binaryFeatures.get(featureName));
        }
        for (String featureName : this.numericFeatures.keySet()) {
            features.put(featureName, this.numericFeatures.get(featureName));
        }
        if (this.propertyValueMap != null) {
            conf.put("properties", properties);
            for (String property : this.propertyValueMap.keySet()) {
                properties.put(property, this.propertyValueMap.get(property));
            }
        }
        if (this.activeInfluences.size() > 0) {
            conf.put("dissectedProperties", dissectedProperties);
            int i = 0;
            for (Map.Entry<FeatureInfluence, Integer> dissected : this.activeInfluences.entrySet()) {
                final JSONObject dissectedLine = new JSONObject();
                dissectedProperties.put("interaction" + i++, dissectedLine);
                final JSONArray activeFeatures = new JSONArray();
                dissectedLine.put("features", activeFeatures);
                activeFeatures
                        .addAll(dissected.getKey().getActiveFeatures().parallelStream().map(Feature::getName).collect(
                                Collectors.toSet()));
                final JSONObject propertyInfluences = new JSONObject();
                dissectedLine.put("properties", propertyInfluences);
                for (Map.Entry<Property, Double> singleInflunece : dissected.getKey().getPropertyInfluence()
                        .entrySet()) {
                    propertyInfluences.put(singleInflunece.getKey(),
                                           singleInflunece.getValue() * dissected.getValue());
                }
                dissectedLine.put("multiplier", dissected.getValue());
            }
        }
        return root.toJSONString();
    }

}
