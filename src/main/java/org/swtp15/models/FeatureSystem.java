package org.swtp15.models;

import lombok.Getter;
import lombok.Setter;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;
import org.swtp15.system.SystemExceptions;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureSystem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private PerformanceInfluenceModel performanceModel;

    @Getter
    @Setter
    private FeatureModel featureModel;


    /**
     * The constructor.
     * <p>
     * TODO: Replace all usages of this constructor to instead use the other
     *
     * @param name             The name of the FeatureSystem.
     * @param featureModel     The belonging {@link FeatureModel}.
     * @param performanceModel The belonging {@link PerformanceInfluenceModel}.
     */
    public FeatureSystem(String name, FeatureModel featureModel, PerformanceInfluenceModel performanceModel) {
        this.name             = name;
        this.featureModel     = featureModel;
        this.performanceModel = performanceModel;
    }

    /**
     * Constructor for FeatureSystem, creating all submodels internally.
     *
     * @param name       The name of the FeatureSystem
     * @param dirPath    Path where all relevant files are stored. Is prepended on all other file paths to reduce
     *                   redundancy
     * @param dimacsPath Filename of the dimacs file. Is appended on the `dirPath`
     * @param xmlPath    Filename of the xml file. Is appended on the `dirPath`. May be `null`
     * @param csvPath    Filename of the csv file. Is appended on the `dirPath`
     * @param isInternal Whether the paths are pointing on internal resources.
     *
     * @throws FileNotFoundException If a File could not be found.
     */
    public FeatureSystem(String name, String dirPath, String dimacsPath, String xmlPath, String csvPath,
                         boolean isInternal)
    throws FileNotFoundException {
        this.name             = name;
        this.featureModel     = FeatureModelParser.parseModel(dirPath + dimacsPath, xmlPath != null ?
                                                                                    dirPath + xmlPath : null,
                                                              isInternal);
        this.performanceModel = PerformanceModelParser.parseModel(dirPath + csvPath, featureModel.getFeatures(),
                                                                  isInternal);
    }

    /**
     * Tests, if feature configuration is valid for this system.
     *
     * @param featureConfiguration The {@link FeatureConfiguration} that should be tested
     *
     * @return Whether the given configuration is valid
     *
     * @throws InterruptedException     If the thread calculating was interrupted before it could finish gracefully
     * @throws IllegalArgumentException If the given configuration contains features not included in the feature model
     */
    public Boolean configurationIsValid(FeatureConfiguration featureConfiguration) throws InterruptedException,
                                                                                          IllegalArgumentException {
        return this.featureModel.isValidConfiguration(featureConfiguration);
    }

    /**
     * Evaluates a {@link FeatureConfiguration}. It sets the Map of the FeatureConfiguration to the evaluated values,
     * but also returns this Map because we will probably need this later, when trying to optimize a Config.
     *
     * @param featureConfiguration The Configuration to be evaluated.
     *
     * @return The evaluated Property values as a Map.
     */
    public Map<Property, Double> evaluateFeatureConfiguration(FeatureConfiguration featureConfiguration) {
        return performanceModel.evaluateConfiguration(featureConfiguration);
    }

    /**
     * Returns a Set containing the properties of this FeatureSystem.
     *
     * @return A Set of properties.
     */
    public Set<Property> getProperties() {
        return performanceModel.getProperties();
    }

    /**
     * Returns a List containing the features of this FeatureSystem.
     *
     * @return A List of features.
     */
    public List<Feature> getFeatures() {
        return featureModel.getFeatures();
    }

    /**
     * Gets the minimal valid configuration of a system.
     *
     * @return minimal valid feature configuration.
     */
    public FeatureConfiguration getMinimalConfiguration() {
        return new FeatureConfiguration(name, featureModel.getMinimalModel(),
                                        featureModel.getInitialNumericFeaturesMap(),
                                        performanceModel.getInitialPropertyMap());
    }

    /**
     * Find (local) optimum for a specific property in a given range. Range here describes the features that can be
     * different from the given configuration.
     *
     * @param configToOptimize {@link FeatureConfiguration} that needs optimization
     * @param propertyName     name of property that should be optimized
     * @param maxDifference    number of features that can be different
     *
     * @return optimized {@link FeatureConfiguration} for property
     *
     * @throws IllegalArgumentException If configuration can't be or is already optimized
     * @throws InterruptedException     If the thread calculating was interrupted before it could finish gracefully
     */
    public FeatureConfiguration findLocalOptimum(FeatureConfiguration configToOptimize, String propertyName,
                                                 int maxDifference) throws InterruptedException,
                                                                           IllegalArgumentException {
        if (!configurationIsValid(configToOptimize)) {
            throw ModelExceptions.CONFIGURATION_NOT_VALID;
        }

        // evaluate given config
        Map<Property, Double> properties = evaluateFeatureConfiguration(configToOptimize);
        Map<String, Double> propertiesAsStrings = new HashMap<>();
        for (Property property : properties.keySet()) {
            propertiesAsStrings.put(property.getName(), properties.get(property));
        }
        configToOptimize.setPropertyValueMap(propertiesAsStrings);

        // get local config(s) as feature map
        Set<Map<String, Boolean>> localConfigMaps = this.featureModel.getNearModels(configToOptimize, maxDifference);

        // create FeatureConfiguration(s) from feature map
        Set<FeatureConfiguration> localConfigs = localConfigMaps.parallelStream()
                .map(binaryFeatureMap -> new FeatureConfiguration(this.name,
                                                                  binaryFeatureMap,
                                                                  configToOptimize.getNumericFeatures(),
                                                                  performanceModel.getInitialPropertyMap()))
                .collect(Collectors.toSet());

        // evaluate FeatureConfiguration(s) and set values
        localConfigs.parallelStream()
                .forEach(config -> {
                    Map<Property, Double> evaluatedProperties = evaluateFeatureConfiguration(config);

                    Map<String, Double> parsedProperties = new HashMap<>();
                    evaluatedProperties.forEach((property, aDouble) ->
                                                        parsedProperties.put(property.getName(), aDouble));

                    config.setPropertyValueMap(parsedProperties);
                });

        // find best-in-property
        Boolean isToMinimize = this.performanceModel.propertyIsToMinimize(propertyName);
        FeatureConfiguration localOptimum = (FeatureConfiguration) configToOptimize.clone();
        for (FeatureConfiguration config : localConfigs) {
            if (isBetter(localOptimum, config, propertyName, isToMinimize)) {
                localOptimum = config;
            }
        }
        if (configToOptimize.equals(localOptimum)) {
            throw SystemExceptions.IS_ALREADY_OPTIMUM;
        }

        return localOptimum;
    }

    /**
     * Compares two {@link FeatureConfiguration} and returns if second configuration is better for given property.
     *
     * @param localOptimum The first configuration
     * @param config       The second configuration
     * @param propertyName The property name that should be compared
     * @param isToMinimize Boolean if the property should be minimized
     *
     * @return TRUE if second configuration is better, FALSE if not
     */
    private boolean isBetter(FeatureConfiguration localOptimum, FeatureConfiguration config,
                             String propertyName, Boolean isToMinimize) {

        double valueOpt = localOptimum.getPropertyValue(propertyName);
        double valueOther = config.getPropertyValue(propertyName);

        if (isToMinimize) {
            return valueOther < valueOpt;
        } else {
            return valueOther > valueOpt;
        }
    }
}
