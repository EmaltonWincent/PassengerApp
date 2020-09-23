package com.example.passengerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private LocationManager locationManager;
    TextView textView, textView1;

    double latitude = 0;
    double longitude = 0;
    double speed = 0;
    String placeId = "";

    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;

    FirebaseDatabase database;
    DatabaseReference myRef;

    TextView textViewEta;
    AlertDialog.Builder builder;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }

    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        builder = new AlertDialog.Builder(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLocationPermission();


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (latitude != 0 && longitude != 0) {
                    GetPlaceId getPlaceId = new GetPlaceId();
                    getPlaceId.execute(latitude + "", longitude + "");
                }
            }
        }, 0, 5000);


    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

//                            assert currentLocation != null;
//                            latitude = currentLocation.getLatitude();
//                            longitude = currentLocation.getLongitude();
//
//                            System.out.println("Return values "+latitude+" "+longitude);

//                            try {
//                                assert currentLocation != null;
//
//                                List<Address> addresses = mGeocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
//                                if (addresses != null && addresses.size() > 0) {
//
//                                    Log.i(TAG, "onComplete: found location! "+ addresses.get(0).getSubLocality());
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    15.0f);//Default_Zoom


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                mLocationPermissionsGranted = true;
                //initialize our map
                initMap();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
    }

    // GPS will drain your battery. This is a good place to stop
    // monitoring it. You can also make it work some other way.
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    // The following is the main callback. Process received GPS info as you wish.
    // We will just show the speed (in km/h, the received data is in m/s)
    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(Location newLoc) {
        //speedText.setText(String.format("%.0f", newLoc.getSpeed() * 3.6));
        System.out.println("Speed: " + String.format("%.0f", newLoc.getSpeed() * 3.6));
        latitude = newLoc.getLatitude();
        longitude = newLoc.getLongitude();
        speed = newLoc.getSpeed();


        mLastLocation = newLoc;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(newLoc.getLatitude(), newLoc.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("I'm Waiting");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationt));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

//        getDataFromFirebase(placeId, newLoc.getSpeed() * 3.6);
        getCustomerDataFromFirebase(newLoc.getLatitude(), newLoc.getLongitude());
    }


    @Override
    public void onProviderDisabled(String arg0) {
    }


    @Override
    public void onProviderEnabled(String arg0) {
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }




    private class GetPlaceId extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String lat = strings[0];
            String lon = strings[1];
            Context context;
            StringBuilder data = new StringBuilder();
            int tmp;
            String key = "AIzaSyCDynhPMRc-OP6fwcZggTBwDSy_F7ktpeY";

            try {
                URL url = new URL("https://roads.googleapis.com/v1/snapToRoads?path=" + lat + "," + lon + "&interpolate=true&key=" + key);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.flush();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data.append((char) tmp);
                }
                is.close();
                httpURLConnection.disconnect();

                return data.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        protected void onPostExecute(String s) {
            String err = null;
            if (s.isEmpty()) {
                Toast.makeText(MapsActivity.this, "No Connection!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("snappedPoints");
                    JSONObject oneObject = jsonArray.getJSONObject(0);
                    placeId = oneObject.getString("placeId");

                    //getDataFromFirebase(placeId, speed);
                } catch (JSONException e) {
                    e.printStackTrace();
                    err = "Exception: " + e.getMessage();
                }
            }

        }
    }

    void showAlertDialog(String cId, String title, String text) {

        builder.setIcon(R.drawable.passenger)
                //set title
                .setTitle("Your passenger waiting for you")
                //set message
                .setMessage(text)
                //set positive button
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //set what would happen when positive button is clicked
//                    }
//                })
//                .show();

        if (cId != null) {
            myRef.child("LiveLocation").child(String.valueOf(cId)).child("status").setValue(0);
        }
    }

    private void distance(final String cId, double dLat, double dLon, double cLat, double cLon) {
        //Calculate longitude difference
        double longDiff = dLon - cLon;
        //Calculate distance
        double distance = Math.sin(degToRad(dLat))
                * Math.sin(degToRad(cLat))
                + Math.cos(degToRad(dLat))
                * Math.cos(degToRad(cLat))
                * Math.cos(degToRad(longDiff));
        distance = Math.acos(distance);
        //Convert distance radian to degree
        distance = radToDeg(distance);
        //Distance in miles
        distance = distance * 60 * 1.1515;
        //Distance in kilometers
        distance = distance * 1.609344;
        //Distance in meter
        distance = distance * 1000;






//        System.out.println("distance: " + String.format(Locale.US, "%2f kms", eta));
//
////        double limit = 100;
////        if (distance < limit) {
//          showAlertDialog(cId,"Customer waiting for you at next bus stop", "Passenger: "+cId+"  Distance: "+eta );

        final double finalDistance = distance;
        myRef.child("LiveLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    DriverData driverData = userSnapshot.getValue(DriverData.class);

                    assert driverData != null;
                    if (driverData.getStatus() == 1) {
                       double speedData = driverData.getSpeed();

                       double eta = (finalDistance /speedData * 0.277778)*0.0166667;
//                        System.out.println("distance: " + String.format("%.1f", +eta));
//                        showAlertDialog(cId,"ETA Prediction", "Passenger: "+cId+"  ETA: "+ String.format("%.1f", +eta) );

                        textViewEta =(TextView)findViewById(R.id.TextViewETA);
                        textViewEta.setText("ETA "+ String.format("%.1f",+eta) +"minits");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    //Convert radian to degree
    private double radToDeg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    //Convert degree to radian
    private double degToRad(double lat) {
        return (lat * Math.PI / 180.0);
    }

    private void getCustomerDataFromFirebase(final double dLat, final double dLon) {
        myRef.child("LiveLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    DriverData driverData = userSnapshot.getValue(DriverData.class);

                    assert driverData != null;
                    if (driverData.getStatus() == 1) {
                        distance(driverData.getId(), dLat, dLon, driverData.latitude, driverData.longitude);


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}