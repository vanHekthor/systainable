package org.swtp15.controller;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swtp15.models.FeatureConfiguration;
import org.swtp15.parser.FeatureConfigurationParser;
import org.swtp15.parser.ResourceReader;
import org.swtp15.system.SystemCache;


@RestController
@RequestMapping(value = "/featuremodel")
public class FeatureSystemController {

    @Autowired
    private SystemCache systemCache;

    /*
  ToDo: This is only hardcoded for now. ...
   */
    private String getFeatureModelJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/featureModelByNameResponse.json"), "");
    }

    @GetMapping()
    public @ResponseBody
    ResponseEntity<String> getFeatureModelByName(@RequestParam String featuresystem) {
        if (featuresystem.equals("example")) {
            return new ResponseEntity<>(getFeatureModelJson(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            String featureModelName = featureConfiguration.getFeatureModelName();
            if (systemCache.hasSystem(featureModelName)) {
                return new ResponseEntity<>(systemCache.configIsValid(featureConfiguration), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } catch (InterruptedException | IllegalArgumentException | ParseException e) {
            return new ResponseEntity<>("An ERROR occurred while validating Configuration!", HttpStatus.BAD_REQUEST);
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

    @GetMapping("/alternative")
    public ResponseEntity<String> alternativeConfiguration() {
        return new ResponseEntity<>(getAlternativeJson(), HttpStatus.OK);
    }

    private String getMinimalValidJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/minimalValidConfigResponse.json"), "");
    }

    @GetMapping("/initconfig")
    public @ResponseBody
    ResponseEntity<String> getMinimalValidConfiguration(@RequestParam String featuresystem) {
        return new ResponseEntity<>(getMinimalValidJson(), HttpStatus.OK);
    }
}
