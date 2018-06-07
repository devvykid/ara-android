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


        scriptEdit = (EditText) findViewById(R.id.JSCodeEdit);


        scriptName = getIntent().getExtras().getString("scriptName");
        final FloatingActionButton fab = findViewById(R.id.fab);

        final File scriptDir = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");
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

        loadScript();


    }

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
            final String str = FileManager.read(script);
            if (str.isEmpty()) {
                scriptEdit.setText("const scriptName=\"" + scriptName + "\";\n\n" +
                        "function response(room, msg, sender, isGroupChat, replier, ImageDB, packageName){\n" +
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
                        "     *제안하는 용도: 리로드시 자동 백업*/\n    \n}"
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
