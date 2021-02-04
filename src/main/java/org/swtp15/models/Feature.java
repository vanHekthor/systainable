package org.swtp15.models;

import lombok.Getter;

import java.util.function.Function;

/**
 * Class that saves one Feature of a {@link FeatureModel}.
 */
public class Feature {

    @Getter
    private final String name;

    @Getter
    private final boolean isBinary;

    @Getter
    private final Integer minValue;

    @Getter
    private final Integer maxValue;

    @Getter
    private final String stepFunction;

    @Getter
    private Function<Integer, Integer> parsedStepFunction = null;

    /**
     * Instantiates a binary Feature.
     *
     * @param name feature name
     */
    public Feature(String name) {
        this.name         = name;
        this.isBinary     = true;
        this.maxValue     = null;
        this.minValue     = null;
        this.stepFunction = null;
    }

    /**
     * Instantiates a numeric Feature.
     *
     * @param name         feature name
     * @param minValue     numeric min value
     * @param maxValue     numeric max value
     * @param stepFunction step function between possible values in range of min and max
     */
    public Feature(String name, int minValue, int maxValue, String stepFunction) {
        this.name         = name;
        this.isBinary     = false;
        this.minValue     = minValue;
        this.maxValue     = maxValue;
        this.stepFunction = stepFunction;
        this.parseStepFunction();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Feature: " + name;
    }

    /**
     * Returns the next valid value of a numeric feature.
     *
     * @param currentValue The current value to which the next value is required.
     *
     * @return The next value of the numeric features.
     *
     * @throws IllegalArgumentException      If the next value would exceed the maximum value of the feature.
     * @throws UnsupportedOperationException If this feature is not a numeric feature
     */
    public int getNextValidValue(int currentValue) throws IllegalArgumentException, UnsupportedOperationException {
        if (this.stepFunction != null) {
            int nextValue = this.parsedStepFunction.apply(currentValue);
            if (nextValue > this.maxValue) {
                throw ModelExceptions.NUMERIC_FEATURE_VALUE_OVER_BOUNDS;
            } else {
                return nextValue;
            }
        } else {
            throw ModelExceptions.FEATURE_NOT_A_NUMERIC_FEATURE;
        }
    }

    private void parseStepFunction() {
        if (stepFunction.contains("+")) {
            int changeNumber = Integer.parseInt(stepFunction.replace(" ", "").split("\\+")[1]);
            this.parsedStepFunction = (i -> i + changeNumber);
        } else if (stepFunction.contains("*")) {
            int changeNumber = Integer.parseInt(stepFunction.replace(" ", "").split("\\*")[1]);
            this.parsedStepFunction = (i -> i * changeNumber);
        } else {
            this.parsedStepFunction = null;
        }
    }
}
