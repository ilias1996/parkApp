package com.example.ilias.finalapplication.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ilias.finalapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = MapsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
    private static final int DEFAULT_ZOOM = 200;
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

    private Boolean mLocationPermissionsGranted = true ;

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

        getDeviceLocation();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


                            deviceLoc = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            DraggableCircle circle = new DraggableCircle(deviceLoc, DEFAULT_RADIUS_METERS);
                            mCircles.add(circle);
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
                .title("Here is a spot" + SpotPark.toString()));

      /*  List<PatternItem> pattern = getSelectedPattern(mStrokePatternSpinner.getSelectedItemPosition());
        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.setStrokePattern(pattern);
        }*/

        float zoomLevel = (float) 15.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SpotPark, zoomLevel));
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
        Log.d(TAG, "getDeviceLocation: getting the devices current location");


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        try{
            //  if(mLocationPermissionsGranted){
            if(true){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();


                            deviceLoc = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            DraggableCircle circle = new DraggableCircle(deviceLoc, DEFAULT_RADIUS_METERS);
                            mCircles.add(circle);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location getDeviceLocation", Toast.LENGTH_SHORT).show();
                            deviceLoc = new LatLng(50.842435,4.322862);
                            DraggableCircle circle = new DraggableCircle(deviceLoc, DEFAULT_RADIUS_METERS);
                            mCircles.add(circle);
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
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

    public String startOne() {

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

        String url = "http://dtsl.ehb.be/~ilias.benchikh/database%20final%20work/api/Device/read.php";

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response

                        Log.d(TAG,response);
                        // txtView.setText(response);
                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();

                        JSONObject jObj = null;
                        try {
                            jObj = new JSONObject(response);
                            JSONArray jArray = jObj.getJSONArray("records");

                            Log.d(TAG, "onResponse: " + jArray.getString(0));
                            Log.d(TAG, "onResponse: " + jObj.toString());
                            // JSONObject jsonObject = jArray.getString(0);
                        /*    for (JSONObject js: jArray
                                 ) {
                                Log.d(TAG, "onResponse: " + js.getString("id"));

                            }*/


                            for (int i=0; i < jArray.length(); i++)
                            {
                                try {
                                    JSONObject oneObject = jArray.getJSONObject(i);
                                    // Pulling items from the array
                                    String id = oneObject.getString("Id");
                                    String isBezet = oneObject.getString("IsBezet");
                                    String latit = oneObject.getString("Latitude");
                                    String longit = oneObject.getString("Longitude");
                                    Log.d(TAG, "onResponse: "+id );
                                    Log.d(TAG, "onResponse: "+ isBezet);
                                    Log.d(TAG, "onResponse: "+ latit);
                                    Log.d(TAG, "onResponse: "+ longit);


                                    if(Double.parseDouble(isBezet) == 0) {

                                        double laat = Double.parseDouble(latit);

                                        double lng = Double.parseDouble(longit);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(laat, lng))
                                                .title("Spot nr" + id)
                                                .snippet("Is bezet" + isBezet)
                                        );

                                    }
                                } catch (JSONException e) {
                                    // Oops
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //  boolean error = jObj.getBoolean("error");
                        answer[0] = response;
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d(TAG,error.toString());
                        answer[0] = error.toString();
                        //    txtView.setText(error.toString());
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                });

// Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);

        return answer[0];
    }
}
