package myweather.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class City {
    @JsonProperty("coord")
    private Coordinates coordinates;

    @JsonProperty("weather")
    private Weather[] weather;

    private String base;

    @JsonProperty("main")
    private Main main;

    private Long visibility;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("rain")
    private Rain rain;

    @JsonProperty("snow")
    private Snow snow;

    @JsonProperty("clouds")
    private Clouds clouds;

    private Long dt;

    @JsonProperty("sys")
    private System system;

    private Long timezone;

    private Long id;

    private String name;

    private Integer cod;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public Long getVisibility() {
        return visibility;
    }

    public Rain getRain() {
        return rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Long getDt() {
        return dt;
    }

    public System getSystem() {
        return system;
    }

    public Long getTimezone() {
        return timezone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCod() {
        return cod;
    }
}
