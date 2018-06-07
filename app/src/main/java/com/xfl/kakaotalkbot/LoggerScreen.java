package com.xfl.kakaotalkbot;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by XFL on 2/19/2018.
 */

public class LoggerScreen extends AppCompatActivity {
    public static TextView logTxt;
    public static ScrollView scrollView;
    private static String scriptName;
    public FloatingActionButton goDownFab;

    public static void appendLogText(final Spanned str) {
        boolean move = false;

        if (scrollView != null) {

            if (scrollView.getScrollY() >= scrollView.getChildAt(0).getMeasuredHeight() - scrollView.getMeasuredHeight()) {

                move = true;
            }
        }
        NotificationListener.UIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (logTxt != null) {
                    logTxt.append(str);

                }
            }
        });

        if (move && scrollView != null) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    public static void clearLogText() {
        if (logTxt != null)
            logTxt.setText("");
    }

    public static void initialize() {
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.setScrollY(MainApplication.getContext().getSharedPreferences("logger", 0).getInt("scrollState", 0));
                }
            });
        }
    }

    private static String getLog() {
        Context ctx = MainApplication.getContext();
        return ctx.getSharedPreferences("log", 0).getString("log", "");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_logger);

        scriptName = getIntent().getExtras().getString("scriptName");
        logTxt = findViewById(R.id.logTxt);
        goDownFab = findViewById(R.id.fab_goDown);
        scrollView = findViewById(R.id.logger_scrollView);
        logTxt.setText(Html.fromHtml(getLog()));
        initialize();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    MainApplication.getContext().getSharedPreferences("logger", 0).edit().putInt("scrollState", scrollView.getScrollY()).apply();
                    if (scrollView.getScrollY() >= scrollView.getChildAt(0).getMeasuredHeight() - scrollView.getMeasuredHeight()) {

                        goDownFab.hide();
                    } else {
                        goDownFab.show();
                    }
                }
            });
        }
        goDownFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clearlogs) {
            clearLogText();

            Log.clear();
        }
        return super.onOptionsItemSelected(item);
    }
}
