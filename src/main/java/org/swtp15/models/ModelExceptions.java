package org.swtp15.models;

/**
 * Collection of Exceptions for model-classes.
 */
public class ModelExceptions {

    public static final IllegalArgumentException CONFIGURATION_NOT_VALID
            = new IllegalArgumentException("The given configuration is not valid for this system.");
    public static final IllegalArgumentException PROPERTY_NOT_IN_MODEL
            = new IllegalArgumentException("No such property in model.");
}