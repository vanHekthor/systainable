package org.swtp15.controller;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swtp15.models.FeatureSystem;
import org.swtp15.parser.SystemParser;
import org.swtp15.system.SystemCache;

import java.util.Set;

@RestController
@RequestMapping(value = "/systems")
public class SystemCacheController {

    @Autowired
    private SystemCache systemCache;

    /**
     * Returns the system names of systems currently known by the system cache.
     *
     * @return ResponseEntity containing JSON containing Array of system names
     */
    @GetMapping
    public ResponseEntity<String> getAllSystems() {
        Set<String> systems = systemCache.getCurrentlyKnownSystems().keySet();
        return new ResponseEntity<>(SystemParser.parseSystemNamesToJson(systems), HttpStatus.OK);
    }

    /**
     * Loads example models and returns the system names of systems currently known by the system cache.
     *
     * @return ResponseEntity containing JSON containing Array of system names
     */
    @GetMapping("/activateExamples")
    public ResponseEntity<String> loadExampleModels() {
        this.generateExampleModels();
        return this.getAllSystems();
    }

    @SneakyThrows
    private void generateExampleModels() {
        FeatureSystem zipSystem = new FeatureSystem("7-ZIP", "/featureSystems/7-ZIP/", "FeatureModel_noNumeric.dimacs",
                                                    "FeatureModel.xml", "model2.csv", true);

        FeatureSystem autoSystem = new FeatureSystem("Auto", "/featureSystems/Auto/", "FeatureModel.dimacs", null,
                                                     "model.csv", true);
        FeatureSystem hsqldbSystem = new FeatureSystem("HSQL DB", "/featureSystems/HSQL DB/", "FeatureModel.dimacs",
                                                       null, "model.csv", true);
        FeatureSystem customBi = new FeatureSystem("OptimizationBinary",
                                                   "/featureSystems/OptimizationBinary/",
                                                   "Model_Optimize.dimacs",
                                                   null, "Model_Optimize.csv", true);
        FeatureSystem customNum = new FeatureSystem("OptimizationNumeric",
                                                    "/featureSystems/OptimizationNumeric/",
                                                    "Model_Optimize.dimacs",
                                                    "Model_Optimize.xml",
                                                    "Model_Optimize.csv", true);
        this.systemCache.getSystemCacheUpdater().setPermanentSystems(Set.of(zipSystem, autoSystem, hsqldbSystem,
                                                                            customBi, customNum));
        this.systemCache.getSystemCacheUpdater().initialize();
    }
}
