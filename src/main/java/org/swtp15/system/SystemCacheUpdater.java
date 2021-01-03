package org.swtp15.system;

import lombok.Getter;
import org.swtp15.models.FeatureModel;
import org.swtp15.models.FeatureSystem;
import org.swtp15.models.PerformanceInfluenceModel;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SystemCacheUpdater {

    @Getter
    private final SystemCache systemCache;

    /**
     * The constructor.
     *
     * @param systemCache the {@link SystemCache} to be updated.
     */
    public SystemCacheUpdater(SystemCache systemCache) {
        this.systemCache = systemCache;
    }

    /*
     * ToDo: this method will also take care of other updates such as parsing the actual models that were read out of
     *   the directory
     */

    /**
     * Updates the {@link SystemCache}.
     *
     * @param pathToModels Path to read {@link FeatureSystem} from.
     *
     * @throws Exception If called methods throw Exceptions.
     */
    public void updateSystemCache(String pathToModels) throws Exception {
        updateCurrentlyKnownSystems(pathToModels);
    }

    /**
     * Reads all {@link FeatureSystem} by reading all dimacs from one and all csv files from another directory and
     * comparing their filenames.
     *
     * @param path Directory to find dimacs and csv files.
     *
     * @throws Exception invalid path or path does not contains subdirectories for dimacs and csv.
     */
    private void updateCurrentlyKnownSystems(String path) throws Exception {
        File modelsDirectory = new File(path);
        if (!(modelsDirectory.exists() && modelsDirectory.isDirectory())) {
            throw new Exception("Given directory path does not exist.");
        }
        Set<Map<String, File>> readSystems = Arrays.stream(
                Objects.requireNonNull(modelsDirectory.listFiles())).parallel().filter(File::isDirectory)
                .map(directory -> {
                    File[] notNullFiles = Objects.requireNonNull(directory.listFiles());
                    List<File> dimacsFiles = Arrays.stream(notNullFiles).parallel()
                            .filter(f -> f.getName().endsWith(".dimacs")).collect(Collectors.toList());
                    List<File> csvFiles = Arrays.stream(notNullFiles).parallel()
                            .filter(f -> f.getName().endsWith(".csv")).collect(Collectors.toList());

                    Map<String, File> fileTypeMap = new HashMap<>();
                    fileTypeMap.put("name", directory);
                    fileTypeMap.put("dimacs", dimacsFiles.get(0));
                    fileTypeMap.put("csv", csvFiles.get(0));

                    return dimacsFiles.size() == 1 & csvFiles.size() == 1 ?
                           fileTypeMap : null;
                }).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<String, FeatureSystem> systemMap = new HashMap<>();


        for (Map<String, File> systemFileNames : readSystems) {
            FeatureModel featureModel = FeatureModelParser.parseModel(systemFileNames.get("dimacs").getPath());
            PerformanceInfluenceModel pIModel = PerformanceModelParser
                    .parseModel(systemFileNames.get("csv").getPath(), featureModel.getFeatures());

            FeatureSystem system = new FeatureSystem(systemFileNames.get("name").getName(), featureModel, pIModel);

            systemMap.put(system.getName(), system);
        }

        this.systemCache.setCurrentlyKnownSystems(systemMap);
    }
}
