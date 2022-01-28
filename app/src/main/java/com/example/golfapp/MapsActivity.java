package com.example.golfapp;

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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private Boolean isPermissionGranted;
    private final int GPS_REQUEST_CODE = 9001;
    private LatLng currentLocation;
    private Polyline mPolyline;
    private TextView distanceText;
    private int holeNumber = 1, shotNumber = 1;
    private String clubRecommendation = "", distance = "";
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
        nextHoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextHole();
            }
        });
        nextShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextShot();
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

    public void nextShot() {
        if (!clubChoiceDropDown.getSelectedItem().toString().equals("Select Club")) {
            shotNumber = shotNumber + 1;
            Toast.makeText(this, "Shot " + shotNumber + " coming up", Toast.LENGTH_SHORT).show();
            resetSpinner();
            mMap.clear();
            setTitle();
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
                    .setMessage("Pocket Caddy recommends for a " + distance + " yds shot you should use a " + clubRecommendation)
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
                calculateDistance(latLng);
            }
        });
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
        calculateDistance(latLng);
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

    public void calculateDistance(LatLng latLng) {
        float[] results = new float[3];
        Location.distanceBetween(currentLocation.latitude,currentLocation.longitude,latLng.latitude,latLng.longitude,results);
        System.out.println(results[0] + "in meters");
        float distance = (float) (Math.round(results[0]) * 1.09);
        int dis = ((int) distance);
        System.out.println(dis + " yrds now");
        distanceText.setText(dis + " yds");
        getRecommendation(dis);
    }

    public void getRecommendation(int dis) {
        TextView recommendation = findViewById(R.id.recommendationText);
        clubRecommendation = map.get("7I");
        distance = String.valueOf(dis);

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
}