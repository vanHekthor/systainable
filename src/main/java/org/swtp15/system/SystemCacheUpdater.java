package org.swtp15.system;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swtp15.models.FeatureModel;
import org.swtp15.models.FeatureSystem;
import org.swtp15.models.PerformanceInfluenceModel;
import org.swtp15.parser.FeatureModelParser;
import org.swtp15.parser.PerformanceModelParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SystemCacheUpdater {

    @Getter
    private final SystemCache systemCache;

    @Setter
    private Set<FeatureSystem> permanentSystems;

    /**
     * The constructor.
     *
     * @param systemCache the {@link SystemCache} to be updated.
     */
    @Autowired
    public SystemCacheUpdater(SystemCache systemCache) {
        this.systemCache = systemCache;
        systemCache.setSystemCacheUpdater(this);
    }

    /**
     * Initializes the {@link SystemCache}.
     * <p>
     * This method scans both the internal used path of the models as well as `$PWD/modelFiles` to search for valid
     * models.
     */
    public void initialize() {
        try {
            readSystemsFromDirectory(new File("modelFiles").getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ToDo: here a method should be started that regularly updates the SystemCache
    }

    /**
     * Reads systems from files
     * <p>
     * Reads by going through all subdirectories of the provided paths and checks whether they contain both a valid
     * dimacs, csv and optionally a xml, compiling a {@link FeatureSystem} for each found.
     *
     * @param paths All directories to scan subdirectories from
     *
     * @throws IllegalArgumentException No subdirectories found or invalid model files
     */
    public void readSystemsFromDirectory(String... paths) throws IllegalArgumentException {
        List<File> foundDirestories = Arrays.stream(paths).map(File::new).filter(File::isDirectory).collect(
                Collectors.toList());
        Map<String, FeatureSystem> systemMap = new HashMap<>();

        Thread[] locatorThreads = new Thread[foundDirestories.size()];

        for (int i = 0; i < foundDirestories.size(); i++) {
            File dir = foundDirestories.get(i);
            locatorThreads[i] = new Thread(() -> {
                Set<Map<String, File>> readSystems = Arrays.stream(
                        Objects.requireNonNull(dir.listFiles())).parallel()
                        .filter(File::isDirectory).map(directory -> {
                            File[] notNullFiles = Objects.requireNonNull(directory.listFiles());
                            List<File> dimacsFiles = Arrays.stream(notNullFiles)
                                    .filter(f -> f.getName().endsWith(".dimacs")).collect(Collectors.toList());
                            List<File> xmlFiles = Arrays.stream(notNullFiles).filter(f -> f.getName().endsWith(".xml"))
                                    .collect(Collectors.toList());
                            List<File> csvFiles = Arrays.stream(notNullFiles).filter(f -> f.getName().endsWith(".csv"))
                                    .collect(Collectors.toList());

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
                    try {
                        FeatureModel featureModel = FeatureModelParser
                                .parseModel(systemFileNames.get("dimacs").getPath(),
                                            xmlEntry == null ? null :
                                            xmlEntry.getPath(), false);
                        PerformanceInfluenceModel pIModel = PerformanceModelParser
                                .parseModel(systemFileNames.get("csv").getPath(), featureModel.getFeatures(), false);

                        FeatureSystem system = new FeatureSystem(systemFileNames.get("name").getName(), featureModel,
                                                                 pIModel);

                        systemMap.put(system.getName(), system);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Arrays.stream(locatorThreads).forEach(Thread::run);
        Arrays.stream(locatorThreads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        if (permanentSystems != null) {
            systemMap.putAll(permanentSystems.parallelStream().collect(Collectors.toMap(FeatureSystem::getName,
                                                                                        system -> system)));
        }

        this.systemCache.setCurrentlyKnownSystems(systemMap);
    }
}
