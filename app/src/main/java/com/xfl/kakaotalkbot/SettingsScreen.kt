package com.xfl.kakaotalkbot

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdRequest.*
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import java.io.File
import java.util.*


/**
 * Created by XFL on 2/19/2018.
 */

class SettingsScreen : AppCompatActivity() {
    private var isAdEverInitialized = false
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settingsscreen)
        val packGroup = ArrayList<CheckBox>()
        val context = this
        val scriptName = intent.extras!!.getString("scriptName")
        val pref = context.getSharedPreferences("settings" + scriptName!!, 0)
        val customPackages = context.getSharedPreferences("publicSettings", 0).getString("customPackages", "")

        for ( kk in customPackages!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            var k = kk.trim { it <= ' ' }
            if (k.isEmpty()) continue
            val chk = CheckBox(this)

            chk.isChecked = context.getSharedPreferences("customs$scriptName", 0).getBoolean(k, false)
            chk.text = k
            (findViewById<View>(R.id.linear_packages) as LinearLayout).addView(chk)
            packGroup.add(chk)
        }
        val chkNormal = findViewById<CheckBox>(R.id.chk_useNormal)
        val chkParallelSpace = findViewById<CheckBox>(R.id.chk_useParallelSpace)
        val chkFacebookMessenger = findViewById<CheckBox>(R.id.chk_useFacebookMessenger)
        val chkLine = findViewById<CheckBox>(R.id.chk_useLine)
        val chkTelegram = findViewById<CheckBox>(R.id.chk_useTelegram)

        val chkJBBotCompat = findViewById<CheckBox>(R.id.chk_JBBotCompat)
        val chkOffOnRuntimeError = findViewById<CheckBox>(R.id.chk_OffOnRuntimeError)
        val chkOnDeleteBackup = findViewById<CheckBox>(R.id.chk_OnDeleteBackup)
        val chkIgnoreApiOff = findViewById<CheckBox>(R.id.chk_ignoreApiOff)
        val chkAllowBridge = findViewById<CheckBox>(R.id.chk_allowBridge)

        val chkSpecificLog = findViewById<CheckBox>(R.id.chk_specificLog)
        val chkUseUnifiedParams = findViewById<CheckBox>(R.id.chk_useUnifiedParams)
        val optimization = findViewById<SeekBar>(R.id.optimization)


        chkNormal.isChecked = pref.getBoolean("useNormal", true)
        chkParallelSpace.isChecked = pref.getBoolean("useParallelSpace", false)
        chkFacebookMessenger.isChecked = pref.getBoolean("useFacebookMessenger", false)
        chkLine.isChecked = pref.getBoolean("useLine", false)
        chkTelegram.isChecked = pref.getBoolean("useTelegram", false)

        chkOffOnRuntimeError.isChecked = pref.getBoolean("offOnRuntimeError", true)
        chkJBBotCompat.isChecked = pref.getBoolean("JBBot", false)
        chkOnDeleteBackup.isChecked = pref.getBoolean("onDeleteBackup", true)
        chkIgnoreApiOff.isChecked = pref.getBoolean("ignoreApiOff", false)
        chkAllowBridge.isChecked = pref.getBoolean("allowBridge", true)

        chkSpecificLog.isChecked = pref.getBoolean("specificLog", false)
        chkUseUnifiedParams.isChecked = pref.getBoolean("useUnifiedParams", false)
        optimization.progress = pref.getInt("optimization", -2) + 1
        findViewById<View>(R.id.fab_settings_apply).setOnClickListener { view ->
            pref.edit().putBoolean("useNormal", chkNormal.isChecked).apply()
            pref.edit().putBoolean("useParallelSpace", chkParallelSpace.isChecked).apply()
            pref.edit().putBoolean("useFacebookMessenger", chkFacebookMessenger.isChecked).apply()
            pref.edit().putBoolean("useLine", chkLine.isChecked).apply()
            pref.edit().putBoolean("useTelegram", chkTelegram.isChecked).apply()
            for (k in context.getSharedPreferences("customs$scriptName", 0).all.keys) {
                context.getSharedPreferences("customs$scriptName", 0).edit().putBoolean(k, false).apply()
            }
            for (chk in packGroup) {
                context.getSharedPreferences("customs$scriptName", 0).edit().putBoolean(chk.text.toString(), chk.isChecked).apply()
            }
            pref.edit().putBoolean("JBBot", chkJBBotCompat.isChecked).apply()
            pref.edit().putBoolean("offOnRuntimeError", chkOffOnRuntimeError.isChecked).apply()
            pref.edit().putBoolean("onDeleteBackup", chkOnDeleteBackup.isChecked).apply()
            pref.edit().putBoolean("ignoreApiOff", chkIgnoreApiOff.isChecked).apply()
            pref.edit().putBoolean("allowBridge", chkAllowBridge.isChecked).apply()

            pref.edit().putBoolean("specificLog", chkSpecificLog.isChecked).apply()
            pref.edit().putBoolean("useUnifiedParams", chkUseUnifiedParams.isChecked).apply()
            pref.edit().putInt("optimization", optimization.progress - 1).apply()

            Snackbar.make(view, R.string.settings_snackbar_applied, Snackbar.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.btn_blacklistMng).setOnClickListener {
            val intent = Intent(MainApplication.context, BlackListManager::class.java)
            intent.putExtra("scriptName", scriptName)
            startActivity(intent)
        }
        findViewById<View>(R.id.btn_help).setOnClickListener {
            val intent = Intent(MainApplication.context, HelpActivity::class.java)
            startActivity(intent)
        }
        findViewById<View>(R.id.btn_deleteScript).setOnClickListener {
            val ad = AlertDialog.Builder(this@SettingsScreen)
            ad.setTitle(R.string.alert_delete_script)
            val et = EditText(this@SettingsScreen)
            et.setHint(R.string.alert_delete_script_hint)
            val container = FrameLayout(this@SettingsScreen)
            val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
            params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
            params.topMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
            et.layoutParams = params
            container.addView(et)
            ad.setView(container)
            ad.setPositiveButton("OK") { dialog, which ->
                if (et.text.toString() == scriptName) {
                    Api.off(scriptName)
                    Api.unload(scriptName)
                    File(MainApplication.basePath.path + File.separator + scriptName).delete()
                    Toast.makeText(this@SettingsScreen, "Deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    this@SettingsScreen.finish()
                }
            }
            ad.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }


            ad.show()
        }
        val btnShowAd = findViewById<Button>(R.id.btn_showAd)
        btnShowAd.setOnClickListener {
            Toast.makeText(context, resources.getString(R.string.wait_for_ad), Toast.LENGTH_SHORT).show()
            if (!isAdEverInitialized) {
                MobileAds.initialize(context, "ca-app-pub-8316062499073563~1566457333")
                isAdEverInitialized = true
            }


            loadInterstitialAd()
        }

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd!!.adUnitId = "ca-app-pub-8316062499073563/7738424496"//testcode: ca-app-pub-3940256099942544/1033173712
        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd!!.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                when (errorCode) {
                    ERROR_CODE_INTERNAL_ERROR -> Toast.makeText(context, "구글이 뭔가 일을 저지른것 같습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    ERROR_CODE_INVALID_REQUEST -> Toast.makeText(context, "개발자가 멍청해서 광고 등록을 잘못했습니다. 개발자에게 연락해주세요.", Toast.LENGTH_SHORT).show()
                    ERROR_CODE_NO_FILL -> Toast.makeText(context, "구글 광고 서버가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    ERROR_CODE_NETWORK_ERROR -> {
                        Toast.makeText(context, "네트워크 연결 확인 후 다시 시도해주세요~!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(context, "광고 로드에 실패했습니다. 다음 에러 코드를 개발자에게 알려주세요: $errorCode", Toast.LENGTH_LONG).show()
                    }
                    else -> Toast.makeText(context, "광고 로드에 실패했습니다. 다음 에러 코드를 개발자에게 알려주세요: $errorCode", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        }

    }


    private fun loadInterstitialAd() {
        mInterstitialAd!!.loadAd(AdRequest.Builder().addTestDevice("5AB011F116AEA8A39CE0E8414D0C1749").build())

    }

    public override fun onResume() {

        super.onResume()
    }

    public override fun onPause() {

        super.onPause()
    }

    public override fun onDestroy() {

        super.onDestroy()
    }


}
