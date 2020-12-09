package org.swtp2015.parserTests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.swtp2015.gcbackend.GreenConfiguratorBackendApplication;
import org.swtp2015.models.Feature;
import org.swtp2015.models.Property;
import org.swtp2015.parser.PerformanceModelParser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GreenConfiguratorBackendApplication.class)
public class PerformanceModelParserTest {

    private final PerformanceModelParser pmp = new PerformanceModelParser();


    /*
     * represents the expected reference-featureSet from the FeatureModel
     */
    private Set<Feature> getExpectedFeatureSet() {
        Set<Feature> features = new HashSet<>();
        features.add(new Feature("feature1"));
        features.add(new Feature("feature2"));
        return features;
    }

    @Test
    void parseFileWrongFiletype() {
        try {
            pmp.parseModel("src/test/testModelFiles/test.txt", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("Delivered file for Performance-Influence-Model is not a .csv file",
                    ex.getMessage());
        }
    }

    @Test
    void parseToShortFile() {
        try {
            pmp.parseModel("src/test/testModelFiles/toShort.csv", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals(".csv-file must contain at least one headline and one value-line",
                    ex.getMessage());
        }
    }

    @Test
    void parseFileWithDifferentLineSizes() {
        try {
            pmp.parseModel("src/test/testModelFiles/DifferentLineSize.csv", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("Lines in .csv-file do not contain same amount of values!",
                    ex.getMessage());
        }
    }

    @Test
    void parseInconsistentWithFmFile() {
        try {
            pmp.parseModel("src/test/testModelFiles/InconsistentWithFeatureModel.csv", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals(".csv-file is not consistent with given reference-FeatureSet (.dimacs file)",
                    ex.getMessage());
        }
    }


    @Test
    void parseInconsistentFeatureValuesFile() {
        try {
            pmp.parseModel("src/test/testModelFiles/InconsistentFeatureValues.csv", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("A Column which have been identified by its headline as Feature-related does not contain 0 or 1!",
                    ex.getMessage());
        }
    }

    @Test
    void parseInconsistentPropertyValuesFile() {
        try {
            pmp.parseModel("src/test/testModelFiles/InconsistentPropertyValues.csv", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("A Column which have been identified by its headline as Property-related does not contain a Double value!",
                    ex.getMessage());
        }
    }

    @Test
    void parseUnallowedOptimizationSymbol() {
        try {
            pmp.parseModel("src/test/testModelFiles/UnallowedOptimizationSymbol.csv", getExpectedFeatureSet());
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("Unallowed symbol for a Property's optimization problem, only '<' or '>' are allowed. Please check format of csv-file!",
                    ex.getMessage());
        }
    }

    @Test
    void parseSimpleCorrectFile() {
        try {
            var featureInfluences =
                    pmp.parseModel("src/test/testModelFiles/SimpleCorrectTest.csv", getExpectedFeatureSet());

            // only one Object should be created for this file ...
            for (var featureInfluence : featureInfluences) {
                Set<String> featureNames = featureInfluence.getActiveFeatures().parallelStream().map(Feature::getName).collect(Collectors.toSet());
                Set<String> propertyNames = featureInfluence.getPropertyInfluence().keySet().parallelStream().map(Property::getName).collect(Collectors.toSet());
                Set<String> propertyUnits = featureInfluence.getPropertyInfluence().keySet().parallelStream().map(Property::getUnit).collect(Collectors.toSet());
                assertTrue(featureNames.equals(Set.of("feature1")) &&
                        propertyNames.equals(Set.of("property1", "property2", "property3")) &&
                        propertyUnits.equals(Set.of("a", "b", "c")) &&
                        featureInfluence.getPropertyInfluence().values().containsAll(Set.of(0.1, 0.2, -0.3)) &&
                        featureInfluence.getPropertyInfluence().values().size() == 3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected Exception thrown!");
        }
    }

    @Test
    void parseCorrectFile() {
        try {
            var featureInfluences =
                    pmp.parseModel("src/test/testModelFiles/CorrectTest.csv", getExpectedFeatureSet());
            if (!(featureInfluences.size() == 4)) {
                fail("Size of featureInfluences doesn't correlate with valueLines in csv-file!");
            }

            for (var featureInfluence : featureInfluences) {

                Set<String> propertyNames = featureInfluence.getPropertyInfluence().keySet().parallelStream().map(Property::getName).collect(Collectors.toSet());
                Set<String> propertyUnits = featureInfluence.getPropertyInfluence().keySet().parallelStream().map(Property::getUnit).collect(Collectors.toSet());
                if (!propertyNames.equals(Set.of("property_1", "property2", "property3"))) {
                    fail("Not all Properties found!");
                }
                if (!propertyUnits.equals(Set.of("x", "y", "%"))) {
                    fail("Wrong Property units");
                }

                Set<String> featureNames = featureInfluence.getActiveFeatures().parallelStream().map(Feature::getName).collect(Collectors.toSet());
                if (featureNames.isEmpty()) {
                    if (!(featureInfluence.getPropertyInfluence().values().containsAll(Set.of(0.0, 0.2, -0.3)) &&
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
                    if (!(featureInfluence.getPropertyInfluence().values().containsAll(Set.of(1.0, -1.1, 1.2342245214)) &&
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
