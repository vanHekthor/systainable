package org.swtp15.models;

/**
 * Collection of Exceptions for model-classes.
 */
public class ModelExceptions {

    public static final IllegalArgumentException CONFIGURATION_NOT_VALID
            = new IllegalArgumentException("The given configuration is not valid for this system.");
    public static final IllegalArgumentException PROPERTY_NOT_IN_MODEL
            = new IllegalArgumentException("No such property in model.");
    public static final IllegalStateException MODEL_HAS_NO_VALID_CONFIGURATIONS
            = new IllegalStateException("This Model has no valid Configurations");
}