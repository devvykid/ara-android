package com.xfl.kakaotalkbot;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ScriptSelectActivity extends AppCompatActivity {
    private static Map<String, Switch> switchMap = new HashMap<>();
    private static Map<String, ProgressBar> progressBarMap = new HashMap<>();
    private LinearLayout linearLayout;
    private File[] lastFiles;
    private File basePath = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");

    public static void refreshProgressBar(String scriptName, boolean b, boolean setColor) {
        if (progressBarMap.get(scriptName) == null) return;
        if (b) {
            progressBarMap.get(scriptName).setVisibility(View.VISIBLE);
        } else {
            progressBarMap.get(scriptName).setVisibility(View.GONE);
            if (setColor && switchMap.get(scriptName) != null)
                switchMap.get(scriptName).setTextColor(MainApplication.getContext().getResources().getColor(R.color.fully_compiled));
        }
    }

    public static void putOn(String scriptName, boolean b) {
        if (switchMap.get(scriptName) != null) {
            switchMap.get(scriptName).setChecked(b);
        }

        MainApplication.getContext().getSharedPreferences("bot" + scriptName, 0).edit().putBoolean("on", b).apply();
    }

    public static void putOnAll(boolean b) {
        Set<String> keySet = switchMap.keySet();
        for (String k : keySet) {
            if (MainApplication.getContext().getSharedPreferences("settings" + k, 0).getBoolean("ignoreApiOff", false))
                continue;
            switchMap.get(k).setChecked(b);
            MainApplication.getContext().getSharedPreferences("bot" + k, 0).edit().putBoolean("on", b).apply();
        }
    }

    public static void noti(Context ctx) {
        StringBuilder stringBuilder = new StringBuilder();

        Set<String> keySet = switchMap.keySet();
        boolean b = false;
        for (String k : keySet) {
            if (switchMap.get(k).isChecked()) {
                b = true;
                stringBuilder.append(k).append("\n");
            }


        }
        NotificationManager notificationManager =
                (NotificationManager) MainApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder noti = new Notification.Builder(MainApplication.getContext()).setStyle(new Notification.BigTextStyle()
                .bigText(stringBuilder.toString()));
        noti.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(ctx.getResources().getString(R.string.app_name))
                .setContentText(stringBuilder.toString())
                .setPriority(Notification.PRIORITY_MAX)

                .setOngoing(true);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, ScriptSelectActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        noti.setContentIntent(pendingIntent);

        Intent reload = new Intent(ctx, ActionReceiver.class);
        reload.putExtra("action", "reload");
        Intent off = new Intent(ctx, ActionReceiver.class);
        off.putExtra("action", "off");
        PendingIntent pReload = PendingIntent.getBroadcast(ctx, 1, reload, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pOff = PendingIntent.getBroadcast(ctx, 2, off, PendingIntent.FLAG_UPDATE_CURRENT);
        noti.addAction(new Notification.Action(R.drawable.ic_menu_refresh, ctx.getResources().getString(R.string.compile_all), pReload));
        noti.addAction(new Notification.Action(R.drawable.ic_close, ctx.getResources().getString(R.string.off_all), pOff));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            noti.setChannelId("com.xfl.kakaotalkbot.notification");
            // Create the NotificationChannel
            CharSequence name = ctx.getString(R.string.channel_name);
            String description = ctx.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("com.xfl.kakaotalkbot.notification", name, importance);
            mChannel.setDescription(description);
            mChannel.setShowBadge(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notificationManager.createNotificationChannel(mChannel);
        }


        if (!b)
            notificationManager.cancel(1);
        else {
            notificationManager.notify(1, noti.build());
        }


    }

    private String getUpdateMessage(int lastVersion, int version) {
        ArrayList<String> msg = new ArrayList<>();
//-21
        msg.add(0, "업데이트 다이얼로그 추가\nresponse 함수에 인자 추가: packageName\nDataBase.getDataBase에서 한줄이 더 추가되는 오류 해결\nApi.canReply(String room)을 통해 replyRoom 사용가능 여부 확인 가능");
        msg.add(1, "블랙리스트가 작동하지 않는 오류 해결");
        msg.add(2, "상세로그 간소화\n튕기는 문제 해결");
        msg.add(3, "최적화, 오류수정");
        msg.add(4, "디버그 모드 레이아웃 개선과 동시에, 개발자의 코딩 실력 부족으로(...) 디버그 모드를 보고있지 않을때 Api.replyRoom으로 디버그 모드에 메시지를 작성할 수 없게 되었습니다.\n" +
                "열심히 공부해서(?) 빠른 시일 내에 고치도록 하겠습니다(...)\n" +
                "대신, 꾹 눌러 복사하기를 추가하였고, 하이퍼링크 등을 지원하게 되었습니다.");
        msg.add(5, "Bridge.getScopeOf(\"스크립트이름.js\")를 통해 다른 스크립트의 전역변수/함수 등에 접근할 수 있습니다.\n" +
                "예) Bridge.getScopeOf(\"멋진메신저봇.js\").a=1\n" +
                "또한, 설정에서 Bridge에 의한 접근을 금지할 수 있습니다.\n" +
                "Bridge.isAllowed(\"스크립트이름.js\")를 통해 접근이 허용되어있는지 확인할 수 있습니다.\n\n" +
                "파일읽기 권한이 작동도중 사라졌을때 앱이 튕기는 문제 해결\n" +
                "Api.replyRoom의 토스트 문제 해결");

        msg.add(6, "[긴급패치]Api.replyRoom 반환값 오류 해결");
        msg.add(7, "최초 컴파일이 여러번 되는 문제 해결");
        msg.add(8, "Api.isOn(\"스크립트이름.js\")와 Utils.parse(\"주소\")가 추가됨. Utils.parse는 jsoup로 get한 결과를 리턴함\n" +
                "명칭 혼동을 막기 위해, Api.reload와 같은 기능을 하는 Api.compile추가\n" +
                "Api.getRootView()추가\n" +
                "Api.isCompiled(\"스크립트이름.js\")추가\n" +
                "Bridge.getScopeOf가 없거나 컴파일 되지 않은 스크립트에 접근하였을 경우 null반환");
        msg.add(9, "메모리 문제 해결\n공용 설정 추가\n(공용 설정)HTML 파싱 제한시간 설정 추가");
        msg.add(10, "Api.prepare(\"스크립트이름.js\"): 스크립트가 단 한번도 컴파일되지 않았을경우만 컴파일합니다.\n컴파일 된 적이 있을경우 2, 컴파일에 성공했을경우 1, 스크립트가 존재하지 않을경우 0을 반환하고, 컴파일에 실패했을경우 에러를 throw합니다.\n" +
                "이제 Api.reload 또는 Api.compile이 컴파일 에러가 발생했을경우 에러를 throw합니다.\n" +
                "이제 Utils.parse 및 Utils.getWebText에서 콘텐츠 타입을 무시합니다. (즉, 인터넷에 있는 .js파일 등을 불러올 수 있습니다.)");
        msg.add(11, "Api.papagoTranslate에서 에러를 반환하지 않고 이전 번역 결과를 반환하는 오류를 수정했습니다. 이제 4번째 인자를 true로 하면 에러를 String으로 반환하고, false로 하면 throw합니다." +
                "\n디버그화면과 샌드박스화면에 기록 지우기 버튼이 추가되었습니다." +
                "\n공용설정에 자동컴파일 관련 설정이 추가되었습니다.");
        StringBuilder result = new StringBuilder();
        for (int i = lastVersion + 1 - 21; i <= version - 21; i++) {
            if (i > msg.size() - 1) break;
            result.append(msg.get(i)).append("\n\n");


        }


        return result.toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationListener.setRootView(getWindow().getDecorView().getRootView());

        try {

            android.content.pm.PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = pInfo.versionCode;
            int lastVersion = getSharedPreferences("versionCode", 0).getInt("versionCode", 20);
            if (lastVersion < version) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ScriptSelectActivity.this);

                ad.setTitle("업데이트");       // 제목 설정

                final ScrollView scroll = new ScrollView(this);
                scroll.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                FrameLayout container = new FrameLayout(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                scroll.setLayoutParams(params);

                final TextView txt = new TextView(this);
                txt.setText(getUpdateMessage(lastVersion, version));
                scroll.addView(txt);
                container.addView(scroll);
                ad.setView(container);
// 확인 버튼 설정
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기

                    }
                });
                ad.show();
            }
            getSharedPreferences("versionCode", 0).edit().putInt("versionCode", version).apply();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        boolean granted;
