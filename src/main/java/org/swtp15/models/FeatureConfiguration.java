package org.swtp15.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
    private Map<String, Boolean> binaryFeatures;

    @Getter
    private Map<String, Integer> numericFeatures;

    @Getter
    @Setter
    private Map<String, Double> propertyValueMap;


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
    public FeatureConfiguration(String featureModelName, @NonNull Map<String, Boolean> binaryFeatures,
                                @NonNull Map<String, Integer> numericFeatures, Map<String, Double> properties) {
        this.featureModelName = featureModelName;
        this.binaryFeatures   = binaryFeatures;
        this.numericFeatures  = numericFeatures;
        this.propertyValueMap = properties;
    }


    /**
     * Returns all features currently active in this configuration.
     *
     * @return Set of active features
     */
    public Set<String> getActiveFeatures() {
        return binaryFeatures.keySet().stream().
                filter(f -> this.binaryFeatures.get(f)).collect(Collectors.toSet());
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
    @Override
    public FeatureConfiguration clone() {
        FeatureConfiguration clone = null;
        try {
            clone = (FeatureConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        clone.featureModelName = this.featureModelName;
        clone.binaryFeatures   = new HashMap<>();
        clone.numericFeatures  = new HashMap<>();
        clone.propertyValueMap = new HashMap<>();

        // create copy of binary Features
        for (String binaryFeature : this.binaryFeatures.keySet()) {
            clone.binaryFeatures.put(binaryFeature, this.binaryFeatures.get(binaryFeature));
        }

        // create copy of numeric Features
        for (String numericFeature : this.numericFeatures.keySet()) {
            clone.numericFeatures.put(numericFeature, this.numericFeatures.get(numericFeature));
        }

        // create copy of property Features
        for (String property : this.propertyValueMap.keySet()) {
            clone.propertyValueMap.put(property, this.propertyValueMap.get(property));
        }

        return clone;
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
        return root.toJSONString();
    }

}
