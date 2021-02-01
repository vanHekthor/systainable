package org.swtp15.controller;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.models.FeatureSystem;
import org.swtp15.parser.FeatureConfigurationParser;
import org.swtp15.parser.SystemParser;
import org.swtp15.system.SystemCache;


@RestController
@RequestMapping(value = "/featuremodel")
public class FeatureSystemController {

    @Autowired
    private SystemCache systemCache;

    /**
     * Returns a specific featureSystem as a JSON containing the features as a list and the properties as a map with the
     * property name as key and its unit as value.
     *
     * @param name The name of the Model/System to be returned.
     *
     * @return The JSON String.
     */
    @GetMapping
    public @ResponseBody
    ResponseEntity<String> getFeatureModelByName(@RequestParam String name) {
        FeatureSystem featureSystem = systemCache.getFeatureSystemByName(name);
        return featureSystem == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
               new ResponseEntity<>(SystemParser.parseSystemToJson(featureSystem), HttpStatus.OK);
    }

    /**
     * Validity check for configuration.
     *
     * @param json JSON representation of a feature configuration
     *
     * @return a ResponseEntity containing a Boolean for a request
     */
    @PostMapping("/valid")
    public ResponseEntity<Object> validateConfiguration(@RequestBody String json) {
        try {
            FeatureConfiguration featureConfiguration = FeatureConfigurationParser.parseConfiguration(json);
            if (systemCache.hasSystem(featureConfiguration.getFeatureModelName())) {
                return new ResponseEntity<>(systemCache.configIsValid(featureConfiguration), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Given FeatureConfiguration has non-existing FeatureModelName",
                                            HttpStatus.BAD_REQUEST);
            }
        } catch (ParseException e) {
            return new ResponseEntity<>("Invalid FeatureConfiguration JSON in Body: " + e.getMessage(),
                                        HttpStatus.BAD_REQUEST);
        } catch (InterruptedException | IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected Error while validating FeatureConfiguration: " + e.getMessage(),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Returns a valid, alternative and preferable similar {@link FeatureConfiguration} for a given configuration.
     *
     * @param json A String containing the JSON representation of a configuration, for which the alternative should be
     *             searched for
     *
     * @return A ResponseEntity containing the alternative configuration as JSON or the exception message, if errors
     * occurred
     */
    @PostMapping("/alternative")
    public ResponseEntity<String> alternativeConfiguration(@RequestBody String json) {
        try {
            FeatureConfiguration featureConfiguration = FeatureConfigurationParser.parseConfiguration(json);
            FeatureConfiguration alternative = systemCache.getAlternativeConfiguration(featureConfiguration);
            return new ResponseEntity<>(alternative.toString(), HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>("Invalid FeatureConfiguration JSON in Body: " + e.getMessage(),
                                        HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        } catch (InterruptedException e) {
            return new ResponseEntity<>("Valid model calculation was interrupted.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns the minimal valid configuration of a system.
     *
     * @param name The name of the system for which the minimal configuration is needed.
     *
     * @return The minimal configuration as JSON string.
     */
    @GetMapping("/initconfig")
    public @ResponseBody
    ResponseEntity<String> getMinimalValidConfiguration(@RequestParam String name) {
        FeatureSystem featureSystem = systemCache.getFeatureSystemByName(name);
            return featureSystem != null ? new ResponseEntity<>(featureSystem.getMinimalConfiguration().toString(),
                                                                HttpStatus.OK) :
                                                                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
