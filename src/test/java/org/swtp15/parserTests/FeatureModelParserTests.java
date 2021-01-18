package org.swtp15.parserTests;

import org.junit.jupiter.api.Test;
import org.swtp15.models.Feature;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.ParserExceptions;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private void loadFileAndAssertException_OnlyBinaryFeatures(String testFile, Exception ex) {
        try {
            FeatureModelParser.parseModel("src/test/testFiles/dimacs/" + testFile, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e, ex);
        }
    }

    /**
     * Method provides codeBlock for Tests with numeric Features for the FeatureModelParser.
     *
     * @param dimacsTestFile Name and file-type-ending of the dimacs-test-file. File must be contained in
     *                       'src/test/testFiles/dimacs/'
     * @param xmlTestFile    Name and file-type-ending of the dimacs-test-file. File must be contained in
     *                       'src/test/testFiles/xml/'
     * @param ex             The expected Exception
     */
    private void loadFileAndAssertException_WithNumerics(String dimacsTestFile, String xmlTestFile, Exception ex) {
        try {
            FeatureModelParser
                    .parseModel("src/test/testFiles/dimacs/" + dimacsTestFile, "src/test/testFiles/xml/" + xmlTestFile);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e, ex);
        }
    }

    @Test
    void parseFileCorrect() {
        var featureModel = FeatureModelParser.parseModel(
                "src/test/testFiles/dimacs/CorrectTest.dimacs", null);
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
    }

    @Test
    void parseFileMissingControlLine() {
        loadFileAndAssertException_OnlyBinaryFeatures("MissingControlLine.dimacs",
                                                      ParserExceptions.FEATURE_MODEL_MISSING_CONTROL_LINE);
    }

    @Test
    void parseFileNotMatchingFeatureNumber() {
        loadFileAndAssertException_OnlyBinaryFeatures("NotMatchingFeatureNumber.dimacs",
                                                      ParserExceptions.FEATURE_MODEL_WRONG_NUMBER_OF_FEATURES_OR_FORMULAS);
    }

    @Test
    void parseFileOccurringLiteralWithoutFeature() {
        loadFileAndAssertException_OnlyBinaryFeatures("OccurringLiteralWithoutFeature.dimacs",
                                                      ParserExceptions.FEATURE_MODEL_UNASSIGNED_LITERAL);
    }

    // Tests for xml-files/numerics start here

    @Test
    void syntacticErrorBecauseOfNoNumericOptionsInXml() {
        loadFileAndAssertException_WithNumerics("xmlReference.dimacs", "syntacticError.xml",
                                                ParserExceptions.XML_SYNTACTIC_ERROR);
    }

    @Test
    void inconsistentBinaryFeaturesInXml() {
        loadFileAndAssertException_WithNumerics("xmlReference.dimacs", "inconsistentBinaryFeatures.xml",
                                                ParserExceptions.INCONSISTENT_BINARY_FEATURES_COUNT_IN_XML_AND_DIMACS);
    }

    @Test
    void xmlWrongFiletype() {
        loadFileAndAssertException_WithNumerics("xmlReference.dimacs", "wrongFiletype.txt",
                                                ParserExceptions.FEATURE_MODEL_WRONG_FILETYPE_XML);
    }

    @Test
    void parseCorrectNumericFile() {
        var featureModel = FeatureModelParser.parseModel(
                "src/test/testFiles/dimacs/xmlReference.dimacs", "src/test/testFiles/xml/correct.xml");
        var readFeatures = featureModel.getFeatures();
        var binaryFeatures = readFeatures.parallelStream().filter(Feature::isBinary).collect(Collectors.toList());
        var numericFeatures = readFeatures.parallelStream().filter(f -> !f.isBinary()).collect(Collectors.toList());

        Set<String> featureNames = readFeatures.parallelStream().map(Feature::getName).collect(Collectors.toSet());
        Set<String> expectedFeatureNames = Set.of("Auto", "Motor", "Anhaengerkupplung", "Diesel", "Elektro", "Gaenge",
                                                  "Tueren");

        Feature gaenge = readFeatures.parallelStream().filter(f -> f.getName().equals("Gaenge"))
                .collect(Collectors.toList()).get(0);
        Feature tueren = readFeatures.parallelStream().filter(f -> f.getName().equals("Tueren"))
                .collect(Collectors.toList()).get(0);

        assertEquals(readFeatures.size(), 7);
        assertEquals(binaryFeatures.size(), 5);
        assertEquals(numericFeatures.size(), 2);
        assertEquals(featureNames, expectedFeatureNames);
        assertEquals(gaenge.getMinValue(), 4);
        assertEquals(gaenge.getMaxValue(), 7);
        assertEquals(gaenge.getStepFunction(), "Gaenge + 1");
        assertEquals(tueren.getMinValue(), 2);
        assertEquals(tueren.getMaxValue(), 6);
        assertEquals(tueren.getStepFunction(), "Tueren + 2");
    }

}
