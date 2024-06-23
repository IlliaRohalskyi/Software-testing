package org.WeatherSkill;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.WeatherServices.JSONService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class WeatherSkillTest {

    private static final String API_KEY = "27d61dcaf4d1425dc27b0fe269be7c57";

    @Mock
    private JSONService jsonService;

    @InjectMocks
    private WeatherSkill weatherSkill;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherSkill = new WeatherSkill(API_KEY);
        weatherSkill.setJsonService(jsonService);
    }

    @Test
    void testCallWeatherApi() throws SocketTimeoutException {
        String city = "Würzburg";
        String mockResponse = "{\"weather\": [{\"description\": \"clear sky\"}]}";

        when(jsonService.getJSONRequestResult(anyString())).thenReturn(mockResponse);

        String result = weatherSkill.callWeatherApi(city);

        assertEquals(mockResponse, result);
        verify(jsonService, times(1)).getJSONRequestResult(anyString());
    }

    @Test
    void testCallWeatherApi_TimeoutException() throws SocketTimeoutException {

        String city = "Berlin";

        when(jsonService.getJSONRequestResult(anyString())).thenThrow(new SocketTimeoutException("Timeout"));

        SocketTimeoutException exception = assertThrows(SocketTimeoutException.class, () -> {
            weatherSkill.callWeatherApi(city);
        });

        assertEquals("Timeout", exception.getMessage());
        verify(jsonService, times(1)).getJSONRequestResult(anyString());
    }

    @Test
    void testCallWeatherApiForecast_Success() throws Exception {

        double lat = 52.52;
        double lon = 13.405;
        String apiResponse = "{\"current\":{\"temp\":25.5}}";

        when(jsonService.getJSONRequestResult(anyString())).thenReturn(apiResponse);


        Method method = WeatherSkill.class.getDeclaredMethod("callWeatherApiForecast", double.class, double.class);
        method.setAccessible(true);


        String result = (String) method.invoke(weatherSkill, lat, lon);


        assertEquals(apiResponse, result);
        verify(jsonService, times(1)).getJSONRequestResult(anyString());
    }

    @Test
    void testCallWeatherApiForecast_TimeoutException() throws Exception {

        double lat = 52.52;
        double lon = 13.405;


        when(jsonService.getJSONRequestResult(anyString())).thenThrow(new SocketTimeoutException("Timeout"));


        Method method = WeatherSkill.class.getDeclaredMethod("callWeatherApiForecast", double.class, double.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(weatherSkill, lat, lon);
        });

        assertEquals(SocketTimeoutException.class, exception.getCause().getClass());
        assertEquals("Timeout", exception.getCause().getMessage());
        verify(jsonService, times(1)).getJSONRequestResult(anyString());
    }

    @Test
    void testCallWeatherApiForecastForCity_Success() throws Exception {
        String city = "Berlin";
        String apiResponse = "{\"coord\": {\"lon\": 13.405, \"lat\": 52.52}, \"weather\": [{\"description\": \"clear sky\"}]}";
        String forecastResponse = "{\"current\":{\"temp\":25.5}}";

        when(jsonService.getJSONRequestResult(anyString())).thenReturn(apiResponse);
        when(jsonService.getJSONRequestResult(contains("onecall"))).thenReturn(forecastResponse);

        Method method = WeatherSkill.class.getDeclaredMethod("callWeatherApiForecastForCity", String.class);
        method.setAccessible(true);


        String result = (String) method.invoke(weatherSkill, city);


        assertEquals(forecastResponse, result);
        verify(jsonService, times(2)).getJSONRequestResult(anyString());
    }


    @Test
    void testCallWeatherApiForecastForCity_TimeoutException() throws Exception {
        String city = "Berlin";

        when(jsonService.getJSONRequestResult(anyString())).thenThrow(new SocketTimeoutException("Timeout"));

        Method method = WeatherSkill.class.getDeclaredMethod("callWeatherApiForecastForCity", String.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(weatherSkill, city);
        });

        assertEquals(SocketTimeoutException.class, exception.getCause().getClass());
        assertEquals("Timeout", exception.getCause().getMessage());
        verify(jsonService, times(1)).getJSONRequestResult(anyString());
    }


}
