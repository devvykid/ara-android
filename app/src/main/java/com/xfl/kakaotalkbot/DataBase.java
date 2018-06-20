package com.xfl.kakaotalkbot;

import android.os.Environment;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by XFL on 2/20/2018.
 */

public class DataBase extends ScriptableObject {
    static File dbDir = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot" + File.separator + "Database");

    @JSStaticFunction
    public static void setDataBase(String fileName, String data) {
        try {

            if (MainApplication.getContext().getSharedPreferences("compatibility", 0).getBoolean("JBBot", false)) {
                if (!data.contains(".")) {
                    data += ".txt";
                }
                String temp = data;
                data = fileName;
                fileName = temp;
            } else {
                if (!fileName.contains(".")) {
                    fileName += ".txt";
                }
            }

            dbDir.mkdirs();
            File file = new File(dbDir, fileName);

            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter fOutWriter = new OutputStreamWriter(fOut);
            fOutWriter.write(data);
            fOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            MainApplication.reportInternalError(e);
        }
    }

    @JSStaticFunction
    public static String getDataBase(String fileName) {

        ArrayList<String> result = new ArrayList<>();
        try {
            if (!fileName.contains(".")) {
                fileName += ".txt";
            }
            File file = new File(dbDir, fileName);
            FileInputStream fIn = null;
            try {
                fIn = new FileInputStream(file);
            } catch (FileNotFoundException e) {

                return null;
            }
            InputStreamReader myInReader = new InputStreamReader(fIn);
            BufferedReader bufferedReader = new BufferedReader(myInReader);
            StringBuilder stringBuilder = new StringBuilder();
            int crt;


            while ((crt = bufferedReader.read()) != -1) {
                stringBuilder.append((char) crt);
            }

            bufferedReader.close();
            myInReader.close();

            fIn.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            MainApplication.reportInternalError(e);
        }

        return null;
    }

    @JSStaticFunction
    public static boolean removeDataBase(String fileName) {
        if (!fileName.contains(".")) {
            fileName += ".txt";
        }
        if (MainApplication.getContext().getSharedPreferences("settings", 0).getBoolean("onDeleteBackup", true)) {
            setDataBase(fileName + ".bak", getDataBase(fileName));
        }
        File file = new File(dbDir, fileName);
        return file.delete();
    }


    public final String getClassName() {
        return "DataBase";
    }
}