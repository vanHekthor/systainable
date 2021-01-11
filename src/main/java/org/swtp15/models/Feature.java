package org.swtp15.models;

import lombok.Getter;

/**
 * Class that saves one Feature of a {@link FeatureModel}.
 */
public class Feature {

    @Getter
    private final String name;

    /**
     * Instantiates a Feature.
     *
     * @param name feature name
     */
    public Feature(String name) {
        this.name = name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Feature: " + name;
    }
}
