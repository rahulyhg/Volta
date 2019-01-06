package com.jmjsolution.solarup.interfaces;

import com.jmjsolution.solarup.model.Solar;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataSolarService {

    @GET("pvwatts/v6.json?api_key=2SL7xTODzFJXbEXT45aIRzkwTJpYZuq6TiuI0Q4J&dataset=intl")
    Call<Solar> getSolarData(@Query("lat") double lat,
                             @Query("lon") double lon,
                             @Query("system_capacity") double systemCap,
                             @Query("module_type") int modType,
                             @Query("losses") double losses,
                             @Query("array_type") int arrayType,
                             @Query("tilt") double tilt,
                             @Query("azimuth") double azimuth);
}