// check to see if the enabledNotificationListeners String contains our package name

        granted = !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
        if (!granted) {
            AlertDialog.Builder ad = new AlertDialog.Builder(ScriptSelectActivity.this);

            ad.setTitle(MainApplication.getContext().getResources().getString(R.string.no_noti_read_permission));       // 제목 설정
            ad.setMessage(MainApplication.getContext().getResources().getString(R.string.alert_set_noti_access_message));   // 내용 설정

// 확인 버튼 설정
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                }
            });
            ad.show();
        }
        basePath.mkdir();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_scriptselect);
        linearLayout = findViewById(R.id.scriptSelectRecycler);
        FloatingActionButton addScript = findViewById(R.id.addScript);
        addScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ScriptSelectActivity.this);

                ad.setTitle(MainApplication.getContext().getResources().getString(R.string.alert_newScript_title));       // 제목 설정
                ad.setMessage(MainApplication.getContext().getResources().getString(R.string.alert_newScript_message));   // 내용 설정

// EditText 삽입하기
                final EditText et = new EditText(ScriptSelectActivity.this);
                ad.setView(et);

// 확인 버튼 설정
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (et.getText().toString().isEmpty()) {
                            return;
                        }
                        try {
                            String fileName = et.getText().toString();
                            if (!fileName.endsWith(".js")) {
                                fileName += ".js";
                            }
                            new File(basePath.getPath() + File.separator + fileName).createNewFile();
                            initialize();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });


