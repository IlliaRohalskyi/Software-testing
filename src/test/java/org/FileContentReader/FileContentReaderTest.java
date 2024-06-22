package org.FileContentReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class FileContentReaderTest {

    private FileContentReader fileContentReader;
    private String testFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        testFilePath = "src/test/resources/testFile.txt";
        Files.writeString(Path.of(testFilePath), "Test line 1\nTest line 2\nTest line 3");
        fileContentReader = new FileContentReader(testFilePath);
    }

    @Test
    public void testGetRandomResponseFromFile() {
        String response = fileContentReader.getRandomResponseFromFile(fileContentReader);
        assertNotNull("The response should not be null", response);
        assertTrue("The response should not be empty", !response.isEmpty());
    }
}