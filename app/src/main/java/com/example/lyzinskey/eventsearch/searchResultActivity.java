package com.example.lyzinskey.eventsearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class searchResultActivity extends AppCompatActivity {

    private List<Anime> animeList;
    private RecyclerView recyclerView;
    private LinearLayout searchingEvents;
    private TextView noSearchResult;
    private boolean noResult;

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_search_result);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        recyclerView = findViewById(R.id.recyclerviewID);
        searchingEvents = findViewById(R.id.searchingEvents);
        noSearchResult = findViewById(R.id.noSearchResult);
        noResult = false;

        animeList = new ArrayList<>();


        String resultTableString = getIntent().getExtras().getString("resultTableString");
        try {
            JSONObject resultTableJSON = new JSONObject(resultTableString);
            JSONArray events = resultTableJSON.getJSONObject("_embedded").getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                JSONObject singleEvent = events.getJSONObject(i);
                Anime anime = new Anime();

                anime.setName(singleEvent.getString("name"));

                anime.setTicketMasterURL(singleEvent.getString("url"));

                anime.setEventID(singleEvent.getString("id"));

                String category = singleEvent.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                anime.setClassifications(category);

                anime.setLocalDate(singleEvent.getJSONObject("dates").getJSONObject("start").getString("localDate"));
                anime.setLocalTime(singleEvent.getJSONObject("dates").getJSONObject("start").getString("localTime"));

                anime.setVenue(singleEvent.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));

                anime.setLat(singleEvent.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude"));

                anime.setLng(singleEvent.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude"));

                String categoryIconURL;
                if (category.equals("Music")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/music_icon.png";
                } else if (category.equals("Sports")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/sport_icon.png";
                } else if (category.equals("Miscellaneous")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/miscellaneous_icon.png";
                } else if (category.equals("Film")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/film_icon.png";
                } else {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/art_icon.png";
                }

                anime.setImageURL(categoryIconURL);

                animeList.add(anime);

                Log.i("Events", events.getJSONObject(i).toString());
            }
        } catch (JSONException e) {
            noResult = true;
            Log.i("ERROR", "No search result");
        }

        RecyclerViewAdapter myadapter = new RecyclerViewAdapter(this, animeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);
        searchingEvents.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        recyclerView = findViewById(R.id.recyclerviewID);
        searchingEvents = findViewById(R.id.searchingEvents);
        noSearchResult = findViewById(R.id.noSearchResult);
        noResult = false;

        animeList = new ArrayList<>();


        String resultTableString = getIntent().getExtras().getString("resultTableString");
        try {
            JSONObject resultTableJSON = new JSONObject(resultTableString);
            JSONArray events = resultTableJSON.getJSONObject("_embedded").getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                JSONObject singleEvent = events.getJSONObject(i);
                Anime anime = new Anime();

                anime.setName(singleEvent.getString("name"));

                anime.setTicketMasterURL(singleEvent.getString("url"));

                anime.setEventID(singleEvent.getString("id"));

                String category = singleEvent.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                anime.setClassifications(category);

                anime.setLocalDate(singleEvent.getJSONObject("dates").getJSONObject("start").getString("localDate"));
                anime.setLocalTime(singleEvent.getJSONObject("dates").getJSONObject("start").getString("localTime"));

                anime.setVenue(singleEvent.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));

                anime.setLat(singleEvent.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude"));

                anime.setLng(singleEvent.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude"));

                String categoryIconURL;
                if (category.equals("Music")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/music_icon.png";
                } else if (category.equals("Sports")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/sport_icon.png";
                } else if (category.equals("Miscellaneous")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/miscellaneous_icon.png";
                } else if (category.equals("Film")) {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/film_icon.png";
                } else {
                    categoryIconURL = "http://csci571.com/hw/hw9/images/android/art_icon.png";
                }

                anime.setImageURL(categoryIconURL);

                animeList.add(anime);

                Log.i("Events", events.getJSONObject(i).toString());
            }
        } catch (JSONException e) {
            noResult = true;
            Log.i("ERROR", "No search result");
        }

        setRecyclerView(animeList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setRecyclerView(List<Anime> animeList) {
        RecyclerViewAdapter myadapter = new RecyclerViewAdapter(this, animeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        searchingEvents.setVisibility(View.GONE);
                        if (noResult) {
                            noSearchResult.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noSearchResult.setVisibility(View.GONE);
                        }

                    }
                }, 1500);
    }
}
