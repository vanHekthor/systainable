package org.swtp15.parser;


import org.swtp15.models.Feature;
import org.swtp15.models.FeatureModel;

import java.io.File;
import java.util.*;

/**
 * Class, that parses a '.dimacs' file into a {@link FeatureModel}.
 */
public class FeatureModelParser extends FileParser {

    /**
     * Checks if a file has the right file format to be parsed into a {@link FeatureModel}.
     *
     * @param filename file to be parsed
     *
     * @return true/false
     */
    public static boolean canParseFile(String filename) {
        return filename.endsWith(".dimacs");
    }

    /**
     * Parses file into {@link FeatureModel}.
     *
     * @param filename file to be parsed
     *
     * @return parsed {@link FeatureModel}
     *
     * @throws IllegalArgumentException If there is any syntax error while parsing a dimacs file
     */
    public static FeatureModel parseModel(String filename) throws IllegalArgumentException {
        Map<Integer, Feature> features = new HashMap<>();
        Set<Set<Integer>> formulas = new HashSet<>();
        String controlLine = null;
        if (!canParseFile(filename)) {
            throw ParserExceptions.FEATURE_MODEL_WRONG_FILETYPE;
        }
        for (String line : FileParser.readFile(filename)) {
            switch (line.charAt(0)) {
                case 'c':
                    parseFeatureLineAndAddToFeaturesMap(line, features);
                    break;
                case 'p':
                    controlLine = line;
                    break;
                default:
                    Set<Integer> formula = parseFormulaLine(line, features);
                    if (formula == null) {
                        throw ParserExceptions.FEATURE_MODEL_UNASSIGNED_LITERAL;
                    }
                    formulas.add(formula);
                    break;
            }
        }
        if (controlLine == null) {
            throw ParserExceptions.FEATURE_MODEL_MISSING_CONTROL_LINE;
        }
        if (!numbersOfFeaturesAndFormulasAreCorrect(controlLine, features.size(), formulas.size())) {
            throw ParserExceptions.FEATURE_MODEL_WRONG_NUMBER_OF_FEATURES_OR_FORMULAS;
        }
        FeatureModel resultingModel = new FeatureModel(features, formulas);
        resultingModel.setName(new File(filename).getName().replace(".dimacs", ""));
        return resultingModel;
    }

    /**
     * Creates a {@link Feature} from a dimacs file line an puts it into the given Map.
     *
     * @param line Line to be parsed
     */
    private static void parseFeatureLineAndAddToFeaturesMap(String line, Map<Integer, Feature> features) {
        var featureLineItems = line.split(" ");
        features.put(Integer.parseInt(featureLineItems[1]), new Feature(featureLineItems[2]));
    }

    /**
     * Create a propositional logical clause from a dimacs file line and checks if every occurring literal is contained
     * in the feature map.
     *
     * @param line Line to be parsed
     *
     * @return clause as set of integers (literals)
     */
    private static Set<Integer> parseFormulaLine(String line, Map<Integer, Feature> features) {
        var literals = line.split(" ");
        Set<Integer> formula = new HashSet<>();
        //last item of the line is always 0, this is not a literal and therefore will be skipped in the iteration
        for (int i = 0; i < literals.length - 1; i++) {
            var literal = Integer.parseInt(literals[i]);
            if (!features.containsKey(Math.abs(literal))) {
                return null;
            }
            formula.add(literal);
        }
        return formula;
    }

    /**
     * Checks if the given number of features and formulas in a dimacs equal the read numbers.
     *
     * @param controlLine      line that contains expected number of features and formulas
     * @param numberOfFeatures read number of features
     * @param numberOfFormulas read number of formulas
     *
     * @return true/false
     */
    private static boolean numbersOfFeaturesAndFormulasAreCorrect(String controlLine, int numberOfFeatures,
                                                                  int numberOfFormulas) {
        var controlLineItems = controlLine.split(" ");
        /*
        ControlLine consists of 4 elements, 3rd element is number of features, 4th is number of formulas, these 2
        elements are being compared
         */
        boolean correctNumberOfFeatures = Integer.parseInt(controlLineItems[2]) == numberOfFeatures;
        boolean correctNumberOfFormulas = Integer.parseInt(controlLineItems[3]) == numberOfFormulas;
        return correctNumberOfFeatures && correctNumberOfFormulas;

    }
}
