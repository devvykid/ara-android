package kr.oror.arabot

import android.graphics.Bitmap
import android.util.Base64

import java.io.ByteArrayOutputStream

class ImageDB(bitmap: Bitmap?) {
    private var profilePic: Bitmap? = null


    fun getProfileImage(): String {
        return getProfileBase64()
    }

    private fun getProfileBase64(): String {
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

    val profileBitmap: Bitmap?
        get() {
            return profilePic
        }
    val image: String?
        get() = null

    init {
        profilePic = bitmap
    }

}
