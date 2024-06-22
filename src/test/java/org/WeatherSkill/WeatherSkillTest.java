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

import java.net.SocketTimeoutException;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherSkillTest {

    private static final String API_KEY = "27d61dcaf4d1425dc27b0fe269be7c57";
    private static final String baseUrl = "https://api.openweathermap.org";

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

}
