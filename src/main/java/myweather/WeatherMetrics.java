package myweather;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.MySSLSocketFactory;
import myweather.openweather.*;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import timely.client.tcp.TcpClient;
import timely.model.Metric;
import timely.model.Tag;
import java.io.IOException;
import java.lang.System;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherMetrics {

    static List<CityRequest> cities = new ArrayList<>();
    static final String apiKey = "4b6e32316a64eee3fde1c8c1f629b8e6";
    static private String latLon = "lat=39.0&lon=-76.6";
    static private int count = 50;
    static private ObjectMapper objectMapper = new ObjectMapper();

    static {
        cities.add(new CityRequest("Crownsville", "Maryland", "United States", "North America"));
        cities.add(new CityRequest("Annapolis", "Maryland", "United States", "North America"));
        cities.add(new CityRequest("Washington D.C.", "District of Columbia", "United States", "North America"));
        cities.add(new CityRequest("Memphis", "Tennessee", "United States", "North America"));
        cities.add(new CityRequest("Miami", "Florida", "United States", "North America"));
        cities.add(new CityRequest("Los Angeles", "California", "United States", "North America"));
        cities.add(new CityRequest("Seattle", "Washington", "United States", "North America"));
        cities.add(new CityRequest("Houston", "Texas", "United States", "North America"));
        cities.add(new CityRequest("New York", "New York", "United States", "North America"));
        cities.add(new CityRequest("Boston", "Massachusetts", "United States", "North America"));
        cities.add(new CityRequest("London", null, "United Kingdom", "Europe"));
        cities.add(new CityRequest("Sydney", null, "Australia", "Australia"));
    }

    public static City getWeatherForCity(String city) {

        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlString = "http://api.openweathermap.org/data/2.5/weather?units=imperial&q=" + encodedCity + "&appid=" + apiKey;

            URL url = new URL(urlString);
            return objectMapper.readValue(url.openStream(), City.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FindResponse getWeatherSurroundingCities() {

        try {
            String urlString = "http://api.openweathermap.org/data/2.5/find?units=imperial&" + latLon + "&cnt=" + count + "&appid=" + apiKey;

            URL url = new URL(urlString);
            return objectMapper.readValue(url.openStream(), FindResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendWeatherDataToTimely(CloseableHttpClient client, City city, List<Tag> tags,  long timestamp) {

        Float temp = city.getMain().getTemp();
        if (temp != null) {
            sendViaHttp(client, "weather.temp.actual", timestamp, temp, tags);
        }

        Float feelsLike = city.getMain().getFeels_like();
        if (feelsLike != null) {
            sendViaHttp(client,"weather.temp.feelslike", timestamp, feelsLike, tags);
        }

        Float pressure = city.getMain().getPressure();
        if (pressure != null) {
            sendViaHttp(client,"weather.pressure", timestamp, pressure, tags);
        }

        Float humidity = city.getMain().getHumidity();
        if (humidity != null) {
            sendViaHttp(client,"weather.humidity", timestamp, humidity, tags);
        }

        Wind wind = city.getWind();
        if (wind != null) {
            if (wind.getSpeed() == null) {
                sendViaHttp(client,"weather.wind.speed", timestamp, 0, tags);
            } else {
                sendViaHttp(client,"weather.wind.speed", timestamp, wind.getSpeed(), tags);
            }
            if (wind.getGust() != null) {
                sendViaHttp(client,"weather.wind.gust", timestamp, wind.getGust(), tags);
            }
            if (wind.getDeg() != null) {
                sendViaHttp(client,"weather.wind.degrees", timestamp, wind.getDeg(), tags);
            }
        }

        Long sunrise = city.getSystem().getSunrise();
        if (sunrise != null) {
            sendViaHttp(client,"weather.sunrise", timestamp, sunrise, tags);
        }

        Long sunset = city.getSystem().getSunset();
        if (sunset != null) {
            sendViaHttp(client,"weather.sunset", timestamp, sunset, tags);
        }

        Long visibility = city.getVisibility();
        if (visibility != null) {
            sendViaHttp(client,"weather.visibility", timestamp, visibility, tags);
        }

        Rain rain = city.getRain();
        if (rain != null) {
            if (rain.getOneHour() != null) {
                sendViaHttp(client,"weather.rainOneHour", timestamp, rain.getOneHour(), tags);
            }
        }

        Snow snow = city.getSnow();
        if (snow != null) {
            if (snow.getOneHour() != null) {
                sendViaHttp(client,"weather.snowOneHour", timestamp, snow.getOneHour(), tags);
            }
        }
    }

    public static void sendViaHttp(CloseableHttpClient client, String name, long timestamp, double value, List<Tag> tags) {

        try {
            Metric metric = new Metric(name, timestamp, value, tags);
            ObjectMapper mapper = new ObjectMapper();
            HttpPost request = new HttpPost("https://localhost:4243/api/put");
            request.setEntity(new StringEntity(mapper.writeValueAsString(metric), ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = client.execute(request);
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void cities(List<CityRequest> cities) {

        try (CloseableHttpClient client = getNewHttpClient()) {
            while (true) {
                long now = System.currentTimeMillis();
                for (CityRequest cityRequest : cities) {
                    City c = getWeatherForCity(cityRequest.getCity());
                    System.out.println(c.getName() + " temp:" + c.getMain().getTemp() + " feels like:" + c.getMain().getFeels_like());
                    sendWeatherDataToTimely(client, c, cityRequest.getTags(),  now);
                }
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static void main(String[] args) {

//      surroundingCities();
        cities(cities);

    }


}
