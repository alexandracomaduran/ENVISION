package com.aac.envision;

public class Post {
    private String GlobalUserID;
    private String MediaURL;
    private int Index;
    private boolean isSelected;


    // Constructors, getters, and setters...

    public Post() {
        // Default constructor required for Firestore
    }

    public Post(String GlobalUserID, String MediaURL, int Index) {
        this.GlobalUserID = GlobalUserID;
        this.MediaURL = MediaURL;
        this.Index = Index;
    }
    

    // Getters and setters...

    public String getGlobalUserID() {
        return GlobalUserID;
    }

    public void setGlobalUserID(String globalUserID) {
        this.GlobalUserID = globalUserID;
    }

    public String getMediaURL() {
        return MediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.MediaURL = mediaURL;
    }


    public int getIndex() {
        return Index;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}