package org.swtp15.configurationTests;

import org.junit.jupiter.api.Test;
import org.swtp15.models.*;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluationTests {

    // Tests with Binary Features only.

    private FeatureSystem getExampleFeatureSystem() throws FileNotFoundException {
        FeatureModel fm = FeatureModelParser.parseModel("src/test/testFiles/dimacs/CorrectTest.dimacs", null, false);
        PerformanceInfluenceModel pm = PerformanceModelParser
                .parseModel("src/test/testFiles/csv/CorrectTest.csv", fm.getFeatures(), false);
        return new FeatureSystem("test", fm, pm);
    }

    private FeatureConfiguration getExampleFeatureConfiguration1() {
        Map<String, Boolean> features = new HashMap<>();
        features.put("feature1", true);
        features.put("feature2", false);
        return new FeatureConfiguration("test", features, new HashMap<>(), null);
    }

    private FeatureConfiguration getExampleFeatureConfiguration2() {
        Map<String, Boolean> features = new HashMap<>();
        features.put("feature1", true);
        features.put("feature2", true);
        return new FeatureConfiguration("test", features, new HashMap<>(), null);
    }

    @Test
    void evaluateConfiguration1() throws FileNotFoundException {
        FeatureSystem featureSystem = getExampleFeatureSystem();
        var result = featureSystem.evaluateFeatureConfiguration(getExampleFeatureConfiguration1());
        assertEquals(result.size(), 3);
        assertEquals(result.get(new Property("property_1", null, false)), 0.3, 0.00001);
        assertEquals(result.get(new Property("property2", null, false)), 0.4, 0.00001);
        assertEquals(result.get(new Property("property3", null, false)), 0, 0.00001);
    }

    @Test
    void evaluateConfiguration2() throws FileNotFoundException {
        FeatureSystem featureSystem = getExampleFeatureSystem();
        var result = featureSystem.evaluateFeatureConfiguration(getExampleFeatureConfiguration2());
        assertEquals(result.size(), 3);
        assertEquals(result.get(new Property("property_1", null, false)), 1.4, 0.00001);
        assertEquals(result.get(new Property("property2", null, false)), -0.945, 0.00001);
        assertEquals(result.get(new Property("property3", null, false)), 1.5342245214, 0.00001);
    }

    // Tests with Numeric Features.

    private FeatureSystem getExampleNumericFeatureSystem() throws FileNotFoundException {
        FeatureModel fm = FeatureModelParser
                .parseModel("src/test/testFiles/dimacs/xmlReference.dimacs", "src/test/testFiles/xml/correct.xml",
                            false);
        PerformanceInfluenceModel pm = PerformanceModelParser
                .parseModel("src/test/testFiles/csv/Numerics.csv", fm.getFeatures(), false);
        return new FeatureSystem("test", fm, pm);
    }

    private FeatureConfiguration getExampleNumericFeatureConfiguration() {
        Map<String, Boolean> binaryFeatures = new HashMap<>();
        Map<String, Integer> numericFeatures = new HashMap<>();
        binaryFeatures.put("Auto", true);
        binaryFeatures.put("Motor", true);
        binaryFeatures.put("Anhaengerkupplung", false);
        binaryFeatures.put("Diesel", true);
        binaryFeatures.put("Elektro", false);
        numericFeatures.put("Gaenge", 5);
        numericFeatures.put("Tueren", 4);
        return new FeatureConfiguration("car", binaryFeatures, numericFeatures, null);
    }

    @Test
    void evaluateNumericConfiguration() throws FileNotFoundException {
        FeatureSystem featureSystem = getExampleNumericFeatureSystem();
        Map<Property, Double> result = featureSystem
                .evaluateFeatureConfiguration(getExampleNumericFeatureConfiguration());
        assertEquals(result.size(), 3);
        assertEquals(result.get(new Property("Geschwindigkeit", null, false)), 125);
        assertEquals(result.get(new Property("Verbrauch", null, true)), 25);
        assertEquals(result.get(new Property("Preis", null, true)), 7600);
    }


}
