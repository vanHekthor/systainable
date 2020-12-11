package org.swtp2015.parser;

/**
 * Collection of Exceptions to prevent code repetition. Mostly used for Unittests
 */
public final class ParserExceptions {

    public static final IllegalArgumentException CONFIGURATION_NOT_SUBSET_OF_MODEL
            = new IllegalArgumentException("Configuration contains foreign features");
    public static final IllegalArgumentException MISSING_FEATURE_CONFIGURATIONS_IN_JSON = new IllegalArgumentException(
            "Missing key 'featureConfigurations' in JSON");
    public static final IllegalArgumentException MISSING_FEATURE_CONFIGURATION_IN_JSON = new IllegalArgumentException(
            "Missing key 'featureConfiguration' in JSON");
    public static final IllegalArgumentException MISSING_FEATURES_ARRAY_IN_JSON = new IllegalArgumentException(
            "Missing array 'features' in 'featureConfiguration'");
    public static final IllegalArgumentException MISSING_PROPERTIES_IN_MAP_IN_JSON = new IllegalArgumentException(
            "Map 'properties' missing entries");
    public static final IllegalArgumentException PROPERTY_VALUE_NOT_A_DOUBLE_IN_JSON = new IllegalArgumentException(
            "Property value is not a Double");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_NOT_CSV_EXTENSION = new IllegalArgumentException(
            "Delivered file for Performance-Influence-Model is not a .csv file");
}


