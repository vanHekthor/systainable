package org.swtp15.system;

import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SystemCacheUpdater {
    @Getter
    private SystemCache systemCache;

    /**
     * The constructor.
     *
     * @param systemCache the systemCache to be updated.
     */
    public SystemCacheUpdater(SystemCache systemCache) {
        this.systemCache = systemCache;
    }

    //ToDo: this method will also take care of other updates such as parsing
    // the actual models that were read out of the directoy

    /**
     * Updates the SystemCache.
     *
     * @param pathToModels Path to read FeatureSystem from.
     *
     * @throws Exception If called methods throw Exceptions.
     */
    public void updateSystemCache(String pathToModels) throws Exception {
        updateCurrentlyKnownSystems(pathToModels);
    }

    /**
     * Reads all FeatureSystems by reading all dimacs from one and all csv files from another directory and comparing
     * their filenames.
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
        Set<String> readSystems = Arrays.stream(
                Objects.requireNonNull(modelsDirectory.listFiles())).parallel().filter(File::isDirectory)
                .filter(directory -> {
                    File[] notNullFiles = Objects.requireNonNull(directory.listFiles());
                    boolean only2Files = notNullFiles.length == 2;
                    boolean hasDimacs =
                            Arrays.stream(notNullFiles).parallel().filter(f -> f.getName().endsWith(".dimacs"))
                                    .toArray().length == 1;
                    boolean hasCsv =
                            Arrays.stream(notNullFiles).parallel().filter(f -> f.getName().endsWith(".csv"))
                                    .toArray().length == 1;
                    return hasDimacs & hasCsv & only2Files;
                }).map(File::getName).collect(Collectors.toSet());

        systemCache.setCurrentlyKnownSystems(readSystems);
    }
}
