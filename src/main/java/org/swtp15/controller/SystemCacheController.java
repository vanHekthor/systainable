package org.swtp15.controller;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swtp15.parser.SystemParser;
import org.swtp15.system.SystemCache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/systems")
public class SystemCacheController {

    @Autowired
    private SystemCache systemCache;

    /**
     * ToDo: Delete when examples are no longer needed.
     */
    @Getter
    private final Set<String> exampleSystemNames = new HashSet<>(Arrays.asList("system1", "system2", "system3"));

    /**
     * ToDo: Delete when examples are no longer needed. Returns a JSON containing hardcoded system names for testing.
     *
     * @return Example ResponseEntity with system names
     */
    @GetMapping("/example")
    public ResponseEntity<String> getAllSystemsExample() {
        return new ResponseEntity<>(SystemParser.parseSystemNamesToJson(getExampleSystemNames()), HttpStatus.OK);
    }

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
}
