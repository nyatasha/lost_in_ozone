package hello;

import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Natallia on 28.04.2017.
 */
public class DataController {

    //Calculating flight parameters
    public void calcFlight(String departure, String arrival) throws IOException {
        String[] arrivalCoords = getAirportCoords(arrival);
        String[] departureCoords = getAirportCoords(departure);

        //direct distance between two airports
        double directDistance = calculateDirectDistance(Double.parseDouble(departureCoords[0]),Double.parseDouble(departureCoords[1]),
                Double.parseDouble(arrivalCoords[0]),Double.parseDouble(arrivalCoords[1]));

        int n = 10;
        double step = (Double.parseDouble(arrivalCoords[1]) - Double.parseDouble(departureCoords[1])) / n;

        //0 row - latitude, 1st row - longtitude, 2nd row - height at this point
        double[][] points = new double[3][n-1];

        for(int j = 0; j < n-1; j++){
            points[1][j] = Double.parseDouble(departureCoords[1]) + (j+1) * step;
            points[0][j] = findLatitudeByLongtitude(Double.parseDouble(departureCoords[0]),Double.parseDouble(departureCoords[1]),
                    Double.parseDouble(arrivalCoords[0]),Double.parseDouble(arrivalCoords[1]), points[1][j]);
            points[2][j] = 10; //km
        }
        points[2][0] = 5; //take off height
        points[2][n-2] = 5; //landing height
    }

    //calculate coordinates of the airport
    public String[] getAirportCoords(String airportName) throws IOException {
        String file = "src/main/resources/static/airports.csv";

        BufferedReader filescan = null;
        try {
            filescan = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String[] coords = new String[2];
        while ((line = filescan.readLine()) != null) {
            if(line.contains(airportName)) {
                List<String> airportInfo = Arrays.asList(line.split(";"));
                if (airportInfo.get(0).contains(airportName) || airportInfo.get(4).equals(airportName)) {
                    coords[0] = airportInfo.get(6);
                    coords[1] = airportInfo.get(7);
                    break;
                }
            }
        }
        return coords;
    }

    //calculate ground track
    public double calculateDirectDistance(double latA, double longA, double latB, double longB) {
        System.out.println("departure "+ latA+ " "+longA);
        System.out.println("arrival "+latB+ " "+longB);

        double EARTH_RADIUS = 6372.795;
        double lat1 = latA * Math.PI / 180;
        double lat2 = latB * Math.PI / 180;
        double long1 = longA * Math.PI / 180;
        double long2 = longB * Math.PI / 180;
        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;

        double ad = Math.atan2(y, x);
        double dist = Math.ceil(ad * EARTH_RADIUS);
        return dist;
    }

    //calculate equation of motion along the ground
    public double findLatitudeByLongtitude(double latA, double longA, double latB, double longB, double lon) {
        return Math.toDegrees(Math.atan((Math.tan(Math.toRadians(latA)) * Math.sin(Math.toRadians(longB-lon))
                + Math.tan(Math.toRadians(latB)) * Math.sin(Math.toRadians(lon - longA)))/ Math.sin(Math.toRadians(longB - longA))));
    }
}