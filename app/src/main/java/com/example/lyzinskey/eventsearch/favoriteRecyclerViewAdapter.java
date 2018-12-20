package com.example.lyzinskey.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Map;

public class favoriteRecyclerViewAdapter extends RecyclerView.Adapter<favoriteRecyclerViewAdapter.FavoriteViewHolder> {

    private Context mContext;
    private List<FavoriteAnime> mData;
    View view;
    RequestOptions option;

    public favoriteRecyclerViewAdapter(Context mContext, List<FavoriteAnime> mData) {
        this.mContext = mContext;
        this.mData = mData;

        // request option for Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.activity_favorite_single_row, viewGroup, false);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder favoriteViewHolder, int position) {
        final FavoriteAnime eventAnime = mData.get(position);
        favoriteViewHolder.eventName.setText(eventAnime.getName());
        favoriteViewHolder.venueName.setText(eventAnime.getVenue());

        String eventLocal_date_time = eventAnime.getLocalDate() + " " + eventAnime.getLocalTime();
        favoriteViewHolder.eventTime.setText(eventLocal_date_time);

        favoriteViewHolder.eventID.setText(eventAnime.getEventID());
        favoriteViewHolder.eventLatitude.setText(eventAnime.getLat());
        favoriteViewHolder.eventLongitude.setText(eventAnime.getLng());

        // load image from the internet and set it into Imageview using Glide
        Glide.with(mContext).load(eventAnime.getImageURL()).apply(option).into(favoriteViewHolder.eventCategory_thumbnail);


        favoriteButton(eventAnime.getName(), eventAnime.getEventID(), position);

        favoriteViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
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

    private void favoriteButton(String eventName, String eventID, int position) {
        Button favoriteButton = view.findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("favorite", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String sharedPreferencesName = eventName + eventID;

                Toast.makeText(view.getContext(), eventName + " was removed from favorites", Toast.LENGTH_SHORT).show();
                editor.remove(sharedPreferencesName);
                editor.commit();
                removeAt(position);
            }
        });
    }

    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("favorite", Context.MODE_PRIVATE);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {

        TextView eventName;
        TextView venueName;
        TextView eventTime;
        ImageView eventCategory_thumbnail;
        TextView eventID;
        TextView eventLatitude;
        TextView eventLongitude;
        LinearLayout linearLayout;


        public FavoriteViewHolder(View itemView) {
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
}
