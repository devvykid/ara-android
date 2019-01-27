package kr.oror.arabot;

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
    @JSStaticFunction
    private static String read(String path) {
        try {
            File f = new File(path);
            f.getParentFile().mkdirs();
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
        BufferedWriter wr;
        try {

            File f = new File(path);
            f.getParentFile().mkdirs();
            f.getAbsoluteFile().createNewFile();
            wr = new BufferedWriter(new FileWriter(f.getAbsoluteFile()));
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
            File f = new File(path);
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(data);
            fw.close();
        } catch (Exception e) {
            Context.reportError(e.getMessage());
        }
        return read(path);
    }

    @JSStaticFunction
    public static boolean remove(String path) {
        try {
            return new File(path).delete();
        } catch (Exception e) {
            Context.reportError(e.toString());
        }
        return false;
    }

    public String getClassName() {
        return "FileStream";
    }
}
