package com.example.lyzinskey.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class eventDetailActivity extends AppCompatActivity {

    private static final String TAG = "eventDetailActivity";

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private LinearLayout loading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);


        if (this.getIntent().hasExtra("eventName")) {
            String eventName = this.getIntent().getStringExtra("eventName");
            Toolbar mActionBarToolbar = findViewById(R.id.eventDetail_toolbar);
            setSupportActionBar(mActionBarToolbar);
            getSupportActionBar().setTitle(eventName);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mViewPager = findViewById(R.id.eventDetail_container);
        loading = findViewById(R.id.Loading);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.eventDetail_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i == 0) {
                tabLayout.getTabAt(i).setIcon(R.drawable.event_tab);
            } else if (i == 1) {
                tabLayout.getTabAt(i).setIcon(R.drawable.artist_tab);
            } else if (i == 2) {
                tabLayout.getTabAt(i).setIcon(R.drawable.venue_tab);
            } else {
                tabLayout.getTabAt(i).setIcon(R.drawable.upcoming_tab);
            }
        }



        if (this.getIntent().hasExtra("eventName")
                && this.getIntent().hasExtra("eventVenue")
                && this.getIntent().hasExtra("ticketMasterURL")
                && this.getIntent().hasExtra("eventID")) {
            String eventName = this.getIntent().getStringExtra("eventName");
            String eventVenue = this.getIntent().getStringExtra("eventVenue");
            String ticketMasterURL = this.getIntent().getStringExtra("ticketMasterURL");
            String eventID = this.getIntent().getStringExtra("eventID");

            // if an event is already in favorite, make the favorite button red
            Button favoriteButton = this.findViewById(R.id.favorite);
            SharedPreferences sharedPreferences = getSharedPreferences("favorite", Context.MODE_PRIVATE);

            String sharedPreferencesName = eventName + eventID;
            if (sharedPreferences.contains(sharedPreferencesName)) {
                favoriteButton.setBackgroundResource(R.drawable.heart_fill_red);
            } else {
                favoriteButton.setBackgroundResource(R.drawable.heart_fill_white);
            }


            twitter(eventName, eventVenue, ticketMasterURL);
            favoriteButton(eventName, eventID);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new eventTabFragment(), "event");
        adapter.addFragment(new artistTabFragment(), "artist(s)");
        adapter.addFragment(new venueTabFragment(), "venue");
        adapter.addFragment(new upcomingTabFragment(), "upcoming");
        viewPager.setAdapter(adapter);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        loading.setVisibility(View.GONE);
                        mViewPager.setVisibility(View.VISIBLE);
                    }
                }, 1500);
    }


    public void twitter(String eventName, String eventVenue, String eventBuyTicketAt) {
        Button twitterButton = this.findViewById(R.id.twitter);
        String twitterEventName = eventName;
        String twitterEventVenue = eventVenue;
        String twitterEventBuyTicketAt = eventBuyTicketAt;
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String twitterURL = "https://twitter.com/intent/tweet?text=Check out "
                        + twitterEventName
                        + " located at " + twitterEventVenue
                        + ". Website: " + twitterEventBuyTicketAt
                        + " %23CSCI571EventSearch";
                Uri uriUrl = Uri.parse(twitterURL);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    public void favoriteButton(String eventName, String eventID) {
        Button favoriteButton = this.findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("favorite", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String sharedPreferencesName = eventName + eventID;

                if (sharedPreferences.contains(sharedPreferencesName)) {
                    Toast.makeText(mViewPager.getContext(), eventName + " was removed from favorites", Toast.LENGTH_SHORT).show();
                    favoriteButton.setBackgroundResource(R.drawable.heart_fill_white);
                    editor.remove(sharedPreferencesName);
                    editor.commit();
                } else {
                    Toast.makeText(mViewPager.getContext(), eventName + " was added to favorites", Toast.LENGTH_SHORT).show();
                    favoriteButton.setBackgroundResource(R.drawable.heart_fill_red);
                    editor.putString(sharedPreferencesName, eventID);
                    editor.apply();
                }

            }
        });
    }
}
