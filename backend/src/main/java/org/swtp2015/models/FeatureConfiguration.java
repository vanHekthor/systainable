package org.swtp2015.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

public class FeatureConfiguration {

    @Setter
    @Getter
    private FeatureModel featureModel = null;

    @Getter
    @Setter
    private String featureModelName;

    @Getter
    private final Set<String> activeFeatures;

    @Getter
    @Setter
    private Map<String, Double> propertyValues;


    /**
     * Constructor for the class. Instead of using instances of {@link Feature} or {@link Property}, Strings are used
     * here to (1) allow for quicker comparison for example inside {@link FeatureModel} as well as (2) avoid creating
     * useless instances of those classes.
     *
     * @param featureModelName Name of the FeatureModel this configuration should belong to
     * @param activeFeatures   Set of the features currently set as active
     * @param properties       Map from the Properties of the FeatureSystem to the respective values
     */
    public FeatureConfiguration(String featureModelName, @NonNull Set<String> activeFeatures,
                                Map<String, Double> properties) {
        this.featureModelName = featureModelName;
        this.activeFeatures   = activeFeatures;
        this.propertyValues   = properties;
    }

    /**
     * @param featureModelName Name of the FeatureModel this configuration should belong to
     * @param activeFeatures   Set of the features currently set as active
     *
     * @see FeatureConfiguration#FeatureConfiguration(String, Set, Map)
     */
    public FeatureConfiguration(String featureModelName, @NonNull Set<String> activeFeatures) {
        this(featureModelName, activeFeatures, null);
    }

    /**
     * @param activeFeatures Set of the features currently set as active
     * @param properties     Map from the Properties of the FeatureSystem to the respective values
     *
     * @see FeatureConfiguration#FeatureConfiguration(String, Set, Map)
     */
    public FeatureConfiguration(@NonNull Set<String> activeFeatures, Map<String, Double> properties) {
        this("", activeFeatures, properties);
    }

    /**
     * @param activeFeatures Set of the features currently set as active
     *
     * @see FeatureConfiguration#FeatureConfiguration(String, Set, Map)
     */
    public FeatureConfiguration(@NonNull Set<String> activeFeatures) {
        this("", activeFeatures, null);
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
        final JSONArray features = new JSONArray();

        root.put("featureConfiguration", conf);
        conf.put("featureModel", this.featureModelName);
        conf.put("features", features);
        features.addAll(this.activeFeatures);
        if (this.propertyValues != null) {
            conf.put("properties", properties);
            for (String property : this.propertyValues.keySet()) {
                properties.put(property, this.propertyValues.get(property));
            }
        }
        return root.toJSONString();
    }

}
