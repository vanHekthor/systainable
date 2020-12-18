package org.swtp15.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swtp15.parser.ResourceReader;

@RestController
@RequestMapping(value = "/configexample")
public class FeatureConfigurationController {
    /*
     ToDo: In Future, a request for an evaluated FeatureConfiguration should contain the JSON of the Configuration to
      be evaluated in the Body.Then, we will only validate the Configuration and send it back by only adding the double
      values of the properties that we got from the CSV-File
     */
    private String getFeatureConfigurationExample() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/configurationExample.json"));
    }

    @GetMapping
    public ResponseEntity<String> getExampleFeatureConfiguration() {
        return new ResponseEntity<>(getFeatureConfigurationExample(), HttpStatus.OK);
    }
}
