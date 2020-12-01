package org.swtp2015.models;

/**
 * Class that saves one Feature of a FeatureModel
 */
public class Feature {

    private final String name;

    /**
     * Instantiates a Feature
     * @param name feature name
     */
    public Feature(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
