package org.swtp2015.parserTests;

import org.junit.jupiter.api.Test;
import org.swtp2015.parser.FeatureModelParser;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureModelParserTests {

    private Set<Set<Integer>> getExpectedFormula() {
        Set<Set<Integer>> formula = new HashSet<>();
        Set<Integer> clause1 = new HashSet<>();
        clause1.add(1);
        Set<Integer> clause2 = new HashSet<>();
        clause2.add(-2);
        clause2.add(1);
        formula.add(clause1);
        formula.add(clause2);
        return formula;
    }

    @Test
    void canParseFileCorrectFormat() {
        assertTrue(FeatureModelParser.canParseFile("test.dimacs"));
    }

    @Test
    void canParseFileWrongFormat() {
        assertFalse(FeatureModelParser.canParseFile("test.txt"));
    }

    @Test
    void parseFileCorrect() {
        try {
            var featureModel = FeatureModelParser.parseModel("src/test/testFiles/dimacs/CorrectTest.dimacs");
            var readFeatures = featureModel.getFeatures();
            boolean featuresEqual = readFeatures.size() == 2;
            for (var feature : readFeatures) {
                if (!(feature.getName().equals("feature1") || feature.getName().equals("feature2"))) {
                    featuresEqual = false;
                    break;
                }
            }
            Set<Set<Integer>> expectedFormulas = getExpectedFormula();
            boolean formulaEqual = featureModel.getFormulas().equals(expectedFormulas);
            assertTrue(featuresEqual && formulaEqual);
        } catch (Exception ex) {
            fail("Unexpected Exception thrown");
        }
    }

    @Test
    void parseFileMissingControlLine() {
        try {
            FeatureModelParser.parseModel("src/test/testFiles/dimacs/MissingControlLine.dimacs");
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("Missing Controlline in Dimacs-File", ex.getMessage());
        }
    }

    @Test
    void parseFileNotMatchingFeatureNumber() {
        try {
            FeatureModelParser.parseModel("src/test/testFiles/dimacs/NotMatchingFeatureNumber.dimacs");
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("Number of read features or formulas does not equal the given number in the Dimacs-File", ex.getMessage());
        }
    }

    @Test
    void parseFileOccuringLiteralWithoutFeature() {
        try {
            FeatureModelParser.parseModel("src/test/testFiles/dimacs/OccuringLiteralWithoutFeature.dimacs");
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertEquals("There is at least one literal without a belonging feature.", ex.getMessage());
        }
    }
}
