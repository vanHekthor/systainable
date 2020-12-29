package org.swtp15.parser;


import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.swtp15.models.FeatureConfiguration;

import java.util.*;

public final class FeatureConfigurationParser {

    /**
     * Parses a list of configurations.
     *
     * @param json JSON string to be parsed
     *
     * @return List of all {@link FeatureConfiguration} which could be parsed from the string
     *
     * @throws IllegalArgumentException If the string does not include the `featureConfigurations` key in its root
     */
    public static List<FeatureConfiguration> parseConfigurations(String json) throws IllegalArgumentException {
        try {
            return FeatureConfigurationParser.parseConfigurations((JSONObject) new JSONParser().parse(json));
        } catch (ParseException e) {
            // Will never happen, as reading a String will not cause this exception
        }
        return null;
    }

    /**
     * Parses a single configurations.
     *
     * @param json JSON string to be parsed
     *
     * @return {@link FeatureConfiguration} parsed from the string (or first Configuration if input was a list of
     * configurations)
     *
     * @throws IllegalArgumentException If the configuration contains unrecoverable syntax errors in the JSON
     */
    public static FeatureConfiguration parseConfiguration(String json) throws IllegalArgumentException {
        try {
            return FeatureConfigurationParser.parseConfiguration((JSONObject) new JSONParser().parse(json));
        } catch (ParseException ignored) {
            // Will never happen, as reading a String will not cause this exception
            return null;
        }
    }


    /**
     * @param root Root node of the JSON
     *
     * @see FeatureConfigurationParser#parseConfigurations(String)
     */
    private static List<FeatureConfiguration> parseConfigurations(@NonNull JSONObject root)
    throws IllegalArgumentException {
        List<FeatureConfiguration> configs = new ArrayList<>();
        JSONArray featureConfigurations = (JSONArray) root.get("featureConfigurations");
        if (featureConfigurations == null) {
            try {
                configs.add(FeatureConfigurationParser.parseConfiguration(root));
            } catch (IllegalArgumentException e) {
                throw ParserExceptions.MISSING_FEATURE_CONFIGURATIONS_IN_JSON;
            }
            return configs;
        }
        for (Object possibleConfiguration : featureConfigurations) {
            try {
                configs.add(FeatureConfigurationParser.parseConfiguration((JSONObject) possibleConfiguration));
            } catch (Exception e) {
                // Empty to just skip invalid configurations
            }
        }
        return configs;
    }


    /**
     * @param root Root node of the JSON
     *
     * @see FeatureConfigurationParser#parseConfiguration(String)
     */
    private static FeatureConfiguration parseConfiguration(@NonNull JSONObject root) throws IllegalArgumentException {
        if ((root = (JSONObject) root.get("featureConfiguration")) == null) {
            throw ParserExceptions.MISSING_FEATURE_CONFIGURATION_IN_JSON;
        }

        String featureModelName = root.get("featureModel") == null ? "" : (String) root.get("featureModel");

        JSONArray features;
        if ((features = (JSONArray) root.get("features")) == null) {
            throw ParserExceptions.MISSING_FEATURES_ARRAY_IN_JSON;
        }
        Set<String> activeFeatures = new HashSet<>();
        for (Object featureName : features) {
            activeFeatures.add((String) featureName);
        }
        Map<String, Double> properties = new HashMap<>();
        JSONObject propertiesRoot;
        if ((propertiesRoot = (JSONObject) root.get("properties")) != null) {
            if (propertiesRoot.keySet().size() == 0) {
                throw ParserExceptions.MISSING_PROPERTIES_IN_MAP_IN_JSON;
            }
            for (Object propertyName : propertiesRoot.keySet()) {
                String propertyNameString = (String) propertyName;
                if (propertiesRoot.get(propertyNameString) instanceof Double) {
                    properties.put(propertyNameString, (Double) propertiesRoot.get(propertyNameString));
                } else {
                    throw ParserExceptions.PROPERTY_VALUE_NOT_A_DOUBLE_IN_JSON;
                }
            }
        }
        return new FeatureConfiguration(featureModelName, activeFeatures, properties);
    }

}
