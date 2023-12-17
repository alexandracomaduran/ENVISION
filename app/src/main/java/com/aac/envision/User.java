package com.aac.envision;

public class User {

    String email, pageDescription, profilePic;
    private String documentId;
    private boolean isSelected;

    public User(){}
    public User(String email, String pageDescription, String profilePic, String GlobalUserID) {
        this.email = email;
        this.pageDescription = pageDescription;
        this.profilePic = profilePic;
        this.documentId = documentId;
    }

    public String getEmail() {
        return email;
    }
    public String getGlobalUserID() {return documentId;}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPageDescription() {
        return pageDescription;
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = pageDescription;
    }

    public String getProfilePic() {
        return profilePic;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
