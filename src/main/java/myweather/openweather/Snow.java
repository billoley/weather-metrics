package myweather.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Snow {

    @JsonProperty("1h")
    private Float oneHour;

    @JsonProperty("3h")
    private Float threeHour;

    public Float getOneHour() {
        return oneHour;
    }

    public Float getThreeHour() {
        return threeHour;
    }
}