package org.swtp15.controller;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.models.FeatureSystem;
import org.swtp15.parser.FeatureConfigurationParser;
import org.swtp15.parser.ResourceReader;
import org.swtp15.system.SystemCache;
import org.swtp15.system.SystemExceptions;

@RestController
@RequestMapping(value = "/performance")
public class PerformanceController {

    @Autowired
    private SystemCache systemCache;

    /**
     * Evaluates a Configuration given as a JSON in the RequestBody of the Http request.
     *
     * @param configurationJSON The Configuration to be evaluated.
     *
     * @return The Configuration with evaluated values.
     */
    @PostMapping
    public @ResponseBody
    ResponseEntity<String> getPropertiesForFeatureConfiguration
    (@RequestBody String configurationJSON) {
        try {
            FeatureConfiguration configuration = FeatureConfigurationParser.parseConfiguration(configurationJSON);
            FeatureSystem relatedSystem =
                    systemCache.getFeatureSystemByName(configuration.getFeatureModelName());
            if (relatedSystem == null) {
                return new ResponseEntity<>("Given FeatureConfiguration has non-existing FeatureModelName",
                                            HttpStatus.BAD_REQUEST);
            }
            relatedSystem.evaluateFeatureConfiguration(configuration);
            return new ResponseEntity<>(configuration.toString(), HttpStatus.OK);

        } catch (ParseException e) {
            return new ResponseEntity<>("Invalid FeatureConfiguration JSON in Body: " + e.getMessage(),
                                        HttpStatus.BAD_REQUEST);
        }
    }

    /*
  ToDo: This is only hardcoded for now. ...
   */
    private String getPredictedPropertiesJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/predictedPropertiesResponse.json"), "");
    }

    @GetMapping("/example")
    public ResponseEntity<String> getPredictedProperties() {
        return new ResponseEntity<>(getPredictedPropertiesJson(), HttpStatus.OK);
    }

    private String getGlobalOptimumJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/globalOptimumResponse.json"), "");
    }

    @GetMapping("/optimum/global")
    public @ResponseBody
    ResponseEntity<String> getGlobalOptimum(@RequestParam String featuresystem,
                                            @RequestParam String property) {
        return new ResponseEntity<>(getGlobalOptimumJson(), HttpStatus.OK);
    }

    private String getLocalOptimumJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/localOptimumResponse.json"), "");
    }

    // ToDo: delete when examples are no longer needed
    @GetMapping("/optimum/local/example")
    public @ResponseBody
    ResponseEntity<String> getLocalOptimumExample(@RequestParam String property,
                                                  @RequestParam int maxDifference) {
        return new ResponseEntity<>(getLocalOptimumJson(), HttpStatus.OK);
    }

    /**
     * Find (local) optimum for a specific property in a given range. Range here describes the features that can be
     * different from the given configuration.
     *
     * @param property          The name of the property, which is to optimize
     * @param maxDifference     Number of features that can be different
     * @param configurationJSON JSON representation of {@link FeatureConfiguration} that should be optimized
     *
     * @return optimized {@link FeatureConfiguration} for property
     */
    @GetMapping("/optimum/local")
    public @ResponseBody
    ResponseEntity<String> getLocalOptimum(@RequestParam String property,
                                           @RequestParam int maxDifference,
                                           @RequestBody String configurationJSON) {
        try {
            FeatureConfiguration featureConfiguration = FeatureConfigurationParser
                    .parseConfiguration(configurationJSON);
            if (maxDifference < 1) {
                return new ResponseEntity<>("Maximal difference parameter must be positive.",
                                            HttpStatus.BAD_REQUEST);
            }
            FeatureConfiguration localOptimum = systemCache.findLocalOptimumForConfiguration(featureConfiguration,
                                                                                             property, maxDifference);
            return new ResponseEntity<>(localOptimum.toString(), HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>("Invalid FeatureConfiguration JSON in Body: " + e.getMessage(),
                                        HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException | InterruptedException e) {
            if(e.getMessage().equals(SystemExceptions.IS_ALREADY_OPTIMUM.getMessage()))
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
