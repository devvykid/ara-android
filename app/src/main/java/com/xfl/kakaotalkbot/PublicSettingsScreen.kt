package com.xfl.kakaotalkbot

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import java.util.*

class PublicSettingsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_settings)
        val pref = getSharedPreferences("publicSettings", 0)
        val jsoupTimeout = findViewById<EditText>(R.id.jsouptimeout)
        val chkAutoCompile = findViewById<CheckBox>(R.id.chk_autoCompile)
        val chkResetSession = findViewById<CheckBox>(R.id.chk_resetSession)
        val customPackages = findViewById<EditText>(R.id.txt_customPackages)
        val fab = findViewById<FloatingActionButton>(R.id.fab_public_settings_apply)
        jsoupTimeout.setText(String.format(Locale.US, "%d", pref.getInt("jsoupTimeout", 10000)))
        customPackages.setText(pref.getString("customPackages", ""))
        chkAutoCompile.isChecked = pref.getBoolean("autoCompile", true)
        chkResetSession.isChecked=pref.getBoolean("resetSession",false)
        fab.setOnClickListener { v ->
            try {
                pref.edit().putInt("jsoupTimeout", Integer.parseInt(jsoupTimeout.text.toString())).apply()
            } catch (e: NumberFormatException) {
                jsoupTimeout.setText("2147483647")
                pref.edit().putInt("jsoupTimeout", 2147483647).apply()

                Toast.makeText(this@PublicSettingsScreen, "시간제한은 0~2147483647사이의 값으로 입력해주세요", Toast.LENGTH_SHORT).show()

            }
            pref.edit().putBoolean("resetSession",chkResetSession.isChecked).apply()

            pref.edit().putString("customPackages", customPackages.text.toString()).apply()
            pref.edit().putBoolean("autoCompile", chkAutoCompile.isChecked).apply()
            Snackbar.make(v, "Saved", Snackbar.LENGTH_SHORT).show()


            if (customPackages.text.toString().contains(applicationContext.packageName)) {
                Toast.makeText(this, "???????????????????????", Toast.LENGTH_LONG).show()

            }
        }
    }
}
