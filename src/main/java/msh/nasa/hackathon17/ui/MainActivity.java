package msh.nasa.hackathon17.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import msh.nasa.hackathon17.Constants;
import msh.nasa.hackathon17.NasaApp;
import msh.nasa.hackathon17.R;
import msh.nasa.hackathon17.model.Destination;
import msh.nasa.hackathon17.model.Path;
import msh.nasa.hackathon17.model.Point;
import msh.nasa.hackathon17.model.Source;
import msh.nasa.hackathon17.model.WayResponse;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private EditText sourceEt;
    private EditText destinationEt;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        sourceEt = (EditText) findViewById(R.id.source_et);
        destinationEt = (EditText) findViewById(R.id.destination_et);
        Button searchBtn = (Button) findViewById(R.id.search_bt);

        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String source = sourceEt.getText().toString();
                final String destination = destinationEt.getText().toString();
                if (TextUtils.isEmpty(source) || TextUtils.isEmpty(destination)) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                final WayResponse wayResponse = getWayResponse(source, destination);
                if (wayResponse == null ) {
                    return;
                }

                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.WAY_RESPONSE, wayResponse);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private WayResponse getWayResponse(final String source, final String destination) {
        if (NasaApp.IS_LOCALE) {
            return getWayFromLocale(source, destination);

        } else {
            //TODO CALL NETWORK API
            return null;
        }

    }

    private WayResponse getWayFromLocale(final String source, final String destination) {
        List<String[]> list = readAirports();
        WayResponse wayResponse = null;
        if (list != null) {
            String [] array = findAirport(source, list);
            Source sourceObject = null;
            if (array != null) {
                sourceObject = new Source();
                sourceObject.setName(array[1]);
                sourceObject.setLatitude(array[6]);
                sourceObject.setLongitude(array[7]);
                array = null;
            }


            array = findAirport(destination, list);
            Destination destinationObject = null;
            if (array != null) {
                destinationObject = new Destination();
                destinationObject.setName(array[1]);
                destinationObject.setLatitude(array[6]);
                destinationObject.setLongitude(array[7]);
                array = null;
            }

            if (sourceObject != null && destinationObject != null) {
                wayResponse = new WayResponse();
                wayResponse.setSource(sourceObject);
                wayResponse.setDestination(destinationObject);

                Point point = calcStartPointAtMaxHeight(Double.parseDouble(sourceObject.getLatitude()), Double.parseDouble(sourceObject.getLongitude()),
                        Double.parseDouble(destinationObject.getLatitude()), Double.parseDouble(destinationObject.getLongitude()));

                Point pointMaxHeight = findCoordsAtDistanceFrom(Double.parseDouble(point.getLatitude()), Double.parseDouble(point.getLongitude()),
                        Double.parseDouble(destinationObject.getLatitude()), Double.parseDouble(destinationObject.getLongitude()), 1000);
                List<Path> paths = new ArrayList<>();
                Path path = new Path();
                path.setPoint(pointMaxHeight);
                paths.add(path);
                wayResponse.setPath(paths);
            }
        }

        return wayResponse;
    }

    private String[] findAirport(final String airport, final List<String[]> list) {
        for(String [] array : list) {
            if (array[1].contains(airport) || array[2].contains(airport)) {
                return array;
            }
        }

        return null;
    }

    private List<String[]> readAirports() {
        String next[] = {};
        List<String[]> list = new ArrayList<String[]>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("airports.csv")));
            while(true) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next[0].split("\\;"));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //calculateFlight path equation (on max height)
    private Point calcEquation(double latA, double longA, double latB, double longB) {
        //height = 9.754;
        Point point = new Point();
        double[] coeff = new double[2];
        point.setLatitude(String.valueOf((longB - longA)/(latB - latA)));
        point.setLongitude(String.valueOf(-latA * longB -longA * (latB - latA)));
        return point;
    }

    //calculate start point on max height
    private Point calcStartPointAtMaxHeight(double latA, double longA, double latB, double longB){
        double height_max = 10;
        double v_takeoff = 220;
        double time_takeoff = 0.13;
        double distance_takeoff = Math.sqrt(Math.pow(v_takeoff*time_takeoff,2) - Math.pow(height_max,2));
        return findCoordsAtDistanceFrom(latA,longA, latB, longB, distance_takeoff);
    }

    //calculate new coordinates at distance from start point on the ground
    private Point findCoordsAtDistanceFrom(double lat1, double lon1, double lat2fin, double lon2fin, double distance){
        double R = 6372.795; //#Radius of the Earth
        double brng = Math.toRadians(getAzimuth(lat1,lon1,lat2fin,lon2fin)); //#Bearing is 90 degrees converted to radians.
        double d = distance; //#Distance in km

        double lat1_rad = Math.toRadians(lat1); //#Current lat point converted to radians
        double lon1_rad = Math.toRadians(lon1); //#Current long point converted to radians

        double lat2_rad = Math.asin( Math.sin(lat1_rad)*Math.cos(d/R) + Math.cos(lat1_rad)*Math.sin(d/R)*Math.cos(brng));

        double lon2_rad = lon1_rad + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(lat1_rad),
                Math.cos(d/R)-Math.sin(lat1_rad)*Math.sin(lat2_rad));

        double lat2 = Math.toDegrees(lat2_rad);
        double lon2= Math.toDegrees(lon2_rad);
        Point point = new Point();
        point.setLatitude(String.valueOf(lat2));
        point.setLongitude(String.valueOf(lon2));
        return point;
    }

    private double bearing(double lat1, double lon1, double lat2, double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    private double getAzimuth(double latA, double longA, double latB, double longB)
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
