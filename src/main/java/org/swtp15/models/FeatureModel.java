package org.swtp15.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import org.swtp15.parser.ParserExceptions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that saves a FeatureModel.
 */
public class FeatureModel {
    /**
     * Mapping from Integers to Features.
     */
    private final Map<Integer, Feature> binaryFeatures;

    private final Map<Integer, Feature> numericFeatures;

    @Getter
    private final List<Feature> features;

    @Getter
    @Setter
    private String name;

    /**
     * Representation of a logical formula in conjunctive normal form of a FeatureModel as Set of Set of Integers (Set
     * of clauses where every clause is a set of literals) literal is an integer (negative if negated) literal refers to
     * a {@link Feature}.
     */
    @Getter
    private final Set<Set<Integer>> formulas;

    private final Thread modelGeneratorThread;
    @Getter
    private boolean isModelCalculationFailed;
    private final List<Set<Integer>> models;

    private Map<String, Boolean> minimalModel;

    @Getter
    private final int amountOfFeatures;
    @Getter
    private final int amountOfFormulas;

    @Setter
    private FeatureSystem featureSystem;

    /**
     * Instantiates a FeatureModel.
     *
     * @param binaryFeatures   Map of the binary features.
     * @param numericFeatures  Map of the numeric features.
     * @param formulas         Set of logical clauses of the feature model
     * @param amountOfFeatures Amount of features
     * @param amountOfFormulas Amount of formulas
     */
    public FeatureModel(@NonNull Map<Integer, Feature> binaryFeatures,
                        @NonNull Map<Integer, Feature> numericFeatures, @NonNull Set<Set<Integer>> formulas,
                        int amountOfFeatures, int amountOfFormulas) {
        this.binaryFeatures  = binaryFeatures;
        this.numericFeatures = numericFeatures;
        this.features        = new ArrayList<>(binaryFeatures.values());
        features.addAll(numericFeatures.values());
        this.formulas                 = formulas;
        this.isModelCalculationFailed = false;
        this.models                   = new ArrayList<>();
        this.minimalModel             = null;
        this.amountOfFeatures         = amountOfFeatures;
        this.amountOfFormulas         = amountOfFormulas;

        Set<Integer> optionalFeatures = this.getFullyOptionalFeatures();

        this.formulas.addAll(optionalFeatures.parallelStream().map(opt -> Set.of(opt, -opt)).collect(
                Collectors.toSet()));

        this.modelGeneratorThread = new Thread(this::generateModels);
        this.modelGeneratorThread.start();
    }


