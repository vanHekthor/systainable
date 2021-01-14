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
import org.swtp15.parser.SystemParser;
import org.swtp15.system.SystemCache;


@RestController
@RequestMapping(value = "/featuremodel")
public class FeatureSystemController {

    @Autowired
    private SystemCache systemCache;

    private String getFeatureModelJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/featureModelByNameResponse.json"), "");
    }

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
        //ToDo: Delete this if statement when the example is no longer needed
        if (name.equals("example")) {
            return new ResponseEntity<>(getFeatureModelJson(), HttpStatus.OK);
        }
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
    @GetMapping("/valid")
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
     * ToDo: delete when examples no longer necessary
     * <p>
     * Get example Boolean (default=false) for validity.
     *
     * @return ResponseEntity
     */
    @GetMapping("/valid/example")
    public ResponseEntity<Boolean> validateConfigurationExample() {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }


    private String getAlternativeJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/alternativeConfigResponse.json"), "");
    }

    //ToDo: This is only hardcoded for now. ...

    @GetMapping("/alternative")
    public ResponseEntity<String> alternativeConfiguration() {
        return new ResponseEntity<>(getAlternativeJson(), HttpStatus.OK);
    }

    private String getMinimalValidJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/minimalValidConfigResponse.json"), "");
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
        //ToDo: Delete this if statement when the example is no longer needed
        if (name.equals("example")) {
            return new ResponseEntity<>(getMinimalValidJson(), HttpStatus.OK);
        }
        FeatureSystem featureSystem = systemCache.getFeatureSystemByName(name);
        return featureSystem == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
               new ResponseEntity<>(featureSystem.getMinimalConfiguration().toString(), HttpStatus.OK);
    }
}
