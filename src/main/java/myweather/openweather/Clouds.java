package myweather.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Clouds {

    @JsonProperty("all")
    private Float percent;

    public Float getPercent() {
        return percent;
    }
}