    /**
     * Generates all valid models based on the constraints in the `dimacs` file.
     * <p>
     * This method is supposed to be run via a separate thread, as generating the models can take time depending on the
     * size and complexity of the input file. If however the calculation takes longer than 30 minutes, this method will
     * time out, causing the whole model to be unusable. This however will require probably tens of thousands of
     * constraints
     */
    private void generateModels() {
        final ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(60);
        solver.newVar(binaryFeatures.size());
        solver.setExpectedNumberOfClauses(this.formulas.size());
        int[] convertedClause;
        for (Set<Integer> clause : formulas) {
            convertedClause = clause.parallelStream().mapToInt(i -> (i == null ? 0 : i)).toArray();
            try {
                solver.addClause(new VecInt(convertedClause));
            } catch (ContradictionException e) {
                System.err.println("Contradiction in Clause");
                e.printStackTrace();
                isModelCalculationFailed = true;
                return;
            }
        }
        final ModelIterator mi = new ModelIterator(solver);
        try {
            while (mi.isSatisfiable()) {
                final Set<Integer> model =
                        Arrays.stream(mi.model()).boxed().filter(i -> i > 0).collect(Collectors.toSet());
                this.models.add(model);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            isModelCalculationFailed = true;
        }
    }

    /**
     * Restarts the thread calculating all possible feature configurations for the feature model.
     * <p>
     * This method only does something, if the Thread has been interrupted or failed from an internal exception
     */
    public void restartModelCalculation() {
        final boolean threadFailedByItself = this.isModelCalculationFailed && !this.modelGeneratorThread.isAlive();
        if (this.modelGeneratorThread.isInterrupted() || threadFailedByItself) {
            this.isModelCalculationFailed = false;
            this.models.clear();
            this.modelGeneratorThread.start();
        }
    }

    /**
     * A proxy to wait on the models to be generated.
     * <p>
     * To be used to wait on the thread to finish
     */
    @SneakyThrows
    void waitOnModelsGenerated() {
        this.modelGeneratorThread.join();
    }


    /**
     * Gets a random configuration of binary features which is valid within the model.
     *
     * @param rand A Random object
     *
     * @return Set of active Features
     */
    public Set<Feature> getRandomValidConfig(Random rand) {
        int randomIndex = rand.nextInt(this.models.size());
        return this.models.get(randomIndex).parallelStream().map(binaryFeatures::get).collect(Collectors.toSet());
    }

    /**
     * Checks whether all given features are contained in this feature model.
     *
     * @param features Set of feature names which are active
     *
     * @return Whether all features could be mapped to instances of {@link Feature} from within this model
     */
    private boolean allFeaturesInModel(@NonNull Set<String> features) {
        return this.features.parallelStream().map(Feature::getName).collect(Collectors.toSet())
                .containsAll(features);
    }

    /**
     * Checks whether a specific Set of features is matching a valid allocation of features given by the constraints.
     *
     * @param features Set of feature names which are active
     *
     * @return Whether the given feature set is valid within the contraints of this model
     */
    private boolean checkIfFeatureSetValid(@NonNull Set<String> features) {
        return this.models.parallelStream()
                .map(integers -> integers.parallelStream()
                        .map(integer -> this.binaryFeatures.get(integer).getName())
                        .collect(Collectors.toSet()))
                .anyMatch(strings -> features.containsAll(strings) && strings.containsAll(features));
    }

    /**
     * Checks whether the given {@link FeatureConfiguration} is valid within this feature model. This both includes all
     * features being included in the model as well as whether the set of active features in the configuration are
     * allowed by the constraints of the model.
     *
     * @param configuration The configuration which should be checked for validity
     *
     * @return Whether the given configuration is valid
     *
     * @throws IllegalArgumentException If the given configuration contains features not included in the feature model
     * @throws InterruptedException     If the thread calculating was interrupted before it could finish gracefully
     */
    public boolean isValidConfiguration(@NonNull FeatureConfiguration configuration) throws
                                                                                     IllegalArgumentException,
                                                                                     InterruptedException {
        final Set<String> activeFeatures = configuration.getActiveFeatures();
        if (!this.isModelCalculationFailed()) {
            if (this.allFeaturesInModel(activeFeatures)) {
                this.modelGeneratorThread.join();
                return this.checkIfFeatureSetValid(activeFeatures);
            } else throw ParserExceptions.CONFIGURATION_NOT_SUBSET_OF_MODEL;
        } else {
            throw new InterruptedException("Thread failed, cannot calculate valid configurations");
        }
    }

    /**
     * Getter for the minimal model. If it is null, starts determination of minimal model, else returns it.
     *
     * @return The map of the features names and their booleans of the minimal model.
     */
    @SneakyThrows
    public Map<String, Boolean> getMinimalModel() {
        if (minimalModel == null) {
            findMinimalModel();
            return minimalModel;
        }
        return minimalModel;
    }

    /**
     * Calculates the minimal model by finding the smallest integer set and creating a map of feature names out of it.
     */
    @SneakyThrows
    private void findMinimalModel() {
        Map<String, Boolean> minimalModel = new HashMap<>();
        int modelSize = Integer.MAX_VALUE;
        Set<Integer> minimalModelSet = null;
        this.modelGeneratorThread.join();
        for (Set<Integer> model : models) {
            if (model.size() < modelSize) {
                modelSize       = model.size();
                minimalModelSet = model;
            }
        }
        List<String> activeFeatures = new ArrayList<>();
        minimalModelSet.forEach(i -> activeFeatures.add(binaryFeatures.get(i).getName()));
        for (Feature feature : binaryFeatures.values()) {
            String featureName = feature.getName();
            minimalModel.put(featureName, activeFeatures.contains(featureName));
        }
        this.minimalModel = minimalModel;
    }

    /**
     * Creates a Map of the numeric feature names and initializes their values with minValue.
     *
     * @return The map of the numeric features.
     */
    public Map<String, Integer> getInitialNumericFeaturesMap() {
        Map<String, Integer> initialNumericFeatures = new HashMap<>();
        for (Feature feature : numericFeatures.values()) {
            initialNumericFeatures.put(feature.getName(), feature.getMinValue());
        }
        return initialNumericFeatures;
    }

    private Set<Integer> getFullyOptionalFeatures() {
        Set<Integer> allFeatures = new HashSet<>();
        for (int i = 1; i <= this.amountOfFeatures; i++) {
            allFeatures.add(i);
        }
        Set<Integer> usedFeatures = new HashSet<>();
        for (Set<Integer> formula : this.formulas) {
            usedFeatures.addAll(formula.parallelStream().map(Math::abs).collect(Collectors.toSet()));
        }
        allFeatures.removeAll(usedFeatures);
        return allFeatures;
    }


    /**
     * Finds all near Models for a given {@link FeatureConfiguration}.
     *
     * @param featureConfiguration The {@link FeatureConfiguration} that determines center of the range
     * @param maxDiff              The number of features that can be different
     *
     * @return A Set of Maps containing the {@link Feature}s as keys and the activ state as value
     *
     * @throws InterruptedException If the thread calculating was interrupted before it could finish gracefully
     */
    public Set<Map<String, Boolean>> getNearModels(FeatureConfiguration featureConfiguration, int maxDiff)
    throws InterruptedException {
        Set<Integer> configAsIntegerSet = convertConfigToIntegerSet(featureConfiguration);

        this.modelGeneratorThread.join();
        Set<Set<Integer>> nearModels = this.models.parallelStream()
                .filter(model -> Math.abs(configAsIntegerSet.size() - model.size()) <= maxDiff)
                .filter(model -> modelIsNear(configAsIntegerSet, model, maxDiff))
                .collect(Collectors.toSet());

        Set<Map<String, Boolean>> nearModelsAsBinaryMap = nearModels.parallelStream()
                .map(this::convertModelToBinaryFeatureMap)
                .collect(Collectors.toSet());

        return nearModelsAsBinaryMap;
    }


    /**
     * Converts a {@link FeatureConfiguration} to a model, comparable to the {@link #models} of this class. Returns a
     * Set of Integers which represent an active feature mapped by {@link #binaryFeatures}.
     *
     * @param featureConfiguration The configuration that should get converted
     *
     * @return A Set of Integers, describing the active features
     */
    private Set<Integer> convertConfigToIntegerSet(FeatureConfiguration featureConfiguration) {
        Map<String, Integer> featureNameToInt = new HashMap<>();
        this.binaryFeatures.keySet().parallelStream()
                .forEach(FeatureAsInt ->
                                 featureNameToInt
                                         .put(this.binaryFeatures.get(FeatureAsInt).getName(), FeatureAsInt));

        Set<String> activeFeatures = featureConfiguration.getActiveFeatures();
        Set<Integer> configAsIntegerSet = activeFeatures.parallelStream()
                .map(featureNameToInt::get)
                .collect(Collectors.toSet());

        return configAsIntegerSet;
    }


    /**
     * Converts a Set of Integers representing active {@link Feature}s to a Map containing all feature names mapped to
     * against their active-state.
     *
     * @param model A Set of Integers representing active features
     *
     * @return A Map containing {@link Feature} names as keys and their active-state as values
     */
    private Map<String, Boolean> convertModelToBinaryFeatureMap(Set<Integer> model) {
        Map<String, Boolean> integerSetAsMap = new HashMap<>();

        List<String> activeFeatures = new ArrayList<>();
        model.forEach(integer -> activeFeatures.add(binaryFeatures.get(integer).getName()));

        binaryFeatures.values().forEach(feature -> integerSetAsMap.put(feature.getName(),
                                                                       activeFeatures.contains(feature.getName())));

        return integerSetAsMap;
    }


    /**
     * Checks if a Set of Integers representing active {@link Feature}s differs another Set of Integers by a maximum of
     * n features.
     *
     * @param modelA  Set of Integers representing active features
     * @param modelB  Another Set of Integers representing active features
     * @param maxDiff Number n which determines the maximum in differences
     *
     * @return TRUE if differences doesn't exceed the given maximum
     */
    private boolean modelIsNear(Set<Integer> modelA, Set<Integer> modelB, int maxDiff) {
        Set<Integer> union = new HashSet<>();

        union.addAll(modelA);
        union.addAll(modelB);

        int notInA = union.size() - modelA.size();
        int notInB = union.size() - modelB.size();
        return (notInA + notInB) <= maxDiff;
    }
}
