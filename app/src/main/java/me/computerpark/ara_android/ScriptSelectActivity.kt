package me.computerpark.ara_android

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import android.text.Html
import android.view.*
import android.widget.*
import java.io.File
import java.io.IOException
import java.util.*


class ScriptSelectActivity : AppCompatActivity() {
    private var linearLayout: LinearLayout? = null
    private var lastFiles: Array<File>? = null
    private val basePath = File(Environment.getExternalStorageDirectory().toString() + File.separator + "arabot")

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationListener.rootView = window.decorView.rootView

        try {

            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionCode
            val lastVersion = getSharedPreferences("versionCode", 0).getInt("versionCode", 20)
            getSharedPreferences("lastVersionCode", 0).edit().putInt("lastVersionCode", lastVersion).apply()
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

                txt.text = Html.fromHtml(getUpdateMessage(lastVersion, version))
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

            ad.setTitle(MainApplication.context!!.resources.getString(R.string.no_noti_read_permission))       // 제목 설정
            ad.setMessage(MainApplication.context!!.resources.getString(R.string.alert_set_noti_access_message))   // 내용 설정

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
        activate.isChecked = MainApplication.context!!.getSharedPreferences("bot", 0).getBoolean("activate", true)
        activate.setOnCheckedChangeListener { _, b ->
            applicationContext.getSharedPreferences("bot", 0).edit().putBoolean("activate", b).apply()
            noti(MainApplication.context!!)
        }
        linearLayout = findViewById(R.id.scriptSelectRecycler)
        val addScript = findViewById<FloatingActionButton>(R.id.addScript)
        addScript.setOnClickListener(View.OnClickListener {
            val ad = AlertDialog.Builder(this@ScriptSelectActivity)

            ad.setTitle(MainApplication.context!!.resources.getString(R.string.alert_newScript_title))       // 제목 설정
            ad.setMessage(MainApplication.context!!.resources.getString(R.string.alert_newScript_message))   // 내용 설정

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
                    MainApplication.context!!.getSharedPreferences("settings$fileName", 0).edit().putBoolean("useUnifiedParams", chk.isChecked).apply()
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

        /*File sessionsPath= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"arabot"+File.separator+"Sessions");
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
        noti(MainApplication.context!!)

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
                swit.setOnCheckedChangeListener { _, b ->
                    if (b && ScriptsManager.container[k.name] != null && ScriptsManager.container[k.name]?.getResponder() == null) {
                        Toast.makeText(this@ScriptSelectActivity, this@ScriptSelectActivity.resources.getString(R.string.switch_redundant), Toast.LENGTH_LONG).show()
                    }
                    applicationContext.getSharedPreferences("bot" + k.name, 0).edit().putBoolean("on", b).apply()
                    noti(ctx)
                }
                reload.setOnClickListener {
                    Thread(Runnable {
                        NotificationListener.UIHandler!!.post {
                            //compiling = true;
                            progressBar.visibility = View.VISIBLE
                        }
                        val bool = ScriptsManager.initializeScript(k.name, true, false)
                        /*if (!) {
                                    NotificationListener.UIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ScriptSelectActivity.this, ScriptSelectActivity.this.getResources().getString(R.string.already_compiling), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } else {*/
                        NotificationListener.UIHandler!!.post {
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
                    if (true) {
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
            if (b) {
                noti(ctx)
                break
            }
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
            if (MainApplication.context!!.getSharedPreferences("lastCompileSuccess2", 0).getLong(k.name, 0) < k.lastModified() || ScriptsManager.container[k.name] == null) {
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
            R.id.action_open_github -> {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/computerpark/ara-android"))
                startActivity(intent)
            }
            R.id.action_help -> {
                val intent = Intent(MainApplication.context, HelpActivity::class.java)
                startActivity(intent)
            }
            R.id.action_quit -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val switchMap = HashMap<String, Switch>()
        private val progressBarMap = HashMap<String, ProgressBar>()
        fun getUpdateMessage(lastVersion: Int, version: Int): String? {
            val msg = ArrayList<String>()
            //-21
            msg.add(0, "<h2>--- 메신저봇 시절 ---</h2> 업데이트 다이얼로그 추가<br />response 함수에 인자 추가: packageName<br />DataBase.getDataBase에서 한줄이 더 추가되는 오류 해결<br />Api.canReply(String room)을 통해 replyRoom 사용가능 여부 확인 가능")
            msg.add(1, "블랙리스트가 작동하지 않는 오류 해결")
            msg.add(2, "상세로그 간소화<br />튕기는 문제 해결")
            msg.add(3, "최적화, 오류수정")
            msg.add(4, "디버그 모드 레이아웃 개선과 동시에, 개발자의 코딩 실력 부족으로(...) 디버그 모드를 보고있지 않을때 Api.replyRoom으로 디버그 모드에 메시지를 작성할 수 없게 되었습니다.<br />" +
                    "열심히 공부해서(?) 빠른 시일 내에 고치도록 하겠습니다(...)<br />" +
                    "대신, 꾹 눌러 복사하기를 추가하였고, 하이퍼링크 등을 지원하게 되었습니다.")
            msg.add(5, "Bridge.getScopeOf(\"스크립트이름.js\")를 통해 다른 스크립트의 전역변수/함수 등에 접근할 수 있습니다.<br />" +
                    "예) Bridge.getScopeOf(\"멋진메신저봇.js\").a=1<br />" +
                    "또한, 설정에서 Bridge에 의한 접근을 금지할 수 있습니다.<br />" +
                    "Bridge.isAllowed(\"스크립트이름.js\")를 통해 접근이 허용되어있는지 확인할 수 있습니다.<br />" +
                    "파일읽기 권한이 작동도중 사라졌을때 앱이 튕기는 문제 해결<br />" +
                    "Api.replyRoom의 토스트 문제 해결")

            msg.add(6, "[긴급패치]Api.replyRoom 반환값 오류 해결")
            msg.add(7, "최초 컴파일이 여러번 되는 문제 해결")
            msg.add(8, "Api.isOn(\"스크립트이름.js\")와 Utils.parse(\"주소\")가 추가됨. Utils.parse는 jsoup로 get한 결과를 리턴함<br />" +
                    "명칭 혼동을 막기 위해, Api.reload와 같은 기능을 하는 Api.compile추가<br />" +
                    "Api.getRootView()추가<br />" +
                    "Api.isCompiled(\"스크립트이름.js\")추가<br />" +
                    "Bridge.getScopeOf가 없거나 컴파일 되지 않은 스크립트에 접근하였을 경우 null반환")
            msg.add(9, "메모리 문제 해결<br />공용 설정 추가<br />(공용 설정)HTML 파싱 제한시간 설정 추가")
            msg.add(10, "Api.prepare(\"스크립트이름.js\"): 스크립트가 단 한번도 컴파일되지 않았을경우만 컴파일합니다.<br />컴파일 된 적이 있을경우 2, 컴파일에 성공했을경우 1, 스크립트가 존재하지 않을경우 0을 반환하고, 컴파일에 실패했을경우 에러를 throw합니다.<br />" +
                    "이제 Api.reload 또는 Api.compile이 컴파일 에러가 발생했을경우 에러를 throw합니다.<br />" +
                    "이제 Utils.parse 및 Utils.getWebText에서 콘텐츠 타입을 무시합니다. (즉, 인터넷에 있는 .js파일 등을 불러올 수 있습니다.)")
            msg.add(11, "Api.papagoTranslate에서 에러를 반환하지 않고 이전 번역 결과를 반환하는 오류를 수정했습니다. 이제 4번째 인자를 true로 하면 에러를 String으로 반환하고, false로 하면 throw합니다." +
                    "<br />디버그화면과 샌드박스화면에 기록 지우기 버튼이 추가되었습니다." +
                    "<br />공용설정에 자동컴파일 관련 설정이 추가되었습니다.")
            msg.add(12, "자바스크립트 버전을 ES6으로 변경했습니다.<br />이제 앱 내부 오류를 따로 구분하여 출력합니다.<br />드디어 디버그창, 샌드박스창의 쓰레드가 분리되어 랙이 없어졌을겁니다(?)<br />네이버 카페가 개설되었습니다.")
            msg.add(13, "컴파일이 되지 않았을때 디버그 화면에 메시지 전송 시 튕기는 오류를 수정했습니다.<br />이제 스크립트에 response함수가 없어도 오류가 나지 않습니다. 대신, 컴파일 후 스위치를 켤 때 경고가 표시됩니다.")
            msg.add(14, "약간의 최적화를 적용했습니다.<br />일부 오류를 수정했습니다.<br />replier.reply(방,메시지)가 추가되었습니다.<br />스크립트별 액티비티가 추가되었습니다.(구현 방법 예시는 새 스크립트를 만들어서 볼 수 있습니다.)")
            msg.add(15, "봇 이름의 컴파일 상태 색이 잘못 지정되는 문제를 해결했습니다.<br />" +
                    "디버그룸에서 튕기는 오류를 해결했습니다.<br />" +
                    "디버그룸 메시지의 최대 가로 크기를 확장했습니다.<br />" +
                    "약간의 최적화를 적용했습니다.<br />" +
                    "전체 봇 활성화/비활성화 버튼을 추가했습니다.<br />" +
                    "UI를 개선했습니다.<br />" +
                    "블랙리스트가 스크립트별로 구분되지 않는 문제를 해결했습니다.")
            msg.add(16, "<h3>2.89</h3> 스크립트 삭제 기능을 추가했습니다.<br />" +
                    "통합 매개변수 기능을 추가했습니다. 이 기능을 체크하실 경우 response함수를 room,msg등의 인자들을 하나의 객체로 모아 호출합니다.<br />" +
                    "약간의 최적화를 적용했습니다.<br />" +
                    "일부 UI를 개선했습니다.")
            msg.add(17, "<h3>2.9</h3> 약간의 안정화를 적용했습니다.")
            msg.add(18, "<h3>2.91</h3> 활성화 스위치가 작동하지 않는 문제를 해결했습니다.")
            msg.add(19, "<h3>2.92</h3> 이제 커스텀 패키지를 추가할 수 있습니다. (추가 방법: 공용 설정의 Custom Packages에 원하는 앱의 패키지명 입력후 적용 -> 스크립트 개별 설정에서 체크)")
            msg.add(20, "<h3>2.93</h3> DB가 없을때 DataBase.removeDataBase를 호출하면 발생하는 오류를 해결했습니다.<br /> Api.UIThread가 작동하지 않는 오류를 해결했습니다.<br />Device객체를 추가했습니다.<br />봇이 켜진 상태로 설정에서 스크립트 삭제시 봇이 계속 구동되는 문제를 해결했습니다. 단, 다른 파일 탐색기에서 삭제하는것은 주의해주세요.<br />Api.unload(\"스크립트이름.js\"): 해당 스크립트의 컴파일 상태를 제거합니다.")
            msg.add(21, "<h3>2.94</h3> Device에 파일읽기쓰기를 넣으면 추후에 보안 관련 분류가 어려워질 것 같아 FileStream.read, FileStream.write로 대체했습니다.<br />Device.getBatteryStatus를 수정했습니다.<br />Device.getBatteryIntent를 추가했습니다.<br />잦은 업데이트 죄송합니다. 업데이트에 좀 더 신중해지도록 하겠습니다.")
            msg.add(22, "")
            msg.add(23, "<h3>2.96</h3> 추후 구현의 용이성을 도모하여 코틀린으로 전환했습니다.<br />안드로이드 누가 미만에서 카카오톡 최신버전의 알림 수신 중 방 이름이 잘못 수신되는 오류를 해결했습니다.<br />카카오톡 구버전과의 연동성도 고려했지만, 카카오톡을 업데이트하는것을 권장합니다.<br />컴파일 시작/완료 로그에 스크립트 이름을 명시합니다.<br />ImageDB.getProfileBitmap()이 추가되었습니다.<br />방 세션 초기화 옵션을 공용설정으로 옮겼습니다.")
            msg.add(24, "<h3>2.97</h3> Api.UIThread오류를 해결했습니다.")
            msg.add(25, "<h3>2.98</h3> 디버그 룸이 안되는 현상, 없는 DB에 대해 getDataBase요청시 내부 오류가 발생하는 현상 등 중대한 오류를 해결했습니다.<br />디버그룸의 메시지 입력창이 세로로 확장되도록 변경했습니다.")
            msg.add(26, "<h3>2.99</h3> 스크립트 액티비티 문제를 해결했습니다.<br />도움말을 개선하였으며, 이제 도움말에서 업데이트 로그를 볼 수 있습니다.")
            msg.add(27, "<h3>3.00</h3> [내부 테스트] 디버그 룸에서 500자를 초과하는 메시지의 경우 전체보기를 표시하도록 했습니다.(데이터는 온전하게 전달됩니다.)")
            msg.add(28, "<h3>3.01</h3> (중요) Api.reload(또는 compile), Api.prepare에 인자를 전달하지 않을때 전체 리로드 중 오류가 나는 스크립트에서 정지되지 않도록 수정했습니다.<br />이제 두번째 인자로 true를 전달하면 오류가 나는 스크립트에서 정지합니다.")
            msg.add(29, "<h3>3.02</h3> Api.isCompiling(\"스크립트이름.js\")이 추가되었습니다. 인자를 전달하지 않을 경우 하나라도 컴파일중인 스크립트가 있을 시 true를 반환합니다.<br />지난 업데이트에서 몇가지 업데이트가 적용되지 않은 문제를 해결했습니다.<br />전역에서 인자없이 Api.compile을 호출 할 시 무한 컴파일이 되는 현상을 방지했습니다.")
            msg.add(30, "<h3>3.03</h3> DataBase.appendDataBase, FileStream.append가 추가되었습니다. <br /> DataBase의 setDataBase, FileStream의 write가 이제 파일의 최종 내용을 리턴합니다.(appendDataBase, append도 마찬가지)<br />DataBase와 FileStream의 각 함수에 이제 경로를 명시할 수 있습니다.<br />onStartCompile호출 중 에러 발생 시 튕기는 오류를 해결하고, 컴파일을 중지하도록 수정하였습니다.<br />도움말을 업데이트하였습니다.")
            msg.add(31, "<h3>3.04</h3> (긴급) 첫 컴파일중 컴파일에러 발생시 앱이 튕기는 문제를 해결했습니다.")
            msg.add(32, "<h3>3.05</h3> 디버그룸에서 replier.reply에 메시지값으로 null전달시 튕기는 문제를 해결했습니다.")
            msg.add(33, "<h2>--- 아라봇 포크 ---</h2>")
            msg.add(34, "<h3>아라봇 18.1006</h3> Messenger봇 관련된 것들을 모두 제거 <s>광고좆까^^</s>, 레이아웃 및 메뉴를 대폭 개편하였습니다. 이제부터는 오픈소스 GPLv3 라이센스로 배포됩니다.")
            msg.add(35, "<h3>아라봇 18.1007</h3> UI, 색, 아이콘 디자인을 대폭 수정하였습니다. (Material Design 대응)")
            msg.add(36, "<h3>아라봇 18.1110</h3> 어색한 번역을 모두 수정하고 영어로된 String을 번역하였습니다. 일본어 번역도 지움 ㅋ")
            msg.add(37, "<h3>아라봇 18.1223</h3> <s>개같은</s>UI 여백과 색을 수정하였습니다.")
            msg.add(38, "<h3>아라봇 18.1224</h3>** 18/12/24 메신저봇의 소스가 내려갔습니다. 시발 Violet 개새키^^ ㅗㅗ **")
            msg.add(39, "<h3>아라봇 19.0127</h3>Utils.getJsonTextByPOST() 함수와 Utils.decodeUnicode()함수, Utils.getAppVersion()함수를 추가했습니다. 자세한 내용은 도움말을 참고하세요.")

            val result = StringBuilder()
            for (i in lastVersion + 1 - 21..version - 21) {
                if (i > msg.size - 1) break
                if (msg[i].isEmpty()) continue
                result.append(msg[i]).append("<br /><br />")


            }


            return result.toString()
        }

        fun refreshProgressBar(scriptName: String, b: Boolean, changeColor: Boolean) {
            if (progressBarMap[scriptName] == null) return
            if (b) {
                progressBarMap[scriptName]!!.setVisibility(View.VISIBLE)
            } else {
                progressBarMap[scriptName]!!.setVisibility(View.GONE)
                if (changeColor && switchMap[scriptName] != null)
                    switchMap[scriptName]!!.setTextColor(MainApplication.context!!.resources.getColor(R.color.fully_compiled))

            }
        }

        fun putOn(scriptName: String, b: Boolean) {
            if (switchMap[scriptName] != null) {
                switchMap[scriptName]!!.isChecked = b
            }

            MainApplication.context!!.getSharedPreferences("bot$scriptName", 0).edit().putBoolean("on", b).apply()
        }

        fun putOnAll(b: Boolean) {
            val keySet = switchMap.keys
            for (k in keySet) {
                if (MainApplication.context!!.getSharedPreferences("settings$k", 0).getBoolean("ignoreApiOff", false))
                    continue
                switchMap[k]!!.isChecked = b
                MainApplication.context!!.getSharedPreferences("bot$k", 0).edit().putBoolean("on", b).apply()
            }
        }

        fun noti(ctx: Context) {
            val stringBuilder = StringBuilder()

            val keySet = switchMap.keys
            var b = false
            if (MainApplication.context!!.getSharedPreferences("bot", 0).getBoolean("activate", true)) {
                for (k in keySet) {
                    if (switchMap[k]!!.isChecked) {
                        b = true
                        stringBuilder.append(k).append("\n")
                    }


                }
            }//else b = false is redundant
            val notificationManager = MainApplication.context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val noti = Notification.Builder(MainApplication.context!!).setStyle(Notification.BigTextStyle()
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
            noti.addAction(Notification.Action(R.drawable.ic_refresh_black_24dp, ctx.resources.getString(R.string.compile_all), pReload))
            noti.addAction(Notification.Action(R.drawable.ic_close_black_24dp, ctx.resources.getString(R.string.off_all), pOff))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                noti.setChannelId("me.computerpark.ara_android.notification")
                // Create the NotificationChannel
                val name = ctx.getString(R.string.channel_name)
                val description = ctx.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_LOW
                val mChannel = NotificationChannel("me.computerpark.ara_android.notification", name, importance)
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
