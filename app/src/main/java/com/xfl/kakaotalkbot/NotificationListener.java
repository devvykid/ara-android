package com.xfl.kakaotalkbot;


import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by XFL on 2/19/2018.
 */

public class NotificationListener extends NotificationListenerService {
    public static String debugRoom;
    public static Map<String, ScriptsManager> container = new HashMap<>();
    public static Handler UIHandler = new Handler();
    public static ScriptableObject execScope = null;
    public static View rootView;
    public static Map<String, Notification.Action> SavedSessions = new HashMap<>();
    private static File basePath = MainApplication.basePath;
    // static File sessionsPath = new File(basePath + File.separator + "Sessions");
    private static Map<String, String[]> banNameArr = new HashMap<>();
    private static Map<String, String[]> banRoomArr = new HashMap<>();
    private static Map<String, Boolean> isCompiling = new HashMap<>();
    android.content.Context context;
    Bitmap photo = null;
    boolean firstCompiling = false;

    public static View getRootView() {
        return rootView;
    }

    public static void setRootView(View v) {
        rootView = v;
    }

    private static void resetSession() {
        try {

            SavedSessions.clear();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Notification.Action getSession(String room) {
        return SavedSessions.get(room);
    }

    public static boolean hasSession(String room) {
        return SavedSessions.get(room) != null;

    }

    public static void callResponder(final String scriptName, final String room, final String msg, final String sender, final boolean isGroupChat, final ImageDB imageDB, final String packName, Notification.Action session, boolean isDebugMode) {


        final ScriptableObject execScope = container.get(scriptName).execScope;
        final Function responder = container.get(scriptName).responder;


        if (!isDebugMode) {
            SavedSessions.put(room, session);
        }

        try {
            Context.enter();
            Context parseContext = new RhinoAndroidHelper().enterContext();
            parseContext.setWrapFactory(new PrimitiveWrapFactory());
            parseContext.setLanguageVersion(Context.VERSION_ES6);
            parseContext.setOptimizationLevel(container.get(scriptName).optimization);
            Api.isDebugMode = isDebugMode;

            MainApplication.getContext().getSharedPreferences("log", 0).edit().putString("logTarget", scriptName).apply();
            if (responder != null) {
                if (MainApplication.getContext().getSharedPreferences("settings" + scriptName, 0).getBoolean("useUnifiedParams", false)) {
                    responder.call(parseContext, execScope, execScope, new Object[]{new ResponseParameters(room, msg, sender, isGroupChat, new SessionCacheReplier(room), imageDB, packName)});
                } else {
                    responder.call(parseContext, execScope, execScope, new Object[]{room, msg, sender, isGroupChat, new SessionCacheReplier(room), imageDB, packName});
                }
            }

            Context.exit();
        } catch (Throwable e) {


            Log.e("parser", "?", e);
            StringBuilder stack = new StringBuilder();
            stack.append("\n");
            if (MainApplication.getContext().getSharedPreferences("settings" + scriptName, 0).getBoolean("specificLog", false)) {
                for (StackTraceElement element : e.getStackTrace()) {
                    stack.append("at ").append(element.toString());
                    stack.append("\n");
                }
            }
            com.xfl.kakaotalkbot.Log.error(e.toString() + stack.toString(), true);
            UIHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (MainApplication.getContext().getSharedPreferences("settings" + scriptName, 0).getBoolean("offOnRuntimeError", true)) {
                        ScriptSelectActivity.putOn(scriptName, false);
                    }
                }
            });

        }
    }

    static void initializeBanList(String scriptName) {
        android.content.Context ctx = MainApplication.getContext();
        String banRoom = ctx.getSharedPreferences("bot", 0).getString("banRoom" + scriptName, "");
        String banName = ctx.getSharedPreferences("bot", 0).getString("banName" + scriptName, "");
        banRoomArr.put(scriptName, banRoom.split("\n"));

        banNameArr.put(scriptName, banName.split("\n"));

    }

    public static void initializeAll(final boolean isManual) {//isManual: true on Api.reload
        basePath.mkdir();
        File[] files = basePath.listFiles();
        for (File k : files) {
            if (k.getName().endsWith(".js")) {


                initializeScript(k.getName(), isManual);


            }
        }
    }

    public static boolean initializeScript(final String scriptName, final boolean isManual) {

        /*if (isCompiling.get(scriptName) != null && isCompiling.get(scriptName)) {
            return false;
        }*/
        isCompiling.put(scriptName, true);
        MainApplication.getContext().getSharedPreferences("log", 0).edit().putString("logTarget", scriptName).apply();
        final String PREF_SETTINGS = "settings" + scriptName;
        int optimization = MainApplication.getContext().getSharedPreferences(PREF_SETTINGS, 0).getInt("optimization", -1);
        final File script = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot" + File.separator + scriptName);

        if (container.get(scriptName) != null) {
            execScope = container.get(scriptName).execScope;
        }
        Function responder;


        com.xfl.kakaotalkbot.Log.info(MainApplication.getContext().getResources().getString(R.string.snackbar_compileStart));

        Context parseContext;
        try {
            parseContext = new RhinoAndroidHelper().enterContext();
            parseContext.setWrapFactory(new PrimitiveWrapFactory());
            parseContext.setLanguageVersion(Context.VERSION_ES6);
            parseContext.setOptimizationLevel(optimization);
        } catch (Exception e) {
            if (!isManual) {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ScriptSelectActivity.refreshProgressBar(scriptName, false, false);

                    }
                });
                Context.reportError(e.toString());
            }
            return false;
        }
        if (container.get(scriptName) != null) {
            if (container.get(scriptName).onStartCompile != null) {
                container.get(scriptName).onStartCompile.call(parseContext, execScope, execScope, new Object[]{});
            }

        }

        System.gc();
        if (MainApplication.getContext().getSharedPreferences(PREF_SETTINGS, 0).getBoolean("resetSession", false))
            resetSession();
        ScriptableObject scope;

        try {

            parseContext.setLanguageVersion(Context.VERSION_ES6);
            scope = (ScriptableObject) parseContext.initStandardObjects(new ImporterTopLevel(parseContext));
            FileReader fileReader = new FileReader(script);
            Script script_real = parseContext.compileReader(fileReader, scriptName, 0, null);
            fileReader.close();

            // ScriptableObject.putProperty(scope, "DataBase", Context.javaToJS(new DataBase(), scope));
            // ScriptableObject.putProperty(scope, "Api", Context.javaToJS(new Api(), scope));
            // ScriptableObject.putProperty(scope, "Utils", Context.javaToJS(new Utils(), scope));
            //ScriptableObject.putProperty(scope, "Log", Context.javaToJS(new com.xfl.kakaotalkbot.Log(), scope));
            Api.scriptName = scriptName;
            ScriptableObject.defineClass(scope, Api.class);
            ScriptableObject.defineClass(scope, DataBase.class);
            ScriptableObject.defineClass(scope, Utils.class);
            ScriptableObject.defineClass(scope, com.xfl.kakaotalkbot.Log.class);
            ScriptableObject.defineClass(scope, AppData.class);
            ScriptableObject.defineClass(scope, Bridge.class);

            execScope = scope;


            script_real.exec(parseContext, scope);
            if (scope.has("response", scope)) {
                responder = (Function) scope.get("response", scope);
            } else {
                responder = null;
            }
            Function onStartCompile = null;
            Function onCreate = null;
            Function onStop = null;
            Function onResume = null;
            Function onPause = null;
            if (scope.has("onStartCompile", scope)) {
                onStartCompile = (Function) scope.get("onStartCompile", scope);
            }
            if (scope.has("onCreate", scope)) {
                onCreate = (Function) scope.get("onCreate", scope);
            }
            if (scope.has("onStop", scope)) {
                onStop = (Function) scope.get("onStop", scope);
            }
            if (scope.has("onResume", scope)) {
                onResume = (Function) scope.get("onResume", scope);
            }
            if (scope.has("onPause", scope)) {
                onPause = (Function) scope.get("onPause", scope);
            }
            container.put(scriptName, new ScriptsManager()
                    .setExecScope(execScope)
                    .setResponder(responder)
                    .setOnStartCompile(onStartCompile)
                    .setOptimization(optimization)
                    .setScope(scope)
                    .setScriptActivity(onCreate, onStop, onResume, onPause)
            );
            Context.exit();

            com.xfl.kakaotalkbot.Log.info(MainApplication.getContext().getResources().getString(R.string.snackbar_compiled));

            isCompiling.put(scriptName, false);
        } catch (final Throwable e) {

            if (UIHandler != null) {
                UIHandler.post(new Runnable() {
                                   @Override
                                   public void run() {

                                       Log.e("parser", "?", e);
                                       Toast.makeText(MainApplication.getContext(), "Compile Error:" + e.toString(), Toast.LENGTH_LONG).show();
                                       com.xfl.kakaotalkbot.Log.error(e.toString(), false);
                                       ScriptSelectActivity.putOn(scriptName, false);
                                   }
                               }
                );
            }

            isCompiling.put(scriptName, false);
            if (!isManual) {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ScriptSelectActivity.refreshProgressBar(scriptName, false, false);

                    }
                });
                Context.reportError(e.toString());

            }
            return false;

        }


        MainApplication.getContext().getSharedPreferences("lastCompileSuccess2", 0).edit().putLong(scriptName, new Date().getTime()).apply();

        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainApplication.getContext(), MainApplication.getContext().getResources().getString(R.string.snackbar_compiled) + ":" + scriptName, Toast.LENGTH_SHORT).show();

            }
        });
        return true;
    }

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
    }

    @Override

    public void onNotificationPosted(final StatusBarNotification sbn) {

        super.onNotificationPosted(sbn);
        if(!MainApplication.getContext().getSharedPreferences("bot",0).getBoolean("activate",true))return;
        if (firstCompiling) return;
        final String packName = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        Log.d("extras", extras.toString());
        try {
            Log.d("txt", extras.getCharSequence("android.text").toString());
            Log.d("Package", sbn.getPackageName());
        } catch (NullPointerException e) {
        }
        if (!(packName.equals("jp.naver.line.android")
                || packName.equals("com.facebook.orca")
                || packName.equals("com.lbe.parallel.intl")
                || packName.equals("com.kakao.talk")
                || packName.equals("org.telegram.messenger"))
                ) {
            return;
        }

        if (isCompiling.size() <= 0 && MainApplication.getContext().getSharedPreferences("publicSettings", 0).getBoolean("autoCompile", true)) {//hasEverCompiled

            firstCompiling = true;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    basePath.mkdir();
                    final File[] files = basePath.listFiles();
                    if (files == null) return;
                    for (File k : files) {
                        if (k.getName().endsWith(".js")) {
                            if (MainApplication.getContext().getSharedPreferences("bot" + k.getName(), 0).getBoolean("on", false)) {
                                final File fk = k;
                                boolean bool = false;
                                UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ScriptSelectActivity.refreshProgressBar(fk.getName(), true, true);
                                    }
                                });
                                if (isCompiling.get(k.getName()) == null) {
                                    bool = initializeScript(k.getName(), true);

                                }
                                final boolean fbool = bool;
                                UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ScriptSelectActivity.refreshProgressBar(fk.getName(), false, fbool);

                                    }
                                });
                            }
                        }
                    }
                    firstCompiling = false;
