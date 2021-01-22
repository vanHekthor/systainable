package org.swtp15.parserTests;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.models.FeatureModel;
import org.swtp15.parser.FeatureConfigurationParser;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.FileParser;
import org.swtp15.parser.ParserExceptions;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureConfigurationValidationTest {

    private String getJsonString(String filename) {
        return String.join("\n", FileParser.readFile("src/test/testFiles/jsons/" + filename));
    }

    /**
     * Method tries to parse a json-file with the {@link FeatureConfigurationParser}. If no exception is thrown this
     * method will fail. If an exception is thrown, its asserted if this exception equals the expected exception.
     *
     * @param jsonFileName      The name of the json-file to be parsed. This file should be handed over like "name.json"
     *                          an be located in the directory-path: src/test/testFiles/jsons/
     * @param expectedException The expected exception
     */
    private void loadJsonAndExpectException(String jsonFileName, Exception expectedException) {
        try {
            String json = this.getJsonString(jsonFileName);
            FeatureConfigurationParser.parseConfiguration(json);
            fail();
        } catch (Exception e) {
            assertEquals(e, expectedException);
        }
    }

    /**
     * Method to check if a given {@link FeatureConfiguration} is valid or not in a specified {@link FeatureModel}.
     *
     * @param dimacsPath The dimacs-file to specify FeatureModel
     * @param xmlPath    The xml-file to specify FeatureModel
     * @param jsonPath   The json-file to describe the configuration which should be evaluated
     * @param assertion  Boolean to assert whether the given configuration is valid within this model or not
     */
    private void validateConfiguration(String dimacsPath, String xmlPath, String jsonPath, boolean assertion)
    throws InterruptedException, ParseException, FileNotFoundException {
        FeatureModel model = FeatureModelParser.parseModel(dimacsPath, xmlPath, false);
        String json = this.getJsonString(jsonPath);
        FeatureConfiguration conf = FeatureConfigurationParser.parseConfiguration(json);
        assertNotNull(conf);
        if (assertion) {
            assertTrue(model.isValidConfiguration(conf));
        } else {
            assertFalse(model.isValidConfiguration(conf));
        }

    }

    @Test
    void missingFeaturesArray() {
        this.loadJsonAndExpectException("MissingFeaturesMap.json",
                                        ParserExceptions.MISSING_FEATURES_MAP_IN_JSON);
    }

    @Test
    void invalidFeaturesStatusType() {
        this.loadJsonAndExpectException("InvalidFeatureStatusType.json",
                                        ParserExceptions.FEATURE_VALUE_NOT_A_BOOLEAN_OR_NUMBER_IN_JSON);
    }

    @Test
    void missingPropertyEntries() {
        this.loadJsonAndExpectException("MissingPropertyEntries.json",
                                        ParserExceptions.MISSING_PROPERTIES_IN_MAP_IN_JSON);
    }

    @Test
    void invalidPropertyType() {
        this.loadJsonAndExpectException("InvalidPropertyType.json",
                                        ParserExceptions.PROPERTY_VALUE_NOT_A_DOUBLE_IN_JSON);
    }

    @Test
    void invalidMessage() {
        this.loadJsonAndExpectException("featureModels.json", ParserExceptions.MISSING_FEATURE_CONFIGURATION_IN_JSON);
    }

    @Test
    void validInvalidNonmatchingConfigurations() throws InterruptedException, FileNotFoundException {
        FeatureModel model = FeatureModelParser.parseModel("src/test/testFiles/dimacs/CorrectTest.dimacs", null, false);
        String json = this.getJsonString("2Valid1Invalid1NotMatching.json");
        List<FeatureConfiguration> featureConfigurationList = FeatureConfigurationParser.parseConfigurations(json);
        if (featureConfigurationList == null) {
            fail();
        }
        assertTrue(model.isValidConfiguration(featureConfigurationList.get(0)));
        assertTrue(model.isValidConfiguration(featureConfigurationList.get(1)));
        assertFalse(model.isValidConfiguration(featureConfigurationList.get(2)));
        try {
            model.isValidConfiguration(featureConfigurationList.get(3));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e, ParserExceptions.CONFIGURATION_NOT_SUBSET_OF_MODEL);
        }
    }

    @Test
    void validConfiguration() throws ParseException, InterruptedException, FileNotFoundException {
        validateConfiguration("src/test/testFiles/dimacs/CorrectTest.dimacs", null, "1Valid.json", true);
    }

    // Test for Systems with numeric Features start here

    @Test
    void numericFeatureWithUnallowedDouble() {
        this.loadJsonAndExpectException("Numeric_UnallowedDoubleValue.json",
                                        ParserExceptions.FEATURE_VALUE_IS_NUMBER_BUT_NOT_INTEGER);
    }

    @Test
    void validNumericConfiguration() throws InterruptedException, ParseException, FileNotFoundException {
        validateConfiguration("src/test/testFiles/dimacs/xmlReference.dimacs", "src/test/testFiles/xml/correct.xml",
                              "Numeric_ValidConfiguration.json", true);
    }

    @Test
    void invalidNumericConfiguration() throws InterruptedException, ParseException, FileNotFoundException {
        validateConfiguration("src/test/testFiles/dimacs/xmlReference.dimacs", "src/test/testFiles/xml/correct.xml",
                              "Numeric_InvalidConfiguration.json", false);
    }

}
