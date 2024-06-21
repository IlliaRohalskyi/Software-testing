package org.WeatherSkill;

import org.FileContentReader.FileContentReader;
import org.WeatherServices.JSONParserWeatherApp;
import org.WeatherServices.JSONService;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherSkill {
    private String API;
    private final String defaultCity = "Würzburg";
    private JSONService jsonService = new JSONService();
    private JSONParserWeatherApp jsonParser;

    private String replacePhrase = "[replaceA]";
    private String replacePhraseB = "[replaceB]";
    private String replacePhraseC = "[replaceC]";
    private String replacePhraseOrt = "[replaceOrt]";

    private String baseUrl;
    private Calendar calendar;

    public WeatherSkill(final String apiKey) {
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.baseUrl = "http://api.openweathermap.org";
        this.API = apiKey;
    }

    public WeatherSkill(final String apiKey, final String url) {
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.baseUrl = url;
        this.API = apiKey;
    }

    public WeatherSkill(final String apiKey, final String url, final Calendar calendar) {
        this.calendar = calendar;
        this.baseUrl = url;
        this.API = apiKey;
    }

    /**
     * HTTP error code : xxx
     *
     * @throws SocketTimeoutException
     *
     */
    public String callWeatherApi(String city) throws SocketTimeoutException {
        city = encodeValue(city);
        String apicall = baseUrl + "/data/2.5/weather?q=" + ( city ).trim() + "&appid=" + API + "&lang=en&units=Metric";
        String weatherResult = jsonService.getJSONRequestResult(apicall);
        if (weatherResult.contains("error")) {
            return weatherResult.substring(weatherResult.length() - 3);
        }
        jsonParser = new JSONParserWeatherApp(weatherResult);
        return weatherResult;
    }

    private String callWeatherApiForecast(double lat, double lon) throws SocketTimeoutException {
        String apicall = baseUrl + "/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + API
                + "&lang=en&units=Metric";
        return jsonService.getJSONRequestResult(apicall);
    }

    private String callWeatherApiForecastForCity(String city) throws SocketTimeoutException {
        String result = callWeatherApi(city);
        if (result.equals("404") || result.equals("400") || result.equals("401")) {
            return result;
        }
        String foreCastResult = callWeatherApiForecast(getLatitude(), getLontitude());
        jsonParser = new JSONParserWeatherApp(foreCastResult);
        return foreCastResult;
    }

    public String handle(String spokenPhrase) throws SocketTimeoutException {
        spokenPhrase = spokenPhrase.toLowerCase();
        String response = handleResults(spokenPhrase);
        if (response == null) {
            response = "Unfortunately I don't have an answer to this question.";
        }
        return response;
    }

    private String handleResults(String spokenPhrase) throws SocketTimeoutException {

        String wetterResult = handleWeather(spokenPhrase);
        if (wetterResult != null) {
            return wetterResult;
        }

        String wetterInResult = handleWeatherIn(spokenPhrase);
        if (wetterInResult != null) {
            return wetterInResult;
        }

        String wetterSunRiseResult = handleSunrise(spokenPhrase);
        if (wetterSunRiseResult != null) {
            return wetterSunRiseResult;
        }
        String wetterSunRiseInResult = handleSunriseIn(spokenPhrase);
        if (wetterSunRiseInResult != null) {
            return wetterSunRiseInResult;
        }
        String wetterSunSetResult = handleSunset(spokenPhrase);
        if (wetterSunSetResult != null) {
            return wetterSunSetResult;
        }
        String wetterSunSetInResult = handleSunsetIn(spokenPhrase);
        if (wetterSunSetInResult != null) {
            return wetterSunSetInResult;
        }

        String result = specialWeather(spokenPhrase);
        if (result != null) {
            return result;
        }
        String tempResult = handleTemp(spokenPhrase);
        if (tempResult != null) {
            return tempResult;
        }
        tempResult = handleTempIn(spokenPhrase);
        if (tempResult != null) {
            return tempResult;
        }
        result = handleWeatherUmIn(spokenPhrase);
        if (result != null) {
            return result;
        }
        result = handleWeatherUm(spokenPhrase);
        if (result != null) {
            return result;
        }

        return null;
    }

    private String specialWeather(String spokenPhrase) throws SocketTimeoutException {
        List<String> wetherTypeRain = Arrays.asList(" rain ", "rains");
        String result = handleSpecialWeatherIn(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }
        List<String> wetherTypeSun = Arrays.asList("sun", "sun is shining", "sunshine");
        result = handleSpecialWeatherIn(spokenPhrase, wetherTypeSun);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeSun);
        if (result != null) {
            return result;
        }
        List<String> wetherTypeClouds = Arrays.asList("cloudy", "is cloudy", "is cloudy", "cloudy", "cloudy", "clouds");
        result = handleSpecialWeatherIn(spokenPhrase, wetherTypeClouds);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeClouds);
        if (result != null) {
            return result;
        }
        wetherTypeRain = Arrays.asList("snow", "snowing");
        result = handleSpecialWeatherIn(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }

        return null;
    }

    private String handleWeatherUmIn(String spokenPhrase) throws SocketTimeoutException {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/training/THWS/weather/output/";

        if (spokenPhrase.matches(".*in [0-9]+.*") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return weatherInXHours(spokenPhrase, userDir, outputPath);
        } else if (spokenPhrase.matches(".*at [0-9]+.*") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && ! spokenPhrase
                .contains(" tomorrow ")) {
            return weatherAtXOClock(spokenPhrase, userDir, outputPath);
        } else if (spokenPhrase.matches(".*at [0-9]+.*") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && spokenPhrase
                .contains(" tomorrow ")) {
            return weatherAtXOClockTomorrow(spokenPhrase, userDir, outputPath);
        }
        return null;

    }

    private String weatherAtXOClockTomorrow(String spokenPhrase, String userDir, String outputPath)
            throws SocketTimeoutException {
        String newOrt = getOrtFromPhrase(spokenPhrase);
        int currentHour = getCurrentHour();
        int hour = getHourFromPhraseUm(spokenPhrase);
        if (hour == - 1) {
            return "Only a 48 hour forecast is possible.";
        }

        int calcHour = hour + 24 - currentHour;

        String result = callWeatherApiForecastForCity(newOrt);
        if (result.equals("404")) {
            return "I dont know " + newOrt;
        }
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterMorgenUmUhr.output.txt");

        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Integer.toString(hour)).replace(replacePhraseB,
                getHourlyWetherResult(calcHour)).replace(replacePhraseOrt, newOrt);
    }

    private String weatherAtXOClock(String spokenPhrase, String userDir, String outputPath) throws SocketTimeoutException {
        String newOrt = getOrtFromPhrase(spokenPhrase);
        int currentHour = getCurrentHour();
        int hour = getHourFromPhraseUm(spokenPhrase);
        if (hour == - 1) {
            return "Only a 48 hour forecast is possible.";
        }
        if (hour < currentHour) {
            return hour + " is already over.";
        }

        int calcHour = hour - currentHour;

        String result = callWeatherApiForecastForCity(newOrt);
        if (result.equals("404")) {
            return "Unfortunately I don't know " + newOrt;
        }

        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterUmUhr.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Integer.toString(hour)).replace(replacePhraseB,
                getHourlyWetherResult(calcHour)).replace(replacePhraseOrt, newOrt);
    }

    private String weatherInXHours(String spokenPhrase, String userDir, String outputPath) throws SocketTimeoutException {
        String newOrt = getOrtFromPhrase(spokenPhrase);
        int hour = getHourFromPhraseIn(spokenPhrase);
        if (hour == - 1) {
            return "Only a 48 hour forecast is possible.";
        }
        String result = callWeatherApiForecastForCity(newOrt);
        if (result.equals("404")) {
            return "I dont know " + newOrt;
        }

        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterInStunden.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Integer.toString(hour) + " hours").replace(
                replacePhraseB, getHourlyWetherResult(hour)).replace(replacePhraseOrt, newOrt);
    }

    private int getHourFromPhraseIn(String spokenPhrase) {
        Pattern inHourPattern = Pattern.compile(".* in ([0-9]+) .*");
        return getHourFromPhrase(spokenPhrase, inHourPattern);
    }

    private int getHourFromPhraseUm(String spokenPhrase) {
        Pattern inHourPattern = Pattern.compile(".* at ([0-9]+) .*");
        return getHourFromPhrase(spokenPhrase, inHourPattern);
    }

    private int getHourFromPhrase(String spokenPhrase, Pattern inHourPattern) {
        Matcher m = inHourPattern.matcher(spokenPhrase);
        if (m.find()) {
            String hour = m.group(1);
            int iHour = Integer.parseInt(hour);
            if (iHour > 48) {
                return - 1;
            }
            return iHour;
        }
        return - 1;
    }

    private String handleWeatherUm(String spokenPhrase) throws SocketTimeoutException {
        if ( ! spokenPhrase.contains(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleWeatherUmIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleTemp(String spokenPhrase) throws SocketTimeoutException {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleTempIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleTempIn(String spokenPhrase) throws SocketTimeoutException {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/training/THWS/weather/output/";

        List<String> wethertypes = Arrays.asList("temperature", "degree", "hot", "warm", "cold", "minimum value",
                "maximum value");
        boolean wetterAndIn = doesSpokenPhraseContainWethertype(spokenPhrase, wethertypes);

        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && ! spokenPhrase.contains("tomorrow")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404")) {
                return "I dont know " + newOrt;
            }

            FileContentReader fcr = new FileContentReader(userDir + outputPath + "temperaturHeute.output.txt");
            return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Double.toString(getCurrentTemperature()))
                    .replace(replacePhraseB, Double.toString(getDailyTemperatureMax(1))).replace(replacePhraseC, Double.toString(
                            getDailyTemperatureMin(1))).replace(replacePhraseOrt, newOrt);
        }
        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && spokenPhrase.contains("tomorrow")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404")) {
                return "I dont know " + newOrt;
            }

            FileContentReader fcr = new FileContentReader(userDir + outputPath + "temperaturMorgen.output.txt");
            return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Double.toString(getDailyTemperatureMax(1)))
                    .replace(replacePhraseB, Double.toString(getDailyTemperatureMin(1))).replace(replacePhraseC, Double.toString(
                            getDailyTemperatureMin(1))).replace(replacePhraseOrt, newOrt);
        }
        return null;
    }

    private String handleSpecialWeather(String spokenPhrase, List<String> wethertypes) throws SocketTimeoutException {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleSpecialWeatherIn(spokenPhrase + " in " + defaultCity, wethertypes);
        }
        return null;
    }

    private String handleSpecialWeatherIn(String spokenPhrase, List<String> wethertypes) throws SocketTimeoutException {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/training/THWS/weather/output/";

        boolean wetterAndIn = doesSpokenPhraseContainWethertype(spokenPhrase, wethertypes);

        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && ! spokenPhrase.contains("tomorrow")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404") || result.equals("400")) {
                return "I dont know " + newOrt;
            } else if (result.equals("401")) {
                return "Unauthorized access!";
            }

            int isRain = doesItWetherToday(wethertypes);
            String replaceWithPhrase = wethertypes.get(1);
            if (isRain == 0) {
                return itRainsNow(userDir, outputPath, newOrt, replaceWithPhrase);
            } else if (isRain > 0) {
                return itRainsInTheNextHours(userDir, outputPath, newOrt, isRain, replaceWithPhrase);
            }
            return itDoesntRain(userDir, outputPath, newOrt, replaceWithPhrase);
        }
        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && spokenPhrase.contains("tomorrow")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404") || result.equals("400")) {
                return "I dont know " + newOrt;
            } else if (result.equals("401")) {
                return "Unauthorized access!";
            }

            FileContentReader fcrMorgenIst = new FileContentReader(userDir + outputPath + "wetterTypMorgenIn.output.txt");
            return fcrMorgenIst.getRandomResponseFromFile(fcrMorgenIst).replace(replacePhrase, getDailyWeather(1)).replace(
                    replacePhraseOrt, newOrt);
        }
        return null;
    }

    private boolean doesSpokenPhraseContainWethertype(String spokenPhrase, List<String> wethertypes) {
        for (String wetherType : wethertypes) {
            boolean phraseResult = spokenPhrase.contains(wetherType);
            if (phraseResult) {
                return true;
            }
        }
        return false;
    }

    private String itDoesntRain(String userDir, String outputPath, String newOrt, String wethertype) {
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterTypHeuteIn.nein.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, wethertype).replace(replacePhraseOrt, newOrt);
    }

    private String itRainsInTheNextHours(String userDir, String outputPath, String newOrt, int isRain, String wethertype) {
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterTypHeuteIn.ja.output.txt");
        String stundenPhrase = "";
        stundenPhrase = handleRainPhrase(isRain);
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, wethertype).replace(replacePhraseOrt, newOrt)
                .replace(replacePhraseB, stundenPhrase);
    }

    private String itRainsNow(String userDir, String outputPath, String newOrt, String wethertype) {
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterTypHeuteInJetzt.ja.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, wethertype).replace(replacePhraseOrt, newOrt);
    }

    private String handleRainPhrase(int isRain) {
        String stundenPhrase;
        if (isRain == 1) {
            stundenPhrase = "in the nex hour";
        } else {
            stundenPhrase = "in " + isRain + " hours";
        }
        return stundenPhrase;
    }

    private int doesItWetherToday(List<String> wethertypes) {
        int currentHour = getCurrentHour();
        for (int hourCounter = 0; hourCounter < ( 24 - currentHour ); hourCounter ++ ) {
            String hourlyWether = getHourlyWetherResult(hourCounter).toLowerCase();
            if (doesSpokenPhraseContainWethertype(hourlyWether, wethertypes)) {
                return hourCounter;
            }
        }
        return - 1;
    }

    private int getCurrentHour() {
        SimpleDateFormat formatterHH = new SimpleDateFormat("HH");
        return Integer.parseInt(formatterHH.format(calendar.getTime()));
    }

    private String handleSunriseIn(String spokenPhrase) throws SocketTimeoutException {
        if (spokenPhrase.matches(".*the sun rises [A-Za-zÄÜÖäüö]+.*") || spokenPhrase.matches(
                ".*sunrise [A-Za-zÄÜÖäüö]+.*")) {

            String newOrt = getOrtFromPhrase(spokenPhrase);
            String wetherApiResult = callWeatherApiForecastForCity(newOrt);

            if (wetherApiResult.equals("404") || wetherApiResult.equals("400")) {
                return "This request did not return any data, please try again.";
            } else if (wetherApiResult.equals("401")) {
                return "Unauthorized access!";
            }
            return "The sun rises at " + getSunrise() + " in " + newOrt;
        }
        return null;
    }

    private String handleSunsetIn(String spokenPhrase) throws SocketTimeoutException {
        if (spokenPhrase.matches(".*the sun goes down [A-Za-zÄÜÖäüö]+.*") || spokenPhrase.matches(
                ".*sunset [A-Za-zÄÜÖäüö]+.*")) {

            String newOrt = getOrtFromPhrase(spokenPhrase);
            String wetherApiResult = callWeatherApiForecastForCity(newOrt);

            if (wetherApiResult.equals("404") || wetherApiResult.equals("400")) {
                return "This request did not return any data, please try again.";
            } else if (wetherApiResult.equals("401")) {
                return "Unauthorized access!";
            }
            return "The sun sets at " + getSunset() + " in " + newOrt;
        }
        return null;
    }

    private String handleWeather(String spokenPhrase) throws SocketTimeoutException {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleWeatherIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleSunrise(String spokenPhrase) throws SocketTimeoutException {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleSunriseIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleSunset(String spokenPhrase) throws SocketTimeoutException {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleSunsetIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleWeatherIn(String spokenPhrase) throws SocketTimeoutException {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/training/THWS/weather/output/";

        boolean WetterAndIn = spokenPhrase.contains("weather") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")
                && ! spokenPhrase.matches(".*in [0-9]+.*") && ! spokenPhrase.matches(".*at [0-9]+.*");
        if (WetterAndIn && ! spokenPhrase.contains("tomorrow")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String wetherApiResult = callWeatherApi(newOrt);
            if (wetherApiResult.equals("404") || wetherApiResult.equals("400")) {
                return "This request did not return any data, please try again.";
            } else if (wetherApiResult.equals("401")) {
                return "Unauthorized access!";
            }
            FileContentReader fcrHeuteIst = new FileContentReader(userDir + outputPath + "wetterHeute.output.txt");
            return fcrHeuteIst.getRandomResponseFromFile(fcrHeuteIst).replace(replacePhrase, getCurrentWeather()).replace(
                    replacePhraseOrt, newOrt);
        }
        if (WetterAndIn && spokenPhrase.contains("tomorrow")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404") || result.equals("400")) {
                return "I dont know " + newOrt;
            } else if (result.equals("401")) {
                return "Unauthorized access!";
            }

            FileContentReader fcrMorgenIst = new FileContentReader(userDir + outputPath + "wetterMorgen.output.txt");
            return fcrMorgenIst.getRandomResponseFromFile(fcrMorgenIst).replace(replacePhrase, getDailyWeather(1)).replace(
                    replacePhraseOrt, newOrt);
        }
        return null;
    }

    private String getOrtFromPhrase(String spokenPhrase) {
        spokenPhrase = spokenPhrase.replace("tomorrow", "").trim();

        Pattern pattern = Pattern.compile("(in )([A-Za-züöäÜÖÄ ]+)");
        Matcher matcher = pattern.matcher(spokenPhrase);
        matcher.find();
        spokenPhrase = matcher.group(2).replace("in ", "");
        return spokenPhrase;
    }

    private double getLontitude() {
        return jsonParser.parseWetherResultsAsDouble("coord", "lon");
    }

    private double getLatitude() {
        return jsonParser.parseWetherResultsAsDouble("coord", "lat");
    }

    private String getDailyWeather(int day) {
        return jsonParser.getDailyWetherResult(day);
    }

    private String getHourlyWetherResult(int hour) {
        return jsonParser.getHourlyWetherResult(hour);
    }

    private double getDailyTemperatureMin(int day) {
        return jsonParser.getDailyDataDouble(day, "temp", "min");
    }

    private double getDailyTemperatureMax(int day) {
        return jsonParser.getDailyDataDouble(day, "temp", "max");
    }

    private double getCurrentTemperature() {
        return jsonParser.parseWetherResultsAsDouble("current", "temp");
    }

    public String getCurrentWeather() {
        return jsonParser.parseWetherResultsAsArray("weather", "description");
    }

    private String getSunrise() {
        return formatResultTimeStampToHourMinute(jsonParser.parseWetherResultsAsLong("current", "sunrise"));
    }

    private String getSunset() {
        return formatResultTimeStampToHourMinute(jsonParser.parseWetherResultsAsLong("current", "sunset"));
    }

    private String formatResultTimeStampToHourMinute(long unitTimeStamp) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return df.format(unitTimeStamp * 1000).replace(":", " uhr ");
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }
        return value;
    }
}
