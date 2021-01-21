package org.swtp15.system;

/**
 * Collection of Exceptions for system-classes.
 */
public class SystemExceptions {

    public static final IllegalArgumentException INVALID_DIRECTORY_PATH
            = new IllegalArgumentException("Given directory path does not exist.");
    public static final IllegalArgumentException MORE_THAN_ONE_FEATURESYSTEM_MATCH_CONFIGURATION
            = new IllegalArgumentException("More than one matching FeatureSystems were found for the given" +
                                           " Configuration!");
    public static final IllegalArgumentException NO_MATCHING_SYSTEM_FOR_CONFIGURATION = new IllegalArgumentException(
            "No feature system for model name.");
    public static final IllegalArgumentException IS_ALREADY_OPTIMUM = new IllegalArgumentException(
            "The optimum has already been reached.");
}
