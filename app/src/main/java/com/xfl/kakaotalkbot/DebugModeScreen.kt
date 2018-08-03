package com.xfl.kakaotalkbot

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.xfl.kakaotalkbot.MessageUI.MessageListAdapter
import com.xfl.kakaotalkbot.MessageUI.UserMessage
import java.util.*


/**
 * Created by XFL on 2/19/2018.
 */

class DebugModeScreen : AppCompatActivity() {
    internal var scriptName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_debugmode)
        val sendBtn = findViewById<Button>(R.id.button_chatbox_send)
        val room = findViewById<EditText>(R.id.txt_room)
        val sender = findViewById<EditText>(R.id.txt_sender)
        val msgTxt = findViewById<EditText>(R.id.edittext_chatbox)
        val chk_groupchat = findViewById<CheckBox>(R.id.chk_groupchat)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        scriptName = intent.extras!!.getString("scriptName")
        chk_groupchat.isChecked = MainApplication.context!!.getSharedPreferences("debugGroupChat", 0).getBoolean(scriptName, false)
        room.setText(MainApplication.context!!.getSharedPreferences("debugRoom", 0).getString(scriptName, ""))
        sender.setText(MainApplication.context!!.getSharedPreferences("debugSender", 0).getString(scriptName, ""))
        if (room.text.isEmpty()) {
            room.requestFocus()
        } else if (sender.text.isEmpty()) {
            sender.requestFocus()
        } else {
            msgTxt.requestFocus()
        }
        if (savedMessageList[scriptName] == null)
            savedMessageList[scriptName] = ArrayList()
        messageList = savedMessageList[scriptName]

        mMessageRecycler = findViewById(R.id.recyclerview_message_list)
        mMessageAdapter = MessageListAdapter(this, messageList)
        mMessageRecycler.layoutManager = LinearLayoutManager(this@DebugModeScreen)
        mMessageRecycler.adapter = mMessageAdapter

        sendBtn.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(msgTxt.text.toString())) return@OnClickListener
            if (ScriptsManager.container[scriptName!!] == null) {
                Toast.makeText(this@DebugModeScreen, this@DebugModeScreen.resources.getString(R.string.please_compile_first), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (room.text.toString().isEmpty()) {
                room.setText("DEBUGROOM")
            }
            if (sender.text.toString().isEmpty()) {
                sender.setText("DEBUGSENDER")
            }
            MainApplication.context!!.getSharedPreferences("debugGroupChat", 0).edit().putBoolean(scriptName, chk_groupchat.isChecked).apply()
            MainApplication.context!!.getSharedPreferences("debugSender", 0).edit().putString(scriptName, sender.text.toString()).apply()
            MainApplication.context!!.getSharedPreferences("debugRoom", 0).edit().putString(scriptName, room.text.toString()).apply()

            messageList?.add(UserMessage(false, msgTxt.text.toString(), sender.text.toString()));
            val newMsgPosition = mMessageAdapter.itemCount - 1
            //mMessageAdapter.notifyItemInserted(newMsgPosition);
            mMessageRecycler.scrollToPosition(newMsgPosition)
            NotificationListener.debugRoom = room.text.toString()
            val scname = scriptName
            //if(NotificationListener.threads[scname]==null)NotificationListener.threads[scname!!]=ArrayList<Thread?>()
            // val id=NotificationListener.threads[scname]!!.size
            val thr = Thread(Runnable {
                NotificationListener.callResponder(scriptName!!, room.text.toString(), msgTxt.text.toString(), sender.text.toString(), chk_groupchat.isChecked, ImageDB(BitmapFactory.decodeResource(MainApplication.context!!.resources, R.drawable.ic_lock_bugreport)), "DEBUGMODE",/*id,*/ null, true)
                //   NotificationListener.threads[scname]!![id]=null
            })

            //   NotificationListener.threads[scname]!!.add(thr)
            thr.start()


            msgTxt.setText("")
        })


    }

    /*private void refreshView() {
        mMessageAdapter.notifyItemInserted(messageList.size() - 1);
    }*/

    override fun onDestroy() {
        super.onDestroy()
        savedMessageList[scriptName] = messageList

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.clear_log, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_clearlogs) {
            mMessageAdapter.clear()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        internal var savedMessageList: MutableMap<String?, MutableList<UserMessage>?> = HashMap()
        internal var messageList: MutableList<UserMessage>? = ArrayList()
        lateinit internal var mMessageRecycler: RecyclerView
        lateinit internal var mMessageAdapter: MessageListAdapter

        fun appendReply(value: String?) {

            //messageList.add(new UserMessage("BOT", value));

            NotificationListener.UIHandler!!.post {
                messageList?.add(UserMessage(true, value, "BOT"))
                mMessageAdapter.notifyItemInserted(mMessageAdapter.getItemCount() - 1);
                mMessageRecycler.scrollToPosition(messageList!!.size - 1)
            }


        }
    }
}
