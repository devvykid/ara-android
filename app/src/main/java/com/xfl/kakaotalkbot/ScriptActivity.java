package com.xfl.kakaotalkbot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.ScriptableObject;

public class ScriptActivity extends AppCompatActivity {
    org.mozilla.javascript.Context parseCtx;
    ScriptableObject excScope;
    private String scriptName;
    private ScriptsManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scriptName = getIntent().getExtras().getString("scriptName");
        manager=NotificationListener.container.get(scriptName);

        if(manager==null){
            Toast.makeText(this,getResources().getString(R.string.please_compile_first),Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(manager.getOnCreate()==null){
            Toast.makeText(this,"There is no function onCreate",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
        parseCtx = new RhinoAndroidHelper().enterContext();
        parseCtx.setWrapFactory(new PrimitiveWrapFactory());
        parseCtx.setOptimizationLevel(manager.optimization);
        excScope = manager.execScope;

            manager.getOnCreate().call(parseCtx, excScope, excScope, new Object[]{savedInstanceState, this});
        } catch (Throwable e) {
            Log.error("onCreate Error("+scriptName+"):"+e.getMessage(),true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(manager==null){return;}
        if(manager.getOnStop()==null){
            Toast.makeText(this,"There is no function onStop",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
        manager.getOnStop().call(parseCtx, excScope, excScope, new Object[]{this});

        } catch (Throwable e) {
            Log.error("onStop Error("+scriptName+"):"+e.getMessage(),true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(manager==null){return;}
        if(manager.getOnResume()==null){
            Toast.makeText(this,"There is no function onResume",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            manager.getOnResume().call(parseCtx, excScope, excScope, new Object[]{this});
        } catch (Throwable e) {
            Log.error("onResume Error("+scriptName+"):"+e.getMessage(),true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(manager==null){return;}
        if(manager.getOnPause()==null){
            Toast.makeText(this,"There is no function onPause",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            manager.getOnPause().call(parseCtx, excScope, excScope, new Object[]{this});
        } catch (Throwable e) {
            Log.error("onPause Error("+scriptName+"):"+e.getMessage(),true);
        }
    }

}
