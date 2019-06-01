package com.example.ilias.finalapplication.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.ilias.finalapplication.AppConfig;
import com.example.ilias.finalapplication.BuildConfig;
import com.example.ilias.finalapplication.Car;
import com.example.ilias.finalapplication.Device;
import com.example.ilias.finalapplication.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = MapsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private OnFragmentInteractionListener mListener;

    private static final LatLng PLaceOne = new LatLng(50.857116, 4.336857);
    // 50.858628, 4.337268
    private static final LatLng PiersStraat = new LatLng(50.858628, 4.337268);
    private static final double DEFAULT_RADIUS_METERS = 500;
    private static final double RADIUS_OF_EARTH_METERS = 6371009;

    private static final int MAX_WIDTH_PX = 50;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;

    private static final int PATTERN_DASH_LENGTH_PX = 100;
    private static final int PATTERN_GAP_LENGTH_PX = 200;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<DraggableCircle> mCircles = new ArrayList<>(1);
    private static final int DEFAULT_ZOOM = 100;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private LatLng deviceLoc;
    // These are the options for stroke patterns. We use their
    // string resource IDs as identifiers.

    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    //  private static final String TAG = MapsActivity.class.getSimpleName();

    // The entry point to the Fused Location Provider.
    // private FusedLocationProviderClient mFusedLocationProviderClient;

    private Button LeavePlace;
    private Button TakePlace;

    private Button ShowPlace;

    private Button StartLoc;
    private Boolean mLocationPermissionsGranted = true ;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

  //  private static final int REQUEST_CHECK_SETTINGS = 100;

    private ArrayList<Car> myCars = new ArrayList<Car>();
    private ArrayList<Device> myDevices = new ArrayList<Device>();
    private String UrlResponse = "";

    private class DraggableCircle {
        private final Marker mCenterMarker;
        private final Marker mRadiusMarker;
        private final Circle mCircle;
        private double mRadiusMeters;

        public DraggableCircle(LatLng center, double radiusMeters) {
            mRadiusMeters = radiusMeters;
            mCenterMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            mRadiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(toRadiusLatLng(center, radiusMeters))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radiusMeters)
                    .strokeWidth(40)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(1,2,5,1))
                    .clickable(true));
        }

        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(mCenterMarker)) {
                mCircle.setCenter(marker.getPosition());
                mRadiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), mRadiusMeters));
                return true;
            }
            if (marker.equals(mRadiusMarker)) {
                mRadiusMeters =
                        toRadiusMeters(mCenterMarker.getPosition(), mRadiusMarker.getPosition());
                mCircle.setRadius(mRadiusMeters);
                return true;
            }
            return false;
        }

        public void onStyleChange() {
            mCircle.setStrokeWidth(1);
            mCircle.setStrokeColor(2);
            mCircle.setFillColor(3);
        }

        public void setStrokePattern(List<PatternItem> pattern) {
            mCircle.setStrokePattern(pattern);
        }

        public void setClickable(boolean clickable) {
            mCircle.setClickable(clickable);
        }
    }


    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radiusMeters) {
        double radiusAngle = Math.toDegrees(radiusMeters / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }
    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    Button show;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LeavePlace =view.findViewById(R.id.btnLeavePlace);
        ShowPlace = view.findViewById(R.id.btnShowPlace);
        TakePlace = view.findViewById(R.id.btnGetPlace);
       allMyCars();
        ShowPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startOne();
               // Log.d(TAG, "onClick: " + niets);

              //  Marker marker = mMap.addMarker(new MarkerOptions()
                //        .position(new LatLng(50.841277, 4.321596))
                  //      .title("Spot nr 2 , 390cm groot, ok voor 1-DNS-144")
                        // .title("Spot nr" + id + " " + length +" cm groot ok voor 1-DNS-144/ 1-vke-721")
                    //    .snippet("Is bezet"));
                Log.d(TAG, "onClick: begin fillMap");
                fillMap();
                Log.d(TAG, "onClick: einde fillMap");
            }
        });


        show = view.findViewById(R.id.btnShowlast);

        StartLoc = view.findViewById(R.id.btnStartLoc);

        StartLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();

            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationButtonClick();
                startLocationUpdates();
                showLastKnownLocation();
            }
        });
        final LatLng leavePosi = new LatLng(50.843126, 4.322620);
        final Marker[] LeavePosMarker = new Marker[1];
        LeavePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Toast.makeText(getContext(), "I leave this place", Toast.LENGTH_SHORT).show();

                //  LatLng leavePosi = new LatLng(0,0);
                MarkerOptions markerOptions = new MarkerOptions();
                LeavePosMarker[0] = mMap.addMarker(new MarkerOptions()
                        .position(leavePosi)
                        .title("San Francisco")
                        .snippet("Population: 776733")
                );

                mMap.addMarker( new MarkerOptions().position(leavePosi)
                        .title("Spot nr 8 , 390cm groot, ok voor 1-DNS-144/ 1-vke-721"));
            }
        });

        TakePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "I take this place", Toast.LENGTH_SHORT).show();


                MarkerOptions markerOptions = new MarkerOptions().position(leavePosi)
                        .title("Here is a My car" + leavePosi.toString());
                //  markerOptions.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker( markerOptions);

                // mMap.setMar


                //   m.setVisible(false);
                //      LeavePosMarker[0].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                //    LeavePosMarker[0].setTitle("delete");


            } });


        getDeviceLocation();
        init();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity().getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        //    toggleButtons();
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //  throw new RuntimeException(context.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();


