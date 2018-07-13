package com.xfl.kakaotalkbot

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap


class ScriptSelectActivity : AppCompatActivity() {
    private var linearLayout: LinearLayout? = null
    private var lastFiles: Array<File>? = null
    private val basePath = File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot")

    private val isFileChanged: Boolean
        get() {


            basePath.mkdir()
            val files = basePath.listFiles()
            var bool = false
            if (files == null || lastFiles == null) {
                return false
            }
            if (files.size != lastFiles!!.size) {
                return true
            }
            for (i in files.indices) {
                if (files[i].name != lastFiles!![i].name) {
                    bool = true
                }
            }
            return bool
        }

    private fun getUpdateMessage(lastVersion: Int, version: Int): String {
        val msg = ArrayList<String>()
        //-21
        msg.add(0, "업데이트 다이얼로그 추가\nresponse 함수에 인자 추가: packageName\nDataBase.getDataBase에서 한줄이 더 추가되는 오류 해결\nApi.canReply(String room)을 통해 replyRoom 사용가능 여부 확인 가능")
        msg.add(1, "블랙리스트가 작동하지 않는 오류 해결")
        msg.add(2, "상세로그 간소화\n튕기는 문제 해결")
        msg.add(3, "최적화, 오류수정")
        msg.add(4, "디버그 모드 레이아웃 개선과 동시에, 개발자의 코딩 실력 부족으로(...) 디버그 모드를 보고있지 않을때 Api.replyRoom으로 디버그 모드에 메시지를 작성할 수 없게 되었습니다.\n" +
                "열심히 공부해서(?) 빠른 시일 내에 고치도록 하겠습니다(...)\n" +
                "대신, 꾹 눌러 복사하기를 추가하였고, 하이퍼링크 등을 지원하게 되었습니다.")
        msg.add(5, "Bridge.getScopeOf(\"스크립트이름.js\")를 통해 다른 스크립트의 전역변수/함수 등에 접근할 수 있습니다.\n" +
                "예) Bridge.getScopeOf(\"멋진메신저봇.js\").a=1\n" +
                "또한, 설정에서 Bridge에 의한 접근을 금지할 수 있습니다.\n" +
                "Bridge.isAllowed(\"스크립트이름.js\")를 통해 접근이 허용되어있는지 확인할 수 있습니다.\n\n" +
                "파일읽기 권한이 작동도중 사라졌을때 앱이 튕기는 문제 해결\n" +
                "Api.replyRoom의 토스트 문제 해결")

        msg.add(6, "[긴급패치]Api.replyRoom 반환값 오류 해결")
        msg.add(7, "최초 컴파일이 여러번 되는 문제 해결")
        msg.add(8, "Api.isOn(\"스크립트이름.js\")와 Utils.parse(\"주소\")가 추가됨. Utils.parse는 jsoup로 get한 결과를 리턴함\n" +
                "명칭 혼동을 막기 위해, Api.reload와 같은 기능을 하는 Api.compile추가\n" +
                "Api.getRootView()추가\n" +
                "Api.isCompiled(\"스크립트이름.js\")추가\n" +
                "Bridge.getScopeOf가 없거나 컴파일 되지 않은 스크립트에 접근하였을 경우 null반환")
        msg.add(9, "메모리 문제 해결\n공용 설정 추가\n(공용 설정)HTML 파싱 제한시간 설정 추가")
        msg.add(10, "Api.prepare(\"스크립트이름.js\"): 스크립트가 단 한번도 컴파일되지 않았을경우만 컴파일합니다.\n컴파일 된 적이 있을경우 2, 컴파일에 성공했을경우 1, 스크립트가 존재하지 않을경우 0을 반환하고, 컴파일에 실패했을경우 에러를 throw합니다.\n" +
                "이제 Api.reload 또는 Api.compile이 컴파일 에러가 발생했을경우 에러를 throw합니다.\n" +
                "이제 Utils.parse 및 Utils.getWebText에서 콘텐츠 타입을 무시합니다. (즉, 인터넷에 있는 .js파일 등을 불러올 수 있습니다.)")
        msg.add(11, "Api.papagoTranslate에서 에러를 반환하지 않고 이전 번역 결과를 반환하는 오류를 수정했습니다. 이제 4번째 인자를 true로 하면 에러를 String으로 반환하고, false로 하면 throw합니다." +
                "\n디버그화면과 샌드박스화면에 기록 지우기 버튼이 추가되었습니다." +
                "\n공용설정에 자동컴파일 관련 설정이 추가되었습니다.")
        msg.add(12, "자바스크립트 버전을 ES6으로 변경했습니다.\n이제 앱 내부 오류를 따로 구분하여 출력합니다.\n드디어 디버그창, 샌드박스창의 쓰레드가 분리되어 랙이 없어졌을겁니다(?)\n네이버 카페가 개설되었습니다.")
        msg.add(13, "컴파일이 되지 않았을때 디버그 화면에 메시지 전송 시 튕기는 오류를 수정했습니다.\n이제 스크립트에 response함수가 없어도 오류가 나지 않습니다. 대신, 컴파일 후 스위치를 켤 때 경고가 표시됩니다.")
        msg.add(14, "약간의 최적화를 적용했습니다.\n일부 오류를 수정했습니다.\nreplier.reply(방,메시지)가 추가되었습니다.\n스크립트별 액티비티가 추가되었습니다.(구현 방법 예시는 새 스크립트를 만들어서 볼 수 있습니다.)")
        msg.add(15, "봇 이름의 컴파일 상태 색이 잘못 지정되는 문제를 해결했습니다.\n" +
                "디버그룸에서 튕기는 오류를 해결했습니다.\n" +
                "디버그룸 메시지의 최대 가로 크기를 확장했습니다.\n" +
                "약간의 최적화를 적용했습니다.\n" +
                "전체 봇 활성화/비활성화 버튼을 추가했습니다.\n" +
                "UI를 개선했습니다.\n" +
                "블랙리스트가 스크립트별로 구분되지 않는 문제를 해결했습니다.")
        msg.add(16, "스크립트 삭제 기능을 추가했습니다.\n" +
                "통합 매개변수 기능을 추가했습니다. 이 기능을 체크하실 경우 response함수를 room,msg등의 인자들을 하나의 객체로 모아 호출합니다.\n" +
                "약간의 최적화를 적용했습니다.\n" +
                "일부 UI를 개선했습니다.")
        msg.add(17, "약간의 안정화를 적용했습니다.")
        msg.add(18, "활성화 스위치가 작동하지 않는 문제를 해결했습니다.")
        msg.add(19, "이제 커스텀 패키지를 추가할 수 있습니다. (추가 방법: 공용 설정의 Custom Packages에 원하는 앱의 패키지명 입력후 적용 -> 스크립트 개별 설정에서 체크)")
        msg.add(20, "DB가 없을때 DataBase.removeDataBase를 호출하면 발생하는 오류를 해결했습니다.\n Api.UIThread가 작동하지 않는 오류를 해결했습니다.\nDevice객체를 추가했습니다.\n봇이 켜진 상태로 설정에서 스크립트 삭제시 봇이 계속 구동되는 문제를 해결했습니다. 단, 다른 파일 탐색기에서 삭제하는것은 주의해주세요.\nApi.unload(\"스크립트이름.js\"): 해당 스크립트의 컴파일 상태를 제거합니다.")
        msg.add(21, "Device에 파일읽기쓰기를 넣으면 추후에 보안 관련 분류가 어려워질 것 같아 FileStream.read, FileStream.write로 대체했습니다.\nDevice.getBatteryStatus를 수정했습니다.\nDevice.getBatteryIntent를 추가했습니다.\n잦은 업데이트 죄송합니다. 업데이트에 좀 더 신중해지도록 하겠습니다.")
        val result = StringBuilder()
        for (i in lastVersion + 1 - 21..version - 21) {
            if (i > msg.size - 1) break
            result.append(msg[i]).append("\n\n")


        }


        return result.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationListener.rootView = window.decorView.rootView

        try {

            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionCode
            val lastVersion = getSharedPreferences("versionCode", 0).getInt("versionCode", 20)
            if (lastVersion < version) {
                val ad = AlertDialog.Builder(this@ScriptSelectActivity)

                ad.setTitle("업데이트")       // 제목 설정

                val scroll = ScrollView(this)
                scroll.setBackgroundColor(resources.getColor(android.R.color.transparent))
                val container = FrameLayout(this)
                val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                params.topMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                scroll.layoutParams = params

                val txt = TextView(this)
                txt.text = getUpdateMessage(lastVersion, version)
                scroll.addView(txt)
                container.addView(scroll)
                ad.setView(container)
                // 확인 버튼 설정
                ad.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()     //닫기
                }
                ad.show()
            }
            getSharedPreferences("versionCode", 0).edit().putInt("versionCode", version).apply()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val contentResolver = contentResolver
        val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val packageName = packageName
        val granted: Boolean
        // check to see if the enabledNotificationListeners String contains our package name

        granted = !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
        if (!granted) {
            val ad = AlertDialog.Builder(this@ScriptSelectActivity)

            ad.setTitle(MainApplication.context.resources.getString(R.string.no_noti_read_permission))       // 제목 설정
            ad.setMessage(MainApplication.context.resources.getString(R.string.alert_set_noti_access_message))   // 내용 설정

            // 확인 버튼 설정
            ad.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()     //닫기
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }
            ad.show()
        }
        basePath.mkdir()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_scriptselect)
        val activate: Switch
        activate = findViewById(R.id.switch_activate)
        activate.isChecked = MainApplication.context.getSharedPreferences("bot", 0).getBoolean("activate", true)
        activate.setOnCheckedChangeListener { compoundButton, b ->
            applicationContext.getSharedPreferences("bot", 0).edit().putBoolean("activate", b).apply()
            noti(MainApplication.context)
        }
        linearLayout = findViewById(R.id.scriptSelectRecycler)
        val addScript = findViewById<FloatingActionButton>(R.id.addScript)
        addScript.setOnClickListener(View.OnClickListener {
            val ad = AlertDialog.Builder(this@ScriptSelectActivity)

            ad.setTitle(MainApplication.context.resources.getString(R.string.alert_newScript_title))       // 제목 설정
            ad.setMessage(MainApplication.context.resources.getString(R.string.alert_newScript_message))   // 내용 설정

            // EditText 삽입하기
            val et = EditText(this@ScriptSelectActivity)
            val chk = CheckBox(this@ScriptSelectActivity)
            val lin = LinearLayout(this@ScriptSelectActivity)
            lin.orientation = LinearLayout.VERTICAL
            chk.setText(R.string.unify_params)
            chk.isChecked = false
            lin.addView(et)
            lin.addView(chk)
            ad.setView(lin)

            // 확인 버튼 설정
            ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                if (et.text.toString().isEmpty()) {
                    return@OnClickListener
                }
                try {
                    var fileName = et.text.toString()
                    if (!fileName.endsWith(".js")) {
                        fileName += ".js"
                    }
                    File(basePath.path + File.separator + fileName).createNewFile()
                    MainApplication.context.getSharedPreferences("settings$fileName", 0).edit().putBoolean("useUnifiedParams", chk.isChecked).apply()
                    initialize()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                dialog.dismiss()     //닫기
                // Event
            })


            // 취소 버튼 설정
            ad.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()     //닫기
                // Event
            }

            // 창 띄우기
            ad.show()
        })
        val setNotiAccess = findViewById<Button>(R.id.notiAccessSet)
        setNotiAccess.setOnClickListener {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
        val openSandbox = findViewById<Button>(R.id.openSandbox)
        openSandbox.setOnClickListener {
            val intent = Intent(applicationContext, CodePlaygroundScreen::class.java)
            startActivity(intent)
        }
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)

        } else {

            initialize()

        }
        /*File sessionsPath= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"katalkbot"+File.separator+"Sessions");
        if(sessionsPath.exists()){
            File[] files=sessionsPath.listFiles();
            try {
                for (File k : files) {
                    FileInputStream sIn = new FileInputStream(k);
                    ObjectInputStream oIn = new ObjectInputStream(sIn);


                    NotificationListener.SavedSessions.add((Notification.Action)oIn.readObject());
                    NotificationListener.Rooms.add(k.getName());

                }
                Toast.makeText(this,"Loaded all Sessions",Toast.LENGTH_SHORT).show();
            }catch (Throwable e){
                Log.e("SessionFileRead","",e);
            }
        }*/


    }

    private fun initialize() {
        switchMap.clear()
        progressBarMap.clear()
        linearLayout!!.removeAllViews()
        CodePlaygroundScreen.initializeScript()
        noti(MainApplication.context)

        val ctx = this
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        basePath.mkdir()
        val files = basePath.listFiles()
        lastFiles = files
        for (k in files) {
            if (k.name.endsWith(".js")) {
                NotificationListener.initializeBanList(k.name)
                val view = inflater.inflate(R.layout.view_scriptselector, linearLayout, false)
                val swit = view.findViewById<Switch>(R.id.switch2)
                switchMap[k.name] = swit
                val reload = view.findViewById<ImageButton>(R.id.btn_reload)
                val edit = view.findViewById<ImageButton>(R.id.btn_editscript)
                val manage = view.findViewById<ImageButton>(R.id.btn_manage)
                val debug = view.findViewById<ImageButton>(R.id.btn_debug)
                val log = view.findViewById<ImageButton>(R.id.btn_log)
                val scriptActivity = view.findViewById<ImageButton>(R.id.btn_scriptActivity)
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar2)

                progressBarMap[k.name] = progressBar
                progressBar.visibility = View.GONE

                swit.isChecked = applicationContext.getSharedPreferences("bot" + k.name, 0).getBoolean("on", false)
                swit.text = k.name
                swit.setOnCheckedChangeListener { compoundButton, b ->
                    if (b && NotificationListener.container[k.name] != null && NotificationListener.container[k.name]?.responder == null) {
                        Toast.makeText(this@ScriptSelectActivity, this@ScriptSelectActivity.resources.getString(R.string.switch_redundant), Toast.LENGTH_LONG).show()
                    }
                    applicationContext.getSharedPreferences("bot" + k.name, 0).edit().putBoolean("on", b).apply()
                    noti(ctx)
                }
                reload.setOnClickListener {
                    Thread(Runnable {
                        NotificationListener.UIHandler.post {
                            //compiling = true;
                            progressBar.visibility = View.VISIBLE
                        }
                        val bool = NotificationListener.initializeScript(k.name, true)
                        /*if (!) {
                                    NotificationListener.UIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ScriptSelectActivity.this, ScriptSelectActivity.this.getResources().getString(R.string.already_compiling), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } else {*/
                        NotificationListener.UIHandler.post {
                            //compiling = false;
                            progressBar.visibility = View.GONE
                            if (bool)
                                swit.setTextColor(resources.getColor(R.color.fully_compiled))
                        }
                        // }
                    }).start()
                }
                edit.setOnClickListener {
                    val i = applicationContext.getSharedPreferences("tutorial", 0).getInt("openAnotherApp", 0)
                    if (i < 2) {
                        applicationContext.getSharedPreferences("tutorial", 0).edit().putInt("openAnotherApp", i + 1).apply()
                        Toast.makeText(applicationContext, applicationContext.resources.getString(R.string.tutorial_openAnotherApp), Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(applicationContext, ScriptEditor::class.java)
                    intent.putExtra("scriptName", k.name)
                    startActivity(intent)
                }
                edit.setOnLongClickListener {
                    val uri = FileProvider.getUriForFile(ctx, applicationContext.packageName + ".provider", k)

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    intent.setDataAndType(uri, "text/*")
                    startActivity(intent)
                    true
                }
                manage.setOnClickListener {
                    val intent = Intent(applicationContext, SettingsScreen::class.java)
                    intent.putExtra("scriptName", k.name)
                    startActivity(intent)
                }
                debug.setOnClickListener {
                    val intent = Intent(applicationContext, DebugModeScreen::class.java)
                    intent.putExtra("scriptName", k.name)
                    startActivity(intent)
                }
                log.setOnClickListener {
                    val intent = Intent(applicationContext, LoggerScreen::class.java)
                    intent.putExtra("scriptName", k.name)
                    startActivity(intent)
                }

                scriptActivity.setOnClickListener {
                    val intent = Intent(applicationContext, ScriptActivity::class.java)
                    intent.putExtra("scriptName", k.name)
                    startActivity(intent)
                }

                view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                linearLayout!!.addView(view)
            }
        }
        val keySet = switchMap.keys
        var b = false
        for (k in keySet) {
            b = switchMap[k]!!.isChecked;

        }
        if (b) {
            noti(ctx)
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return

        }
        if (isFileChanged) {
            initialize()
        }
        val files = basePath.listFiles()
        for (k in files) {
            if (!k.name.endsWith(".js")) continue
            if (MainApplication.context.getSharedPreferences("lastCompileSuccess2", 0).getLong(k.name, 0) < k.lastModified() || NotificationListener.container[k.name] == null) {
                switchMap[k.name]!!.setTextColor(resources.getColor(R.color.need_compile))
            } else {
                switchMap[k.name]!!.setTextColor(resources.getColor(R.color.fully_compiled))
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initialize()

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1)

                    }
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.go_public_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val intent: Intent
        when (id) {
            R.id.action_go_public_settings -> {

                intent = Intent(applicationContext, PublicSettingsScreen::class.java)
                startActivity(intent)
            }
            R.id.action_open_cafe -> {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.cafe.naver.com/msgbot"))
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val switchMap = HashMap<String, Switch>()
        private val progressBarMap = HashMap<String, ProgressBar>()

        fun refreshProgressBar(scriptName: String, b: Boolean, changeColor: Boolean) {
            if (progressBarMap[scriptName] == null) return
            if (b) {
                progressBarMap[scriptName]!!.setVisibility(View.VISIBLE)
            } else {
                progressBarMap[scriptName]!!.setVisibility(View.GONE)
                if (changeColor && switchMap[scriptName] != null)
                    switchMap[scriptName]!!.setTextColor(MainApplication.context.resources.getColor(R.color.fully_compiled))

            }
        }

        fun putOn(scriptName: String, b: Boolean) {
            if (switchMap[scriptName] != null) {
                switchMap[scriptName]!!.isChecked=b
            }

            MainApplication.context.getSharedPreferences("bot$scriptName", 0).edit().putBoolean("on", b).apply()
        }

        fun putOnAll(b: Boolean) {
            val keySet = switchMap.keys
            for (k in keySet) {
                if (MainApplication.context.getSharedPreferences("settings$k", 0).getBoolean("ignoreApiOff", false))
                    continue
                switchMap[k]!!.isChecked=b
                MainApplication.context.getSharedPreferences("bot$k", 0).edit().putBoolean("on", b).apply()
            }
        }

        fun noti(ctx: Context) {
            val stringBuilder = StringBuilder()

            val keySet = switchMap.keys
            var b = false
            if (MainApplication.context.getSharedPreferences("bot", 0).getBoolean("activate", true)) {
                for (k in keySet) {
                    if (switchMap[k]!!.isChecked) {
                        b = true
                        stringBuilder.append(k).append("\n")
                    }


                }
            }//else b = false is redundant
            val notificationManager = MainApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val noti = Notification.Builder(MainApplication.context).setStyle(Notification.BigTextStyle()
                    .bigText(stringBuilder.toString()))
            noti.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(ctx.resources.getString(R.string.app_name))
                    .setContentText(stringBuilder.toString())
                    .setPriority(Notification.PRIORITY_MAX)

                    .setOngoing(true)

            val pendingIntent = PendingIntent.getActivity(ctx, 0, Intent(ctx, ScriptSelectActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            noti.setContentIntent(pendingIntent)

            val reload = Intent(ctx, ActionReceiver::class.java)
            reload.putExtra("action", "reload")
            val off = Intent(ctx, ActionReceiver::class.java)
            off.putExtra("action", "off")
            val pReload = PendingIntent.getBroadcast(ctx, 1, reload, PendingIntent.FLAG_UPDATE_CURRENT)
            val pOff = PendingIntent.getBroadcast(ctx, 2, off, PendingIntent.FLAG_UPDATE_CURRENT)
            noti.addAction(Notification.Action(R.drawable.ic_menu_refresh, ctx.resources.getString(R.string.compile_all), pReload))
            noti.addAction(Notification.Action(R.drawable.ic_close, ctx.resources.getString(R.string.off_all), pOff))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                noti.setChannelId("com.xfl.kakaotalkbot.notification")
                // Create the NotificationChannel
                val name = ctx.getString(R.string.channel_name)
                val description = ctx.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_LOW
                val mChannel = NotificationChannel("com.xfl.kakaotalkbot.notification", name, importance)
                mChannel.description = description
                mChannel.setShowBadge(false)
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this

                notificationManager.createNotificationChannel(mChannel)
            }


            if (!b)
                notificationManager.cancel(1)
            else {
                notificationManager.notify(1, noti.build())
            }


        }
    }
}