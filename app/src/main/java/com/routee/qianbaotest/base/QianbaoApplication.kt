package com.routee.qianbaotest.base

import android.app.Application
import android.content.Context
import android.support.v7.app.AlertDialog
import com.routee.qianbaotest.BuildConfig
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixManager

/**
 * Created by hdb on 2017/7/31.
 * description: ${TODO}
 */

class QianbaoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Global.sContext = this
        SophixManager.getInstance().queryAndLoadNewPatch()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SophixManager.getInstance().setContext(this)
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub { mode, code, info, handlePatchVersion ->
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        AlertDialog.Builder(this@QianbaoApplication).setTitle("你需要重启您的手机")
                                .setCancelable(false)
                                .setPositiveButton("确定") { dialog, which -> SophixManager.getInstance().killProcessSafely() }
                                .show()
                    } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                        // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                        SophixManager.getInstance().cleanPatches()
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                    }
                }.initialize()
    }
}
