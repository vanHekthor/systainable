package org.swtp15.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureConfiguration {
    @Getter
    @Setter
    private String featureModelName;

    @Getter
    private Map<String, Boolean> features;

    @Getter
    @Setter
    private Map<String, Double> propertyValues;


    /**
     * Constructor for the class. Instead of using instances of {@link Feature} or {@link Property}, Strings are used
     * here to (1) allow for quicker comparison for example inside {@link FeatureModel} as well as (2) avoid creating
     * useless instances of those classes.
     *
     * @param featureModelName Name of the FeatureModel this configuration should belong to
     * @param features         Map of features with active status
     * @param properties       Map from the Properties of the FeatureSystem to the respective values
     */
    public FeatureConfiguration(String featureModelName, @NonNull Map<String, Boolean> features,
                                Map<String, Double> properties) {
        this.featureModelName = featureModelName;
        this.features         = features;
        this.propertyValues   = properties;
    }

    /**
     * @param featureModelName Name of the FeatureModel this configuration should belong to
     * @param features         Map of features with active status
     *
     * @see FeatureConfiguration#FeatureConfiguration(String, Map, Map)
     */
    public FeatureConfiguration(String featureModelName, @NonNull Map<String, Boolean> features) {
        this(featureModelName, features, null);
    }

    /**
     * @param features   Map of features with active status
     * @param properties Map from the Properties of the FeatureSystem to the respective values
     *
     * @see FeatureConfiguration#FeatureConfiguration(String, Map, Map)
     */
    public FeatureConfiguration(@NonNull Map<String, Boolean> features, Map<String, Double> properties) {
        this("", features, properties);
    }

    /**
     * @param features Map of features with active status
     *
     * @see FeatureConfiguration#FeatureConfiguration(String, Map, Map)
     */
    public FeatureConfiguration(@NonNull Map<String, Boolean> features) {
        this("", features, null);
    }

    /**
     * Returns all features currently active in this configuration.
     *
     * @return Set of active features
     */
    public Set<String> getActiveFeatures() {
        return features.keySet().stream().
                filter(f -> this.features.get(f)).collect(Collectors.toSet());
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
        for (String featureName : this.features.keySet()) {
            features.put(featureName, this.features.get(featureName));
        }
        if (this.propertyValues != null) {
            conf.put("properties", properties);
            for (String property : this.propertyValues.keySet()) {
                properties.put(property, this.propertyValues.get(property));
            }
        }
        return root.toJSONString();
    }

}
