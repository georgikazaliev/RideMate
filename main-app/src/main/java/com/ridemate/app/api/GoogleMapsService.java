package com.ridemate.app.api;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.ridemate.app.exceptions.GoogleMapsException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapsService {

    private final GeoApiContext geoApiContext;

    public GoogleMapsService(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    public GeocodingResult[] getGeocoding(String address) {
        try {
            return GeocodingApi.geocode(geoApiContext, address).await();
        } catch (ApiException | InterruptedException | IOException e) {
            throw new GoogleMapsException("Error while geocoding address: " + address, e);
        }
    }
}
