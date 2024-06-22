package org.WeatherSkill;

import org.WeatherServices.JSONParserWeatherApp;
import org.WeatherServices.JSONService;
import org.FileContentReader.FileContentReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

public class WeatherSkillDataParsingTest {

    private static final String API_KEY = "27d61dcaf4d1425dc27b0fe269be7c57";
    @Mock
    private JSONParserWeatherApp jsonParser;


    @InjectMocks
    private WeatherSkill weatherSkill;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        weatherSkill = new WeatherSkill(API_KEY);
        setJsonParser(weatherSkill, jsonParser);
    }

    private void setJsonParser(WeatherSkill weatherSkill, JSONParserWeatherApp jsonParser) throws Exception {
        Field jsonParserField = WeatherSkill.class.getDeclaredField("jsonParser");
        jsonParserField.setAccessible(true);
        jsonParserField.set(weatherSkill, jsonParser);
    }


    @Test
    void testGetDailyWeather() throws Exception {
        when(jsonParser.getDailyWetherResult(1)).thenReturn("sunny");

        Method method = WeatherSkill.class.getDeclaredMethod("getDailyWeather", int.class);
        method.setAccessible(true);

        String result = (String) method.invoke(weatherSkill, 1);

        assertEquals("sunny", result);
        verify(jsonParser, times(1)).getDailyWetherResult(1);
    }

    @Test
    void testGetHourlyWeatherResult() throws Exception {
        when(jsonParser.getHourlyWetherResult(1)).thenReturn("cloudy");

        Method method = WeatherSkill.class.getDeclaredMethod("getHourlyWetherResult", int.class);
        method.setAccessible(true);

        String result = (String) method.invoke(weatherSkill, 1);

        assertEquals("cloudy", result);
        verify(jsonParser, times(1)).getHourlyWetherResult(1);
    }

    @Test
    void testGetDailyTemperatureMin() throws Exception {
        when(jsonParser.getDailyDataDouble(1, "temp", "min")).thenReturn(15.0);

        Method method = WeatherSkill.class.getDeclaredMethod("getDailyTemperatureMin", int.class);
        method.setAccessible(true);

        double result = (double) method.invoke(weatherSkill, 1);

        assertEquals(15.0, result);
        verify(jsonParser, times(1)).getDailyDataDouble(1, "temp", "min");
    }

    @Test
    void testGetDailyTemperatureMax() throws Exception {
        when(jsonParser.getDailyDataDouble(1, "temp", "max")).thenReturn(30.0);

        Method method = WeatherSkill.class.getDeclaredMethod("getDailyTemperatureMax", int.class);
        method.setAccessible(true);

        double result = (double) method.invoke(weatherSkill, 1);

        assertEquals(30.0, result);
        verify(jsonParser, times(1)).getDailyDataDouble(1, "temp", "max");
    }

    @Test
    void testGetCurrentTemperature() throws Exception {
        when(jsonParser.parseWetherResultsAsDouble("current", "temp")).thenReturn(25.0);

        Method method = WeatherSkill.class.getDeclaredMethod("getCurrentTemperature");
        method.setAccessible(true);

        double result = (double) method.invoke(weatherSkill);

        assertEquals(25.0, result);
        verify(jsonParser, times(1)).parseWetherResultsAsDouble("current", "temp");
    }

    @Test
    void testGetCurrentWeather() throws Exception {
        when(jsonParser.parseWetherResultsAsArray("weather", "description")).thenReturn("clear sky");

        Method method = WeatherSkill.class.getDeclaredMethod("getCurrentWeather");
        method.setAccessible(true);

        String result = (String) method.invoke(weatherSkill);

        assertEquals("clear sky", result);
        verify(jsonParser, times(1)).parseWetherResultsAsArray("weather", "description");
    }

    @Test
    void testGetSunrise() throws Exception {
        when(jsonParser.parseWetherResultsAsLong("current", "sunrise")).thenReturn(1600416000L);

        Method method = WeatherSkill.class.getDeclaredMethod("getSunrise");
        method.setAccessible(true);

        String result = (String) method.invoke(weatherSkill);

        assertEquals("11 uhr 00", result);
        verify(jsonParser, times(1)).parseWetherResultsAsLong("current", "sunrise");
    }

    @Test
    void testGetSunset() throws Exception {
        when(jsonParser.parseWetherResultsAsLong("current", "sunset")).thenReturn(1600459200L);

        Method method = WeatherSkill.class.getDeclaredMethod("getSunset");
        method.setAccessible(true);


        String result = (String) method.invoke(weatherSkill);

        assertEquals("23 uhr 00", result);
        verify(jsonParser, times(1)).parseWetherResultsAsLong("current", "sunset");
    }
}