//                            deviceLoc = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
  //                          DraggableCircle circle = new DraggableCircle(deviceLoc, DEFAULT_RADIUS_METERS);
    //                        mCircles.add(circle);
                            for (int i = 0 ; i < mCircles.size();i++){
                                Log.d(TAG + "task is successful", mCircles.get(i).toString());
                            }
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                            deviceLoc = new LatLng(50.842435,4.322862);
                            DraggableCircle circle = new DraggableCircle(deviceLoc, DEFAULT_RADIUS_METERS);
                            mCircles.add(circle);

                            Marker d = mMap.addMarker(new MarkerOptions()
                                    .position(deviceLoc)
                                    .title("standaard loc")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                    ));

                            for (int i = 0 ; i < mCircles.size();i++){
                                Log.d(TAG + "task is not successful", mCircles.get(i).toString());
                            }


                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }


      /* DraggableCircle circle = new DraggableCircle(PLaceOne, DEFAULT_RADIUS_METERS);
        mCircles.add(circle);
        for (int i = 0 ; i < mCircles.size();i++){
            Log.d(TAG + "task is finished", mCircles.get(i).toString());
        }*/
        // Set up the click listener for the circle.
        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // Flip the red, green and blue compon
                // ents of the circle's stroke color.
                circle.setStrokeColor(circle.getStrokeColor() ^ 0x00ffffff);
            }
        });

        LatLng SpotPark = new LatLng(50.842341, 4.322284);

       /* mMap.addMarker(new MarkerOptions().position(PiersStraat)
                .title("Here is a spot" + PiersStraat.toString()));*/
        mMap.addMarker(new MarkerOptions().position(SpotPark)
                .title("Spot nr 8 , 600cm groot, ok voor 1-DNS-144/ 1-PPM-123"));
        Marker marker2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.8419685,4.32184219))
                .title("Spot nr 2 , 500cm groot, ok voor 1-PPM-123")
                // .title("Spot nr" + id + " " + length +" cm groot ok voor 1-DNS-144/ 1-vke-721")
                .snippet("Is bezet"));
        Marker marker3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.8422813,4.321531772))
                .title("Spot nr 3 , 450cm groot, ok voor 1-DNS-144/ 1-vke-721")
                // .title("Spot nr" + id + " " + length +" cm groot ok voor 1-DNS-144/ 1-vke-721")
                .snippet("Is bezet"));

      /*  List<PatternItem> pattern = getSelectedPattern(mStrokePatternSpinner.getSelectedItemPosition());
        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.setStrokePattern(pattern);
        }*/

        float zoomLevel = (float) 50.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SpotPark, zoomLevel));
        DraggableCircle circle = new DraggableCircle(SpotPark, DEFAULT_RADIUS_METERS);
        mCircles.add(circle);
        Log.d(TAG, "startOne: draw circle");
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void getDeviceLocation(){
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();


                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            DraggableCircle circle = new DraggableCircle(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_RADIUS_METERS);
                            mCircles.add(circle);
                            Log.d(TAG, "startOne: draw circle");
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            LatLng mDefaultLocation = new LatLng(0,0);
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }


    }

    public void allMyCars(){
        RequestQueue mRequestQueue;
        final String[] answer = new String[1];

        // Instantiate the cache
        Cache cache = (Cache) new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();



        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_READ_CARS_BYID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response

                        Log.d(TAG,response);
                        // txtView.setText(response);
                        //   Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();

                        JSONObject jObj = null;
                        try {
                            jObj = new JSONObject(response);
                            JSONArray jArray = jObj.getJSONArray("records");

                            Log.d(TAG, "onResponse: " + jArray.getString(0));
                            Log.d(TAG, "onResponse: " + jObj.toString());

                            for (int i=0; i < jArray.length(); i++)
                            {
                                try {
                                    JSONObject oneObject = jArray.getJSONObject(i);
                                    // Pulling items from the array
                                    String id = oneObject.getString("Id");
                                    String UserId = oneObject.getString("UserId");
                                    String LicensePlate = oneObject.getString("LicensePlate");
                                    String Kind = oneObject.getString("Kind");
                                    String Length = oneObject.getString("Length");
                                    Log.d(TAG, "onResponse: "+id );
                                    Log.d(TAG, "onResponse: "+ Kind);
                                    Log.d(TAG, "onResponse: "+ UserId);
                                    Log.d(TAG, "onResponse: "+ LicensePlate);

                                    Car car = new Car(id,LicensePlate,Kind,Length);

                                    myCars.add(car);
                                    Log.d(TAG, "onResponse: "+ car.toString() );


                                } catch (JSONException e) {

                                    // Oops

                                    Log.d(TAG, e.getMessage() + " volley goed maar kleine error");

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        answer[0] = response;
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d(TAG, "GEEN connectie" + error.toString());
                        answer[0] = error.toString();
                        //    txtView.setText(error.toString());
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                });


        mRequestQueue.add(stringRequest);

        Log.d(TAG, "allMyCars: " + myCars.toString());


    }

    public String startOne() {

       Log.d(TAG, "allMyCars: " + myCars.toString());
        RequestQueue mRequestQueue;
        final String[] answer = new String[1];

        // Instantiate the cache
        Cache cache = (Cache) new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();

        String url = "http://dtsl.ehb.be/~youssef.imgharane/api/Device/read.php";

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response

                        Log.d(TAG,response);
                        // txtView.setText(response);
                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();

                        UrlResponse = response;
                        JSONObject jObj = null;
                        try {
                            jObj = new JSONObject(response);
                            JSONArray jArray = jObj.getJSONArray("records");

                            Log.d(TAG, "onResponse: line 726 " + jArray.getString(0));
                            Log.d(TAG, "onResponse: line 727 " + jObj.toString());
                            // JSONObject jsonObject = jArray.getString(0);
                        /*    for (JSONObject js: jArray
                                 ) {
                                Log.d(TAG, "onResponse: " + js.getString("id"));

                            }*/


                            for (int i = 0; i < jArray.length(); i++) {
                                try {
                                    JSONObject oneObject = jArray.getJSONObject(i);
                                    // Pulling items from the array
                                    String id = oneObject.getString("Id");
                                    String isBezet = oneObject.getString("IsBezet");
                                    String latit = oneObject.getString("Latitude");
                                    String longit = oneObject.getString("Longitude");
                                    String length = oneObject.getString("Length");


                                    Log.d(TAG, "onResponse: " + id);
                                    Log.d(TAG, "onResponse: " + isBezet);
                                    Log.d(TAG, "onResponse: " + latit);
                                    Log.d(TAG, "onResponse: " + longit);
                                    Log.d(TAG, "onResponse: " + length);

                                    int Indentifier = Integer.parseInt(id);
                                    int IsBezet = Integer.parseInt(isBezet);
                                    double lat = Double.parseDouble(latit);
                                    double longi = Double.parseDouble(longit);
                                //    int lengte = Integer.parseInt(length);


                                    StringBuilder naam = new StringBuilder();

                                    Device device = new Device(Indentifier, longi,lat,IsBezet,Integer.parseInt(length));

                                    myDevices.add(device);
                                    if(Double.parseDouble(isBezet) == 0) {

                                        double laat = Double.parseDouble(latit);

                                        double lng = Double.parseDouble(longit);
                                        MarkerOptions markerOptions = new MarkerOptions();

                                        String titel = "hallo";
                                        int lengte = Integer.parseInt(length);

                                        for (Car c : myCars) {

                                            int carlengte = Integer.parseInt(c.getLength());
                                            if (lengte > carlengte)
                                                naam.append("/");
                                                naam.append(c.getLicenseplate());

                                        }
                                    }
                                    Log.d(TAG, "onResponse: Naam: " + naam.toString());

                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, longi))
                                            .title("Spot nr" + id + "lengte " + length+ " " + naam.toString())
                                            .snippet("Is bezet" + isBezet)
                                    );

                            




                                    Log.d(TAG, "onResponse: marker");

                                } catch (JSONException e) {
                                    // Oops
                                    Log.d(TAG, "onResponse: 796 JSONException");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: 801 JSONException");
                        }
                        //  boolean error = jObj.getBoolean("error");
                        answer[0] = response;
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d(TAG, "GEEN connectie" + error.toString());
                        answer[0] = error.toString();
                        //    txtView.setText(error.toString());
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                });

// Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);



        return answer[0];
    }


    public void fillMap(){

        Log.d(TAG, "fillMap: " + UrlResponse);
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(UrlResponse);
            JSONArray jArray = jObj.getJSONArray("records");

            Log.d(TAG, "onResponse: line 834 " + jArray.getString(0));
            Log.d(TAG, "onResponse: line 835" + jObj.toString());



            for (int i=0; i < jArray.length(); i++)
            {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String id = oneObject.getString("Id");
                    String isBezet = oneObject.getString("IsBezet");
                    String latit = oneObject.getString("Latitude");
                    String longit = oneObject.getString("Longitude");
                    String length = oneObject.getString("Length");

                    Log.d(TAG, "nu de plaatsen");
                    Log.d(TAG, "onResponse: line 851 "+id );
                    Log.d(TAG, "onResponse:line 851 "+ isBezet);
                    Log.d(TAG, "onResponse: line 851"+ latit);
                    Log.d(TAG, "onResponse: line 851"+ longit);
                    Log.d(TAG, "onResponse: line 851"+ length);

                    int Indentifier = Integer.parseInt(id);
                    int IsBezet = Integer.parseInt(isBezet);
                    double lat  = Double.parseDouble(latit);
                    double longi  = Double.parseDouble(longit);
                    int lengte  = Integer.parseInt(length);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat,longi))

                    );



                /*    Device device = new Device(Indentifier, longi,lat,IsBezet,lengte);

                    myDevices.add(device);
                    if(Double.parseDouble(isBezet) == 0) {

                        double laat = Double.parseDouble(latit);

                        double lng = Double.parseDouble(longit);
                        MarkerOptions markerOptions = new MarkerOptions();

                        String titel = "hallo";
//                                        int lengte = integer.parseint(length);
//                                        stringbuilder titel = new stringbuilder() ;
//                                        for ( car c : mycars){
//
//                                            int carlengte = integer.parseint(c.getlength());
//                                            if ( lengte > carlengte)
//                                                titel.append(c.getlicenseplate());
//                                        }

                    }



                    Log.d(TAG, "onResponse: marker");
*/

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  boolean error = jObj.getBoolean("error");
/*

       // String titel = " ";
        if (myDevices.isEmpty() == false) {
            for (Device d : myDevices
            ) {
                //   titel = " spot nr " + d.getID() + " ok voor " ;
                if (d.isIsbezet() == 0) {

                    for (Car c : myCars) {

                        Log.d(TAG, "fillMap: test" + d.getID() + " " + c.getLicenseplate());
                        if (d.getLengte() > Integer.parseInt(c.getLength())) {
                            //   titel += c.getLicenseplate() + " ";

                        }


                    }
                    //   Marker marker3 = mMap.addMarker(new MarkerOptions()
                    //         .position(new LatLng(d.getLatitude(), d.getLongitude()))
                    //       .title(titel)
                    //     .snippet("Is bezet"));
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(d.getLatitude(), d.getLongitude()))
                            .title("Spot nr 2 , 390cm groot, ok voor 1-DNS-144")
                            //.title("Spot nr" + id + " " + length +" cm groot ok voor 1-DNS-144/ 1-vke-721")
                            .snippet("Is bezet"));

                }

            }
        } else {
            Log.d(TAG, "fillMap: leeg");
        }

*/

    }


    // @OnClick(R.id.btn_start_location_updates)
    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }

                    //  @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        // updateLocationUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onFailure: ");
                        }

                        // updateLocationUI();
                    }
                });
    }
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        Log.d(TAG, "openSettings: success");
    }
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


                DraggableCircle circle = new DraggableCircle(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()), DEFAULT_RADIUS_METERS);
                mCircles.add(circle);
                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        Log.d(TAG, "init: ");
    }
    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            Log.d(TAG, "updateLocationUI: ");
            double longitude =  mCurrentLocation.getLongitude();
            double latit = mCurrentLocation.getLatitude();
            String coord = "longitude: " + Double.toString(longitude) + " latitude: " + Double.toString(latit);
            Toast.makeText(getContext(), coord,Toast.LENGTH_LONG);

            DraggableCircle circle = new DraggableCircle(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()), DEFAULT_RADIUS_METERS);
            mCircles.add(circle);


            // giving a blink animation on TextView
            //      txtLocationResult.setAlpha(0);
            //    txtLocationResult.animate().alpha(1).setDuration(300);

            // location last updated time
            //  txtUpdatedOn.setText("Last updated on: " + mLastUpdateTime);
        }

        //toggleButtons();
    }
    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {

            Log.d(TAG, "showLastKnownLocation: " + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() );
            Toast.makeText(getActivity().getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
                    + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()))
                    .title("Here Am I")
              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker( markerOptions);
            Log.d(TAG, "showLastKnownLocation: add marker");

            DraggableCircle circle = new DraggableCircle(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()), DEFAULT_RADIUS_METERS);
            mCircles.add(circle);
            Log.d(TAG, "showLastKnownLocation: draw circle");


        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "showLastKnownLocation: error" );

        }
    }

}
