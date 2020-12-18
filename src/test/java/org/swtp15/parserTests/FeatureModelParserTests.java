package org.swtp15.parserTests;

import org.junit.jupiter.api.Test;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.ParserExceptions;

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

    /**
     * Method provides codeBlock for most Tests for the FeatureModelParser.
     *
     * @param testFile Name and file-type-ending of the test-file. File must be contained in
     *                 'src/test/testFiles/dimacs/'
     * @param ex       The expected Exception
     */
    private void loadFileAndAssertException(String testFile, Exception ex) {
        try {
            FeatureModelParser.parseModel("src/test/testFiles/dimacs/" + testFile);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e, ex);
        }
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
            var featureModel = FeatureModelParser.parseModel(
                    "src/test/testFiles/dimacs/CorrectTest.dimacs");
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
        loadFileAndAssertException("MissingControlLine.dimacs", ParserExceptions.FEATURE_MODEL_MISSING_CONTROL_LINE);
    }

    @Test
    void parseFileNotMatchingFeatureNumber() {
        loadFileAndAssertException("NotMatchingFeatureNumber.dimacs",
                                   ParserExceptions.FEATURE_MODEL_WRONG_NUMBER_OF_FEATURES_OR_FORMULAS);
    }

    @Test
    void parseFileOccurringLiteralWithoutFeature() {
        loadFileAndAssertException("OccurringLiteralWithoutFeature.dimacs",
                                   ParserExceptions.FEATURE_MODEL_UNASSIGNED_LITERAL);
    }
}
