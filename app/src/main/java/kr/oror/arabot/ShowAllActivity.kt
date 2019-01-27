package kr.oror.arabot

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView

class ShowAllActivity : AppCompatActivity() {
    private lateinit var vscroll: ScrollView
    private lateinit var hscroll: HorizontalScrollView
    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getStringExtra("showAllData")

        vscroll = ScrollView(this)

        hscroll = HorizontalScrollView(this)
        hscroll.isHorizontalScrollBarEnabled = true
        vscroll.isFillViewport = true
        vscroll.isVerticalScrollBarEnabled = true


        textView = TextView(this)
        textView.setTextIsSelectable(true)
        textView.setTextColor(Color.BLACK)


        textView.setPadding(dpToPx(15), dpToPx(15), dpToPx(15), dpToPx(15))

        textView.text = data
        hscroll.addView(textView)
        vscroll.addView(hscroll)

        setContentView(vscroll)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.adjustToScreen -> {
                item.isChecked = !item.isChecked
                setAdjust(item.isChecked)
                getSharedPreferences("chatShowAll", 0).edit().putBoolean("adjustToScreen", item.isChecked).apply()

            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun setAdjust(b: Boolean) {
        if (b) {
            hscroll.removeAllViews()
            vscroll.removeAllViews()
            vscroll.addView(textView)
            setContentView(vscroll)

        } else {
            hscroll.removeAllViews()
            vscroll.removeAllViews()
            hscroll.addView(textView)
            vscroll.addView(hscroll)
            setContentView(vscroll)
        }
    }
}
