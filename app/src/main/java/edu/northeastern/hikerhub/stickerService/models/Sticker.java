package edu.northeastern.hikerhub.stickerService.models;

import edu.northeastern.hikerhub.R;

public class Sticker {

    public String stickerId;
    public String imageName;
    public int drawableId;

    public Sticker() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Sticker(String stickerId, String imageName, int drawableId) {
        this.stickerId = stickerId;
        this.imageName = imageName;
        this.drawableId = drawableId;
    }

}
