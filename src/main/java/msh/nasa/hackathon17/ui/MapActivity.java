package msh.nasa.hackathon17.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

import java.util.Arrays;
import java.util.List;

import msh.nasa.hackathon17.Constants;
import msh.nasa.hackathon17.R;
import msh.nasa.hackathon17.model.WayResponse;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng latLngSource;
    private LatLng latLngDestination;
    private WayResponse wayResponse;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(latLngSource)
                .title(wayResponse.getSource().getName()));
        googleMap.addMarker(new MarkerOptions().position(latLngDestination)
                .title(wayResponse.getDestination().getName()));

        final List<PatternItem> pattern = Arrays.asList(
                new Dash(30), new Gap(5));

        googleMap.addPolyline(new PolylineOptions()
                .add(latLngSource)
                .add(latLngDestination)
                .width(8)
                .zIndex(50)
                .color(Color.BLUE)
                .geodesic(true)
//                .pattern(pattern)
                .startCap(new RoundCap())
                .endCap(new SquareCap())
                .jointType(JointType.ROUND));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngSource));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));

        CircleOptions circleOptions1 = new CircleOptions()
                .center(new LatLng(Double.parseDouble(wayResponse.getPath().get(0).getPoint().getLatitude()),
                        Double.parseDouble(wayResponse.getPath().get(0).getPoint().getLongitude())))
                .radius(50000)
                .fillColor(Color.RED); // In meters

        CircleOptions circleOptions2 = new CircleOptions()
                .center(latLngSource)
                .strokeColor(Color.RED)
                .strokeWidth(5)
                .radius(100000); // In meters

        CircleOptions circleOptions3 = new CircleOptions()
                .center(latLngDestination)
                .strokeColor(Color.GREEN)
                .strokeWidth(5)
                .radius(100000); // In meters

        googleMap.addCircle(circleOptions1);
        googleMap.addCircle(circleOptions2);
        googleMap.addCircle(circleOptions3);

//        final LatLng latLngCenter = new LatLng(52.5, 13.5);
//        googleMap.addGroundOverlay(new GroundOverlayOptions().
//                image(addCircleToMap(500)).
//                position(latLngCenter,10*2,10*2).
//                transparency(0.4f));

//        final ValueAnimator valueAnimator = new ValueAnimator();
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
//        valueAnimator.setIntValues(0, 100);
//        valueAnimator.setDuration(10000);
//        valueAnimator.setEvaluator(new IntEvaluator());
//        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float animatedFraction = valueAnimator.getAnimatedFraction();
//                polyline.setZIndex(animatedFraction * 100);
//            }
//        });
//        valueAnimator.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            wayResponse = bundle.getParcelable(Constants.WAY_RESPONSE);
            latLngSource = new LatLng(Double.parseDouble(wayResponse.getSource().getLatitude()), Double.parseDouble(wayResponse.getSource().getLongitude()));
            latLngDestination = new LatLng(Double.parseDouble(wayResponse.getDestination().getLatitude()), Double.parseDouble(wayResponse.getDestination().getLongitude()));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private BitmapDescriptor addCircleToMap(final int diameter) {
        Bitmap bm = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.material_blue_grey_800));
        c.drawCircle(diameter/2, diameter/2, diameter/2, p);

        return BitmapDescriptorFactory.fromBitmap(bm);
    }
}
