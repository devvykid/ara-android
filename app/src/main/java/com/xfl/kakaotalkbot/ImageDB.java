package com.xfl.kakaotalkbot;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageDB {
    private Bitmap profilePic = null;

    public ImageDB(Bitmap bitmap) {
        profilePic = bitmap;
    }

    public String getProfileImage() {
        if (profilePic == null) return "[[NO_PROFILE_PICTURE]]";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String getImage() {
        return null;
    }

}
