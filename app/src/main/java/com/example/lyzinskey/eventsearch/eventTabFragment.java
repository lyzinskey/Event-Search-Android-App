package com.example.lyzinskey.eventsearch;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

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

public class eventTabFragment extends Fragment {
    private static final String TAG = "eventTabFragment";

    RequestQueue eventDetailQueue;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.event_detail_event_tab_fragment, container, false);

        eventDetailQueue = Volley.newRequestQueue(this.getContext());

        if (getActivity().getIntent().hasExtra("eventID")) {
            String eventID = getActivity().getIntent().getStringExtra("eventID");
            String eventDetailURL = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/eventID?eventID=" + eventID;
            getEventDetails(eventDetailURL);

        }

        return view;
    }

    public void getEventDetails(String eventDetailURL) {
        Log.i("eventDetailURL", eventDetailURL);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, eventDetailURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String eventArtistOrTeams = "";
                        String eventVenue = "";
                        String eventTime = "";
                        String eventCategory = "";
                        String eventPriceRange = "";
                        String eventTicketStatus = "";
                        String eventBuyTicketAt = "";
                        String eventSeatMap = "";
                        String eventName = "";


                        try {

                            // event name
                            eventName = response.getString("name");

                            // artist or team names
                            JSONArray attractions = response.getJSONObject("_embedded").getJSONArray("attractions");
                            StringBuilder artistOrTeams = new StringBuilder();
                            for (int i = 0; i < attractions.length(); i++) {
                                String singleName = attractions.getJSONObject(i).getString("name");
                                if (i == 0) {
                                    artistOrTeams.append(singleName);
                                } else {
                                    artistOrTeams.append(" | ");
                                    artistOrTeams.append(singleName);
                                }
                            }
                            eventArtistOrTeams = artistOrTeams.toString();

                            // venue name
                            try {
                                eventVenue = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                            } catch (JSONException e) {
                                LinearLayout eventVenueLayout = view.findViewById(R.id.eventVenueLayout);
                                eventVenueLayout.setVisibility(view.GONE);
                            }

                            // time
                            try {
                                String localDate = response.getJSONObject("dates").getJSONObject("start").getString("localDate");
                                String localTime = response.getJSONObject("dates").getJSONObject("start").getString("localTime");
                                eventTime = formatDate(localDate) + " " + localTime;
                            } catch (JSONException e) {
                                LinearLayout eventTimeLayout = view.findViewById(R.id.eventTimeLayout);
                                eventTimeLayout.setVisibility(view.GONE);
                            }

                            // category
                            try {
                                String segment = response.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                                String genre = response.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name");
                                eventCategory = segment + " | " + genre;
                            } catch (JSONException e) {
                                LinearLayout eventCategoryLayout = view.findViewById(R.id.eventCategoryLayout);
                                eventCategoryLayout.setVisibility(view.GONE);
                            }

                            // price range
                            try {
                                String min = "$" + response.getJSONArray("priceRanges").getJSONObject(0).getString("min");
                                String max = "$" + response.getJSONArray("priceRanges").getJSONObject(0).getString("max");
                                if (min.length() > 1 && max.length() > 1) {
                                    eventPriceRange = min + " ~ " + max;
                                } else if (min.length() > 1) {
                                    eventPriceRange = min;
                                } else {
                                    eventPriceRange = max;
                                }
                            } catch (JSONException e) {
                                LinearLayout eventPriceRangeLayout = view.findViewById(R.id.eventPriceRangeLayout);
                                eventPriceRangeLayout.setVisibility(view.GONE);
                            }

                            // ticket status
                            try {
                                eventTicketStatus = response.getJSONObject("dates").getJSONObject("status").getString("code");
                            } catch (JSONException e) {
                                LinearLayout eventTicketStatusLayout = view.findViewById(R.id.eventTicketStatusLayout);
                                eventTicketStatusLayout.setVisibility(view.GONE);
                            }

                            // buy ticket at
                            try {
                                eventBuyTicketAt = response.getString("url");
                            } catch (JSONException e) {
                                LinearLayout eventBuyTicketAtLayout = view.findViewById(R.id.eventBuyTicketAtLayout);
                                eventBuyTicketAtLayout.setVisibility(view.GONE);
                            }

                            // seat map
                            try {
                                eventSeatMap = response.getJSONObject("seatmap").getString("staticUrl");
                            } catch (JSONException e) {
                                LinearLayout eventSeatMapLayout = view.findViewById(R.id.eventSeatMapLayout);
                                eventSeatMapLayout.setVisibility(view.GONE);
                            }

                            setEventTabText(eventArtistOrTeams,
                                            eventVenue,
                                            eventTime,
                                            eventCategory,
                                            eventPriceRange,
                                            eventTicketStatus,
                                            eventBuyTicketAt,
                                            eventSeatMap);


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

        eventDetailQueue.add(request);
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

    private void setEventTabText(String eventArtistOrTeams,
                                 String eventVenue,
                                 String eventTime,
                                 String eventCategory,
                                 String eventPriceRange,
                                 String eventTicketStatus,
                                 String eventBuyTicketAt,
                                 String eventSeatMap) {

        TextView eventArtistOrTeamsTextView = view.findViewById(R.id.eventArtistOrTeams);
        eventArtistOrTeamsTextView.setText(eventArtistOrTeams);

        TextView eventVenueTextView = view.findViewById(R.id.eventVenue);
        eventVenueTextView.setText(eventVenue);

        TextView eventTimeTextView = view.findViewById(R.id.eventTime);
        eventTimeTextView.setText(eventTime);

        TextView eventCategoryTextView = view.findViewById(R.id.eventCategory);
        eventCategoryTextView.setText(eventCategory);

        TextView eventPriceRangeTextView = view.findViewById(R.id.eventPriceRange);
        eventPriceRangeTextView.setText(eventPriceRange);

        TextView eventTicketStatusTextView = view.findViewById(R.id.eventTicketStatus);
        eventTicketStatusTextView.setText(eventTicketStatus);


        TextView eventBuyTicketAtTextView = view.findViewById(R.id.eventBuyTicketAt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            eventBuyTicketAtTextView.setText(Html.fromHtml("<a href='" + eventBuyTicketAt + "' target='_blank'>Ticketmaster</a>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            eventBuyTicketAtTextView.setText(Html.fromHtml("<a href='" + eventBuyTicketAt + "' target='_blank'>Ticketmaster</a>"));
        }
        eventBuyTicketAtTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView eventSeatMapTextView = view.findViewById(R.id.eventSeatMap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            eventSeatMapTextView.setText(Html.fromHtml("<a href='" + eventSeatMap + "' target='_blank'>View Seat Map Here</a>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            eventSeatMapTextView.setText(Html.fromHtml("<a href='" + eventSeatMap + "' target='_blank'>View Seat Map Here</a>"));
        }
        eventSeatMapTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
