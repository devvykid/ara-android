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
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XFL on 2/19/2018.
 */

public class NotificationListener extends NotificationListenerService {
    public static String debugRoom;
    public static Map<String, ScriptsManager> container = new HashMap<>();
    public static NativeArray scriptArray;
    public static Handler UIHandler = new Handler();
    public static ScriptableObject execScope = null;
    public static View rootView;
    public static Integer SessionsNum = 0;
    public static List<String> Rooms = new ArrayList<String>();
    public static List<Notification.Action> SavedSessions = new ArrayList<Notification.Action>();
    private static File basePath = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");
    static File sessionsPath = new File(basePath + File.separator + "Sessions");
    private static String[] banNameArr;
    private static String[] banRoomArr;
    private static Map<String, com.xfl.kakaotalkbot.Log> logger = new HashMap<>();
    private static Map<String, Boolean> isCompiling = new HashMap<>();
    android.content.Context context;
    Bitmap photo = null;

    public static View getRootView() {
        return rootView;
    }

    public static void setRootView(View v) {
        rootView = v;
    }

    private static void resetSession() {
        try {
            SessionsNum = 0;

            SavedSessions.clear();
            Rooms.clear();
            sessionsPath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Notification.Action getSession(String room) {
        return SavedSessions.get(getRoomNum(room));
    }

    public static Integer getRoomNum(String room) {
        return Rooms.indexOf(room);

    }

    public static void callResponder(final String scriptName, String room, String msg, String sender, boolean isGroupChat, ImageDB imageDB, String packName, Notification.Action session, boolean isDebugMode) {



                final ScriptableObject execScope = container.get(scriptName).execScope;
                final Function responder = container.get(scriptName).responder;

                try {
           /* if(!sessionsPath.exists())sessionsPath.mkdirs();
            File sessionFile=new File(sessionsPath.getAbsolutePath()+File.separator+room);
            sessionFile.setWritable(true);
            FileOutputStream sOut=new FileOutputStream(sessionFile);
            ObjectOutput oOut=new ObjectOutputStream(sOut);*/


                if (!isDebugMode) {
                    if (Rooms.indexOf(room) == -1) {
                        //sessionFile.createNewFile();
                        //oOut.writeObject(session);
                        //oOut.flush();
                        // oOut.close();
                        Log.d("Sessions", "added Session");
                        SavedSessions.add(session);
                        Rooms.add(room);
                        SessionsNum++;
                    } else if (!session.equals(SavedSessions.get(Rooms.indexOf(room)))) {
                        // oOut.writeObject(session);
                        //oOut.flush();
                        //oOut.close();
                        Log.d("Sessions", "changed Session");
                        SavedSessions.set(Rooms.indexOf(room), session);

                    }
                }
            }catch(Throwable e){
                    StringBuilder stack = new StringBuilder();
                    stack.append("\n");

                    for (StackTraceElement element : e.getStackTrace()) {
                        stack.append("at ").append(element.toString());
                        stack.append("\n");
                    }

                Toast.makeText(MainApplication.getContext(),MainApplication.getContext().getResources().getString(R.string.internal_error),Toast.LENGTH_LONG).show();
                    final String formed = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(new Date()) + (MainApplication.getContext().getResources().getString(R.string.internal_error)+"\n"+stack.toString()).replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
                    LoggerScreen.appendLogText(Html.fromHtml("<font color=RED>"+formed+"</font><br><br>"));
            }
        try {
            Context.enter();
            Context parseContext = new RhinoAndroidHelper().enterContext();
            parseContext.setWrapFactory(new PrimitiveWrapFactory());
            parseContext.setLanguageVersion(Context.VERSION_ES6);
            parseContext.setOptimizationLevel(container.get(scriptName).optimization);
            Api.isDebugMode = isDebugMode;

            MainApplication.getContext().getSharedPreferences("log", 0).edit().putString("logTarget", scriptName).apply();

            responder.call(parseContext, execScope, execScope, new Object[]{room, msg, sender, isGroupChat, new SessionCacheReplier(room), imageDB, packName});


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
        banRoomArr = banRoom.split("\n");
        banNameArr = banName.split("\n");
        for (String s : banRoomArr) {
            Log.d("banRoom" + scriptName, s);
        }
        for (String s : banNameArr) {
            Log.d("banName" + scriptName, s);
        }
    }

    public static void initializeAll(final boolean isManual) {
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

            Script script_real = parseContext.compileReader(new FileReader(script), scriptName, 0, null);


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

            responder = (Function) scope.get("response", scope);
            Function onStartCompile;

            if (scope.has("onStartCompile", scope)) {
                onStartCompile = (Function) scope.get("onStartCompile", scope);
            } else {
                onStartCompile = null;
            }


            container.put(scriptName, new ScriptsManager()
                    .setExecScope(execScope)
                    .setResponder(responder)
                    .setOnStartCompile(onStartCompile)
                    .setOptimization(optimization)
                    .setScope(scope)
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
        try {

            MainApplication.getContext().getSharedPreferences("lastCompileSuccess", 0).edit().putString(scriptName, FileManager.read(script)).apply();
        } catch (Exception e) {
        }
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
        Bundle extras = sbn.getNotification().extras;
        Log.d("extras", extras.toString());
        try {
            Log.d("txt", extras.getCharSequence("android.text").toString());
            Log.d("Package", sbn.getPackageName());
        } catch (NullPointerException e) {
        }
        if (isCompiling.size() <= 0 && MainApplication.getContext().getSharedPreferences("publicSettings", 0).getBoolean("autoCompile", true)) {//hasEverCompiled
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

                                UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ScriptSelectActivity.refreshProgressBar(fk.getName(), true, true);
                                    }
                                });
                                if (isCompiling.get(k.getName()) == null) {
                                    initializeScript(k.getName(), true);
                                    onNotificationPosted(sbn);
                                }
                                UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ScriptSelectActivity.refreshProgressBar(fk.getName(), false, true);

                                    }
                                });
                            }
                        }
                    }

                }
            }).start();

        }
        String PREF_SETTINGS;
        final String packName = sbn.getPackageName();
        try {
            if (Build.VERSION.SDK_INT <= 23)
                if (((ApplicationInfo) extras.get("android.rebuild.applicationInfo")).packageName.contains("com.kakao.talk")) {
                    Log.d("ApplicationInfo", "katalk");

                    if (extras.get("android.largeIcon") instanceof Bitmap)
                        photo = (Bitmap) extras.get("android.largeIcon");
                }
        } catch (NullPointerException e) {
        }

        boolean isAvailable;
        Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
        if (container.size() <= 0) return;
        Set<String> keySet = container.keySet();
        for (String key : keySet) {
            if (!getApplicationContext().getSharedPreferences("bot" + key, 0).getBoolean("on", false))
                continue;

            PREF_SETTINGS = "settings" + key;

            isAvailable = (packName.equals("jp.naver.line.android")
                    && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useLine", false))
                    || (packName.equals("com.facebook.orca")
                    && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useFacebookMessenger", false))
                    || (packName.equals("com.lbe.parallel.intl")
                    && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useParallelSpace", false))
                    || (packName.equals("com.kakao.talk")
                    && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useNormal", true));


            if (isAvailable) {
                Log.d("isAvailable", "true");

                for (Notification.Action act : wExt.getActions()) {

                    Log.d("actions", act.title.toString());
                    Log.d("actionsExtra", act.getExtras().toString());

                    if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {

                        if (act.title.toString().toLowerCase().contains("reply") ||
                                act.title.toString().contains("답장") || act.title.toString().contains("返信") || act.title.toString().contains("답글")) {

                            String room, sender, msg;


                            boolean isGroupChat = extras.get("android.text") instanceof SpannableString;


                            if (Build.VERSION.SDK_INT > 23) {
                                if (extras.get("android.largeIcon") instanceof Bitmap)
                                    photo = (Bitmap) extras.get("android.largeIcon");
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
                            if (banNameArr != null && banRoomArr != null) {
                                for (String k : banNameArr) {
                                    if (k.equals(sender)) return;
                                }
                                for (String k : banRoomArr) {
                                    if (k.equals(sender)) return;
                                }
                            }
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
                            break;

                        }
                    }
                }
            }
        }


    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.i("Msg", "Notification Removed");

    }


}
