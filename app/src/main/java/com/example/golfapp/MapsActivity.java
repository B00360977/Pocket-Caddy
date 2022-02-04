package com.example.golfapp;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.golfapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private Boolean isPermissionGranted;
    private final int GPS_REQUEST_CODE = 9001;
    private LatLng currentLocation, startLocation, endLocation;
    private Polyline mPolyline;
    private TextView distanceText;
    private int holeNumber = 1, shotNumber = 1;
    private String clubRecommendation = "", distance = "";
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Map<String, String> map;
    Spinner clubChoiceDropDown;
    Button nextHoleBtn, nextShotBtn;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActionBar(binding.toolbar);
        getActionBar().setTitle("Pocket Caddy - Hole " + holeNumber + "  Shot " + shotNumber);
        checkMapPermission();
        initMap();
        mLocationClient = new FusedLocationProviderClient(this);
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }});
        distanceText = findViewById(R.id.yrdsText);
        clubChoiceDropDown = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.golf_clubs_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubChoiceDropDown.setAdapter(adapter);
        fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecommendationPopup();
            }
        });
        nextHoleBtn = findViewById(R.id.nextHoleBtn);
        nextShotBtn = findViewById(R.id.nextShotBtn);
        nextShotBtn.setText("Start Shot " + shotNumber);
        nextHoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextShotBtn.getText().toString().contains("Finish")) {
                    Toast.makeText(getApplicationContext(), "Please finish current shot before moving to the next hole", Toast.LENGTH_SHORT).show();
                } else {
                    nextHole();
                }
            }
        });
        nextShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextShotBtn.getText().toString().contains("Start Shot")) {
                    startShot();
                } else {
                    finishShot();
                }
            }
        });

        map = new HashMap<String, String>();
        map.put("D", "Driver");
        map.put("1W", "1 Wood");
        map.put("2W", "2 Wood");
        map.put("3W", "3 Wood");
        map.put("4W", "4 Wood");
        map.put("5W", "5 Wood");
        map.put("6W", "6 Wood");
        map.put("7W", "7 Wood");
        map.put("3H", "3 Hybrid");
        map.put("4H", "4 Hybrid");
        map.put("5H", "5 Hybrid");
        map.put("1I", "1 Iron");
        map.put("2I", "2 Iron");
        map.put("3I", "3 Iron");
        map.put("4I", "4 Iron");
        map.put("5I", "5 Iron");
        map.put("6I", "6 Iron");
        map.put("7I", "7 Iron");
        map.put("8I", "8 Iron");
        map.put("9I", "9 Iron");
        map.put("PW", "Pitching Wedge");
        map.put("SW", "Sand Wedge");
        map.put("LW", "Lob Wedge");
        map.put("P", "Putter");

    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Quit")
                .setMessage("Your progress will be lost")
                .setPositiveButton("Yes", ((dialogInterface, i) -> {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }))
                .setNegativeButton("No", ((dialogInterface, i) -> {
                    dialogInterface.cancel();
                }))
                .setCancelable(true)
                .show();
    }

    public void startShot() {
        if (!clubChoiceDropDown.getSelectedItem().toString().equals("Select Club")) {
            startLocation = currentLocation;
            nextShotBtn.setText("Finish Shot " + shotNumber);
            nextShotBtn.setBackgroundColor(getResources().getColor(R.color.quantum_googred));
        } else {
            Toast.makeText(this, "Please select a club", Toast.LENGTH_SHORT).show();
        }
    }

    public void finishShot() {
        if (!clubChoiceDropDown.getSelectedItem().toString().equals("Select Club")) {
            endLocation = currentLocation;
            shotNumber = shotNumber + 1;
            Toast.makeText(this, "Shot " + shotNumber + " coming up", Toast.LENGTH_SHORT).show();
            resetSpinner();
            mMap.clear();
            setTitle();
            nextShotBtn.setText("Start Shot " + shotNumber);
            nextShotBtn.setBackgroundColor(android.graphics.Color.parseColor("#048741"));
        } else {
            Toast.makeText(this, "Please select a club", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextHole() {
        holeNumber = holeNumber + 1;
        shotNumber = 1;
        Toast.makeText(this, "On to the next hole", Toast.LENGTH_SHORT).show();
        resetSpinner();
        mMap.clear();
        setTitle();
    }

    public void resetSpinner() {
        clubChoiceDropDown.setSelection(0);
    }

    public void setTitle() {
        getActionBar().setTitle("Pocket Caddy - Hole " + holeNumber + "  Shot "+ shotNumber);
    }

    public void createRecommendationPopup() {
        if (!distance.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Golf Club Recommendation")
                    .setMessage("Pocket Caddy recommends for a " + distance + " yds shot you should use a " + map.get(clubRecommendation))
                    .setPositiveButton("Thanks!", ((dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }))
                    .setCancelable(true)
                    .show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Golf Club Recommendation")
                    .setMessage("To get a recommendation you must select a distance")
                    .setPositiveButton("Thanks!", ((dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }))
                    .setCancelable(true)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.quitBtn) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm Quit")
                    .setMessage("Your progress will be lost")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }))
                    .setNegativeButton("No", ((dialogInterface, i) -> {
                        dialogInterface.cancel();
                    }))
                    .setCancelable(true)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        if (isPermissionGranted) {
            if (isGPSEnabled()) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(this);
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app to work. Please enable GPS")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void checkMapPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setOnMapLongClickListener(this);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                mPolyline.remove();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                marker.setPosition(latLng);
                addRoute(latLng);
                try {
                    calculateDistance(latLng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        startLocationUpdates();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Hole")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        addRoute(latLng);
        try {
            calculateDistance(latLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void addRoute(LatLng latlng) {
        if (mPolyline != null) {
            mPolyline.remove();
        }
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mPolyline = mMap.addPolyline(new PolylineOptions()
                        .clickable(false)
                        .add(currentLocation, latlng));
            }
        });
    }

    public void calculateDistance(LatLng latLng) throws JSONException {
        float[] results = new float[3];
        Location.distanceBetween(currentLocation.latitude,currentLocation.longitude,latLng.latitude,latLng.longitude,results);
        float distanceYds = (float) (Math.round(results[0]) * 1.09);
        int dis = ((int) distanceYds);
        distance = String.valueOf(dis);
        distanceText.setText(dis + " yds");
        getRecommendation(dis);
    }

    public void getRecommendation(int dis) throws JSONException {
        PayloadGenerator payloadGenerator = new PayloadGenerator();
        JsonObjectRequest jsonObjectRequest = payloadGenerator.createJSONPayload(distance);
        RecommendationAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
        updateRecommendation();
    }

    public void updateRecommendation() {
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView recommendation = findViewById(R.id.recommendationText);
                clubRecommendation = GlobalVariables.getInstance().getRecommendation();
                recommendation.setText(clubRecommendation);
            }
        }, 1000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnabled) {
                Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS is not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    }
                },
                Looper.myLooper());
    }
}