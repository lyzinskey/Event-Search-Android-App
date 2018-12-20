package com.example.lyzinskey.eventsearch;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class artistTabFragment extends Fragment {
    private static final String TAG = "artistTabFragment";

    RequestQueue artistQueue;
    View view;
    RequestQueue spotifyQueue_first;
    RequestQueue spotifyQueue_second;
    RequestQueue googleCustomSearch_first;
    RequestQueue googleCustomSearch_second;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.artist_tab_row, container, false);
        artistQueue = Volley.newRequestQueue(this.getContext());
        spotifyQueue_first = Volley.newRequestQueue(this.getContext());
        spotifyQueue_second = Volley.newRequestQueue(this.getContext());
        googleCustomSearch_first = Volley.newRequestQueue(this.getContext());
        googleCustomSearch_second = Volley.newRequestQueue(this.getContext());

        if (getActivity().getIntent().hasExtra("eventID")) {
            String eventID = getActivity().getIntent().getStringExtra("eventID");
            String eventDetailURL = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/eventID?eventID=" + eventID;
            getCategory(eventDetailURL);
        }

        return view;
    }

    private void getCategory(String eventDetailURL) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, eventDetailURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // category
                            String category = response.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");

                            List<String> artistOrTeams = new ArrayList<>();
                            JSONArray attractions = response.getJSONObject("_embedded").getJSONArray("attractions");
                            for (int i = 0; i < attractions.length(); i++) {
                                artistOrTeams.add(attractions.getJSONObject(i).getString("name"));
                            }

                            if (category.equals("Music")) {
                                getSpotifyDetail(artistOrTeams);
                            }

                            googleCustomSearch(artistOrTeams);

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

        artistQueue.add(request);
    }

    private void getSpotifyDetail(List<String> artistOrTeams) {
        String url1 = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/spotify?artistOrTeamName=" + artistOrTeams.get(0);
        setSpotify_First(url1, artistOrTeams.get(0));

        if (artistOrTeams.size() >= 2) {
            String url2 = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/spotify?artistOrTeamName=" + artistOrTeams.get(1);
            setSpotify_Second(url2, artistOrTeams.get(1));
        }
    }

    private void setSpotify_First(String url1, String artistName) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("artistJSON", response.toString());
                        try {
                            LinearLayout artistDetailLayout_first = view.findViewById(R.id.artistDetailLayout_first);
                            artistDetailLayout_first.setVisibility(View.VISIBLE);

                            // followers
                            String followers = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getJSONObject("followers").getString("total");

                            // popularity
                            String popularity = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("popularity");

                            // spotify url
                            String spotify_url = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getJSONObject("external_urls").getString("spotify");


                            TextView artistNameTextView = view.findViewById(R.id.artistName_first);
                            artistNameTextView.setText(artistName);

                            TextView followersTextView = view.findViewById(R.id.artistFollowers_first);
                            followersTextView.setText(followers);

                            TextView popularityTextView = view.findViewById(R.id.artistPopularity_first);
                            popularityTextView.setText(popularity);


                            TextView spotifyTextView = view.findViewById(R.id.artistSpotify_first);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                spotifyTextView.setText(Html.fromHtml("<a href='" + spotify_url + "' target='_blank'>Spotify</a>", Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                spotifyTextView.setText(Html.fromHtml("<a href='" + spotify_url + "' target='_blank'>Spotify</a>"));
                            }
                            spotifyTextView.setMovementMethod(LinkMovementMethod.getInstance());


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

        spotifyQueue_first.add(request);
    }

    private void setSpotify_Second(String url2, String artistName) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("artistJSON", response.toString());
                        try {
                            LinearLayout artistDetailLayout_first = view.findViewById(R.id.artistDetailLayout_second);
                            artistDetailLayout_first.setVisibility(View.VISIBLE);

                            // followers
                            String followers = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getJSONObject("followers").getString("total");

                            // popularity
                            String popularity = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("popularity");

                            // spotify url
                            String spotify_url = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getJSONObject("external_urls").getString("spotify");


                            TextView artistNameTextView = view.findViewById(R.id.artistName_second);
                            artistNameTextView.setText(artistName);

                            TextView followersTextView = view.findViewById(R.id.artistFollowers_second);
                            followersTextView.setText(followers);

                            TextView popularityTextView = view.findViewById(R.id.artistPopularity_second);
                            popularityTextView.setText(popularity);


                            TextView spotifyTextView = view.findViewById(R.id.artistSpotify_second);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                spotifyTextView.setText(Html.fromHtml("<a href='" + spotify_url + "' target='_blank'>Spotify</a>", Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                spotifyTextView.setText(Html.fromHtml("<a href='" + spotify_url + "' target='_blank'>Spotify</a>"));
                            }
                            spotifyTextView.setMovementMethod(LinkMovementMethod.getInstance());


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

        spotifyQueue_second.add(request);
    }

    private void googleCustomSearch(List<String> artistOrTeams) {
        TextView artistNameTextView_second = view.findViewById(R.id.artistName_title_second);
        String url1 = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/googleCustomSearch?artistOrTeamName=" + artistOrTeams.get(0);
        setgoogleCustomSearch_First(url1, artistOrTeams.get(0));

        if (artistOrTeams.size() == 1) {
            artistNameTextView_second.setVisibility(View.GONE);
        } else if (artistOrTeams.size() >= 2) {
            String url2 = "http://lyzinskeycsci571hw9.us-west-1.elasticbeanstalk.com/googleCustomSearch?artistOrTeamName=" + artistOrTeams.get(1);
            artistNameTextView_second.setVisibility(View.VISIBLE);
            setgoogleCustomSearch_Second(url2, artistOrTeams.get(1));
        }
    }

    private void setgoogleCustomSearch_First(String url1, String artistName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String imageUrl1 = response.getJSONArray("items").getJSONObject(0).getString("link");
                            String imageUrl2 = response.getJSONArray("items").getJSONObject(1).getString("link");
                            String imageUrl3 = response.getJSONArray("items").getJSONObject(2).getString("link");
                            String imageUrl4 = response.getJSONArray("items").getJSONObject(3).getString("link");
                            String imageUrl5 = response.getJSONArray("items").getJSONObject(4).getString("link");
                            String imageUrl6 = response.getJSONArray("items").getJSONObject(5).getString("link");

                            TextView artistNameTextView = view.findViewById(R.id.artistName_title_first);
                            artistNameTextView.setText(artistName);

                            ImageView image1 = view.findViewById(R.id.image1_first);
                            Glide.with(view).load(imageUrl1).into(image1);

                            ImageView image2 = view.findViewById(R.id.image2_first);
                            Glide.with(view).load(imageUrl2).into(image2);

                            ImageView image3 = view.findViewById(R.id.image3_first);
                            Glide.with(view).load(imageUrl3).into(image3);

                            ImageView image4 = view.findViewById(R.id.image4_first);
                            Glide.with(view).load(imageUrl4).into(image4);

                            ImageView image5 = view.findViewById(R.id.image5_first);
                            Glide.with(view).load(imageUrl5).into(image5);

                            ImageView image6 = view.findViewById(R.id.image6_first);
                            Glide.with(view).load(imageUrl6).into(image6);

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

        googleCustomSearch_first.add(request);
    }

    private void setgoogleCustomSearch_Second(String url2, String artistName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String imageUrl1 = response.getJSONArray("items").getJSONObject(0).getString("link");
                            String imageUrl2 = response.getJSONArray("items").getJSONObject(1).getString("link");
                            String imageUrl3 = response.getJSONArray("items").getJSONObject(2).getString("link");
                            String imageUrl4 = response.getJSONArray("items").getJSONObject(3).getString("link");
                            String imageUrl5 = response.getJSONArray("items").getJSONObject(4).getString("link");
                            String imageUrl6 = response.getJSONArray("items").getJSONObject(5).getString("link");

                            TextView artistNameTextView = view.findViewById(R.id.artistName_title_second);
                            artistNameTextView.setText(artistName);

                            ImageView image1 = view.findViewById(R.id.image1_second);
                            Glide.with(view).load(imageUrl1).into(image1);

                            ImageView image2 = view.findViewById(R.id.image2_second);
                            Glide.with(view).load(imageUrl2).into(image2);

                            ImageView image3 = view.findViewById(R.id.image3_second);
                            Glide.with(view).load(imageUrl3).into(image3);

                            ImageView image4 = view.findViewById(R.id.image4_second);
                            Glide.with(view).load(imageUrl4).into(image4);

                            ImageView image5 = view.findViewById(R.id.image5_second);
                            Glide.with(view).load(imageUrl5).into(image5);

                            ImageView image6 = view.findViewById(R.id.image6_second);
                            Glide.with(view).load(imageUrl6).into(image6);

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

        googleCustomSearch_second.add(request);
    }
}
