package edu.northeastern.hikerhub;

public class Country {
    private String country_id;
    private Double probability;

    public Country(String country_id, Double probability) {
        this.country_id = country_id;
        this.probability = probability;
    }

    public String getCountry_id() {
        return country_id;
    }


    public Double getProbability() {
        return probability;
    }

}
