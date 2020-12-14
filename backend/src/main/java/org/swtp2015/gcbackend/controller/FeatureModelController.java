package org.swtp2015.gcbackend.controller;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swtp2015.parser.FileParser;

@RestController
@RequestMapping(value = "/featuremodels")
public class FeatureModelController {

    /*
    ToDo: This is only hardcoded for now. Later, we want to parse a JSON of any FeatureModels. Also, we want to parse
     the JSON-list of all FeatureModels by getting all Files in the Models-Directory.
     */
    private String getAllFeatureModelsJson() {
        return StringUtils.join(FileParser.readFile("src/main/exampleFiles/listOfFeatureModelsExample.json"));
    }

    private String getFeatureModel1Json() {
        return StringUtils.join(FileParser.readFile("src/main/exampleFiles/featureModelExample.json"));
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
}
