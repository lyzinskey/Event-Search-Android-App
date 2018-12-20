package com.example.lyzinskey.eventsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class upcomingTabFragment extends Fragment {
    private static final String TAG = "upcomingTabFragment";
    private RequestQueue mVenueIDQueue;
    private RequestQueue mUpcomingQueue;
    String venue;
    View view;
    private List<upcomingAnime> upcomingAnimeList;
    private List<upcomingAnime> defaultList;
    private RecyclerView recyclerView;
    private boolean ascending;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.upcoming_all_results, container, false);
        mVenueIDQueue = Volley.newRequestQueue(this.getContext());
        mUpcomingQueue = Volley.newRequestQueue(this.getContext());
        recyclerView = view.findViewById(R.id.upcomingRecyclerView);
        upcomingAnimeList = new ArrayList<>();
        defaultList = new ArrayList<>();
        ascending = true;

        if (getActivity().getIntent().hasExtra("eventVenue")) {
            venue = getActivity().getIntent().getStringExtra("eventVenue");
            String venueIDURL = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/upcomingEventsID?&venueName=" + venue;
            getVenueID(venueIDURL);
        }

        return view;
    }

    private void setupAscendingOrDescending() {
        Spinner ascendingOrDescendingSpinner = view.findViewById(R.id.ascendingOrDescending_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.ascendingOrDescending, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ascendingOrDescendingSpinner.setAdapter(adapter);

        ascendingOrDescendingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ascending = true;
                } else if (position == 1) {
                    ascending = false;
                }
                setupSortSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSortSelection() {
        Spinner sortingSelectionSpinner = view.findViewById(R.id.sortingSelection_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.sortingSelection, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSelectionSpinner.setAdapter(adapter);

        sortingSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    UpcomingRecyclerViewAdapter myadapter = new UpcomingRecyclerViewAdapter(getContext(), defaultList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(myadapter);
                } else {
                    if (position == 1) {
                        sortByName();
                    } else if (position == 2) {
                        sortByTime();
                    } else if (position == 3) {
                        sortByArtist();
                    } else if (position == 4) {
                        sortByType();
                    }
                    UpcomingRecyclerViewAdapter myadapter = new UpcomingRecyclerViewAdapter(getContext(), upcomingAnimeList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(myadapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sortByName() {
        Collections.sort(upcomingAnimeList, (anime1, anime2) -> {
            if (ascending) {
                return anime1.getUpcomingEventName().compareTo(anime2.getUpcomingEventName());
            } else {
                return anime2.getUpcomingEventName().compareTo(anime1.getUpcomingEventName());
            }
        });
    }

    private void sortByTime() {
        Collections.sort(upcomingAnimeList, (anime1, anime2) -> {
            if (ascending) {
                return anime1.getUpcomingDate().compareTo(anime2.getUpcomingDate());
            } else {
                return anime2.getUpcomingDate().compareTo(anime1.getUpcomingDate());
            }
        });
    }

    private void sortByArtist() {
        Collections.sort(upcomingAnimeList, (anime1, anime2) -> {
            if (ascending) {
                return anime1.getUpcomingEventArtist().compareTo(anime2.getUpcomingEventArtist());
            } else {
                return anime2.getUpcomingEventArtist().compareTo(anime1.getUpcomingEventArtist());
            }
        });
    }

    private void sortByType() {
        Collections.sort(upcomingAnimeList, (anime1, anime2) -> {
            if (ascending) {
                return anime1.getUpcomingEventType().compareTo(anime2.getUpcomingEventType());
            } else {
                return anime2.getUpcomingEventType().compareTo(anime1.getUpcomingEventType());
            }
        });
    }


    public void getVenueID(String VenueIDURL) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, VenueIDURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // venue name
                            String venueID = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("venue").getJSONObject(0).getString("id");
                            getUpcomingJSON(venueID);

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

        mVenueIDQueue.add(request);
    }

    public void getUpcomingJSON(String venueID) {
        String upcomingURL = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/upcomingEventsDetail?&upcomingEventsID=" + venueID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, upcomingURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // venue name
                            JSONArray upcomingEvents = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
                            for (int i = 0; i < upcomingEvents.length(); i++) {
                                try {
                                    JSONObject singleEvent = upcomingEvents.getJSONObject(i);
                                    upcomingAnime anime = new upcomingAnime();

                                    anime.setUpcomingEventName(singleEvent.getString("displayName"));

                                    anime.setUpcomingEventArtist(singleEvent.getJSONArray("performance").getJSONObject(0).getString("displayName"));

                                    anime.setUpcomingDate(formatDate(singleEvent.getJSONObject("start").getString("date")));

                                    anime.setUpcomingTime(singleEvent.getJSONObject("start").getString("time"));

                                    anime.setUpcomingEventType(singleEvent.getString("type"));

                                    anime.setUpcomingURL(singleEvent.getString("uri"));

                                    upcomingAnimeList.add(anime);
                                    defaultList.add(anime);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            setupSortSelection();
                            setupAscendingOrDescending();

                            UpcomingRecyclerViewAdapter myadapter = new UpcomingRecyclerViewAdapter(getContext(), upcomingAnimeList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(myadapter);

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

        mUpcomingQueue.add(request);
    }

    private String formatDate(String date) {
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MMM dd, yyyy");
        String reformattedStr = "";

        try {

            reformattedStr = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }
}
