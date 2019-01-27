package me.computerpark.ara_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.faendir.rhino_android.RhinoAndroidHelper
import me.computerpark.ara_android.MessageUI.MessageListAdapter
import me.computerpark.ara_android.MessageUI.UserMessage
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject
import java.util.*


class CodePlaygroundScreen : AppCompatActivity() {
    private lateinit var mMessageRecycler: RecyclerView
    private lateinit var mMessageAdapter: MessageListAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_codeplayground)
        parseContext = RhinoAndroidHelper.prepareContext()
        val msgTxt = findViewById<EditText>(R.id.sand_edittext_chatbox)
        val sendBtn = findViewById<Button>(R.id.sand_button_chatbox_send)
        mMessageRecycler = findViewById(R.id.sand_recyclerview_message_list)
        mMessageAdapter = MessageListAdapter(this, messageList)
        mMessageRecycler.layoutManager = LinearLayoutManager(this@CodePlaygroundScreen)
        mMessageRecycler.adapter = mMessageAdapter
        sendBtn.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(msgTxt.text.toString())) return@OnClickListener


            messageList.add(UserMessage(false, msgTxt.text.toString(), ""))
            val newMsgPosition = messageList.size - 1
            mMessageAdapter.notifyItemInserted(newMsgPosition)
            mMessageRecycler.scrollToPosition(newMsgPosition)

            val thr = Thread(Runnable {
                Context.enter()
                appendReply(runScript(msgTxt.text.toString()))
                Context.exit()
            })
            thr.start()


            msgTxt.setText("")
        })
        /*
        final ChatView chatView = findViewById(R.id.codeplaygroundchatview);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(final ChatMessage chatMessage) {
                if (!chatMessage.getMessage().isEmpty()) {
                    Thread thr = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Context.enter();
                            msg = runScript(chatMessage.getMessage());
                            Context.exit();
                            NotificationListener.UIHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    chatView.addMessage(new ChatMessage(msg, new Date().getTime(), ChatMessage.Type.RECEIVED));

                                }
                            });
                        }
                    });
                    thr.start();


                }

                return true;
            }
        });*/
    }

    private fun appendReply(str: String) {

        NotificationListener.UIHandler!!.post {
            messageList.add(UserMessage(true, str, ""))
            mMessageAdapter.notifyItemInserted(messageList.size - 1)
            mMessageRecycler.scrollToPosition(messageList.size - 1)
        }

    }

    private fun runScript(str: String): String {


        parseContext.wrapFactory = PrimitiveWrapFactory()
        parseContext.languageVersion = Context.VERSION_ES6
        parseContext.optimizationLevel = -1

        return responder.call(parseContext, execScope, execScope, arrayOf<Any>(str)).toString()
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
        internal val messageList: MutableList<UserMessage> = ArrayList()
        lateinit var parseContext: Context
        lateinit var execScope: ScriptableObject
        lateinit var responder: Function

        fun initializeScript() {
            System.gc()

            val scope: ScriptableObject

            try {


                parseContext = RhinoAndroidHelper.prepareContext()

                parseContext.optimizationLevel = -1

                parseContext.languageVersion = org.mozilla.javascript.Context.VERSION_ES6
                scope = parseContext.initStandardObjects(ImporterTopLevel(parseContext)) as ScriptableObject
                parseContext.wrapFactory = PrimitiveWrapFactory()
                val script_real = parseContext.compileString("function response(str){try{return eval(str)+'';}catch(e){return e.name+'\\n'+e.message;}}", "CodePlayground", 0, null)
                Api.scriptName = "CodePlayground"
                ScriptableObject.defineClass(scope, Api::class.java)
                ScriptableObject.defineClass(scope, DataBase::class.java)
                ScriptableObject.defineClass(scope, Utils::class.java)
                ScriptableObject.defineClass(scope, me.computerpark.ara_android.Log::class.java)
                ScriptableObject.defineClass(scope, AppData::class.java)
                ScriptableObject.defineClass(scope, Bridge::class.java)
                ScriptableObject.defineClass(scope, Device::class.java)
                ScriptableObject.defineClass(scope, FileStream::class.java)
                execScope = scope

                script_real.exec(parseContext, scope)


                responder = scope.get("response", scope) as Function
                Toast.makeText(MainApplication.context, "Ready", Toast.LENGTH_SHORT).show()

                //Snackbar.make(, R.string.snackbar_script_saved,Snackbar.LENGTH_SHORT).setAction(R.string.btn_dismiss, null).show()

                Context.exit()
            } catch (e: Exception) {
                e.printStackTrace()


            }

        }
    }
}
