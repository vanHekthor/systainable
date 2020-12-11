package org.swtp2015.parser;

import org.swtp2015.models.Feature;
import org.swtp2015.models.FeatureModel;

import java.util.*;

/**
 * Class, that parses a Dimacs file into a Feature-Model
 */
public class FeatureModelParser extends FileParser {

    /**
     * checks, if a file has the right file format to be parsed into a FeatureModel
     * @param filename file to be parsed
     * @return true/false
     */
    public static boolean canParseFile(String filename) {
        return filename.endsWith(".dimacs");
    }

    /**
     * parses file into FeatureModel
     *
     * @param filename file to be parsed
     * @return parsed FeatureModel
     */
    public static FeatureModel parseModel(String filename) throws Exception {
        Map<Integer, Feature> features = new HashMap<>();
        Set<Set<Integer>> formulas = new HashSet<>();
        String controlLine = null;
        if (!canParseFile(filename)) {
            throw new Exception("Delivered file is not a .dimacs file");
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
                        throw new Exception("There is at least one literal without a belonging feature.");
                    }
                    formulas.add(formula);
                    break;
            }
        }
        if (controlLine == null) {
            throw new Exception("Missing Controlline in Dimacs-File");
        }
        if (!numbersOfFeaturesAndFormulasAreCorrect(controlLine, features.size(), formulas.size())) {
            throw new Exception("Number of read features or formulas does not equal the given number in the Dimacs-File");
        }
        FeatureModel resultingModel = new FeatureModel(features, formulas);
        resultingModel.setName(filename.substring(filename.lastIndexOf("/")).replace(".dimacs", ""));
        return resultingModel;
    }

    /**
     * creates a Feature from a dimacs file line an puts it into the given Map
     *
     * @param line line to be parsed
     */
    private static void parseFeatureLineAndAddToFeaturesMap(String line, Map<Integer, Feature> features) {
        var featureLineItems = line.split(" ");
        features.put(Integer.parseInt(featureLineItems[1]), new Feature(featureLineItems[2]));
    }

    /**
     * creates a propositional logical clause from a dimacs file line and checks if every occuring literal is contained in FeatureMap
     *
     * @param line line to be parsed
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
     * checks, if the given number of features and formulas in a Dimacs equal the read numbers
     *
     * @param controlLine      line that contains expected number of features and formulas
     * @param numberOfFeatures read number of features
     * @param numberOfFormulas read number of formulas
     * @return true/false
     */
    private static boolean numbersOfFeaturesAndFormulasAreCorrect(String controlLine, int numberOfFeatures, int numberOfFormulas) {
        var controlLineItems = controlLine.split(" ");
        // ControlLine consists of 4 elements, 3rd element is number of features, 4th is number of formulas, these 2 elements are being compared
        boolean correctNumberOfFeatures = Integer.parseInt(controlLineItems[2]) == numberOfFeatures;
        boolean correctNumberOfFormulas = Integer.parseInt(controlLineItems[3]) == numberOfFormulas;
        return correctNumberOfFeatures && correctNumberOfFormulas;

    }
}
