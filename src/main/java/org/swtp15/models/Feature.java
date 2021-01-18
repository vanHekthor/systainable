package org.swtp15.models;

import lombok.Getter;

/**
 * Class that saves one Feature of a {@link FeatureModel}.
 */
public class Feature {

    @Getter
    private final String name;

    @Getter
    private boolean isBinary;

    @Getter
    private Integer minValue;

    @Getter
    private Integer maxValue;

    @Getter
    private String stepFunction;

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
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Feature: " + name;
    }
}
