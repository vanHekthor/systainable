package org.swtp15.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swtp15.parser.ResourceReader;


@RestController
@RequestMapping(value = "/featuremodel")
public class FeatureSystemController {

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

    private String getValidationJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/validationResponse.json"), "");
    }

    @GetMapping("/valid")
    public ResponseEntity<String> validateConfiguration() {
        return new ResponseEntity<>(getValidationJson(), HttpStatus.OK);
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
