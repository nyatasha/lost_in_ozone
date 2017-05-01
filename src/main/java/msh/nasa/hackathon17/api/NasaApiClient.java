package msh.nasa.hackathon17.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author mohsen_shahini on 4/29/17.
 */

public class NasaApiClient {

    private static final String BASE_URL = "http://api.ourbackend.com/";
    private Retrofit retrofit;

    public NasaApiClient() {
        if (retrofit == null) {
            final Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
    }

    public NasaApiEndpointInterface getWeatherService() {
        return retrofit.create(NasaApiEndpointInterface.class);
    }
}
