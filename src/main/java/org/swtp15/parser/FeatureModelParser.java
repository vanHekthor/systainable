package org.swtp15.parser;


import org.swtp15.models.Feature;
import org.swtp15.models.FeatureModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * Class, that parses a '.dimacs' file into a {@link FeatureModel}.
 */
public class FeatureModelParser extends FileParser {

    /**
     * Parses file into {@link FeatureModel}.
     *
     * @param dimacsPath      dimacs file to parse binary features and the formula of their interaction
     * @param xmlPath         xml file to parse numeric features and their co-domain.
     * @param isInternalModel Whether the model is constructed from internal resources, required due to issues with
     *                        accessing file when packaged into jar
     *
     * @return parsed {@link FeatureModel}
     *
     * @throws IllegalArgumentException If there is any syntax error while parsing a dimacs file
     * @throws FileNotFoundException    When the file cannot be found, usually when the path is incorrect
     */
    public static FeatureModel parseModel(String dimacsPath, String xmlPath, boolean isInternalModel)
    throws IllegalArgumentException, FileNotFoundException {
        if (!dimacsPath.endsWith(".dimacs")) {
            throw ParserExceptions.FEATURE_MODEL_WRONG_FILETYPE_DIMACS;
        }
        if (xmlPath != null) {
            if (!xmlPath.endsWith(".xml")) {
                throw ParserExceptions.FEATURE_MODEL_WRONG_FILETYPE_XML;
            }
        }
        FeatureModel resultingModel;
        if (isInternalModel) {
            resultingModel = parseModelFromString(ResourceReader.readFileFromResources(dimacsPath),
                                                  xmlPath != null ? FeatureModelParser.class
                                                          .getResourceAsStream(xmlPath) : null);
        } else {
            resultingModel = parseModelFromString(FileParser.readFile(dimacsPath),
                                                  xmlPath != null ? new FileInputStream(xmlPath) : null);
        }

        resultingModel.setName(new File(dimacsPath).getName().replace(".dimacs", ""));
        return resultingModel;
    }

    private static FeatureModel parseModelFromString(List<String> dimacsContent, InputStream xmlStream)
    throws IllegalArgumentException {
        Map<Integer, Feature> binaryFeatures = new HashMap<>();
        Set<Set<Integer>> formulas = new HashSet<>();
        String controlLine = null;
        for (String line : dimacsContent) {
            switch (line.charAt(0)) {
                case 'c':
                    parseFeatureLineAndAddToFeaturesMap(line, binaryFeatures);
                    break;
                case 'p':
                    controlLine = line;
                    break;
                default:
                    Set<Integer> formula = parseFormulaLine(line, binaryFeatures);
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
        if (!numbersOfFeaturesAndFormulasAreCorrect(controlLine, binaryFeatures.size(), formulas.size())) {
            throw ParserExceptions.FEATURE_MODEL_WRONG_NUMBER_OF_FEATURES_OR_FORMULAS;
        }
        Map<Integer, Feature> numericFeatures = parseNumericFeaturesFromXml(xmlStream, binaryFeatures);
        return new FeatureModel(binaryFeatures, numericFeatures, formulas, binaryFeatures.size(), formulas.size());
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

    /**
     * Parses the numeric features given in a xml file and returns them as a map of integers with the belonging feature
     * as value. The integers starts with the next int after the max int of the given binary features map.
     *
     * @param xmlStream path of the xml file to be parsed.
     * @param features  the binary features read out of the belonging dimacs file.
     *
     * @return a map of the read numeric features.
     *
     * @throws IllegalArgumentException error while parsing the xml.
     */
    public static Map<Integer, Feature> parseNumericFeaturesFromXml(InputStream xmlStream,
                                                                    Map<Integer, Feature> features)
    throws IllegalArgumentException {
        Map<Integer, Feature> numericFeatures = new HashMap<>();
        if (xmlStream == null) {
            return numericFeatures;
        }
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(xmlStream);
            document.getDocumentElement().normalize();
            NodeList binaryOptionsElement = document.getElementsByTagName("binaryOptions");
            NodeList numericOptionsElement = document.getElementsByTagName("numericOptions");
            if (binaryOptionsElement.getLength() != 1 || numericOptionsElement.getLength() != 1) {
                throw ParserExceptions.XML_SYNTACTIC_ERROR;
            }
            List<Node> binaryXmlFeatures = removeEmptyNodes(binaryOptionsElement.item(0).getChildNodes());
            List<Node> numericXmlFeatures = removeEmptyNodes(numericOptionsElement.item(0).getChildNodes());
            if (binaryXmlFeatures.size() != features.size()) {
                throw ParserExceptions.INCONSISTENT_BINARY_FEATURES_COUNT_IN_XML_AND_DIMACS;
            }
            int featureCount = binaryXmlFeatures.size();
            for (Node numericFeature : numericXmlFeatures) {
                Element feature = (Element) numericFeature;
                String name = feature.getElementsByTagName("name").item(0).getTextContent();
                String stepFunction = feature.getElementsByTagName("stepFunction").item(0).getTextContent();
                String minValue = feature.getElementsByTagName("minValue").item(0).getTextContent();
                String maxValue = feature.getElementsByTagName("maxValue").item(0).getTextContent();
                numericFeatures.put(++featureCount, new Feature(name, Integer.parseInt(minValue),
                                                                Integer.parseInt(maxValue), stepFunction));

            }
            return numericFeatures;

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw ParserExceptions.XML_PARSE_ERROR;
        }
    }

    /**
     * Removes the empty xml nodes from NodeList object and returns a list of nodes with only not empty nodes.
     *
     * @param nodes the NodeList to be emptied.
     *
     * @return the emptied list of node.
     */
    private static List<Node> removeEmptyNodes(NodeList nodes) {
        List<Node> emptied = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                emptied.add(node);
            }
        }
        return emptied;
    }
}
