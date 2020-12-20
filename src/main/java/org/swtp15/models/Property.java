package org.swtp15.models;

import lombok.Getter;

import java.util.Objects;

/**
 * Class to describe a system-property by its name.
 */

public class Property {

    @Getter
    private final String name;

    @Getter
    private final String unit;

    @Getter
    private final boolean toMinimize;

    /**
     * Instantiates a Property.
     *
     * @param name       The property's name
     * @param unit       The property's unit
     * @param toMinimize By reading the csv-file "&lt;" or "&gt;" are obtained. &lt; means its better if the value of
     *                   this Property is minimal, so toMinimize=true &gt; means its better if the value of this
     *                   Property is maximal, so toMinimize=false
     */
    public Property(String name, String unit, boolean toMinimize) {
        this.name       = name;
        this.unit       = unit;
        this.toMinimize = toMinimize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Property: " + name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(o instanceof Property){
            Property other = (Property) o;
            return this.name.equals(other.getName());
        }
        return false;
    }
}
