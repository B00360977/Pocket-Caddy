package com.example.golfapp;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import org.json.JSONException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This is the main class of the application. In this class is where the shots are all recorded
 * and where the recommendation is retrieved
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private Boolean isPermissionGranted;
    private final int GPS_REQUEST_CODE = 9001;
    private LatLng currentLocation, startLocation, endLocation;
    private Polyline mPolyline;
    private TextView distanceText;
    private int holeNumber = 1;
    private int shotNumber = 1;
    private int roundID;
    private String holeID;
    private String clubRecommendation = "", distance = "";
    private LocationRequest mLocationRequest;
    private Map<String, String> map;
    private Spinner clubChoiceDropDown;
    private Button nextHoleBtn, nextShotBtn;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.golfapp.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());

        // set screen content
        setContentView(binding.getRoot());
        setActionBar(binding.toolbar);
        getActionBar().setTitle("Pocket Caddy - Hole " + holeNumber + "  Shot " + shotNumber);
        try {
            checkMapPermission();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // loads the map
        initMap();
        mLocationClient = new FusedLocationProviderClient(this);
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location;
                location = task.getResult();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }});
        roundID = GlobalVariables.getInstance().getRoundID();
        distanceText = findViewById(R.id.yrdsText);
        clubChoiceDropDown = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.golf_clubs_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubChoiceDropDown.setAdapter(adapter);
        fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(view -> createRecommendationPopup());
        nextHoleBtn = findViewById(R.id.nextHoleBtn);
        nextShotBtn = findViewById(R.id.nextShotBtn);
        nextShotBtn.setText("Start Shot " + shotNumber);
        nextHoleBtn.setOnClickListener(view -> {
            if (nextShotBtn.getText().toString().contains("Finish")) {
                Toast.makeText(getApplicationContext(), "Please finish current shot before moving to the next hole", Toast.LENGTH_SHORT).show();
            } else {
                nextHole();
            }
        });
        nextShotBtn.setOnClickListener(view -> {
            if (nextShotBtn.getText().toString().contains("Start Shot")) {
                startShot();
            } else {
                finishShot();
            }
        });

        map = new HashMap<>();
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

        // adds first hole to round
        insertHole();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Quit")
                .setMessage("Your progress will be lost")
                .setPositiveButton("Yes", ((dialogInterface, i) -> startActivity(new Intent(getApplicationContext(), HomeActivity.class))))
                .setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.cancel()))
                .setCancelable(true)
                .show();
    }

    // get the users current location and sets as the start location
    private void startShot() {
        if (!clubChoiceDropDown.getSelectedItem().toString().equals("Select Club")) {
            startLocation = currentLocation;
            nextShotBtn.setText("Finish Shot " + shotNumber);
            nextShotBtn.setBackgroundColor(getResources().getColor(R.color.quantum_googred));
        } else {
            Toast.makeText(this, "Please select a club", Toast.LENGTH_SHORT).show();
        }
    }

    // records the users final location calculates the distance and add to the database
    private void finishShot() {
        if (!clubChoiceDropDown.getSelectedItem().toString().equals("Select Club")) {
            endLocation = currentLocation;
            String clubID = getClubID(map, clubChoiceDropDown.getSelectedItem().toString());
            insertShot(clubID);
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

    // get the club id (key) from the hash map given the value
    private static <T, E> T getClubID(Map<T, E> map1, E value) {
        for (Map.Entry<T, E> entry : map1.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    // updates hole counter and inserts a new hole record into database
    private void nextHole() {
        // checks if user is on the final hole and asks to confirm they are completed
        if (holeNumber == 18) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Round Complete")
                    .setMessage("Please confirm you have finished your round")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> startActivity(new Intent(getApplicationContext(), EndScreen.class))))
                    .setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.cancel()))
                    .setCancelable(true)
                    .show();
        } else {
            // updates text on button to finish round once completed hole 17
            if (holeNumber == 17) {
                nextHoleBtn.setText(R.string.finish_round);
            }
            holeNumber = holeNumber + 1;
            insertHole();
            shotNumber = 1;
            Toast.makeText(this, "On to the next hole", Toast.LENGTH_SHORT).show();
            resetSpinner();
            nextShotBtn.setText("Start Shot " + shotNumber);
            // clears the map so the user starts fresh on the next hole
            mMap.clear();
            setTitle();
        }
    }
    // resets club dropdown to default position
    private void resetSpinner() {
        clubChoiceDropDown.setSelection(0);
    }

    // updates the title shown on screen
    private void setTitle() {
        getActionBar().setTitle("Pocket Caddy - Hole " + holeNumber + "  Shot "+ shotNumber);
    }

    // creates the popup that displays the recommendation to the user
    private void createRecommendationPopup() {
        if (!distance.equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("Golf Club Recommendation")
                    .setMessage("Pocket Caddy recommends for a " + distance + " yds shot you should use a " + map.get(clubRecommendation))
                    .setPositiveButton("Thanks!", ((dialogInterface, i) -> dialogInterface.dismiss()))
                    .setCancelable(true)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Golf Club Recommendation")
                    .setMessage("To get a recommendation you must select a distance")
                    .setPositiveButton("Thanks!", ((dialogInterface, i) -> dialogInterface.dismiss()))
                    .setCancelable(true)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.quitBtn) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Quit")
                    .setMessage("Your progress so far will be saved")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> startActivity(new Intent(getApplicationContext(), HomeActivity.class))))
                    .setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.cancel()))
                    .setCancelable(true)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // initialises the map and checks if location permission has been enabled, if not is asks the user to enable location
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

    // verifies GPS is enabled on the device
    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            new AlertDialog.Builder(this)
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

    // checks that app has access to fine location and creates popup to get permission if not given
    private void checkMapPermission() throws InterruptedException {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Grant Location Permission")
                        .setMessage("Pocket Caddy needs your location to record distances")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            isPermissionGranted = false;
        } else {
            isPermissionGranted = true;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
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

    // adds the location pin to the map and calculates the distance
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

    // adds the polyline from the users current location to the dropped pin
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

    // calculates the distance between users current location and a point
    private void calculateDistance(LatLng latLng) throws JSONException {
        float[] results = new float[3];
        Location.distanceBetween(currentLocation.latitude,currentLocation.longitude,latLng.latitude,latLng.longitude,results);
        float distanceYds = (float) (Math.round(results[0]) * 1.09);
        int dis = ((int) distanceYds);
        distance = String.valueOf(dis);
        distanceText.setText(dis + " yds");
        getRecommendation();
    }

    // retrieves recommendation from the machine learning algorithm
    private void getRecommendation() throws JSONException {
        PayloadGenerator payloadGenerator = new PayloadGenerator();
        JsonObjectRequest jsonObjectRequest = payloadGenerator.createJSONPayload(distance);
        RecommendationAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
        updateRecommendation();
    }

    // updates the recommendation box with the latest recommendation
    private void updateRecommendation() {
        Handler handler= new Handler();
        handler.postDelayed(() -> {
            TextView recommendation = findViewById(R.id.recommendationText);
            clubRecommendation = GlobalVariables.getInstance().getRecommendation();
            recommendation.setText(clubRecommendation);
        }, 1200);
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
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnabled) {
                Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS is not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // starts to get the users current location in intervals to keep it up to date
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 10 * 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 2000;
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

    // adds a new record to tbl.Hole in database
    private void insertHole() {
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.connectionClass();
            if (connection != null) {
                String[] returnID = { "holeID" };
                String query = "INSERT INTO dbo.[tbl.Hole](holeNumber, roundID)\n" +
                        "VALUES ('" + holeNumber + "', " + roundID + ")";

                PreparedStatement statement = connection.prepareStatement(query);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Insert Failed. No rows updated");
                }
                holeID = new StringBuilder().append(holeNumber).append(roundID).toString();

            } else {
                String connectionResult = "Check Connection";
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    // adds a new record to tbl.Shots in database
    private void insertShot(String clubID) {
        String UID = GlobalVariables.getInstance().getUid();
        System.out.println(clubID +"   "+distance+"    "+UID+"   "+shotNumber+"   "+holeID);
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.connectionClass();
            if (connection != null) {

                String query = "INSERT INTO dbo.[tbl.Shots](clubID, distance, UID, shotNumber, holeID)\n" +
                        "VALUES ('" + clubID + "', " + distance + ", '" + UID + "', " + shotNumber + ", " + holeID + ")";

                PreparedStatement statement = connection.prepareStatement(query);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Insert Failed. No rows updated");
                }
            } else {
                String connectionResult = "Check Connection";
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}