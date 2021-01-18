package org.swtp15.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.swtp15.models.Feature;
import org.swtp15.models.FeatureSystem;
import org.swtp15.models.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SystemParser {

    /**
     * Converts the SystemNames to String.
     *
     * @param systemNames Set of system names
     *
     * @return JSON representation of SystemNames as String
     */
    @SuppressWarnings("unchecked")
    public static String parseSystemNamesToJson(Set<String> systemNames) {
        List<String> orderedSystemNames = new ArrayList<>(systemNames);
        Collections.sort(orderedSystemNames);
        JSONObject root = new JSONObject();
        JSONArray names = new JSONArray();

        names.addAll(orderedSystemNames);
        root.put("systemNames", names);

        return root.toJSONString();
    }

    /**
     * Converts a {@link FeatureSystem} to String in JSON format.
     *
     * @param featureSystem {@link FeatureSystem}, that should be parsed
     *
     * @return JSON representation of {@link FeatureSystem} as String
     */
    @SuppressWarnings("unchecked")
    public static String parseSystemToJson(FeatureSystem featureSystem) {
        JSONObject root = new JSONObject();
        JSONObject features = new JSONObject();
        JSONArray binaryFeatures = new JSONArray();
        JSONObject numericFeatures = new JSONObject();
        JSONObject properties = new JSONObject();

        for (Feature feature : featureSystem.getFeatures()) {
            if (feature.isBinary()) {
                binaryFeatures.add(feature.getName());
            } else {
                numericFeatures.put(feature.getName(),
                                    feature.getMinValue() + ";" + feature.getMaxValue() + ";" +
                                    feature.getStepFunction());
            }
        }
        features.put("binaryFeatures", binaryFeatures);
        features.put("numericFeatures", numericFeatures);

        root.put("features", features);

        for (Property property : featureSystem.getProperties()) {
            properties.put(property.getName(), property.getUnit() + " " + (property.isToMinimize() ? "<" : ">"));
        }
        root.put("properties", properties);

        return root.toJSONString();
    }
}
