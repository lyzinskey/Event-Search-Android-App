package com.example.lyzinskey.eventsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tab1Fragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SEARCH";

    Spinner categorySpinner;

    EditText distanceInput;
    Spinner distanceSpinner;

    RadioGroup radioGroup;
    RadioButton currentLocationRadioButton;
    RadioButton otherLocationRadioButton;

    EditText otherLocationInput;

    Button search_btnTest;
    Button clear_btnTest;

    TextView invalidKeyword;
    TextView invalidLocation;

    LocationManager locationManager;
    LocationListener locationListener;
    double userLatitude;
    double userLongitude;

    double geoLat;
    double geoLng;

    private RequestQueue mgeoLocationQueue;
    private RequestQueue mresultTableQueue;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    AppCompatAutoCompleteTextView autoCompleteTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


        // invalid keyword input
        invalidKeyword = view.findViewById(R.id.keyword_validation_textView);
        invalidKeyword.setVisibility(View.GONE);

        // invalid location input
        invalidLocation = view.findViewById(R.id.location_validation_textView);
        invalidLocation.setVisibility(View.GONE);

        // keyword input
        autoCompleteTextView = view.findViewById(R.id.auto_complete_edit_text);
//        keywordInput = view.findViewById(R.id.keywordEditText);

        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(this.getContext(), android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });


        // category selection
        categorySpinner = view.findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.category, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        // distance input
        distanceInput = view.findViewById(R.id.distanceEditText);

        // distance selection
        distanceSpinner = view.findViewById(R.id.distanceSpinner);
        ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.distance, android.R.layout.simple_spinner_item);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        distanceSpinner.setAdapter(distanceAdapter);
        distanceSpinner.setOnItemSelectedListener(this);

        // current location or other location radio
        radioGroup = view.findViewById(R.id.location_radio_group);
        currentLocationRadioButton = view.findViewById(R.id.current_location_radio);
        otherLocationRadioButton = view.findViewById(R.id.other_location_radio);

        // other location input
        otherLocationInput = view.findViewById(R.id.other_location_editText);
        otherLocationInput.setEnabled(false);

        currentLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherLocationInput.setText("");
                otherLocationInput.setEnabled(false);
                invalidLocation.setVisibility(View.GONE);
            }
        });

        otherLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherLocationInput.setEnabled(true);
            }
        });

        search_btnTest = view.findViewById(R.id.search_btnTest);


        // geoCode JSON
        mgeoLocationQueue = Volley.newRequestQueue(this.getContext());

        // search result JSON
        mresultTableQueue = Volley.newRequestQueue(this.getContext());


        search_btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoCompleteTextView.getText().toString().length() > 0) {
//                    Log.i("Keyword: ", keywordInput.getText().toString());
                } else {
//                    Log.i("Keyword: ", "No input");
                    invalidKeyword.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }

//                Log.i("Category: ", categorySpinner.getSelectedItem().toString());

//                if (distanceInput.getText().toString().length() > 0) {
//                    Log.i("Distance: ", distanceInput.getText().toString());
//                } else {
//                    Log.i("Distance: ", "10");
//                }

//                Log.i("Distance: ", distanceSpinner.getSelectedItem().toString());

                if (currentLocationRadioButton.isChecked()) {
//                    Log.i("From: ", "Current Location");
                } else if (otherLocationRadioButton.isChecked()) {
                    if (otherLocationInput.getText().toString().length() > 0) {
//                        Log.i("From: ", otherLocationInput.getText().toString());
                    } else {
//                        Log.i("From: ", "No input");
                        invalidLocation.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                    }
                }

                if (autoCompleteTextView.getText().toString().length() > 0) {
                    if (currentLocationRadioButton.isChecked() || otherLocationRadioButton.isChecked() && otherLocationInput.getText().toString().length() > 0) {

                        getGeoLocation();
                    }
                }
            }
        });

        clear_btnTest = view.findViewById(R.id.clear_btnTest);

        clear_btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "CLEAR BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                clear();

            }
        });

        return view;
    }

    private void makeApiCall(String text) {
        ApiCall.make(this.getContext(), text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONObject("_embedded").getJSONArray("attractions");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void clear() {
        invalidKeyword.setVisibility(View.GONE);
        invalidLocation.setVisibility(View.GONE);
        autoCompleteTextView.setText("");
        categorySpinner.setSelection(0);
        distanceInput.setText("");
        currentLocationRadioButton.setChecked(true);
        otherLocationInput.setText("");
        otherLocationInput.setEnabled(false);
    }

    public void getGeoLocation() {
        String latlng = Double.toString(userLatitude) + "," + Double.toString(userLongitude);
        String url;
        if (otherLocationInput.getText().toString().length() > 0) {
            url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                    + otherLocationInput.getText().toString()
                    + "&key=AIzaSyAkZNY2CiQKs6sqp5iekdWGuxFpuVXIV80";
        } else {
            url = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/geocode?"
                    + "&enteredLocation=" + otherLocationInput.getText().toString()
                    + "&latlng=" + latlng;
        }
        Log.i("geoURL: ", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            geoLat = location.getDouble("lat");
                            geoLng = location.getDouble("lng");
                            showResultTable(geoLat, geoLng);
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

        mgeoLocationQueue.add(request);
    }

    public void showResultTable(double geoLat, double geoLng) {
        Log.i("getLat: ", Double.toString(geoLat));
        Log.i("getLng: ", Double.toString(geoLng));

        String category;
        if (categorySpinner.getSelectedItem().toString().equals("Music")) {
            category = "music";
        } else if (categorySpinner.getSelectedItem().toString().equals("Sports")) {
            category = "sports";
        } else if (categorySpinner.getSelectedItem().toString().equals("Arts & Theatre")) {
            category = "arts";
        } else if (categorySpinner.getSelectedItem().toString().equals("Film")) {
            category = "film";
        } else if (categorySpinner.getSelectedItem().toString().equals("Miscellaneous")) {
            category = "miscellaneous";
        } else {
            category = "all";
        }

        String distance;
        if (distanceInput.getText().toString().length() > 0) {
            distance = distanceInput.getText().toString();
        } else {
            distance = "10";
        }

        String milesOrKilometer;
        if (distanceSpinner.getSelectedItem().toString().equals("Miles")) {
            milesOrKilometer = "miles";
        } else {
            milesOrKilometer = "km";
        }

        String url = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/resultTable?"
                + "keyword=" + autoCompleteTextView.getText().toString()
                + "&category=" + category
                + "&distance=" + distance
                + "&milesOrKilometer=" + milesOrKilometer
                + "&location=" + ""
                + "&enteredLocation=" + ""
                + "&lat=" + Double.toString(geoLat)
                + "&lng=" + Double.toString(geoLng);

        Log.i("resultTableURL: ", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        openSearchResult(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mresultTableQueue.add(request);
    }

    public void openSearchResult(String resultTableString) {
        Intent intent = new Intent(this.getActivity(), searchResultActivity.class);
        intent.putExtra("resultTableString", resultTableString);
        startActivity(intent);

    }
}
