package org.swtp15.configurationTests;

import org.junit.jupiter.api.Test;
import org.swtp15.models.*;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationTests {

    private FeatureSystem getExampleFeatureSystem() {
        FeatureModel fm = FeatureModelParser.parseModel("src/test/testFiles/dimacs/CorrectTest.dimacs");
        PerformanceInfluenceModel pm = PerformanceModelParser
                .parseModel("src/test/testFiles/csv/CorrectTest.csv", fm.getFeatures());
        return new FeatureSystem("test", fm, pm);
    }

    private FeatureConfiguration getExampleFeatureConfiguration1() {
        Set<String> activeFeatures = new HashSet<>();
        activeFeatures.add("feature1");
        return new FeatureConfiguration("test", activeFeatures);
    }

    private FeatureConfiguration getExampleFeatureConfiguration2() {
        Set<String> activeFeatures = new HashSet<>();
        activeFeatures.add("feature1");
        activeFeatures.add("feature2");
        return new FeatureConfiguration("test", activeFeatures);
    }

    @Test
    void evaluateConfiguration1() {
        FeatureSystem featureSystem = getExampleFeatureSystem();
        var result = featureSystem.evaluateFeatureConfiguration(getExampleFeatureConfiguration1());
        assertEquals(result.size(), 3);
        assertEquals(result.get(new Property("property_1", null, false)), 0.3, 0.00001);
        assertEquals(result.get(new Property("property2", null, false)), 0.4, 0.00001);
        assertEquals(result.get(new Property("property3", null, false)), 0, 0.00001);
    }

    @Test
    void evaluateConfiguration2() {
        FeatureSystem featureSystem = getExampleFeatureSystem();
        var result = featureSystem.evaluateFeatureConfiguration(getExampleFeatureConfiguration2());
        assertEquals(result.size(), 3);
        assertEquals(result.get(new Property("property_1", null, false)), 1.4, 0.00001);
        assertEquals(result.get(new Property("property2", null, false)), -0.945, 0.00001);
        assertEquals(result.get(new Property("property3", null, false)), 1.5342245214, 0.00001);
    }
}
