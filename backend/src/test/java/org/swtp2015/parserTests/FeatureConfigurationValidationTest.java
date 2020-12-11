package org.swtp2015.parserTests;

import org.junit.jupiter.api.Test;
import org.swtp2015.models.FeatureConfiguration;
import org.swtp2015.models.FeatureModel;
import org.swtp2015.parser.FeatureModelParser;
import org.swtp2015.parser.FeatureConfigurationParser;
import org.swtp2015.parser.FileParser;
import org.swtp2015.parser.ParserExceptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureConfigurationValidationTest {

    private String getJsonString(String filename) {
        return String.join("\n", FileParser.readFile("src/test/testFiles/jsons/" + filename));
    }

    private void loadJsonAndExpectException(String jsonFileName, Exception expectedException) {
        try {
            String json = this.getJsonString(jsonFileName);
            FeatureConfigurationParser.parseConfiguration(json);
            fail();
        } catch (Exception e) {
            assertEquals(e, expectedException);
        }
    }

    @Test
    void missingFeaturesArray() {
        this.loadJsonAndExpectException("correctTest.dimacs/MissingFeaturesArray.json", ParserExceptions.MISSING_FEATURES_ARRAY_IN_JSON);
    }

    @Test
    void missingPropertyEntries() {
        this.loadJsonAndExpectException("correctTest.dimacs/MissingPropertyEntries.json", ParserExceptions.MISSING_PROPERTIES_IN_MAP_IN_JSON);
    }

    @Test
    void invalidPropertyType() {
        this.loadJsonAndExpectException("correctTest.dimacs/InvalidPropertyType.json", ParserExceptions.PROPERTY_VALUE_NOT_A_DOUBLE_IN_JSON);
    }

    @Test
    void invalidMessage() {
        this.loadJsonAndExpectException("featureModels.json", ParserExceptions.MISSING_FEATURE_CONFIGURATION_IN_JSON);
    }

    @Test
    void validInvalidNonmatchingConfigurations() {
        try {
            FeatureModel model = FeatureModelParser.parseModel("src/test/testFiles/dimacs/CorrectTest.dimacs");
            String json = this.getJsonString("correctTest.dimacs/2Valid1Invalid1NotMatching.json");
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
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void validConfiguration() {
        try {
            FeatureModel model = FeatureModelParser.parseModel("src/test/testFiles/dimacs/CorrectTest.dimacs");
            String json = this.getJsonString("correctTest.dimacs/1Valid.json");
            FeatureConfiguration conf = FeatureConfigurationParser.parseConfiguration(json);
            assertNotNull(conf);
            assertTrue(model.isValidConfiguration(conf));
        } catch (Exception e) {
            fail();
        }
    }
}
