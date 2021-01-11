package org.swtp15.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
    private final Map<Integer, Feature> featureMap;

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
    private boolean errorInThread;
    private final Set<Set<Integer>> models;

    /**
     * Instantiates a FeatureModel.
     *
     * @param featureMap list of all features
     * @param formulas   set of logical clauses of the feature model
     */
    public FeatureModel(@NonNull Map<Integer, Feature> featureMap, @NonNull Set<Set<Integer>> formulas) {
        this.featureMap           = featureMap;
        this.features             = new ArrayList<>(featureMap.values());
        this.formulas             = formulas;
        this.errorInThread        = false;
        this.models               = new HashSet<>();
        this.modelGeneratorThread = new Thread(this::generateModels);
        this.modelGeneratorThread.start();
    }

    /**
     * Checks whether there has been an error during the calculation of all valid models.
     *
     * @return Whether the model generation thread stopped by error.
     */
    public boolean isModelCalculationFailed() {
        return this.errorInThread;
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
        solver.setTimeout(30);
        solver.newVar(featureMap.size());
        solver.setExpectedNumberOfClauses(this.formulas.size());
        solver.setTimeout(1800);

        int[] convertedClause;
        for (Set<Integer> clause : formulas) {
            convertedClause = clause.parallelStream().mapToInt(i -> (i == null ? 0 : i)).toArray();
            try {
                solver.addClause(new VecInt(convertedClause));
            } catch (ContradictionException e) {
                System.err.println("Contradiction in Clause");
                e.printStackTrace();
                errorInThread = true;
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
            errorInThread = true;
        }
    }

    /**
     * Restarts the thread calculating all possible feature configurations for the feature model.
     * <p>
     * This method only does something, if the Thread has been interrupted or failed from an internal exception
     */
    public void restartModelCalculation() {
        final boolean threadFailedByItself = this.errorInThread && !this.modelGeneratorThread.isAlive();
        if (this.modelGeneratorThread.isInterrupted() || threadFailedByItself) {
            this.errorInThread = false;
            this.models.clear();
            this.modelGeneratorThread.start();
        }
    }


    /**
     * Checks whether all given features are contained in this feature model.
     *
     * @param features Set of feature names which are active
     *
     * @return Whether all features could be mapped to instances of {@link Feature} from within this model
     */
    private boolean allFeaturesInModel(@NonNull Set<String> features) {
        return this.features.parallelStream().map(Feature::getName).collect(Collectors.toSet()).containsAll(features);
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
                .map(integers -> integers.parallelStream().map(integer -> this.featureMap.get(integer).getName())
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
    public boolean isValidConfiguration(@NonNull FeatureConfiguration configuration) throws IllegalArgumentException,
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

}
