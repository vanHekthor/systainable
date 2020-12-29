package org.swtp15.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swtp15.parser.ResourceReader;

@RestController
@RequestMapping(value = "/performance")
public class PerformanceController {

    /*
  ToDo: This is only hardcoded for now. ...
   */
    private String getPredictedPropertiesJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/predictedPropertiesResponse.json"), "");
    }

    @GetMapping
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

    @GetMapping("/optimum/local")
    public @ResponseBody
    ResponseEntity<String> getLocalOptimum(@RequestParam String property,
                                           @RequestParam String maxDifference) {
        return new ResponseEntity<>(getLocalOptimumJson(), HttpStatus.OK);
    }
}
