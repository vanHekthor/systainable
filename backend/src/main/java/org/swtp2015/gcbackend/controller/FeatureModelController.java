package org.swtp2015.gcbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/featuremodels")
public class FeatureModelController {

    //ToDo: This is only hardcoded for now. Later, we want to parse a JSON of any FeatureModels. Also, we want to parse the JSON-list of all FeatureModels by getting all Files in the Models-Directory.
    private String getAllFeatureModelsJson(){
        return "{\"featureModels\":[\"featuremodel1\"]}\"";
    }

    private String getFeatureModel1Json(){
        return "{\"featureModel\":{\"featureModelName\":\"featuremodel1\",\"feature\":[\"root\",\"compressed_skript\",\"encryption\",\"crypt_aes\",\"crypt_blowfish\",\"transaction_control\",\"txc_mvlocks\",\"txc_nvcc\",\"txc_locks\",\"table_type\",\"memory_tables\",\"cached_tables\",\"small_cache\",\"large_cache\",\"logging\",\"detailed_logging\",\"no_write_delay\",\"small_log\"],\"properties\":[\"energy\",\"time\",\"cpu\"]}}";
    }

    @GetMapping
    @RequestMapping(value = "/example")
    public ResponseEntity<String> getAllFeatureModelNames(){
        return new ResponseEntity<>(getAllFeatureModelsJson(), HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping(value = "/example/{name}")
    public ResponseEntity<String> getFeatureModelByName(@PathVariable String name){
        if(name.equals("featuremodel1")){
            return new ResponseEntity<>(getFeatureModel1Json(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
