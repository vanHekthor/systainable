package org.swtp2015.models;

/**
 * Class to describe a system-property by its name.
 */

public class Property {

    private final String name;
    private final String unit;
    private final boolean toMinimize;

    /**
     * Instantiates a property
     *
     * @param name                  The property's name
     * @param unit                  The property's unit
     * @param toMinimize By reading the csv-file "<" or ">" are obtained.
     *                   < means its better if the value of this Property is minimal, so toMinimize=true
     *                   > means its better if the value of this Property is maximal, so toMinimize=false
     */
    public Property(String name, String unit, boolean toMinimize) {
        this.name = name;
        this.unit = unit;
        this.toMinimize = toMinimize;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public boolean getToMinimize() {
        return toMinimize;
    }

}
