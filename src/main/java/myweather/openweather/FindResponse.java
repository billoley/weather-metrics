package myweather.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FindResponse {

    @JsonProperty("list")
    private City[] cities;

    private String message;

    private String cod;

    private Integer count;

    public City[] getCities() {
        return cities;
    }

    public String getMessage() {
        return message;
    }

    public String getCod() {
        return cod;
    }

    public Integer getCount() {
        return count;
    }
}
