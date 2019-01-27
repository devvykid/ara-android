package me.computerpark.ara_android

import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.TextView

/**
 * Created by XFL, modified by 컴터박 on 2/19/2018.
 */

class LoggerScreen : AppCompatActivity() {
    lateinit var goDownFab: FloatingActionButton


    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_logger)

        scriptName = intent.extras!!.getString("scriptName")
        logTxt = findViewById(R.id.logTxt)
        goDownFab = findViewById(R.id.fab_goDown)
        scrollView = findViewById(R.id.logger_scrollView)
        logTxt!!.text = Html.fromHtml(log)
        initialize()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView!!.setOnScrollChangeListener { _, _, _, _, _ ->
                MainApplication.context!!.getSharedPreferences("logger", 0).edit().putInt("scrollState", scrollView!!.scrollY).apply()
                if (scrollView!!.scrollY >= scrollView!!.getChildAt(0).measuredHeight - scrollView!!.measuredHeight) {

                    goDownFab.hide()
                } else {
                    goDownFab.show()
                }
            }
        }
        goDownFab.setOnClickListener { scrollView!!.post { scrollView!!.fullScroll(View.FOCUS_DOWN) } }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.clear_log, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_clearlogs) {
            clearLogText()

            Log.clear()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        var logTxt: TextView? = null
        var scrollView: ScrollView? = null
        private var scriptName: String? = null

        fun appendLogText(str: Spanned) {
            var move = false

            if (scrollView != null) {

                if (scrollView!!.scrollY >= scrollView!!.getChildAt(0).measuredHeight - scrollView!!.measuredHeight) {

                    move = true
                }
            }
            NotificationListener.UIHandler!!.post {
                if (logTxt != null) {
                    logTxt!!.append(str)

                }
            }

            if (move && scrollView != null) {
                scrollView!!.post { scrollView!!.fullScroll(View.FOCUS_DOWN) }
            }
        }

        fun clearLogText() {
            if (logTxt != null)
                logTxt!!.text = ""
        }

        fun initialize() {
            if (scrollView != null) {
                scrollView!!.post { scrollView!!.scrollY = MainApplication.context!!.getSharedPreferences("logger", 0).getInt("scrollState", 0) }
            }
        }

        private val log: String
            get() {
                val ctx = MainApplication.context
                return ctx!!.getSharedPreferences("log", 0).getString("log", "")
            }
    }
}
