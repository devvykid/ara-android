package com.xfl.kakaotalkbot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.json.JSONObject;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by XFL on 2/20/2018.
 */

public final class Api extends ScriptableObject {
    public static boolean isDebugMode;

    public static String scriptName;

    private static Context ctx = MainApplication.getContext();

    @JSStaticFunction
    public static final View getRootView() {
        return NotificationListener.getRootView();
    }

    @JSStaticFunction
    public static final Context getContext() {
        return MainApplication.getContext();
    }

    @JSStaticFunction
    public static final void UIThread(final org.mozilla.javascript.Function function, final Function onComplete) {
        final org.mozilla.javascript.Context parseCtx = new RhinoAndroidHelper().enterContext();
        parseCtx.setWrapFactory(new PrimitiveWrapFactory());
        final ScriptableObject excScope;

        parseCtx.setOptimizationLevel(NotificationListener.container.get(scriptName).optimization);
        excScope = NotificationListener.container.get(scriptName).execScope;


        NotificationListener.UIHandler.post(new Runnable() {
            @Override
            public void run() {
                org.mozilla.javascript.Context.enter();
                Throwable error = null;
                Object result = null;
                try {

                    result = function.call(parseCtx, excScope, excScope, new Object[]{});

                } catch (Throwable e) {
                    error = e;
                    //parseCtx.getErrorReporter().error(e.getMessage(), "a", 0, "aaa", 0);
                }
                try {
                    if (onComplete != null)
                        onComplete.call(parseCtx, excScope, excScope, new Object[]{error, result});
                } catch (Throwable e) {
                    Log.error(e.toString(), true);
                }
                org.mozilla.javascript.Context.exit();

            }
        });


    }

