package kr.oror.arabot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.EditText

/**
 * Created by XFL, modified by 컴터박 on 2/21/2018.
 */

class BlackListManager : AppCompatActivity() {
    private val ctx = MainApplication.context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklistmanager)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val scriptName = intent.extras!!.getString("scriptName")
        val banRoom = findViewById<View>(R.id.banRoom) as EditText
        val banName = findViewById<View>(R.id.banName) as EditText
        banRoom.setText(ctx!!.getSharedPreferences("bot", 0).getString("banRoom$scriptName", ""))
        banName.setText(ctx.getSharedPreferences("bot", 0).getString("banName$scriptName", ""))
        findViewById<View>(R.id.blackListApply).setOnClickListener {
            ctx.getSharedPreferences("bot", 0).edit().putString("banRoom$scriptName", banRoom.text.toString()).apply()
            ctx.getSharedPreferences("bot", 0).edit().putString("banName$scriptName", banName.text.toString()).apply()
            NotificationListener.initializeBanList(scriptName)
            finish()
        }

    }
}