//                        onNotificationPosted(sbn);
                }
            }).start();
            return;

        }
        String PREF_SETTINGS;

        try {
            if (Build.VERSION.SDK_INT <= 23)
                if (((ApplicationInfo) extras.get("android.rebuild.applicationInfo")).packageName.contains("com.kakao.talk")) {
                    Log.d("ApplicationInfo", "katalk");

                    if (extras.get("android.largeIcon") instanceof Bitmap)
                        photo = (Bitmap) extras.get("android.largeIcon");
                }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        boolean isAvailable;
        Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
        if (container.size() <= 0) return;
        Set<String> keySet = container.keySet();
        for (Notification.Action act : wExt.getActions()) {

            Log.d("actions", act.title.toString());
            Log.d("actionsExtra", act.getExtras().toString());

            if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {

                if (act.title.toString().toLowerCase().contains("reply") ||
                        act.title.toString().contains("답장") || act.title.toString().contains("返信") || act.title.toString().contains("답글")) {

                    String room, sender, msg;


                    boolean isGroupChat = extras.get("android.text") instanceof SpannableString;


                    if (Build.VERSION.SDK_INT > 23) {
                        try {
                            if (extras.get("android.largeIcon") instanceof Bitmap)
                                photo = (Bitmap) extras.get("android.largeIcon");
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        room = extras.getString("android.summaryText");

                        sender = extras.get("android.title").toString();
                        msg = extras.get("android.text").toString();

                        if (room == null) {
                            room = sender;
                            isGroupChat = false;
                        } else {
                            isGroupChat = true;
                        }
                        if (packName.equals("com.facebook.orca")) {
                            if (!(extras.get("android.text") instanceof String)) {
                                String html = Html.toHtml((Spanned) extras.get("android.text"));

                                sender = Html.fromHtml(html.split("<b>")[1].split("</b>")[0]).toString();
                                msg = Html.fromHtml(html.split("</b>")[1].split("</p>")[0].substring(1)).toString();
                                isGroupChat = true;
                            } else {
                                isGroupChat = false;
                            }
                        }

                    } else {
                        room = extras.getString("android.title");
                        if (!(extras.get("android.text") instanceof String)) {
                            String html = Html.toHtml((Spanned) extras.get("android.text"));
                            sender = Html.fromHtml(html.split("<b>")[1].split("</b>")[0]).toString();
                            msg = Html.fromHtml(html.split("</b>")[1].split("</p>")[0].substring(1)).toString();
                        } else {
                            sender = room;
                            msg = extras.get("android.text").toString();
                        }
                    }
                    Log.d("room", room);
                    Log.d("msg", msg);
                    Log.d("sender", sender);
                    Log.d("isGroupChat", isGroupChat + "");
                            /*if (MainApplication.getContext().getSharedPreferences(PREF_SETTINGS, 0).getBoolean("specificLog", false)) {
                                com.xfl.kakaotalkbot.Log.debug("App: " + packName);
                                com.xfl.kakaotalkbot.Log.debug("room: " + room);
                                com.xfl.kakaotalkbot.Log.debug("msg: " + msg);
                                com.xfl.kakaotalkbot.Log.debug("sender: " + sender);
                                com.xfl.kakaotalkbot.Log.debug("isGroupChat: " + isGroupChat);
                            }
*/

                    for (String key : keySet) {
                        if (!getApplicationContext().getSharedPreferences("bot" + key, 0).getBoolean("on", false))
                            continue;
                        String[] banNames = banNameArr.get(key);
                        String[] banRooms = banRoomArr.get(key);
                        if (banNames != null && banRooms != null) {
                            boolean banned = false;
                            for (String k : banNames) {
                                if (k.equals(sender)) banned = true;
                            }
                            for (String k : banRooms) {
                                if (k.equals(room)) banned = true;
                            }
                            if (banned) continue;
                        }
                        PREF_SETTINGS = "settings" + key;

                        isAvailable = (packName.equals("jp.naver.line.android")
                                && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useLine", false))
                                || (packName.equals("com.facebook.orca")
                                && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useFacebookMessenger", false))
                                || (packName.equals("com.lbe.parallel.intl")
                                && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useParallelSpace", false))
                                || (packName.equals("com.kakao.talk")
                                && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useNormal", true))
                                || (packName.equals("org.telegram.messenger")
                                && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useTelegram", false));
//TODO: isAvailable에 패키지 추가하면 위에있는 패키지 목록도 갱신해야함

                        if (isAvailable) {
                            Log.d("isAvailable", "true");

                            final String froom = room;
                            final String fmsg = msg;
                            final String fsender = sender;
                            final boolean fisGroupChat = isGroupChat;
                            final Notification.Action fact = act;

                            final ImageDB imageDB = new ImageDB(photo);
                            final String fkey = key;


                            final Thread thr = new Thread(new Runnable() {
                                public void run() {
                                    callResponder(fkey, froom, fmsg, fsender, fisGroupChat, imageDB, packName, fact, false);

                                }

                            });

                            thr.start();
                        }
                    }

                    break;

                }
            }
        }


    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.i("Msg", "Notification Removed");

    }


}
