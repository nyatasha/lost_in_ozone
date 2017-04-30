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

//        double directDistance = calculateDirectDistance(Double.parseDouble(departureCoords[0]),Double.parseDouble(departureCoords[1]),
//                Double.parseDouble(arrivalCoords[0]),Double.parseDouble(arrivalCoords[1]));

        //point where max height was reached
        double[] startMaxHeight = calcStartPointAtMaxHeight(Double.parseDouble(departureCoords[0]),
                Double.parseDouble(departureCoords[1]),Double.parseDouble(arrivalCoords[0]),Double.parseDouble(arrivalCoords[1]));

        double[] coord = findCoordsAtDistanceFrom(startMaxHeight[0],startMaxHeight[1],Double.parseDouble(arrivalCoords[0]),Double.parseDouble(arrivalCoords[1]),50);

        System.out.println(calculateDirectDistance(startMaxHeight[0],startMaxHeight[1],coord[0],coord[1]));
//        //final point where max height was reached
//        double[] finalMaxHeight = findCoordsAtDistanceFrom(startMaxHeight[0],startMaxHeight[1],calculateRealDistance(directDistance));
//
//        //here you find point at any distance(km) from start point.Example:
//        double[] coords = findCoordsAtDistanceFrom(startMaxHeight[0],startMaxHeight[1],50);
//        System.out.println("At distance 50km from startMaxHeight "+coords[0]+" "+coords[1]);
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
        double flightDuration = realDistance / v_max;// + time_takeoff + time_landing;

        System.out.println("directDistance " + directDistance);
        System.out.println("realDistance " + realDistance);
        System.out.println("flightDuration " + flightDuration);

        return flightDuration;
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
    public double[] calcStartPointAtMaxHeight(double latA, double longA,double latB, double longB){
        double height_max = 10;
        double v_takeoff = 220;
        double time_takeoff = 0.13;
        double distance_takeoff = Math.sqrt(Math.pow(v_takeoff*time_takeoff,2) - Math.pow(height_max,2));
        return findCoordsAtDistanceFrom(latA,longA,latB,longB,distance_takeoff);
    }

    //calculate new coordinates at distance from start point on the ground
    public double[] findCoordsAtDistanceFrom(double lat1, double lon1, double lat2fin, double lon2fin, double distance){
        double R = 6372.795; //#Radius of the Earth
        double brng = bearing(lat1,lon1,lat2fin,lon2fin); //#Bearing is 90 degrees converted to radians.
        double d = distance; //#Distance in km

        double lat1_rad = Math.toRadians(lat1); //#Current lat point converted to radians
        double lon1_rad = Math.toRadians(lon1); //#Current long point converted to radians

        double lat2_rad = Math.asin( Math.sin(lat1_rad)*Math.cos(d/R) + Math.cos(lat1_rad)*Math.sin(d/R)*Math.cos(brng));

        double lon2_rad = lon1_rad + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(lat1_rad),
                Math.cos(d/R)-Math.sin(lat1_rad)*Math.sin(lat2_rad));

        double lat2 = Math.toDegrees(lat2_rad);
        double lon2= Math.toDegrees(lon2_rad);
        double[] newCoords = new double[2];
        newCoords[0] = lat2;//
        newCoords[1] = lon2;//
        return newCoords;
    }
    public double bearing(double lat1, double lon1, double lat2, double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }
    public double[] distanceFrom(double lat1, double lon1, double final_lat2, double final_lon2, double distance){
        double R = 6372.795; //#Radius of the Earth
        double brng = 1.57; //#Bearing is 90 degrees converted to radians.
        double s = distance; //#Distance in km

        double a = 63781370;
        double b =  6356752.31424;
        double f =(a-b)/a;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(final_lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(final_lat2);
        double delta = final_lon2 - lon1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double x = (cl1*sl2) - (sl1*cl2*cdelta);
        double y = sdelta*cl2;
        double z = Math.toDegrees(Math.atan(-y/x));

        if (x < 0)
            z = z+180;

        double z_degree = (z+180) % 360 - 180;
        double z2 = - Math.toRadians(z_degree);
        double anglerad2 = z2 - ((2*Math.PI)*Math.floor((z2/(2*Math.PI))) );
        double azim1 = getAzimuth(lat1, lon1, final_lat2, final_lon2);//(anglerad2*180.)/Math.PI;

        double sinα1 = Math.sin(azim1);
        double cosα1 = Math.cos(azim1);

        double tanU1 = (1-f) * Math.tan(lat1);
        double cosU1 = 1 / Math.sqrt((1 + tanU1*tanU1));
        double sinU1 = tanU1 * cosU1;
        double teta1 = Math.atan2(tanU1, cosα1);
        double sinα = cosU1 * sinα1;
        double cosSqα = 1 - sinα*sinα;
        double uSq = cosSqα * (a*a - b*b) / (b*b);
        double A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
        double B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));

        double teta = s / (b*A), teta123;
        double sinteta;
        double costeta;
        double cos2tetaM;
        do {
            cos2tetaM = Math.cos(2*teta1 + teta);
            sinteta = Math.sin(teta);
            costeta = Math.cos(teta);
            //System.out.println("B "+B);
            double deltateta = B*sinteta*(cos2tetaM+B/4*(costeta*(-1+2*cos2tetaM*cos2tetaM)-
                    B/6*cos2tetaM*(-3+4*sinteta*sinteta)*(-3+4*cos2tetaM*cos2tetaM)));
            //System.out.println("deltateta "+deltateta);
            teta123 = teta;
            teta = s / (b*A) + deltateta;
            //System.out.println("loop " + (teta - teta123));
        } while (Math.abs(teta-teta123) > 1e9);
        double tmp = sinU1*sinteta - cosU1*costeta*cosα1;
        double lat2 = Math.atan2(sinU1*costeta + cosU1*sinteta*cosα1, (1-f)*Math.sqrt(sinα*sinα + tmp*tmp));
        double λ = Math.atan2(sinteta*sinα1, cosU1*costeta - sinU1*sinteta*cosα1);
        double C = f/16*cosSqα*(4+f*(4-3*cosSqα));
        double L = λ - (1-C) * f * sinα *
                (teta + C*sinteta*(cos2tetaM+C*costeta*(-1+2*cos2tetaM*cos2tetaM)));
        double lon2 = (lon1+L+3*Math.PI)%(2*Math.PI) - Math.PI;  // normalise to -180...+180

        double revAz = Math.atan2(sinα, -tmp);
        double[] newCoords = new double[2];
        newCoords[0] = lat2;
        newCoords[1] = lon2;

        return newCoords;
    }
    public double getAzimuth(double latA, double longA, double latB, double longB)
    {
        double longitudinalDifference = longB - longA;
        double latitudinalDifference = latB - latA;
        double azimuth = (Math.PI * .5d) - Math.atan(latitudinalDifference / longitudinalDifference);
        if (longitudinalDifference > 0) return Math.toDegrees(azimuth);
        else if (longitudinalDifference < 0) return Math.toDegrees(azimuth + Math.PI);
        else if (latitudinalDifference < 0) return Math.toDegrees(Math.PI);
        return 0d;
    }
}