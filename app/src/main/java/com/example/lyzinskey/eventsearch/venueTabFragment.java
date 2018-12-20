package com.example.lyzinskey.eventsearch;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.example.lyzinskey.eventsearch.venueTabFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class venueTabFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "venueTabFragment";

    RequestQueue eventVenueQueue;
    View view;
    GoogleMap map;
    MapView mapView;
    double latitude;
    double longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.event_detail_venue_tab_fragment, container, false);
        eventVenueQueue = Volley.newRequestQueue(this.getContext());

        if (getActivity().getIntent().hasExtra("eventVenue")) {
            String venue = getActivity().getIntent().getStringExtra("eventVenue");
            String eventVenueURL = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/venue?&venueName=" + venue;
            String eventLatitude = getActivity().getIntent().getStringExtra("eventLatitude");
            String eventLongitude = getActivity().getIntent().getStringExtra("eventLongitude");
            latitude = Double.parseDouble(eventLatitude);
            longitude = Double.parseDouble(eventLongitude);

            getEventDetails(eventVenueURL);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View mview, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mview, savedInstanceState);

        mapView = view.findViewById(R.id.map);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public void getEventDetails(String eventVenueURL) {
        Log.i("eventVenueURL", eventVenueURL);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, eventVenueURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String venueName = "";
                        String venueAddress = "";
                        String venueCity = "";
                        String venuePhoneNumber = "";
                        String venueOpenHours = "";
                        String venueGeneralRule = "";
                        String venueChildRule = "";


                        try {

                            // venue name
                            venueName = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
//                            LinearLayout venueNameLayout = view.findViewById(R.id.venueNameLayout);
//                            venueNameLayout.setVisibility(view.GONE);

                            // venue address
                            try {
                                venueAddress = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("address").getString("line1");
                            } catch (JSONException e) {
                                LinearLayout venueAddressLayout = view.findViewById(R.id.venueAddressLayout);
                                venueAddressLayout.setVisibility(view.GONE);
                            }

                            // venue city
                            try {
                                String city = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("city").getString("name");
                                String state = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("state").getString("name");
                                venueCity = city + ", " + state;
                            } catch (JSONException e) {
                                LinearLayout venueCityLayout = view.findViewById(R.id.venueCityLayout);
                                venueCityLayout.setVisibility(view.GONE);
                            }

                            // venue phone number
                            try {
                                venuePhoneNumber = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("boxOfficeInfo").getString("phoneNumberDetail");
                            } catch (JSONException e) {
                                LinearLayout venuePhoneNumberLayout = view.findViewById(R.id.venuePhoneNumberLayout);
                                venuePhoneNumberLayout.setVisibility(view.GONE);
                            }

                            // venue open hours
                            try {
                                venueOpenHours = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("boxOfficeInfo").getString("openHoursDetail");
                            } catch (JSONException e) {
                                LinearLayout venueOpenHoursLayout = view.findViewById(R.id.venueOpenHoursLayout);
                                venueOpenHoursLayout.setVisibility(view.GONE);
                            }

                            // venue general rule
                            try {
                                venueGeneralRule = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("generalInfo").getString("generalRule");
                            } catch (JSONException e) {
                                LinearLayout venueGeneralRuleLayout = view.findViewById(R.id.venueGeneralRuleLayout);
                                venueGeneralRuleLayout.setVisibility(view.GONE);
                            }

                            // venue child rule
                            try {
                                venueChildRule = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("generalInfo").getString("childRule");
                            } catch (JSONException e) {
                                LinearLayout venueChildRuleLayout = view.findViewById(R.id.venueChildRuleLayout);
                                venueChildRuleLayout.setVisibility(view.GONE);
                            }

//                            // latitude and longitude
//                            String lat = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude");
//                            String lng = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude");
//                            latitude = Double.parseDouble(lat);
//                            longitude = Double.parseDouble(lng);

                            setVenueTabText(venueName,
                                    venueAddress,
                                    venueCity,
                                    venuePhoneNumber,
                                    venueOpenHours,
                                    venueGeneralRule,
                                    venueChildRule);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        eventVenueQueue.add(request);
    }


    private void setVenueTabText(String venueName,
                                 String venueAddress,
                                 String venueCity,
                                 String venuePhoneNumber,
                                 String venueOpenHours,
                                 String venueGeneralRule,
                                 String venueChildRule) {

        TextView venueNameTextView = view.findViewById(R.id.venueName);
        venueNameTextView.setText(venueName);

        TextView venueAddressTextView = view.findViewById(R.id.venueAddress);
        venueAddressTextView.setText(venueAddress);

        TextView venueCityTextView = view.findViewById(R.id.venueCity);
        venueCityTextView.setText(venueCity);

        TextView venuePhoneNumberTextView = view.findViewById(R.id.venuePhoneNumber);
        venuePhoneNumberTextView.setText(venuePhoneNumber);

        TextView venueOpenHoursTextView = view.findViewById(R.id.venueOpenHours);
        venueOpenHoursTextView.setText(venueOpenHours);

        TextView venueGeneralRuleTextView = view.findViewById(R.id.venueGeneralRule);
        venueGeneralRuleTextView.setText(venueGeneralRule);

        TextView venueChildRuleTextView = view.findViewById(R.id.venueChildRule);
        venueChildRuleTextView.setText(venueChildRule);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
        CameraPosition position = CameraPosition.builder().target(new LatLng(latitude, longitude)).zoom(14).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }
}