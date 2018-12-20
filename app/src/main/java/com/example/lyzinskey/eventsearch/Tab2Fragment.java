package com.example.lyzinskey.eventsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "FAVOURITE";

    private List<FavoriteAnime> animeList;
    private RecyclerView recyclerView;
    View view;
    private RequestQueue favoriteQueue;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab2_fragment, container, false);

        favoriteQueue = Volley.newRequestQueue(this.getContext());
        recyclerView = view.findViewById(R.id.favoriteRecyclerview);
        animeList = new ArrayList<>();

        drawFavoriteList();
        return view;
    }

    private void drawFavoriteList() {
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("favorite", Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();

        if (keys.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            TextView noFavorite = view.findViewById(R.id.noFavorite);
            noFavorite.setVisibility(View.GONE);

            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                String eventDetailURL = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/eventID?eventID=" + entry.getValue();

                JsonRequest request = new JsonObjectRequest(Request.Method.GET, eventDetailURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    String eventName = response.getString("name");
                                    String eventVenue = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                                    String eventLocalDate = response.getJSONObject("dates").getJSONObject("start").getString("localDate");
                                    String eventLocalTime = response.getJSONObject("dates").getJSONObject("start").getString("localTime");
                                    String eventTime = eventLocalDate + " " + eventLocalTime;
                                    String classifications = response.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                                    String eventID = response.getString("id");
                                    String imageURL;
                                    String lat = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude");
                                    String lng = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude");
                                    String ticketMasterURL = response.getString("url");


                                    FavoriteAnime anime = new FavoriteAnime();

                                    anime.setName(eventName);
                                    anime.setVenue(eventVenue);
                                    anime.setLocalDate(eventLocalDate);
                                    anime.setLocalTime(eventLocalTime);
                                    anime.setEventTime(eventTime);
                                    anime.setClassifications(classifications);
                                    anime.setEventID(eventID);
                                    anime.setLat(lat);
                                    anime.setLng(lng);
                                    anime.setTicketMasterURL(ticketMasterURL);

                                    String categoryIconURL;
                                    if (classifications.equals("Music")) {
                                        categoryIconURL = "http://csci571.com/hw/hw9/images/android/music_icon.png";
                                    } else if (classifications.equals("Sports")) {
                                        categoryIconURL = "http://csci571.com/hw/hw9/images/android/sport_icon.png";
                                    } else if (classifications.equals("Miscellaneous")) {
                                        categoryIconURL = "http://csci571.com/hw/hw9/images/android/miscellaneous_icon.png";
                                    } else if (classifications.equals("Film")) {
                                        categoryIconURL = "http://csci571.com/hw/hw9/images/android/film_icon.png";
                                    } else {
                                        categoryIconURL = "http://csci571.com/hw/hw9/images/android/art_icon.png";
                                    }

                                    anime.setImageURL(categoryIconURL);

                                    animeList.add(anime);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                favoriteRecyclerViewAdapter myadapter = new favoriteRecyclerViewAdapter(view.getContext(), animeList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                recyclerView.setAdapter(myadapter);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        recyclerView.setVisibility(View.GONE);
                        TextView noFavorite = view.findViewById(R.id.noFavorite);
                        noFavorite.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                });
                favoriteQueue.add(request);
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            TextView noFavorite = view.findViewById(R.id.noFavorite);
            noFavorite.setVisibility(View.VISIBLE);
        }
    }
}
