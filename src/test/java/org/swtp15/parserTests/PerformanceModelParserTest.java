package org.swtp15.parserTests;

import org.junit.jupiter.api.Test;
import org.swtp15.models.Feature;
import org.swtp15.models.Property;
import org.swtp15.parser.ParserExceptions;
import org.swtp15.parser.PerformanceModelParser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PerformanceModelParserTest {


    /**
     * represents the expected reference-featureSet from the FeatureModel.
     */
    private Set<Feature> getExpectedFeatureSet() {
        Set<Feature> features = new HashSet<>();
        features.add(new Feature("feature1"));
        features.add(new Feature("feature2"));
        return features;
    }

    /**
     * Method provides codeBlock for most Tests for the PerformanceModelParser.
     *
     * @param testFile Name and file-type-ending of the test-file. File must be contained in 'src/test/testFiles/csv/'
     * @param ex       The expected Exception
     */
    private void loadFileAndAssertException(String testFile, Exception ex) {
        try {
            PerformanceModelParser.parseModel("src/test/testFiles/csv/" + testFile, getExpectedFeatureSet());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e, ex);
        }
    }

    @Test
    void parseFileWrongFiletype() {
        loadFileAndAssertException("test.txt", ParserExceptions.PROPERTY_INFLUENCE_MODEL_NOT_CSV_EXTENSION);

    }

    @Test
    void parseToShortFile() {
        loadFileAndAssertException("toShort.csv", ParserExceptions.PROPERTY_INFLUENCE_MODEL_LESS_THAN_2_LINES);
    }

    @Test
    void parseFileWithDifferentLineSizes() {
        loadFileAndAssertException("DifferentLineSize.csv",
                ParserExceptions.PROPERTY_INFLUENCE_MODEL_DIFFERENT_AMOUNT_OF_VALUES_IN_LINES);
    }

    @Test
    void parseInconsistentWithFmFile() {
        loadFileAndAssertException("InconsistentWithFeatureModel.csv",
                ParserExceptions.PROPERTY_INFLUENCE_MODEL_INCONSISTENT_WITH_DIMACS);
    }


    @Test
    void parseInconsistentFeatureValuesFile() {
        loadFileAndAssertException("InconsistentFeatureValues.csv",
                ParserExceptions.PROPERTY_INFLUENCE_MODEL_INCORRECT_FEATURE_COLUMN_VALUE);
    }

    @Test
    void parseInconsistentPropertyValuesFile() {
        loadFileAndAssertException("InconsistentPropertyValues.csv",
                ParserExceptions.PROPERTY_INFLUENCE_MODEL_INCORRECT_PROPERTY_COLUMN_VALUE);
    }

    @Test
    void parseUnallowedOptimizationSymbol() {
        loadFileAndAssertException("UnallowedOptimizationSymbol.csv",
                ParserExceptions.PROPERTY_INFLUENCE_MODEL_UNALLOWED_OPTIMIZATION_SYMBOL);
    }

    @Test
    void parseSimpleCorrectFile() {
        try {
            var performanceInfluenceModel =
                    PerformanceModelParser.parseModel("src/test/testFiles/csv/SimpleCorrectTest.csv",
                            getExpectedFeatureSet());

            // only one Object should be created for this file ...
            for (var featureInfluence : performanceInfluenceModel.getFeatureInfluences()) {
                Set<String> featureNames = featureInfluence.getActiveFeatures().parallelStream().map(Feature::getName)
                        .collect(Collectors.toSet());
                Set<String> propertyNames = featureInfluence.getPropertyInfluence().keySet().parallelStream()
                        .map(Property::getName).collect(Collectors.toSet());
                Set<String> propertyUnits = featureInfluence.getPropertyInfluence().keySet().parallelStream()
                        .map(Property::getUnit).collect(Collectors.toSet());
                assertEquals(Set.of("feature1"), featureNames);
                assertEquals(Set.of("property1", "property2", "property3"), propertyNames);
                assertEquals(Set.of("a", "b", "c"), propertyUnits);
                assertTrue(featureInfluence.getPropertyInfluence().values().containsAll(Set.of(0.1, 0.2, -0.3)));
                assertEquals(featureInfluence.getPropertyInfluence().values().size(), 3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected Exception thrown!");
        }
    }

    @Test
    void parseCorrectFile() {
        try {
            var performanceInfluenceModel =
                    PerformanceModelParser.parseModel("src/test/testFiles/csv/CorrectTest.csv",
                            getExpectedFeatureSet());
            var featureInfluences = performanceInfluenceModel.getFeatureInfluences();
            if (!(featureInfluences.size() == 4)) {
                fail("Size of featureInfluences doesn't correlate with valueLines in csv-file!");
            }

            for (var featureInfluence : featureInfluences) {

                Set<String> propertyNames = featureInfluence.getPropertyInfluence().keySet().parallelStream()
                        .map(Property::getName).collect(Collectors.toSet());
                Set<String> propertyUnits = featureInfluence.getPropertyInfluence().keySet().parallelStream()
                        .map(Property::getUnit).collect(Collectors.toSet());
                if (!propertyNames.equals(Set.of("property_1", "property2", "property3"))) {
                    fail("Not all Properties found!");
                }
                if (!propertyUnits.equals(Set.of("x", "y", "%"))) {
                    fail("Wrong Property units");
                }

                Set<String> featureNames = featureInfluence.getActiveFeatures().parallelStream().map(Feature::getName)
                        .collect(Collectors.toSet());
                if (featureNames.isEmpty()) {
                    if (!(featureInfluence.getPropertyInfluence().values().containsAll(Set.of(0.0, 0.2, -0.1)) &&
                            featureInfluence.getPropertyInfluence().values().size() == 3)) {
                        fail("Error in valueLine 1");
                    }
                } else if (featureNames.equals(Set.of("feature1"))) {
                    if (!(featureInfluence.getPropertyInfluence().values().containsAll(Set.of(0.3, 0.2, 0.1)) &&
                            featureInfluence.getPropertyInfluence().values().size() == 3)) {
                        fail("Error in valueLine 2");
                    }
                } else if (featureNames.equals(Set.of("feature2"))) {
                    if (!(featureInfluence.getPropertyInfluence().values().containsAll(Set.of(0.1, -0.245, 0.3)) &&
                            featureInfluence.getPropertyInfluence().values().size() == 3)) {
                        fail("Error in valueLine 3");
                    }
                } else if (featureNames.equals(Set.of("feature1", "feature2"))) {
                    if (!(featureInfluence.getPropertyInfluence().values()
                            .containsAll(Set.of(1.0, -1.1, 1.2342245214)) &&
                            featureInfluence.getPropertyInfluence().values().size() == 3)) {
                        fail("Error in valueLine 4");
                    }
                } else {
                    fail("Unexpected Set of active Features found");
                }
                assertTrue(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected Exception thrown!");
        }
    }

}
