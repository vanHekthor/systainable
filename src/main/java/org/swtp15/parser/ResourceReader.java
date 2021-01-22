package org.swtp15.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ResourceReader {
    /**
     * Reads an internal resource.
     * <p>
     * This is an alternative to {@link FileParser#readFile(String)}, as that method does not work for internal files if
     * the application is packaged to a jar. This method however can only read internal resources, although it makes it
     * work both for development as well as production.
     *
     * @param filename File to be read. If you want to have `src/main/resources` as root of the path, use a `/` in front
     *                 of the filename.
     *
     * @return List of lines as strings
     */
    public static List<String> readFileFromResources(String filename) {
        InputStream inputStream = ResourceReader.class.getResourceAsStream(filename);
        List<String> lines = new ArrayList<>();
        try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
