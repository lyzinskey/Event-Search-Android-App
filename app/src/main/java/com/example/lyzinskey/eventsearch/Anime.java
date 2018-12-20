package com.example.lyzinskey.eventsearch;

public class Anime {
    private String name;
    private String venue;
    private String localDate;
    private String localTime;
    private String classifications;
    private String eventID;
    private String imageURL;
    private String lat;
    private String lng;
    private String ticketMasterURL;

    public Anime() {
    }

    public Anime(String name, String venue, String localDate, String localTime, String classifications, String eventID, String imageURL, String lat, String lng, String ticketMasterURL) {
        this.name = name;
        this.venue = venue;
        this.localDate = localDate;
        this.localTime = localTime;
        this.classifications = classifications;
        this.eventID = eventID;
        this.imageURL = imageURL;
        this.lat = lat;
        this.lng = lng;
        this.ticketMasterURL = ticketMasterURL;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public String getLocalDate() {
        return localDate;
    }

    public String getLocalTime() {
        return localTime;
    }

    public String getClassifications() {
        return classifications;
    }

    public String getEventID() {
        return eventID;
    }

    public String getImageURL() { return imageURL; }

    public String getLat() { return lat; }

    public String getLng() { return lng; }

    public String getTicketMasterURL() { return ticketMasterURL; }

    public void setName(String name) {
        this.name = name;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public void setClassifications(String classifications) { this.classifications = classifications; }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public void setLat(String lat) { this.lat = lat; }

    public void setLng(String lng) { this.lng = lng; }

    public void setTicketMasterURL(String ticketMasterURL) { this.ticketMasterURL = ticketMasterURL; }
}
