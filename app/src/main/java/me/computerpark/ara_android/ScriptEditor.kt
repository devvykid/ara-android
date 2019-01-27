package me.computerpark.ara_android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem

import android.widget.EditText
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets




/**
 * Created by XFL, modified by 컴터박 on 2/19/2018.
 */

class ScriptEditor : AppCompatActivity() {
    private lateinit var scriptEdit: EditText
    private lateinit var scrollView: NestedScrollView
    private var lastSave: String? = null

    private var script: File? = null
    private var scriptName: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_scripteditor)

        //val debug = findViewById<TextView>(R.id.debug)
        scriptEdit = findViewById(R.id.JSCodeEdit)

        scriptName = intent.extras!!.getString("scriptName")
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        val scriptDir = MainApplication.basePath
        if (!scriptDir.exists())
            scriptDir.mkdir()

        script = File(scriptDir.path + File.separator + scriptName)
        if (!script!!.exists()) {
            try {
                script!!.createNewFile()
            } catch (e: Exception) {
                Toast.makeText(MainApplication.context!!, "파일생성오류", Toast.LENGTH_SHORT).show()
            }

        }

        scrollView = findViewById(R.id.scriptEdit_scrollView)
        scrollView.post { scrollView.scrollY = MainApplication.context!!.getSharedPreferences("editor", 0).getInt("scrollState", 0) }

        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ -> MainApplication.context!!.getSharedPreferences("editor", 0).edit().putInt("scrollState", scrollView.scrollY).apply() })


        fab.setOnClickListener {
            val i = applicationContext.getSharedPreferences("tutorial", 0).getInt("saveAndCompile", 0)
            if (true) {
                applicationContext.getSharedPreferences("tutorial", 0).edit().putInt("saveAndCompile", i + 1).apply()
                Toast.makeText(applicationContext, applicationContext.resources.getString(R.string.tutorial_saveAndCompile), Toast.LENGTH_SHORT).show()
            }
            save()
            //Toast.makeText(MainApplication.context!!, R.string.snackbar_script_saved, Toast.LENGTH_SHORT).show()
            Snackbar.make(findViewById(R.id.scriptedit_root), R.string.snackbar_script_saved,Snackbar.LENGTH_SHORT).setAction(R.string.btn_dismiss, null).show()


        }
        fab.setOnLongClickListener {
            save()
            //Toast.makeText(MainApplication.context!!, R.string.snackbar_compileStart, Toast.LENGTH_SHORT).show()
            Snackbar.make(findViewById(R.id.scriptedit_root), R.string.snackbar_compileStart,Snackbar.LENGTH_SHORT).setAction(R.string.btn_dismiss, null).show()

            Thread(Runnable { ScriptsManager.initializeScript(scriptName!!, true, false) }).start()

            true
        }
        /*scriptEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
Thread thr;
            @Override
            public void afterTextChanged(final Editable s) {
                if(thr!=null&&thr.isAlive())thr.interrupt();
                thr=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String res = preCompile();
                            if(res==null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        s.setSpan(new ForegroundColorSpan(Color.BLACK),0,s.toString().length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                });
                                return;
                            }
                            int line = Integer.parseInt(res.split("#")[1].split("\\)")[0]);
                            String[] str = s.toString().split("\n");
                            int indexStart = 0;
                            int indexEnd = 0;
                            int i;
                            for (i = 0; i < line; i++) {
                                indexStart += str[i].length()+1;
                            }

                           // indexStart++;
                            indexEnd = indexStart + str[i].length();
                            final int fS = indexStart;
                            final int fE = indexEnd;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    debug.setText(res);
                                    s.setSpan(new ForegroundColorSpan(Color.RED), fS, fE, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            });
                        }catch(Throwable e){
                            e.printStackTrace();
                        }
                    }
                });
                thr.start();
            }
        });*/
        loadScript()


    }

    /*private String preCompile(){
            try {
                Context parseContext = new RhinoAndroidHelper().enterContext();
                parseContext.setWrapFactory(new PrimitiveWrapFactory());
                parseContext.setLanguageVersion(Context.VERSION_ES6);
                parseContext.setOptimizationLevel(-1);
                ScriptableObject scope = (ScriptableObject) parseContext.initStandardObjects(new ImporterTopLevel(parseContext));
                Script script_real = parseContext.compileString(getScript(),scriptName,0,null);
                Api.scriptName = scriptName;
                ScriptableObject.defineClass(scope, Api.class);
                ScriptableObject.defineClass(scope, DataBase.class);
                ScriptableObject.defineClass(scope, Utils.class);
                ScriptableObject.defineClass(scope, me.computerpark.ara_android.Log.class);
                ScriptableObject.defineClass(scope, AppData.class);
                ScriptableObject.defineClass(scope, Bridge.class);
                script_real.exec(parseContext, scope);

                Function func=(Function) scope.get("response", scope);
                Context.exit();
            }catch(Throwable e){
                return e.getMessage();
            }
            return null;
    }*/
    fun save() {
        script!!.setWritable(true)

        try {
            val fOut = FileOutputStream(script!!)

            val myOutWriter = OutputStreamWriter(fOut, StandardCharsets.UTF_8)
            myOutWriter.write(getScript())
            Log.d("Script", getScript())
            myOutWriter.close()
            lastSave = getScript()

            fOut.flush()
            fOut.close()


        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(MainApplication.context!!, R.string.snackbar_script_save_failed, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_open_on_another_app) {

            val uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", script!!)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(uri, "text/*")
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    fun getScript(): String {
        return scriptEdit.text.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun loadScript(): Boolean {
        var changed = false
        if (script == null) {
            throw NullPointerException()
        }
        try {
            Thread(Runnable { }).start()

            val str = FileManager.read(script!!)
            if (str!!.isEmpty()) {
                val param: String
                if (MainApplication.context!!.getSharedPreferences("settings" + scriptName!!, 0).getBoolean("useUnifiedParams", false)) {
                    param = "params"
                } else {
                    param = "room, msg, sender, isGroupChat, replier, ImageDB, packageName, threadId"
                }
                scriptEdit.setText("const scriptName=\"" + scriptName + "\";\n\n" +
                        "function response(" + param + "){\n" +
                        "    /*(이 내용은 길잡이일 뿐이니 지우셔도 무방합니다)\n" +
                        "     *(String) room: 메시지를 받은 방 이름\n" +
                        "     *(String) msg: 메시지 내용\n" +
                        "     *(String) sender: 전송자 닉네임\n" +
                        "     *(boolean) isGroupChat: 단체/오픈채팅 여부\n" +
                        "     *replier: 응답용 객체. replier.reply(\"메시지\") 또는 replier.reply(\"방이름\",\"메시지\")로 전송\n" +
                        "     *(String) ImageDB.getProfileImage(): 전송자의 프로필 이미지를 Base64로 인코딩하여 반환\n" +
                        "     *(String) packageName: 메시지를 받은 메신저의 패키지 이름. (카카오톡: com.kakao.talk, 페메: com.facebook.orca, 라인: jp.naver.line.android\n" +
                        "     *(int) threadId: 현재 쓰레드의 순번(스크립트별로 따로 매김)" +
                        "     *Api,Utils객체에 대해서는 설정의 도움말 참조*/" +
                        "\n    \n}\n\nfunction onStartCompile(){\n" +
                        "    /*컴파일 또는 Api.reload호출시, 컴파일 되기 이전에 호출되는 함수입니다.\n" +
                        "     *제안하는 용도: 리로드시 자동 백업*/\n    \n}\n\n" +
                        "//아래 4개의 메소드는 액티비티 화면을 수정할때 사용됩니다.\n" +
                        "function onCreate(savedInstanceState,activity) {\n" +
                        "    var layout=new android.widget.LinearLayout(activity);\n" +
                        "    layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);\n" +
                        "    var txt=new android.widget.TextView(activity);\n" +
                        "    txt.setText(\"액티비티 사용 예시입니다.\");\n" +
                        "    layout.addView(txt);\n" +
                        "    activity.setContentView(layout);\n" +
                        "}\nfunction onResume(activity) {}\nfunction onPause(activity) {}\nfunction onStop(activity) {}"
                )
            } else {
                if (str != lastSave) {
                    lastSave = str
                    scriptEdit.setText(str)

                    changed = true
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()

            MainApplication.reportInternalError(e)

        }

        return changed
    }


    public override fun onResume() {
        super.onResume()
        if (loadScript()) {
            Toast.makeText(this, resources.getString(R.string.script_autoloaded), Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        MainApplication.context!!.getSharedPreferences("editor", 0).edit().putInt("scrollState", scrollView.scrollY).apply()
    }
}
