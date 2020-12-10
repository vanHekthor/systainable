package org.swtp2015.parser;


import org.swtp2015.models.Feature;
import org.swtp2015.models.FeatureInfluence;
import org.swtp2015.models.FeatureModel;
import org.swtp2015.models.Property;


import java.util.*;
import java.util.stream.Collectors;


/**
 * Class that parses a Performance-Influence-Model (csv-file) to a Set of instances of {@link FeatureInfluence}
 * (data structure to describe a line of the .csv-file by a Set of the active Features an a Map
 * displaying the influence on Properties)
 */
public class PerformanceModelParser extends FileParser {

    /**
     * @param filename          the name of the .csv-file to parse
     * @param referenceFeatures The Set of all Features from the {@link FeatureModel},  which should correspond
     *                          to the features in the .csv-file
     * @return A Set of instances of {@link FeatureInfluence} (representing all non-headlines of .csv-file)
     */
    public Set<FeatureInfluence> parseModel(String filename, Set<Feature> referenceFeatures) throws Exception {

        Set<FeatureInfluence> featureInfluences = new HashSet<>();

        if (!filename.endsWith(".csv")) {
            throw new Exception("Delivered file for Performance-Influence-Model is not a .csv file");
        }

        List<String> csvLines = readFile(filename);

        if (csvLines.size() == 0 || csvLines.size() == 1) {
            throw new Exception(".csv-file must contain at least one headline and one value-line");
        }

        for (int i = 0; i < csvLines.size() - 1; i++) {
            if (splitLine(csvLines.get(i + 1)).length != splitLine(csvLines.get(i)).length) {
                throw new Exception("Lines in .csv-file do not contain same amount of values!");
            }
        }

        Map<Integer, Feature> featureMap = mapFeatures(csvLines.get(0));
        Map<Integer, Property> propertyMap = mapProperties(csvLines.get(0));

        if (featureMap.isEmpty()) {
            throw new Exception("featureMap is empty!");
        }
        if (propertyMap.isEmpty()) {
            throw new Exception("propertyMap is empty");
        }

        Set<String> referenceFeaturesNames = referenceFeatures.parallelStream().map(Feature::getName).collect(Collectors.toSet());
        Set<String> mappedFeaturesNames = featureMap.values().parallelStream().map(Feature::getName).collect(Collectors.toSet());

        if (!referenceFeaturesNames.equals(mappedFeaturesNames)) {
            throw new Exception(".csv-file is not consistent with given reference-FeatureSet (.dimacs file)");
        }

        // create a Set of FeatureInfluence's from all non-headline lines in .csv-file
        for (int i = 1; i < csvLines.size(); i++) {
            String line = csvLines.get(i);
            featureInfluences.add(createFeatureInfluence(line, featureMap, propertyMap));
        }
        return featureInfluences;
    }

    /**
     * Method to identify all Features by their name in the headline of .csv-file.
     * FeatureNames and PropertyNames are distinguished by the existence of a ";" in a Property-related
     * space between commas.
     *
     * @param headline the first line of the .csv-file, already delivered as a String
     * @return Map with Features in headline in consecutive order, mapped to Integer-keys which represent
     * their position in the headline
     */
    private Map<Integer, Feature> mapFeatures(String headline) {
        String[] wordsInHeadline = splitLine(headline);
        Map<Integer, Feature> featureMap = new HashMap<>();
        for (int i = 0; i < wordsInHeadline.length; i++) {
            if (!wordsInHeadline[i].contains(";")) {
                featureMap.put(i, new Feature(wordsInHeadline[i]));
            }
        }
        return featureMap;
    }


    /**
     * Method to identify all Properties by their name in the headline of .csv-file.
     * FeatureNames and PropertyNames are distinguished by the existence of a ";" in a Property-related
     * space between commas.
     *
     * @param headline the first line of the .csv-file, already delivered as a String
     * @return Map with Properties in headline in consecutive order, mapped to Integer-keys which represent
     * their position in the headline
     */
    private Map<Integer, Property> mapProperties(String headline) throws Exception {
        String[] wordsInHeadline = splitLine(headline);
        Map<Integer, Property> propertyMap = new HashMap<>();
        for (int i = 0; i < wordsInHeadline.length; i++) {
            if (wordsInHeadline[i].contains(";")) {

                String[] propertyAttributes = splitPropertyString(wordsInHeadline[i]);

                if (propertyAttributes.length != 3) {
                    throw new Exception("More than 3 Property attributes identified. Make sure Properties in csv-headline have correct format");
                }
                if (! (propertyAttributes[2].equals("<") || propertyAttributes[2].equals(">")) ){
                    throw new Exception("Unallowed symbol for a Property's optimization problem, only '<' or '>' are allowed. Please check format of csv-file!");
                }

                propertyMap.put(i, new Property(propertyAttributes[0], propertyAttributes[1], propertyAttributes[2].equals("<")));
            }
        }
        return propertyMap;
    }

    /**
     * @param currentLine the line of the .csv-file which is currently translated into
     *                    an instance of {@link FeatureInfluence}
     * @param featureMap  Map with Features in headline in consecutive order, mapped to Integer-keys which represent
     *                    their position in the headline
     * @param propertyMap Map with Properties in headline in consecutive order, mapped to Integer-keys which represent
     *                    their position in the headline
     * @return the {@link FeatureInfluence}-instance represented by the current line
     */
    private FeatureInfluence createFeatureInfluence(String currentLine, Map<Integer, Feature> featureMap,
                                                    Map<Integer, Property> propertyMap) throws Exception {

        String[] valuesInLine = splitLine(currentLine);

        Set<Feature> activeFeatures = new HashSet<>();
        Map<Property, Double> propertyInfluence = new HashMap<>();

        for (int i = 0; i < currentLine.length(); i++) {
            if (featureMap.containsKey(i)) {
                if (!(valuesInLine[i].equals("0") || valuesInLine[i].equals("1"))) {
                    throw new Exception("A Column which have been identified by its headline as Feature-related does not contain 0 or 1!");
                }
                if (valuesInLine[i].equals("1")) {
                    activeFeatures.add(featureMap.get(i));
                }
            }
            if (propertyMap.containsKey(i)) {
                if (! isDouble(valuesInLine[i]) ) {
                    throw new Exception("A Column which have been identified by its headline as Property-related does not contain a Double value!");
                }
                propertyInfluence.put(propertyMap.get(i), Double.parseDouble(valuesInLine[i]));
            }
        }
        return new FeatureInfluence(activeFeatures, propertyInfluence);
    }

    /**
     * Method to check if the given String represents a Double
     *
     * @param str the given String
     * @return true if the String is parsable to a Double, false else
     */
    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * method to split a line of a csv-file at commas and trim
     *
     * @param line the line to split
     * @return String Array with trimmed values between commas
     */
    private String[] splitLine(String line) {
        String[] splitted = line.split(",");
        for (int i = 0; i < splitted.length; i++) {
            splitted[i] = splitted[i].trim();
        }
        return splitted;
    }

    /**
     * Method to extract the relevant Information from a Property-related headline String
     *
     * @param propertyString the Property-related headline String
     * @return a String[] with the following structure: [property_name, unit, optimizationDirection]
     */
    private String[] splitPropertyString(String propertyString) {
        String[] splitted = propertyString.split("[();]");
        for (int i = 0; i < splitted.length; i++) {
            splitted[i] = splitted[i].trim();
        }
        return splitted;
    }


}

