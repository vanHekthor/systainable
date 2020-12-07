package org.swtp2015.gcbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/configexample")
public class FeatureConfigurationController {
    //ToDo: In Future, a request for an evaluated FeatureConfiguration should contain the JSON of the Configuration to be evaluated in the Body.
    //ToDo: Then, we will only validate the Configuration and send it back by only adding the double values of the properties that we got from the CSV-File
    private String getFeatureConfigurationExample(){
        return "{\"featureModel\":{\"featureModelName\":\"FeatureModel1\",\"features\":[\"root\":true,\"compressed_skript\":true,\"encryption\":false,\"crypt_aes\":false,\"crypt_blowfish\":false,\"transaction_control\":true,\"txc_mvlocks\":true,\"txc_nvcc\":true,\"txc_locks\":true,\"table_type\":false,\"memory_tables\":false,\"cached_tables\":false,\"small_cache\":false,\"large_cache\":false,\"logging\":true,\"detailed_logging\":true,\"no_write_delay\":true,\"small_log\":false]\"properties\":[\"energy\":2.91,\"time\":10.5,\"cpu\":3.8]}}";
    }

    @GetMapping
    public ResponseEntity<String> getExampleFeatureConfiguration(){
        return new ResponseEntity<>(getFeatureConfigurationExample(), HttpStatus.OK);
    }
}
