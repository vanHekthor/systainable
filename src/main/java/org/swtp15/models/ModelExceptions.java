package org.swtp15.models;

/**
 * Collection of Exceptions for model-classes.
 */
public class ModelExceptions {

    public static final IllegalArgumentException CONFIGURATION_NOT_VALID
            = new IllegalArgumentException("The given configuration is not valid for this system.");
    public static final IllegalArgumentException PROPERTY_NOT_IN_MODEL
            = new IllegalArgumentException("No such property in model.");
    public static final IllegalArgumentException NUMERIC_FEATURE_VALUE_OVER_BOUNDS = new IllegalArgumentException(
            "The next value exceeds the maximum value");
    public static final UnsupportedOperationException FEATURE_NOT_A_NUMERIC_FEATURE = new UnsupportedOperationException(
            "The given Feature is not a numeric one");
    public static final IllegalStateException MODEL_HAS_NO_VALID_CONFIGURATIONS
            = new IllegalStateException("This Model has no valid Configurations");
    public static final IllegalArgumentException NUMERIC_FEATURES_IN_CONFIG_DONT_MATCH_MODEL
            = new IllegalArgumentException("The numeric features in the configuration don't match the features in " +
                                           "the model.");
    public static final IllegalArgumentException NUMERIC_VALUE_IN_CONFIG_INVALID
            = new IllegalArgumentException("The numeric value in the configuration is invalid.");
}
