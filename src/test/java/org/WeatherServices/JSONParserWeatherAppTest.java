package org.WeatherServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JSONParserWeatherAppTest {

    private JSONParserWeatherApp parser;

    @BeforeEach
    public void setUp() {
        String jsonString = "{ \"topElement\": { \"subElement\": 10.5, \"subInt\": 5, \"subLong\": 1000000, \"subString\": \"test\" }, "
            + "\"daily\": [ { \"topterm\": { \"subterm\": 20.0, \"subtermString\": \"20.0 as string\" }, \"weather\": [ { \"description\": \"clear sky\" } ] } ], "
            + "\"hourly\": [ { \"weather\": [ { \"description\": \"partly cloudy\" } ] } ] }";
        parser = new JSONParserWeatherApp(jsonString);
    }

    @Test
    public void testParseWetherResultsAsDouble() {
        double result = parser.parseWetherResultsAsDouble("topElement", "subElement");
        assertEquals(10.5, result, 0.01);
    }

    @Test
    public void testParseWetherResultsAsInt() {
        int result = parser.parseWetherResultsAsInt("topElement", "subInt");
        assertEquals(5, result);
    }

    @Test
    public void testParseWetherResultsAsLong() {
        long result = parser.parseWetherResultsAsLong("topElement", "subLong");
        assertEquals(1000000L, result);
    }

    @Test
    public void testParseWetherResultsAsString() {
        String result = parser.parseWetherResultsAsString("topElement", "subString");
        assertEquals("test", result);
    }

    @Test
    public void testGetDailyDataDouble() {
        double result = parser.getDailyDataDouble(0, "topterm", "subterm");
        assertEquals(20.0, result, 0.01);
    }

    @Test
    public void testGetDailyDataString() {
        String result = parser.getDailyDataString(0, "topterm", "subtermString");
        assertEquals("20.0 as string", result);
    }

    @Test
    public void testGetDailyWetherResult() {
        String result = parser.getDailyWetherResult(0);
        assertEquals("clear sky", result);
    }

    @Test
    public void testGetHourlyWetherResult() {
        String result = parser.getHourlyWetherResult(0);
        assertEquals("partly cloudy", result);
    }
}
