package com.mlex0.musicschoolandroidclient.Model;

import com.mlex0.musicschoolandroidclient.Classes.Constants;

public class User {

    public String ID, Email, Password, UserImage, IsOnLine, LastOnlineTime, IDRole;


    public User() {

    }

    public User(String ID, String email, String password, String userImage, String isOnLine, String lastOnlineTime, String IDRole, String UserImage) {
        this.ID = ID;
        Email = email;
        Password = password;
        this.UserImage = userImage;
        IsOnLine = isOnLine;
        LastOnlineTime = lastOnlineTime;
        this.IDRole = IDRole;
    }

    public String getImageURL() {

        if(UserImage != null) {
            if(UserImage.equals("") || UserImage.equals("NULL")){
                return "default";
            }
            String rightPath = UserImage.toString().replace("uploads\\", "");
            String lastPath = Constants.ApiUrl.replace("api/", "") + "uploads/" + rightPath;;
            return lastPath;
        }

        return "default";
    }

}
