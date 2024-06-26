package org.FileContentReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileContentReaderTest {

    private FileContentReader fileContentReader;
    private String testFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        testFilePath = "src/test/resources/testFile.txt";
        Files.writeString(Path.of(testFilePath), "Test line 1\nTest line 2\nTest line 3"); //comment for CI demo
        fileContentReader = new FileContentReader(testFilePath);
    }

    @Test
    public void testReadContent() {
        try {
            Method readContentMethod = FileContentReader.class.getDeclaredMethod("readContent");
            readContentMethod.setAccessible(true);

            List<String> result = (List<String>) readContentMethod.invoke(fileContentReader);

            assertEquals("The returned list should have the correct number of lines", 3, result.size());
            assertEquals("The first line should match", "Test line 1", result.get(0));
            assertEquals("The second line should match", "Test line 2", result.get(1));
            assertEquals("The third line should match", "Test line 3", result.get(2));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail("Reflection to access readContent method failed: " + e.getMessage());
        }
    }

    @Test
    public void testGetRandomResponseFromFile() {
        String response = fileContentReader.getRandomResponseFromFile(fileContentReader);
        assertNotNull("The response should not be null", response);
        assertTrue("The response should not be empty", !response.isEmpty());
    }
}