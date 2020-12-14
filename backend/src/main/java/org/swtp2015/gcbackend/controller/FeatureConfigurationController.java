package org.swtp2015.gcbackend.controller;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swtp2015.parser.FileParser;

@RestController
@RequestMapping(value = "/configexample")
public class FeatureConfigurationController {
    /*
     ToDo: In Future, a request for an evaluated FeatureConfiguration should contain the JSON of the Configuration to
      be evaluated in the Body.Then, we will only validate the Configuration and send it back by only adding the double
      values of the properties that we got from the CSV-File
     */
    private String getFeatureConfigurationExample() {
        return StringUtils.join(FileParser.readFile("src/main/exampleFiles/configurationExample.java"));
    }

    @GetMapping
    public ResponseEntity<String> getExampleFeatureConfiguration() {
        return new ResponseEntity<>(getFeatureConfigurationExample(), HttpStatus.OK);
    }
}
