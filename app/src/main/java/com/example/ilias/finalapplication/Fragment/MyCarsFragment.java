package com.example.ilias.finalapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.ilias.finalapplication.Activity.AddCarActivity;
import com.example.ilias.finalapplication.Activity.LoginActivity;
import com.example.ilias.finalapplication.Activity.MainActivity;
import com.example.ilias.finalapplication.AppConfig;
import com.example.ilias.finalapplication.R;
import com.example.ilias.finalapplication.RecyclerViewAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCarsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyCarsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCarsFragment extends Fragment {

    private final String TAG = MyCarsFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button goToCar;

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mLicense = new ArrayList<>();
    private ArrayList<String> mKind = new ArrayList<>();

    public MyCarsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCarsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCarsFragment newInstance(String param1, String param2) {
        MyCarsFragment fragment = new MyCarsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_cars, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerAuto);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
       initLijst();
        allMyCars();
        mAdapter = new RecyclerViewAdapter( mIds,mLicense,mKind,getContext());

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);

        goToCar = view.findViewById(R.id.btnGoToCar);

        goToCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddCarActivity.class);
                startActivity(intent);
            }
        });


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
//            throw new RuntimeException(context.toString()
  //                  + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                                    Log.d(TAG, "onResponse: "+id );
                                    Log.d(TAG, "onResponse: "+ Kind);
                                    Log.d(TAG, "onResponse: "+ UserId);
                                    Log.d(TAG, "onResponse: "+ LicensePlate);

                                    mIds.add(id);
                                    mKind.add(Kind);
                                    mLicense.add(LicensePlate);

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


    private void initLijst(){
        Log.d(TAG, "initialiseert lijst");

        if (mIds.size() == 0) {
            mIds.add("1");
            mKind.add("coupé");
            mLicense.add("1-dns-144");

            mIds.add("3");
            mKind.add("Berline");
            mLicense.add("1-vke-721");
        }

    }
}
