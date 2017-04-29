package hello;

import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Natallia on 28.04.2017.
 */
public class DataController {
//    private double[] startMaxHeight;
//    private double[] finalMaxHeight;

    //Calculating flight parameters
    public void calcFlight(String departure, String arrival) throws IOException {
        String[] arrivalCoords = getAirportCoords(arrival);
        String[] departureCoords = getAirportCoords(departure);

        double directDistance = calculateDirectDistance(Double.parseDouble(departureCoords[0]),Double.parseDouble(departureCoords[1]),
                Double.parseDouble(arrivalCoords[0]),Double.parseDouble(arrivalCoords[1]));

        //point where max height was reached
        double[] startMaxHeight = calcStartPointAtMaxHeight(Double.parseDouble(departureCoords[0]),Double.parseDouble(departureCoords[1]));
        //final point where max height was reached
        double[] finalMaxHeight = findCoordsAtDistanceFrom(startMaxHeight[0],startMaxHeight[1],calculateRealDistance(directDistance));

        //here you find point at any distance(km) from start point.Example:
        double[] coords = findCoordsAtDistanceFrom(startMaxHeight[0],startMaxHeight[1],50);
        System.out.println("At distance 50km from startMaxHeight "+coords[0]+" "+coords[1]);
    }

    //calculate flight distance
    public double calculateRealDistance(double directDistance){
        double realDistance = 0;
        double v_max = 900;
        double height_max = 10;
        double v_takeoff = 220;
        double time_takeoff = 0.13;
        double time_landing = 0.42;
        realDistance = directDistance - 2 * Math.sqrt(Math.pow(v_takeoff*time_takeoff,2) - Math.pow(height_max,2));
        double flightDuration = realDistance / v_max + time_takeoff + time_landing;

        System.out.println("directDistance " + directDistance);
        System.out.println("realDistance " + realDistance);
        System.out.println("flightDuration " + flightDuration);

        return realDistance;
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

    //calculateFlight path equation (on max height)
    public double[] calcEquation(double latA, double longA, double latB, double longB) {
        //height = 9.754;
        double[] coeff = new double[2];
        coeff[0] = (longB - longA)/(latB - latA);
        coeff[1] = -latA * longB -longA * (latB - latA);
        //System.out.println(coeff[0]+" "+coeff[1]);
        return coeff;
    }

    //calculate Equation of motion along the ground
    public double findLongtitudeByLatitude(double latA, double longA, double latB, double longB, double lon) {
        return Math.atan(Math.tan(latA) * Math.sin(longB-lon) + Math.tan(latB) * Math.sin(lon - longA) / Math.sin(longB - longA));
    }

    //calculate start point on max height
    public double[] calcStartPointAtMaxHeight(double latA, double longA){
        double height_max = 10;
        double v_takeoff = 220;
        double time_takeoff = 0.13;
        double distance_takeoff = Math.sqrt(Math.pow(v_takeoff*time_takeoff,2) - Math.pow(height_max,2));
        return findCoordsAtDistanceFrom(latA,longA,distance_takeoff);
    }

    //calculate new coordinates at distance from start point on the ground
    public double[] findCoordsAtDistanceFrom(double lat1, double lon1, double distance){
        double R = 6378.1; //#Radius of the Earth
        double brng = 1.57; //#Bearing is 90 degrees converted to radians.
        double d = distance; //#Distance in km

        double lat1_rad = Math.toRadians(lat1); //#Current lat point converted to radians
        double lon1_rad = Math.toRadians(lon1); //#Current long point converted to radians

        double lat2_rad = Math.asin( Math.sin(lat1_rad)*Math.cos(d/R) + Math.cos(lat1_rad)*Math.sin(d/R)*Math.cos(brng));

        double lon2_rad = lon1_rad + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(lat1_rad),
                Math.cos(d/R)-Math.sin(lat1_rad)*Math.sin(lat2_rad));

        double lat2 = Math.toDegrees(lat2_rad);
        double lon2= Math.toDegrees(lon2_rad);
        double[] newCoords = new double[2];
        newCoords[0] = lat2;
        newCoords[1] = lon2;
        return newCoords;
    }
}