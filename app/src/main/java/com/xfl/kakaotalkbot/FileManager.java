package com.xfl.kakaotalkbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileManager {
    public static String read(File script) {
        try {
            script.createNewFile();
            FileInputStream fIn = new FileInputStream(script);
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
            MainApplication.Companion.reportInternalError(e);
        }
        return null;
    }

}
