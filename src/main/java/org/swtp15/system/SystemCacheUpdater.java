package org.swtp15.system;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swtp15.models.FeatureModel;
import org.swtp15.models.FeatureSystem;
import org.swtp15.models.PerformanceInfluenceModel;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SystemCacheUpdater {

    @Getter
    private final SystemCache systemCache;

    /**
     * The constructor.
     *
     * @param systemCache the {@link SystemCache} to be updated.
     */
    @Autowired
    public SystemCacheUpdater(SystemCache systemCache) {
        this.systemCache = systemCache;
    }

    /**
     * Initializes the {@link SystemCache}.
     *
     * This method scans both the internal used path of the models as well as `$PWD/modelFiles` to search for valid
     * models.
     */
    public void initialize() {
        try {
            readSystemsFromDirectory(this.systemCache.getPathToModels(), new File("modelFiles").getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ToDo: here a method should be started that regularly updates the SystemCache
    }

    /**
     * Reads systems from files
     *
     * Reads by going through all subdirectories of the provided paths and checks whether
     * they contain both a valid dimacs, csv and optionally a xml, compiling a {@link FeatureSystem} for each found.
     *
     * @param paths All directories to scan subdirectories from
     *
     * @throws IllegalArgumentException No subdirectories found or invalid model files
     */
    public void readSystemsFromDirectory(String... paths) throws IllegalArgumentException {
        Set<File> foundDirestories = Arrays.stream(paths).map(File::new).filter(File::isDirectory).collect(
                Collectors.toSet());
        if (foundDirestories.size() == 0) {
            throw SystemExceptions.INVALID_DIRECTORY_PATH;
        }
        Map<String, FeatureSystem> systemMap = new HashMap<>();

        for (File dir : foundDirestories) {
            Set<Map<String, File>> readSystems = Arrays.stream(
                    Objects.requireNonNull(dir.listFiles())).parallel().filter(File::isDirectory)
                    .map(directory -> {
                        File[] notNullFiles = Objects.requireNonNull(directory.listFiles());
                        List<File> dimacsFiles = Arrays.stream(notNullFiles).parallel()
                                .filter(f -> f.getName().endsWith(".dimacs")).collect(Collectors.toList());
                        List<File> xmlFiles = Arrays.stream(notNullFiles).parallel()
                                .filter(f -> f.getName().endsWith(".xml")).collect(Collectors.toList());
                        List<File> csvFiles = Arrays.stream(notNullFiles).parallel()
                                .filter(f -> f.getName().endsWith(".csv")).collect(Collectors.toList());

                        Map<String, File> fileTypeMap = new HashMap<>();
                        fileTypeMap.put("name", directory);
                        fileTypeMap.put("dimacs", dimacsFiles.get(0));
                        fileTypeMap.put("xml", xmlFiles.size() == 0 ? null : xmlFiles.get(0));
                        fileTypeMap.put("csv", csvFiles.get(0));

                        return dimacsFiles.size() == 1 & csvFiles.size() == 1 & xmlFiles.size() <= 1 ?
                               fileTypeMap : null;
                    }).filter(Objects::nonNull).collect(Collectors.toSet());


            for (Map<String, File> systemFileNames : readSystems) {
                File xmlEntry = systemFileNames.get("xml");
                FeatureModel featureModel = FeatureModelParser.parseModel(systemFileNames.get("dimacs").getPath(),
                                                                          xmlEntry == null ? null : xmlEntry.getPath());
                PerformanceInfluenceModel pIModel = PerformanceModelParser
                        .parseModel(systemFileNames.get("csv").getPath(), featureModel.getFeatures());

                FeatureSystem system = new FeatureSystem(systemFileNames.get("name").getName(), featureModel, pIModel);

                systemMap.put(system.getName(), system);
            }
        }

        this.systemCache.setCurrentlyKnownSystems(systemMap);
    }
}
