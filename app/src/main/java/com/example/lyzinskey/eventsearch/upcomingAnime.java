package com.example.lyzinskey.eventsearch;

public class upcomingAnime {
    private String upcomingEventName;
    private String upcomingEventArtist;
    private String upcomingEventTime;
    private String upcomingEventType;
    private String upcomingDate;
    private String upcomingTime;
    private String upcomingURL;

    public upcomingAnime() {
    }

    public upcomingAnime(String upcomingEventName, String upcomingEventArtist, String upcomingEventTime, String upcomingEventType, String upcomingDate, String upcomingTime, String upcomingURL) {
        this.upcomingEventName = upcomingEventName;
        this.upcomingEventArtist = upcomingEventArtist;
        this.upcomingEventTime = upcomingEventTime;
        this.upcomingEventType = upcomingEventType;
        this.upcomingDate = upcomingDate;
        this.upcomingTime = upcomingTime;
        this.upcomingURL = upcomingURL;
    }

    public String getUpcomingEventName() { return upcomingEventName; }

    public String getUpcomingEventArtist() { return upcomingEventArtist; }

    public String getUpcomingEventTime() { return upcomingEventTime; }

    public String getUpcomingEventType() { return upcomingEventType; }

    public String getUpcomingDate() { return upcomingDate; }

    public String getUpcomingTime() { return upcomingTime; }

    public String getUpcomingURL() { return upcomingURL; }

    public void setUpcomingEventName(String upcomingEventName) { this.upcomingEventName = upcomingEventName; }

    public void setUpcomingEventArtist(String upcomingEventArtist) { this.upcomingEventArtist = upcomingEventArtist; }

    public void setUpcomingEventTime(String upcomingEventTime) { this.upcomingEventTime = upcomingEventTime; }

    public void setUpcomingEventType(String upcomingEventType) { this.upcomingEventType = upcomingEventType; }

    public void setUpcomingDate(String upcomingDate) { this.upcomingDate = upcomingDate; }

    public void setUpcomingTime(String upcomingTime) { this.upcomingTime = upcomingTime; }

    public void setUpcomingURL(String upcomingURL) { this.upcomingURL = upcomingURL; }
}