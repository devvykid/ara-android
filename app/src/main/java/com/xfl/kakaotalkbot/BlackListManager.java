package com.xfl.kakaotalkbot;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by XFL on 2/21/2018.
 */

public class BlackListManager extends AppCompatActivity {
    private Context ctx = MainApplication.getContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklistmanager);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final String scriptName = getIntent().getExtras().getString("scriptName");
        final EditText banRoom = (EditText) findViewById(R.id.banRoom);
        final EditText banName = (EditText) findViewById(R.id.banName);
        banRoom.setText(ctx.getSharedPreferences("bot", 0).getString("banRoom" + scriptName, ""));
        banName.setText(ctx.getSharedPreferences("bot", 0).getString("banName" + scriptName, ""));
        findViewById(R.id.blackListApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctx.getSharedPreferences("bot", 0).edit().putString("banRoom" + scriptName, banRoom.getText().toString()).apply();
                ctx.getSharedPreferences("bot", 0).edit().putString("banName" + scriptName, banName.getText().toString()).apply();
                NotificationListener.initializeBanList(scriptName);
                finish();
            }
        });

    }
}
