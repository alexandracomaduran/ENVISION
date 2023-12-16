package com.aac.envision;

public class Post {
    private String GlobalUserID;
    private String MediaURL;
    private int Index;


    // Constructors, getters, and setters...

    public Post() {
        // Default constructor required for Firestore
    }

    public Post(String GlobalUserID, String MediaURL, int Index) {
        this.GlobalUserID = GlobalUserID;
        this.MediaURL = MediaURL;
        this.Index = Index;
        //Index will be set on the server side when document is created
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

}