// 취소 버튼 설정
                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

// 창 띄우기
                ad.show();


            }
        });
        Button setNotiAccess = findViewById(R.id.notiAccessSet);
        setNotiAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);

            }
        });
        Button openSandbox = findViewById(R.id.openSandbox);
        openSandbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CodePlaygroundScreen.class);
                startActivity(intent);

            }
        });
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        } else {

            initialize();

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

    private void initialize() {
        switchMap.clear();
        progressBarMap.clear();
        linearLayout.removeAllViews();
        CodePlaygroundScreen.initializeScript();


        final Context ctx = this;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


        basePath.mkdir();
        File[] files = basePath.listFiles();
        lastFiles = files;
        for (File k : files) {
            if (k.getName().endsWith(".js")) {
                NotificationListener.initializeBanList(k.getName());
                View view = inflater.inflate(R.layout.view_scriptselector, linearLayout, false);
                final Switch swit = view.findViewById(R.id.switch2);
                switchMap.put(k.getName(), swit);
                final ImageButton reload = view.findViewById(R.id.btn_reload);
                final ImageButton edit = view.findViewById(R.id.btn_editscript);
                final ImageButton manage = view.findViewById(R.id.btn_manage);
                final ImageButton debug = view.findViewById(R.id.btn_debug);
                final ImageButton log = view.findViewById(R.id.btn_log);
                final ProgressBar progressBar = view.findViewById(R.id.progressBar2);

                progressBarMap.put(k.getName(), progressBar);
                progressBar.setVisibility(View.GONE);

                final File fk = k;

                swit.setChecked(getApplicationContext().getSharedPreferences("bot" + k.getName(), 0).getBoolean("on", false));
                swit.setText(k.getName());
                swit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        getApplicationContext().getSharedPreferences("bot" + fk.getName(), 0).edit().putBoolean("on", b).apply();
                        noti(ctx);
                    }
                });
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NotificationListener.UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //compiling = true;
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });
                                final boolean bool = NotificationListener.initializeScript(fk.getName(), true);
                                /*if (!) {
                                    NotificationListener.UIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ScriptSelectActivity.this, ScriptSelectActivity.this.getResources().getString(R.string.already_compiling), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } else {*/
                                NotificationListener.UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //compiling = false;
                                        progressBar.setVisibility(View.GONE);
                                        if (bool)
                                            swit.setTextColor(getResources().getColor(R.color.fully_compiled));

                                    }
                                });
                                // }

                            }
                        }).start();


                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int i = getApplicationContext().getSharedPreferences("tutorial", 0).getInt("openAnotherApp", 0);
                        if (i < 2) {
                            getApplicationContext().getSharedPreferences("tutorial", 0).edit().putInt("openAnotherApp", i + 1).apply();
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tutorial_openAnotherApp), Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getApplicationContext(), ScriptEditor.class);
                        intent.putExtra("scriptName", fk.getName());
                        startActivity(intent);
                    }
                });
                edit.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Uri uri = FileProvider.getUriForFile(ctx, getApplicationContext().getPackageName() + ".provider", fk);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.setDataAndType(uri, "text/*");
                        startActivity(intent);
                        return true;
                    }
                });
                manage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SettingsScreen.class);
                        intent.putExtra("scriptName", fk.getName());
                        startActivity(intent);
                    }
                });
                debug.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), DebugModeScreen.class);
                        intent.putExtra("scriptName", fk.getName());
                        startActivity(intent);
                    }
                });
                log.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), LoggerScreen.class);
                        intent.putExtra("scriptName", fk.getName());
                        startActivity(intent);
                    }
                });


                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(view);
            }
        }
        Set<String> keySet = switchMap.keySet();
        boolean b = false;
        for (String k : keySet) {
            b = switchMap.get(k).isChecked();

        }
        if (b) {
            noti(ctx);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;

        }
        if (isFileChanged()) {
            initialize();
        }
        File[] files = basePath.listFiles();
        for (File k : files) {
            if (!k.getName().endsWith(".js")) continue;
            if (!(MainApplication.getContext().getSharedPreferences("lastCompileSuccess", 0).getString(k.getName(), "").equals(FileManager.read(k)))) {
                switchMap.get(k.getName()).setTextColor(getResources().getColor(R.color.need_compile));
            }
        }

    }

    private boolean isFileChanged() {


        basePath.mkdir();
        File[] files = basePath.listFiles();
        boolean bool = false;
        if (files == null || lastFiles == null) {
            return false;
        }
        if (files.length != lastFiles.length) {
            return true;
        }
        for (int i = 0; i < files.length; i++) {
            if (!(files[i].getName().equals(lastFiles[i].getName()))) {
                bool = true;
            }
        }
        return bool;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initialize();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);

                    }
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_public_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_go_public_settings) {
            Intent intent = new Intent(getApplicationContext(), PublicSettingsScreen.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
