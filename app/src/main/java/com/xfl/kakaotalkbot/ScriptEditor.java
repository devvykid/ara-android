package com.xfl.kakaotalkbot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


/**
 * Created by XFL on 2/19/2018.
 */

public class ScriptEditor extends AppCompatActivity {
    EditText scriptEdit;
    NestedScrollView scrollView;
    String lastSave;

    private File script;
    private String scriptName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scripteditor);

        final TextView debug = findViewById(R.id.debug);
        scriptEdit = (EditText) findViewById(R.id.JSCodeEdit);

        scriptName = getIntent().getExtras().getString("scriptName");
        final FloatingActionButton fab = findViewById(R.id.fab);

        final File scriptDir = MainApplication.basePath;
        if (!scriptDir.exists())
            scriptDir.mkdir();

        script = new File(scriptDir.getPath() + File.separator + scriptName);
        if (!script.exists()) {
            try {
                script.createNewFile();
            } catch (Exception e) {
                Toast.makeText(MainApplication.getContext(), "파일생성오류", Toast.LENGTH_SHORT).show();
            }
        }

        scrollView = findViewById(R.id.scriptEdit_scrollView);
        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.setScrollY(MainApplication.getContext().getSharedPreferences("editor", 0).getInt("scrollState", 0));
                            }
                        }
        );

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                MainApplication.getContext().getSharedPreferences("editor", 0).edit().putInt("scrollState", scrollView.getScrollY()).apply();

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = getApplicationContext().getSharedPreferences("tutorial", 0).getInt("saveAndCompile", 0);
                if (i < 2) {
                    getApplicationContext().getSharedPreferences("tutorial", 0).edit().putInt("saveAndCompile", i + 1).apply();
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tutorial_saveAndCompile), Toast.LENGTH_SHORT).show();
                }
                save();
                Toast.makeText(MainApplication.getContext(), R.string.snackbar_script_saved, Toast.LENGTH_SHORT).show();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                save();
                Toast.makeText(MainApplication.getContext(), R.string.snackbar_compileStart, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationListener.initializeScript(scriptName, true);
                    }
                }).start();

                return true;
            }
        });
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
        loadScript();


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
                ScriptableObject.defineClass(scope, com.xfl.kakaotalkbot.Log.class);
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
    public void save() {
        script.setWritable(true);

        try {
            FileOutputStream fOut = new FileOutputStream(script);

            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, StandardCharsets.UTF_8);
            myOutWriter.write(getScript());
            Log.d("Script", getScript());
            myOutWriter.close();
            lastSave = getScript();

            fOut.flush();
            fOut.close();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainApplication.getContext(), R.string.snackbar_script_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open_on_another_app) {

            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", script);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "text/*");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public String getScript() {
        return scriptEdit.getText().toString();
    }

    private boolean loadScript() {
        boolean changed = false;
        if (script == null) {
            throw new NullPointerException();
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();
            final String str = FileManager.read(script);
            if (str.isEmpty()) {
                String param;
                if(MainApplication.getContext().getSharedPreferences("settings"+scriptName,0).getBoolean("useUnifiedParams",false)){
                    param="params";
                }else{
                    param="room, msg, sender, isGroupChat, replier, ImageDB, packageName";
                }
                scriptEdit.setText("const scriptName=\"" + scriptName + "\";\n\n" +
                        "function response("+param+"){\n" +
                        "    /*(이 내용은 길잡이일 뿐이니 지우셔도 무방합니다)\n" +
                        "     *room: 메시지를 받은 방 이름\n" +
                        "     *msg: 메시지 내용\n" +
                        "     *sender: 전송자 닉네임\n" +
                        "     *isGroupChat: 단체/오픈채팅 여부\n" +
                        "     *replier: 응답용 객체. replier.reply(\"메시지\") 또는 replier.reply(\"방이름\",\"메시지\")로 전송\n" +
                        "     *ImageDB.getProfileImage(): 전송자의 프로필 이미지를 Base64로 인코딩하여 반환\n" +
                        "     *packageName: 메시지를 받은 메신저의 패키지 이름. (카카오톡: com.kakao.talk, 페메: com.facebook.orca, 라인: jp.naver.line.android\n" +
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
                );
            } else {
                if (!str.equals(lastSave)) {
                    lastSave = str;
                    scriptEdit.setText(str);

                    changed = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return changed;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (loadScript()) {
            Toast.makeText(this, getResources().getString(R.string.script_autoloaded), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainApplication.getContext().getSharedPreferences("editor", 0).edit().putInt("scrollState", scrollView.getScrollY()).apply();
    }
}
