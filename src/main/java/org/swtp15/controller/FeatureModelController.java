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
@RequestMapping(value = "/featuremodels")
public class FeatureModelController {

    @Autowired
    private SystemCache systemCache;

    /*
    ToDo: This is only hardcoded for now. Later, we want to parse a JSON of any FeatureModels. Also, we want to parse
     the JSON-list of all FeatureModels by getting all Files in the Models-Directory.
     */
    private String getAllFeatureModelsJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/listOfFeatureModelsExample.json"));
    }

    private String getFeatureModel1Json() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/featureModelExample.json"));
    }

    @GetMapping
    @RequestMapping(value = "/example")
    public ResponseEntity<String> getAllFeatureModelNames() {
        return new ResponseEntity<>(getAllFeatureModelsJson(), HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping(value = "/example/{name}")
    public ResponseEntity<String> getFeatureModelByName(@PathVariable String name) {
        if (name.equals("featuremodel1")) {
            return new ResponseEntity<>(getFeatureModel1Json(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ToDo: Write tests for controller

    /**
     * Validity check for configuration.
     *
     * @param json JSON representation of a feature configuration
     *
     * @return a ResponseEntity containing a Boolean for a request
     */
    @GetMapping
    @RequestMapping(value = "/valid")
    public ResponseEntity<Object> configIsValid(@RequestBody String json) {
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
    @GetMapping
    @RequestMapping(value = "/valid/example")
    public ResponseEntity<Boolean> configIsValid() {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
