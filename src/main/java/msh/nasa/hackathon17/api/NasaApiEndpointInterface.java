package msh.nasa.hackathon17.api;

import msh.nasa.hackathon17.model.WayResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author mohsen_shahini on 4/29/17.
 */

public interface NasaApiEndpointInterface {

    @GET("/api/way")
    Call<WayResponse> getWay(
            @Query("source") String source,
            @Query("destination") String destination,
            @Query("time") String time);

}
