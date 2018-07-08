package com.xfl.kakaotalkbot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class PublicSettingsScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_settings);
        final SharedPreferences pref = getSharedPreferences("publicSettings", 0);
        final EditText jsoupTimeout = findViewById(R.id.jsouptimeout);
        final CheckBox chkAutoCompile = findViewById(R.id.chk_autoCompile);
        final EditText customPackages = findViewById(R.id.txt_customPackages);
        final FloatingActionButton fab = findViewById(R.id.fab_public_settings_apply);
        jsoupTimeout.setText(String.format(Locale.US, "%d", pref.getInt("jsoupTimeout", 10000)));
        customPackages.setText(pref.getString("customPackages", ""));
        chkAutoCompile.setChecked(pref.getBoolean("autoCompile", true));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pref.edit().putInt("jsoupTimeout", Integer.parseInt(jsoupTimeout.getText().toString())).apply();
                } catch (NumberFormatException e) {
                    jsoupTimeout.setText("2147483647");
                    pref.edit().putInt("jsoupTimeout", 2147483647).apply();

                    Toast.makeText(PublicSettingsScreen.this, "시간제한은 0~2147483647사이의 값으로 입력해주세요", Toast.LENGTH_SHORT).show();

                }
                pref.edit().putString("customPackages", customPackages.getText().toString()).apply();
                pref.edit().putBoolean("autoCompile", chkAutoCompile.isChecked()).apply();
                Snackbar.make(v, "Saved", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
