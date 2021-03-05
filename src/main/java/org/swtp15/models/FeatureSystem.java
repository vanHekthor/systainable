package org.swtp15.models;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;
import org.swtp15.system.SystemExceptions;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Map<String, FeatureConfiguration> globalOptimumPerProperty;

    final Thread globalNearOptimumThread = new Thread(this::generateNearOptimalConfigurations);

    private boolean threadRuns = false;


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

        this.featureModel.setFeatureSystem(this);
        this.performanceModel.setFeatureSystem(this);
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

        this.featureModel.setFeatureSystem(this);
        this.performanceModel.setFeatureSystem(this);
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
     * A proxy to wait on the models to be generated.
     * <p>
     * To be used to wait on the thread to finish
     */
    public void waitOnModelsGenerated() {
        this.featureModel.waitOnModelsGenerated();
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
     */
    @SneakyThrows
    public FeatureConfiguration findLocalOptimum(FeatureConfiguration configToOptimize, String propertyName,
                                                 int maxDifference) throws IllegalArgumentException {
        if (!configurationIsValid(configToOptimize)) {
            throw ModelExceptions.CONFIGURATION_NOT_VALID;
        }

        // evaluate given config
        evaluateFeatureConfiguration(configToOptimize);

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
        localConfigs.parallelStream().forEach(this::evaluateFeatureConfiguration);

        // find best-in-property
        Boolean isToMinimize = this.performanceModel.propertyIsToMinimize(propertyName);
        FeatureConfiguration localOptimum = configToOptimize.clone();
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
     * Starts the async thread generating the near optimum of every Property of the system.
     */
    public void startGenerateGlobalOptimum() {
        this.globalOptimumPerProperty = new HashMap<>();
        this.globalNearOptimumThread.start();
    }

    /**
     * Returns the respectively optimal {@link FeatureConfiguration} for each {@link Property}.
     * <p>
     * The returned feature configuration may not be the absolute best, but it is a very close estimate. Calculating the
     * very best is far too slow to be viable for big systems.
     *
     * @return Map from the Property to optimize to the corresponding near-optimal FeatureConfiguration
     */
    @SneakyThrows
    public Map<String, FeatureConfiguration> getGlobalOptimumPerProperty() {
        this.featureModel.waitOnModelsGenerated();
        if (!this.threadRuns) {
            this.startGenerateGlobalOptimum();
        }
        this.globalNearOptimumThread.join();
        return this.globalOptimumPerProperty;
    }


    /**
     * Generates a very good estimate for a global optimum for all {@link Property} instances of this System.
     */
    private void generateNearOptimalConfigurations() {
        this.threadRuns = true;
        Random rand = new Random();
        for (Property property : this.getProperties()) {
            Map<String, Integer> optimalNumericValues = this.calculateGlobalOptimum(property);
            List<Set<Feature>> binaryConfigs = Stream.generate(() -> this.featureModel.getRandomValidConfig(rand))
                    .limit(10).collect(Collectors.toList());
            List<Map<String, Boolean>> sampledBinaryMaps =
                    binaryConfigs.parallelStream().map(bFeatures ->
                                                               this.getFeatures().parallelStream()
                                                                       .collect(Collectors
                                                                                        .toMap(Feature::getName,
                                                                                               bFeatures::contains)))
                            .collect(Collectors.toList());
            FeatureConfiguration sampledConfig = sampledBinaryMaps.parallelStream()
                    .map(map -> new FeatureConfiguration(this.name, map, optimalNumericValues))
                    .min((configA, configB) -> (property.isToMinimize() ? 1 : -1) * (int) Math
                            .signum(this.evaluateFeatureConfiguration(configA).get(property) -
                                    this.evaluateFeatureConfiguration(configB).get(property))).orElse(null);
            FeatureConfiguration optimizedConfig;
            try {
                optimizedConfig = this.findLocalOptimum(sampledConfig, property.getName(), 3);
            } catch (Exception ignored) {
                optimizedConfig = sampledConfig;
            }
            this.globalOptimumPerProperty.put(property.getName(), optimizedConfig);
        }
    }

    private Map<String, Integer> calculateGlobalOptimum(Property property) {
        Map<String, Integer> map = new HashMap<>();
        FeatureConfiguration conf = this.getMinimalConfiguration();
        for (Feature numericFeature :
                this.getFeatures().stream().filter(feature -> !feature.isBinary()).collect(Collectors.toSet())) {
            conf.getNumericFeatures().put(numericFeature.getName(), numericFeature.getMinValue());
            double valueForMin = this.performanceModel.evaluateConfiguration(conf).get(property);
            conf.getNumericFeatures().put(numericFeature.getName(), numericFeature.getMaxValue());
            double valueForMax = this.performanceModel.evaluateConfiguration(conf).get(property);
            boolean minIsBetter = (valueForMin < valueForMax) == property.isToMinimize();
            map.put(numericFeature.getName(),
                    minIsBetter ? numericFeature.getMinValue() : numericFeature.getMaxValue());
        }
        return map;
    }

    /**
     * Searches for a valid, alternative and preferable similar {@link FeatureConfiguration} for a given configuration.
     *
     * @param featureConfiguration The configuration, for which the alternative should be searched for
     *
     * @return A {@link FeatureConfiguration} near the given configuration
     *
     * @throws IllegalStateException If system has no valid configurations
     * @throws InterruptedException  If the thread calculating was interrupted before it could finish gracefully
     */
    public FeatureConfiguration getAlternativeConfiguration(FeatureConfiguration featureConfiguration)
    throws IllegalStateException, InterruptedException {

        FeatureConfiguration alternative = null;

        // increase maximum difference between configurations until alternative is found
        for (int maxDiff = 0; maxDiff < this.featureModel.getFeatures().size(); maxDiff++) {
            Set<Map<String, Boolean>> nearModels = this.featureModel.getNearModels(featureConfiguration, maxDiff);
            if (!nearModels.isEmpty()) {
                Map<String, Boolean> biFeatureMap = nearModels.iterator().next();
                alternative = new FeatureConfiguration(this.name, biFeatureMap,
                                                       featureConfiguration.getNumericFeatures(),
                                                       null);
                break;
            }
        }

        if (alternative == null) {
            throw ModelExceptions.MODEL_HAS_NO_VALID_CONFIGURATIONS;
        }

        evaluateFeatureConfiguration(alternative);

        return alternative;
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
