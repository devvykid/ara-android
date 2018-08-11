package com.xfl.kakaotalkbot;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class FileStream extends ScriptableObject {
    public String getClassName(){
        return "FileStream";
    }
    @JSStaticFunction
    public static String read(String path) {
        try {
            File f = new File(path);
            f.mkdirs();
            f.createNewFile();
            FileInputStream fIn = new FileInputStream(f);
            InputStreamReader myInReader = new InputStreamReader(fIn, "UTF-8");
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
        } catch (Exception e) {
            Context.reportError(e.toString());
        }
        return null;
    }

    @JSStaticFunction
    public static String write(String path, String data) {
        BufferedWriter wr = null;
        try {

            File f = new File(path);
            f.mkdirs();
            f.createNewFile();
            wr = new BufferedWriter(new FileWriter(path));
            wr.write(data);
            wr.flush();
            wr.close();

        } catch (Exception e) {
            Context.reportError(e.toString());

        }
        return read(path);
    }

    @JSStaticFunction
    public static String append(String path, String data) {
        try {
            FileWriter fw = new FileWriter(path, true);
            fw.write(data);
            fw.close();
        } catch (Exception e) {
            Context.reportError(e.getMessage());
        }
        return read(path);
    }
}
