package com.xfl.kakaotalkbot

import android.graphics.Bitmap
import android.util.Base64

import java.io.ByteArrayOutputStream

class ImageDB(bitmap: Bitmap) {
    private var profilePic: Bitmap? = null

    val profileImage: String
        get() {
            if (profilePic == null) return "[[NO_PROFILE_PICTURE]]"
            val byteArrayOutputStream = ByteArrayOutputStream()

            profilePic!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            try {
                byteArrayOutputStream.close()
            } catch (e: Throwable) {
                MainApplication.reportInternalError(e)
            }

            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

    val image: String?
        get() = null

    init {
        profilePic = bitmap
    }

}
