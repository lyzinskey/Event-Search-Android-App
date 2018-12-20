package com.example.lyzinskey.eventsearch;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class UpcomingRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingRecyclerViewAdapter.UpcomingViewHolder> {


    private Context mContext;
    private List<upcomingAnime> mData;

    public UpcomingRecyclerViewAdapter(Context mContext, List<upcomingAnime> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.upcoming_single_role, viewGroup, false);

        return new UpcomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder upcomingViewHolder, int position) {

        String upcomingHTML = "<font color='#0099ff'><a href='" + mData.get(position).getUpcomingURL() + "' target='_blank'>"
                + mData.get(position).getUpcomingEventName() + "</a></font>";
        Spannable s = (Spannable) Html.fromHtml(upcomingHTML);
        for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        upcomingViewHolder.upcomingEventName.setText(s);
        upcomingViewHolder.upcomingEventName.setMovementMethod(LinkMovementMethod.getInstance());

        upcomingViewHolder.upcomingEventArtist.setText(mData.get(position).getUpcomingEventArtist());

        String upcomingEventTime;
        if (mData.get(position).getUpcomingTime().length() < 5) {
            upcomingEventTime = mData.get(position).getUpcomingDate();
        } else {
            upcomingEventTime = mData.get(position).getUpcomingDate() + " " + mData.get(position).getUpcomingTime();
        }
        upcomingViewHolder.upcomingEventTime.setText(upcomingEventTime);

        String upcomingEventType = "Type: " + mData.get(position).getUpcomingEventType();
        upcomingViewHolder.upcomingEventType.setText(upcomingEventType);

        upcomingViewHolder.upcomingURL.setText(mData.get(position).getUpcomingURL());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class UpcomingViewHolder extends RecyclerView.ViewHolder {

        TextView upcomingEventName;
        TextView upcomingEventArtist;
        TextView upcomingEventTime;
        TextView upcomingEventType;
        TextView upcomingURL;


        public UpcomingViewHolder(View itemView) {
            super(itemView);

            upcomingEventName = itemView.findViewById(R.id.upcomingEventName);
            upcomingEventArtist = itemView.findViewById(R.id.upcomingEventArtist);
            upcomingEventTime = itemView.findViewById(R.id.upcomingEventTime);
            upcomingEventType = itemView.findViewById(R.id.upcomingEventType);
            upcomingURL = itemView.findViewById(R.id.upcomingURL);
        }
    }
}
