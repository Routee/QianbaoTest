package com.routee.qianbaotest.base;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.routee.qianbaotest.BuildConfig;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Created by hdb on 2017/7/31.
 * description: ${TODO}
 */

public class QianbaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Global.setContext(this);
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SophixManager.getInstance().setContext(this)
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info
                            , final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                            new AlertDialog.Builder(QianbaoApplication.this).setTitle("你需要重启您的手机")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SophixManager.getInstance().killProcessSafely();
                                        }
                                    })
                                    .show();
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
    }
}
