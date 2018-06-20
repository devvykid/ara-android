package com.xfl.kakaotalkbot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;


/**
 * Created by XFL on 2/19/2018.
 */

public class SettingsScreen extends AppCompatActivity {
    boolean isAdEverInitialized = false;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settingsscreen);
        final String scriptName = getIntent().getExtras().getString("scriptName");
        final CheckBox chkNormal = findViewById(R.id.chk_useNormal);
        final CheckBox chkParallelSpace = findViewById(R.id.chk_useParallelSpace);
        final CheckBox chkFacebookMessenger = findViewById(R.id.chk_useFacebookMessenger);
        final CheckBox chkLine = findViewById(R.id.chk_useLine);
        final CheckBox chkTelegram = findViewById(R.id.chk_useTelegram);

        final CheckBox chkJBBotCompat = findViewById(R.id.chk_JBBotCompat);
        final CheckBox chkOffOnRuntimeError = findViewById(R.id.chk_OffOnRuntimeError);
        final CheckBox chkOnDeleteBackup = findViewById(R.id.chk_OnDeleteBackup);
        final CheckBox chkIgnoreApiOff = findViewById(R.id.chk_ignoreApiOff);
        final CheckBox chkAllowBridge = findViewById(R.id.chk_allowBridge);
        final CheckBox chkResetSession = findViewById(R.id.chk_resetSession);
        final CheckBox chkSpecificLog = findViewById(R.id.chk_specificLog);
        final CheckBox chkUseUnifiedParams = findViewById(R.id.chk_useUnifiedParams);
        final SeekBar optimization = findViewById(R.id.optimization);


        final Context context = this;
        final SharedPreferences pref = context.getSharedPreferences("settings" + scriptName, 0);
        chkNormal.setChecked(pref.getBoolean("useNormal", true));
        chkParallelSpace.setChecked(pref.getBoolean("useParallelSpace", false));
        chkFacebookMessenger.setChecked(pref.getBoolean("useFacebookMessenger", false));
        chkLine.setChecked(pref.getBoolean("useLine", false));
        chkTelegram.setChecked(pref.getBoolean("useTelegram", false));

        chkOffOnRuntimeError.setChecked(pref.getBoolean("offOnRuntimeError", true));
        chkJBBotCompat.setChecked(pref.getBoolean("JBBot", false));
        chkOnDeleteBackup.setChecked(pref.getBoolean("onDeleteBackup", true));
        chkIgnoreApiOff.setChecked(pref.getBoolean("ignoreApiOff", false));
        chkAllowBridge.setChecked(pref.getBoolean("allowBridge", true));
        chkResetSession.setChecked(pref.getBoolean("resetSession", false));
        chkSpecificLog.setChecked(pref.getBoolean("specificLog", false));
        chkUseUnifiedParams.setChecked(pref.getBoolean("useUnifiedParams", false));
        optimization.setProgress(pref.getInt("optimization", -2) + 1);
        findViewById(R.id.fab_settings_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pref.edit().putBoolean("useNormal", chkNormal.isChecked()).apply();
                pref.edit().putBoolean("useParallelSpace", chkParallelSpace.isChecked()).apply();
                pref.edit().putBoolean("useFacebookMessenger", chkFacebookMessenger.isChecked()).apply();
                pref.edit().putBoolean("useLine", chkLine.isChecked()).apply();
                pref.edit().putBoolean("useTelegram", chkTelegram.isChecked()).apply();

                pref.edit().putBoolean("JBBot", chkJBBotCompat.isChecked()).apply();
                pref.edit().putBoolean("offOnRuntimeError", chkOffOnRuntimeError.isChecked()).apply();
                pref.edit().putBoolean("onDeleteBackup", chkOnDeleteBackup.isChecked()).apply();
                pref.edit().putBoolean("ignoreApiOff", chkIgnoreApiOff.isChecked()).apply();
                pref.edit().putBoolean("allowBridge", chkAllowBridge.isChecked()).apply();
                pref.edit().putBoolean("resetSession", chkResetSession.isChecked()).apply();
                pref.edit().putBoolean("specificLog", chkSpecificLog.isChecked()).apply();
                pref.edit().putBoolean("useUnifiedParams", chkUseUnifiedParams.isChecked()).apply();
                pref.edit().putInt("optimization", optimization.getProgress() - 1).apply();

                Snackbar.make(view, R.string.settings_snackbar_applied, Snackbar.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_blacklistMng).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainApplication.getContext(), BlackListManager.class);
                intent.putExtra("scriptName", scriptName);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainApplication.getContext(), HelpActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_deleteScript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingsScreen.this);
                ad.setTitle(R.string.alert_delete_script);
                final EditText et = new EditText(SettingsScreen.this);
                et.setHint(R.string.alert_delete_script_hint);
                FrameLayout container = new FrameLayout(SettingsScreen.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                et.setLayoutParams(params);
                container.addView(et);
                ad.setView(container);
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().toString().equals(scriptName)) {

                            new File(MainApplication.basePath.getPath() + File.separator + scriptName).delete();
                            Toast.makeText(SettingsScreen.this, "Deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            SettingsScreen.this.finish();
                        }
                    }
                });
                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });


                ad.show();

            }
        });
        final Button btnShowAd = findViewById(R.id.btn_showAd);
        btnShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, getResources().getString(R.string.wait_for_ad), Toast.LENGTH_SHORT).show();
                if (!isAdEverInitialized) {
                    MobileAds.initialize(context, "ca-app-pub-8316062499073563~1566457333");
                    isAdEverInitialized = true;
                }


                loadInterstitialAd();

            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8316062499073563/7738424496");//testcode: ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                switch (errorCode) {
                    case ERROR_CODE_INTERNAL_ERROR:
                        Toast.makeText(context, "구글이 뭔가 일을 저지른것 같습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_CODE_INVALID_REQUEST:
                        Toast.makeText(context, "개발자가 멍청해서 광고 등록을 잘못했습니다. 개발자에게 연락해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_CODE_NO_FILL:
                        Toast.makeText(context, "구글 광고 서버가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_CODE_NETWORK_ERROR:
                        Toast.makeText(context, "네트워크 연결 확인 후 다시 시도해주세요~!", Toast.LENGTH_SHORT).show();
                    default:
                        Toast.makeText(context, "광고 로드에 실패했습니다. 다음 에러 코드를 개발자에게 알려주세요: " + errorCode, Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });

    }


    private void loadInterstitialAd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("5AB011F116AEA8A39CE0E8414D0C1749").build());

    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


}
