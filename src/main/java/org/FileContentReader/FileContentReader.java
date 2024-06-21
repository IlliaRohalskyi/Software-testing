package org.FileContentReader;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileContentReader {
    private BufferedReader reader;

    public FileContentReader(String file) {
        try {
            reader = new BufferedReader(new FileReader(new File(file)));
        } catch (FileNotFoundException e) {
        }
    }

    public String getRandomResponseFromFile(FileContentReader fcr) {
        ArrayList<String> data = fcr.readContent();
        int randomNumber = ( int ) ( Math.random() * data.size() );
        return data.get(randomNumber);
    }

    private ArrayList<String> readContent() {

        ArrayList<String> data = new ArrayList<String>();
        String line;
        try {
            while ( ( line = reader.readLine() ) != null) {
                data.add(line);
            }
        } catch (IOException e) {
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return data;
    }
}
