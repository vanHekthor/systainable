package org.swtp2015.models;

import java.util.Map;
import java.util.Set;

/**
 * Class that saves a FeatureModel
 */
public class FeatureModel {
    /**
     * Mapping from Integers to Features
     */
    private final Map<Integer, Feature> features;

    /**
     * Presentation of a propositional logical formula of a FeatureModel as Set of Set of Integers
     * (Set of clauses where every clause is a set of literals)
     * literal is an integer (negative if negated)
     * literal refers to a Feature (s. features)
     */
    private final Set<Set<Integer>> formulas;

    /**
     * Instantiates a FeatureModel
     * @param features list of all features
     * @param formulas set of logical clauses of the feature model
     */
    public FeatureModel(Map<Integer, Feature> features, Set<Set<Integer>> formulas){
        this.features = features;
        this.formulas = formulas;
    }

    public Map<Integer, Feature> getFeatures() {
        return features;
    }

    public Set<Set<Integer>> getFormulas() {
        return formulas;
    }
}
