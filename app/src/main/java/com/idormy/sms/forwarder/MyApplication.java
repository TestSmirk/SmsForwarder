package com.idormy.sms.forwarder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.idormy.sms.forwarder.sender.SendHistory;
import com.idormy.sms.forwarder.utils.Define;
import com.idormy.sms.forwarder.utils.PhoneUtils;
import com.idormy.sms.forwarder.utils.SettingUtil;
import com.smailnet.emailkit.EmailKit;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    //SIM卡信息
    public static List<PhoneUtils.SimInfo> SimInfoList = new ArrayList<>();
    //是否关闭页面提示
    public static boolean showHelpTip = true;
    //企业微信
    public static String QyWxAccessToken;
    public static long QyWxAccessTokenExpiresIn = 0;

    /**
     * <meta-data
     * android:name="UMENG_CHANNEL"
     * android:value="Umeng">
     * </meta-data>
     *
     * @param ctx
     * @return
     */
    // 获取渠道工具函数
    public static String getChannelName(Context ctx) {
        if (ctx == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.get("UMENG_CHANNEL") + "";
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(channelName)) {
            channelName = "Unknown";
        }
        Log.d(TAG, "getChannelName: " + channelName);
        return channelName;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        Intent intent = new Intent(this, FrontService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        SendHistory.init(this);
        SettingUtil.init(this);

        EmailKit.initialize(this);

        SharedPreferences sp = MyApplication.this.getSharedPreferences(Define.SP_CONFIG, Context.MODE_PRIVATE);
        showHelpTip = sp.getBoolean(Define.SP_CONFIG_SWITCH_HELP_TIP, true);

    }
}
