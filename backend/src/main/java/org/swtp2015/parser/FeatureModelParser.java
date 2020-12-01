package org.swtp2015.parser;

import org.swtp2015.models.*;

/**
 * Abstract class for a FeatureModelParser
 */
public abstract class FeatureModelParser extends FileParser{

    /**
     * checks, if a file has the right file format to be parsed into a FeatureModel
     * @param filename file to be parsed
     * @return true/false
     */
    public abstract boolean canParseFile(String filename);

    /**
     * parses file into FeatureModel
     * @param filename file to be parsed
     * @return parsed FeatureModel
     */
    public abstract FeatureModel parseFeatureModel(String filename);
}
