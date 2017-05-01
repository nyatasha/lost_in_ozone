package msh.nasa.hackathon17;

import android.app.Application;

import msh.nasa.hackathon17.api.NasaApiClient;
import msh.nasa.hackathon17.api.NasaApiEndpointInterface;

/**
 * @author mohsen_shahini on 4/29/17.
 */

public class NasaApp extends Application {

    public static final boolean IS_LOCALE = true;
    private static NasaApiEndpointInterface sApiService;

    @Override
    public void onCreate() {
        super.onCreate();

        initApiClient();
    }

    public static NasaApiEndpointInterface getNasaApi() {
        return sApiService;
    }

    private void initApiClient() {
        NasaApiClient nasaApiClient = new NasaApiClient();
        sApiService = nasaApiClient.getWeatherService();
    }
}
