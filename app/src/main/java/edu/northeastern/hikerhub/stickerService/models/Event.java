package edu.northeastern.hikerhub.stickerService.models;

public class Event {

    public String eventId;
    public String stickerId;
    public String sender;
    public String receiver;
    public long timestampInMillis;
    public Boolean notifyStatus;


    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public Event(String eventId, String stickerId, String sender, String receiver, long timestampInMillis, Boolean notifyStatus) {
        this.eventId = eventId;
        this.stickerId = stickerId;
        this.sender = sender;
        this.receiver = receiver;
        this.timestampInMillis = timestampInMillis;
        this.notifyStatus = notifyStatus;
    }
}
