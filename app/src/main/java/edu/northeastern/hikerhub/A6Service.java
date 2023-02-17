package edu.northeastern.hikerhub;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface A6Service {
    @GET("/")
    Call<CountryList> listCountry(@Query("name") String name);
}
