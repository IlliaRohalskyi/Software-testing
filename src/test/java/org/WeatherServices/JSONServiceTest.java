package org.WeatherServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class JSONServiceTest {
    private JSONService jsonService;


    @Test
    void testGetJSONRequestResult_Success() throws Exception {
        String testUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid=f535e9282da03950c3311e505a8ef03a";

        jsonService = new JSONService();

        String result = jsonService.getJSONRequestResult(testUrl);

        assertFalse(result.isEmpty());
        assertTrue(result.trim().startsWith("{"));

        assertTrue(result.contains("\"cod\""));
        assertTrue(result.contains("\"list\""));
    }

    @Test
    void testGetJSONRequestResult_HttpError() throws Exception {
        String testUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139";

        jsonService = new JSONService();

        String result = jsonService.getJSONRequestResult(testUrl);

        assertEquals("Fehler : HTTP error code : 401", result);
    }
}
