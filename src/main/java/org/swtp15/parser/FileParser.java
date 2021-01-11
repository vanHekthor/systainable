package org.swtp15.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functionality for reading files.
 */
public abstract class FileParser {

    /**
     * Reads a file.
     *
     * @param filename File to be read
     *
     * @return List of lines as strings
     */
    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("File " + filename + " not found");
        }
        return lines;
    }

}
