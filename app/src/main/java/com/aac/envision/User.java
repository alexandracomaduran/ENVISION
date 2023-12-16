package com.aac.envision;

public class User {

    String email, pageDescription, profilePic;

    public User(){}
    public User(String email, String pageDescription, String profilePic) {
        this.email = email;
        this.pageDescription = pageDescription;
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

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

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
