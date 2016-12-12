package com.deyi.news;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class SpalasActivity extends Activity {

    private TextView tvname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalas);
        getVersionName();

         tvname = (TextView) findViewById(R.id.tv_name);
         tvname.setText("版本名："+getVersionName());
    }
    /**
     * 获取版本名称哈哈
     */
    private String getVersionName(){
        PackageManager pm = getPackageManager();//包管理器
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);//根据包名获取相关信息
            String versionName = packageInfo.versionName;//版本名称
            //int versionCode = packageInfo.versionCode;//版本号
           // System.out.println("VersionName:"+versionName+"\n"+"Versioncode:"+versionCode);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
