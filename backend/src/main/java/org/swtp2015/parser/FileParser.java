package org.swtp2015.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for a FileParser
 */
public abstract class FileParser {

    /**
     * Reads a file
     * @param filename file to be read
     * @return List of lines as strings
     */
    protected final List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try(FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr))
        {
            String line = br.readLine();
            while(line != null){
                lines.add(line);
                line = br.readLine();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return lines;
    }
}