    @JSStaticFunction
    public static final void showToast(final String str, final int length) {
        NotificationListener.UIHandler.post(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(MainApplication.getContext(), str, length).show();
            }
        });

    }

    @JSStaticFunction
    public static final boolean canReply(String room) {
        return NotificationListener.getRoomNum(room) != -1;
    }

    @JSStaticFunction
    public static final boolean replyRoom(String room, String str, boolean hideToast) {
        try {
            return new SessionCacheReplier(room).reply(room, str, hideToast);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @JSStaticFunction
    public static final boolean off(final String scriptName) {

        if (scriptName.equals("undefined")) {
            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScriptSelectActivity.putOnAll(false);

                }
            });

        } else {
            if (!(new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot" + File.separator + scriptName).exists())) {
                return false;
            }
            if (MainApplication.getContext().getSharedPreferences("settings" + scriptName, 0).getBoolean("ignoreApiOff", false))
                return false;
            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScriptSelectActivity.putOn(scriptName, false);
                }
            });
        }
        return true;

    }

    @JSStaticFunction
    public static final boolean on(final String scriptName) {

        if (scriptName.equals("undefined")) {
            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScriptSelectActivity.putOnAll(true);

                }
            });

        } else {
            if (!(new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot" + File.separator + scriptName).exists())) {
                return false;
            }
            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScriptSelectActivity.putOn(scriptName, true);
                }
            });
        }
        return true;

    }

    @JSStaticFunction
    public static final boolean isOn(final String scriptName) {
        return getContext().getSharedPreferences("bot" + scriptName, 0).getBoolean("on", false);
    }

    @JSStaticFunction
    public static final boolean isCompiled(final String scriptName) {
        return NotificationListener.container.get(scriptName) != null;
    }

    @JSStaticFunction
    public static Scriptable getScriptNames() {
        File basePath = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");
        basePath.mkdir();
        File[] files = basePath.listFiles();
        List<String> list = new ArrayList<>();
        for (File k : files) {
            if (k.getName().endsWith(".js")) {
                list.add(k.getName());
            }
        }

        return org.mozilla.javascript.Context.enter().newArray(NotificationListener.container.get(scriptName).execScope, list.toArray());

    }
    /*@JSStaticFunction
    public static Scriptable getScriptFiles(){

        File basePath = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");
        basePath.mkdir();

        File[] files = basePath.listFiles();
        List<File>list=new ArrayList<>();

        for (File k : files) {
            if (k.getName().endsWith(".js")) {
                list.add(k);
            }
        }
        return org.mozilla.javascript.Context.enter().newArray(NotificationListener.container.get(scriptName).execScope,list.toArray());
    }*/


    @JSStaticFunction
    public static final boolean makeNoti(final String title, final String content, final int id) {
        Notification.Builder noti = new Notification.Builder(MainApplication.getContext());
        final NotificationManager notificationManager =
                (NotificationManager) MainApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        try {
            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    Notification.Builder noti = new Notification.Builder(MainApplication.getContext());


                    noti.setSmallIcon(R.mipmap.ic_launcher);
                    noti.setContentTitle(title);
                    noti.setContentText(content);
                    noti.setPriority(Notification.PRIORITY_MAX);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        noti.setChannelId("com.xfl.kakaotalkbot.customNotification");
                        // Create the NotificationChannel
                        CharSequence name = ctx.getString(R.string.channel_name);
                        String description = ctx.getString(R.string.channel_description);
                        int importance = NotificationManager.IMPORTANCE_MAX;
                        NotificationChannel mChannel = new NotificationChannel("com.xfl.kakaotalkbot.customNotification", name, NotificationManager.IMPORTANCE_HIGH);
                        mChannel.setDescription(description);
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this

                        notificationManager.createNotificationChannel(mChannel);
                    }


                    notificationManager.notify(1, noti.build());


                }
            });


        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @JSStaticFunction
    public static final int prepare(final String scriptName) {
        if (Api.isCompiled(scriptName)) return 2;
        if (Api.reload(scriptName)) return 1;
        else return 0;
    }

    @JSStaticFunction
    public static final boolean compile(final String scriptName) {
        return reload(scriptName);
    }

    @JSStaticFunction
    public static final boolean reload(final String scriptName) {


        if (scriptName.equals("undefined")) {

            NotificationListener.initializeAll(false);


        } else {
            if (!(new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot" + File.separator + scriptName).exists())) {
                return false;
            }


            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScriptSelectActivity.refreshProgressBar(scriptName, true, true);

                }
            });
            final boolean bool = NotificationListener.initializeScript(scriptName, false);

            NotificationListener.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScriptSelectActivity.refreshProgressBar(scriptName, false, bool);

                }
            });

            return bool;
        }
        return true;

    }

    @JSStaticFunction
    public static final String papagoTranslate(final String source, final String target, final String str, Boolean errorToString) {
        return doPapagoTranslate(source, target, str, errorToString);
    }

    @JSStaticFunction
    private static final String doPapagoTranslate(final String source, final String target, final String str, final Boolean errorToString) {

        String res = null;


        String clientId = "80WHs92cd42tX_6Y_mIZ";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "8nD99bYAlD";//애플리케이션 클라이언트 시크릿값";
        try {
            String text = URLEncoder.encode(str, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/language/translate";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source=" + source + "&target=" + target + "&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생

                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String einputLine;
                StringBuffer eresponse = new StringBuffer();
                while ((einputLine = br.readLine()) != null) {
                    eresponse.append(einputLine);
                }
                throw new Exception(new JSONObject(eresponse.toString()).getString("errorMessage"));

            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            JSONObject jsonObject = new JSONObject(response.toString());

            res = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");

        } catch (final Exception e) {
            if (errorToString) {
                res = e.getMessage();
            } else {

                org.mozilla.javascript.Context.enter();
                org.mozilla.javascript.Context.reportError(e.getMessage());

            }
            System.out.println(e);
        }


        return res;
    }

    @JSStaticFunction
    public static final void gc() {
        System.gc();
    }

    public final String getClassName() {
        return "Api";
    }


}
