package org.swtp15.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swtp15.parser.ResourceReader;

@RestController
@RequestMapping(value = "/systems")
public class SystemController {
    /*
    ToDo: This is only hardcoded for now. ...
     */
    private String getAllSystemNamesJson() {
        return StringUtils.join(new ResourceReader()
                                        .readFileFromResources("/exampleFiles/systemNames.json"), "");
    }

    @GetMapping
    public ResponseEntity<String> getAllSystemNames() {
        return new ResponseEntity<>(getAllSystemNamesJson(), HttpStatus.OK);
    }
}
