package org.swtp15.parser;

/**
 * Collection of Exceptions for parser-classes to prevent code repetition. Mostly used for Unittests
 */
public final class ParserExceptions {

    // Exceptions for FeatureConfigurationParser or FeatureConfigurationValidationTest
    public static final IllegalArgumentException CONFIGURATION_NOT_SUBSET_OF_MODEL
            = new IllegalArgumentException("Configuration contains foreign features");
    public static final IllegalArgumentException MISSING_FEATURE_CONFIGURATIONS_IN_JSON = new IllegalArgumentException(
            "Missing key 'featureConfigurations' in JSON");
    public static final IllegalArgumentException MISSING_FEATURE_CONFIGURATION_IN_JSON = new IllegalArgumentException(
            "Missing key 'featureConfiguration' in JSON");
    public static final IllegalArgumentException MISSING_FEATURES_MAP_IN_JSON = new IllegalArgumentException(
            "Missing array 'features' in 'featureConfiguration'");
    public static final IllegalArgumentException FEATURE_VALUE_NOT_A_BOOLEAN_IN_JSON = new IllegalArgumentException(
            "Feature value is not a Boolean");
    public static final IllegalArgumentException MISSING_PROPERTIES_IN_MAP_IN_JSON = new IllegalArgumentException(
            "Map 'properties' missing entries");
    public static final IllegalArgumentException PROPERTY_VALUE_NOT_A_DOUBLE_IN_JSON = new IllegalArgumentException(
            "Property value is not a Double");

    // Exceptions for csv-file or PerformanceModelParser
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_NOT_CSV_EXTENSION
            = new IllegalArgumentException(
            "Delivered file for Performance-Influence-Model is not a .csv file");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_LESS_THAN_2_LINES
            = new IllegalArgumentException(
            "Csv-file must contain at least one headline and one value-line");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_DIFFERENT_AMOUNT_OF_VALUES_IN_LINES =
            new IllegalArgumentException("Lines in .csv-file do not contain same amount of values!");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_EMPTY_FEATURE_MAP =
            new IllegalArgumentException("FeatureMap is empty!");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_EMPTY_PROPERTY_MAP =
            new IllegalArgumentException("PropertyMap is empty!");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_INCONSISTENT_WITH_DIMACS =
            new IllegalArgumentException("Csv-file is not consistent with given reference-FeatureSet (.dimacs file)");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_WRONG_AMOUNT_OF_PROPERTIES =
            new IllegalArgumentException("More or less than 3 Property attributes identified. " +
                                         "Make sure Properties in csv-headline have correct format");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_UNALLOWED_OPTIMIZATION_SYMBOL =
            new IllegalArgumentException("Unallowed symbol for a Property's optimization problem, only '<' or '>' " +
                                         "are allowed. Please check format of csv-file!");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_INCORRECT_FEATURE_COLUMN_VALUE =
            new IllegalArgumentException("A Column which have been identified by its headline as Feature-related " +
                                         "does not contain 0 or 1!");
    public static final IllegalArgumentException PROPERTY_INFLUENCE_MODEL_INCORRECT_PROPERTY_COLUMN_VALUE =
            new IllegalArgumentException("A Column which have been identified by its headline as Property-related " +
                                         "does not contain a Double value!");

    // Exceptions for dimacs-file FeatureModelParser
    public static final IllegalArgumentException FEATURE_MODEL_MISSING_CONTROL_LINE = new IllegalArgumentException(
            "Missing Controlline in Dimacs-File");
    public static final IllegalArgumentException FEATURE_MODEL_WRONG_FILETYPE = new IllegalArgumentException(
            "Delivered file is not a .dimacs file");
    public static final IllegalArgumentException FEATURE_MODEL_UNASSIGNED_LITERAL = new IllegalArgumentException(
            "There is at least one literal without a belonging feature.");
    public static final IllegalArgumentException FEATURE_MODEL_WRONG_NUMBER_OF_FEATURES_OR_FORMULAS =
            new IllegalArgumentException("Number of read features or formulas does not equal the given " +
                                         "number in the Dimacs-File");

}


