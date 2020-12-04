package org.swtp2015.parserTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.swtp2015.gcbackend.GreenConfiguratorBackendApplication;
import org.swtp2015.models.Feature;
import org.swtp2015.parser.DimacsFeatureModelParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GreenConfiguratorBackendApplication.class)
public class DimacsFeatureModelParserTests {
    private final DimacsFeatureModelParser dfmp = new DimacsFeatureModelParser();

    private Set<String> getExpectedFeatureNamesSet(){
        Set<String> featureSet = new HashSet<>();
        featureSet.add("feature1");
        featureSet.add("feature2");
        return featureSet;
    }

    private Set<Set<Integer>> getExpectedFormula(){
        Set<Set<Integer>> formula = new HashSet<>();
        var clause1 = new HashSet<Integer>();
        clause1.add(1);
        var clause2 = new HashSet<Integer>();
        clause2.add(1);
        clause2.add(-2);
        formula.add(clause1);
        formula.add(clause2);
        return formula;
    }

    @Test
    void canParseFileCorrectFormat(){
         assertTrue(dfmp.canParseFile("test.dimacs"));
     }

     @Test
    void canParseFileWrongFormat(){
        assertFalse(dfmp.canParseFile("test.txt"));
     }

     @Test
    void parseFileCorrect(){
        try{
            var featureModel = dfmp.parseFeatureModel("src/test/testModelFiles/CorrectTest.dimacs");
            var readFeatures = featureModel.getFeatures();
            var featuresEqual = readFeatures.size() == 2;
            for(var feature : readFeatures){
                if(!(feature.getName().equals("feature1") || feature.getName().equals("feature2") )){
                    featuresEqual = false;
                }
            }
            var formulaEqual = featureModel.getFormulas().equals(getExpectedFormula());
            assertTrue(featuresEqual && formulaEqual);
        }
        catch(Exception ex){
            fail("Unexpected Exception thrown");
        }
     }

    @Test
    void parseFileMissingControlLine(){
        try{
            dfmp.parseFeatureModel("src/test/testModelFiles/MissingControlLine.dimacs");
            fail("Exception not thrown");
        }
        catch(Exception ex){
            assertEquals("Missing Controlline in Dimacs-File", ex.getMessage());
        }
    }

    @Test
    void parseFileNotMatchingFeatureNumber(){
        try{
            dfmp.parseFeatureModel("src/test/testModelFiles/NotMatchingFeatureNumber.dimacs");
            fail("Exception not thrown");
        }
        catch(Exception ex){
            assertEquals("Number of read features or formulas does not equal the given number in the Dimacs-File", ex.getMessage());
        }
    }

    @Test
    void parseFileOccuringLiteralWithoutFeature(){
        try{
            dfmp.parseFeatureModel("src/test/testModelFiles/OccuringLiteralWithoutFeature.dimacs");
            fail("Exception not thrown");
        }
        catch(Exception ex){
            assertEquals("There is at least one literal without a belonging feature.", ex.getMessage());
        }
    }
}
