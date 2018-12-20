package com.example.lyzinskey.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Anime> mData;
    RequestOptions option;
    View view;

    public RecyclerViewAdapter(Context mContext, List<Anime> mData) {
        this.mContext = mContext;
        this.mData = mData;

        // request option for Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.activity_search_result_single_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final Anime eventAnime = mData.get(position);
        myViewHolder.eventName.setText(eventAnime.getName());
        myViewHolder.venueName.setText(eventAnime.getVenue());

        String eventLocal_date_time = eventAnime.getLocalDate() + " " + eventAnime.getLocalTime();
        myViewHolder.eventTime.setText(eventLocal_date_time);

        myViewHolder.eventID.setText(eventAnime.getEventID());
        myViewHolder.eventLatitude.setText(eventAnime.getLat());
        myViewHolder.eventLongitude.setText(eventAnime.getLng());

        // load image from the internet and set it into Imageview using Glide
        Glide.with(mContext).load(eventAnime.getImageURL()).apply(option).into(myViewHolder.eventCategory_thumbnail);

        // if an event is already in favorite, make the favorite button red
        Button favoriteButton = view.findViewById(R.id.favorite);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("favorite", Context.MODE_PRIVATE);

        String sharedPreferencesName = eventAnime.getName() + eventAnime.getEventID();
        if (sharedPreferences.contains(sharedPreferencesName)) {
            favoriteButton.setBackgroundResource(R.drawable.heart_fill_red);
        } else {
            favoriteButton.setBackgroundResource(R.drawable.heart_outline_black);
        }


        favoriteButton(eventAnime.getName(), eventAnime.getEventID());

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, eventDetailActivity.class);
                intent.putExtra("eventName", eventAnime.getName());
                intent.putExtra("eventID", eventAnime.getEventID());
                intent.putExtra("eventVenue", eventAnime.getVenue());
                intent.putExtra("eventLatitude", eventAnime.getLat());
                intent.putExtra("eventLongitude", eventAnime.getLng());
                intent.putExtra("ticketMasterURL", eventAnime.getTicketMasterURL());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView venueName;
        TextView eventTime;
        ImageView eventCategory_thumbnail;
        TextView eventID;
        TextView eventLatitude;
        TextView eventLongitude;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
            venueName = itemView.findViewById(R.id.venue);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventCategory_thumbnail = itemView.findViewById(R.id.thumbnail);
            eventID = itemView.findViewById(R.id.eventID);
            eventLatitude = itemView.findViewById(R.id.eventLatitude);
            eventLongitude = itemView.findViewById(R.id.eventLongitude);
            linearLayout = itemView.findViewById(R.id.eventsLinearLayout);
        }
    }

    public void favoriteButton(String eventName, String eventID) {
        Button favoriteButton = view.findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("favorite", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String sharedPreferencesName = eventName + eventID;

                if (sharedPreferences.contains(sharedPreferencesName)) {
                    Toast.makeText(view.getContext(), eventName + " was removed from favorites", Toast.LENGTH_SHORT).show();
                    favoriteButton.setBackgroundResource(R.drawable.heart_outline_black);
                    editor.remove(sharedPreferencesName);
                    editor.commit();
                } else {
                    Toast.makeText(view.getContext(), eventName + " was added to favorites", Toast.LENGTH_SHORT).show();
                    favoriteButton.setBackgroundResource(R.drawable.heart_fill_red);
                    editor.putString(sharedPreferencesName, eventID);
                    editor.apply();
                }
            }
        });
    }
}
