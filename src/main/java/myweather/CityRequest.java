package myweather;

import timely.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class CityRequest {

    private String city;
    private String state;
    private String country;
    private String continent;

    public CityRequest(String name, String state, String country, String continent) {
        this.city = name;
        this.state = state;
        this.country = country;
        this.continent = continent;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public List<Tag> getTags() {
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("city", this.city));
        if (this.state != null) {
            tagList.add(new Tag("state", this.state));
        }
        if (this.country != null) {
            tagList.add(new Tag("country", this.country));
        }
        if (this.continent != null) {
            tagList.add(new Tag("continent", this.continent));
        }
        return tagList;
    }
}
