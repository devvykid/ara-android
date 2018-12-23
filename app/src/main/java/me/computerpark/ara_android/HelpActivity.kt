package me.computerpark.ara_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.activity_help.*

/**
 * Created by XFL on 2/26/2018.
 */

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        helpTxt.movementMethod = LinkMovementMethod.getInstance()
        helpTxt.text = Html.fromHtml(resources.getString(R.string.help).replace("-\\*\\*".toRegex(), "<b>")
                .replace("\\*\\*-".toRegex(), "</b>")
                .replace("-\\*".toRegex(), "<h1>")
                .replace("\\*-".toRegex(), "</h1>")
                .replace("\n", "<br />")
        )

        helpTxt.append(Html.fromHtml("<br /><br /><h1> Changelog</h1><br />"))
        helpTxt.append(Html.fromHtml(ScriptSelectActivity.getUpdateMessage(40, this.packageManager.getPackageInfo(packageName, 0).versionCode).toString()))

    }

}