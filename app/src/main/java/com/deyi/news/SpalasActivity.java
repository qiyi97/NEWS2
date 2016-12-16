package com.deyi.news;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import deyi.news.utils.StreamUtil;


public class SpalasActivity extends Activity {

    private TextView tv_Splash_versionname;
    private String apkurl;
    public int VersonCode;
    private String des;
    private String VersionName;
    public static final int MSG_UPDATE_DIALOG = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_DIALOG:
                    //弹出对话框
                    showdialog();
                    break;
            }
        }

        /**
         * 弹出对话框
         *
         */
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalas);
        getVersionName();

        tv_Splash_versionname = (TextView) findViewById(R.id.tv_Splash_versionname);
        tv_Splash_versionname.setText("版本名：" + getVersionName());
        update();
    }

    //弹出对话
    private void showdialog() {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setCancelable(false)
                .setMessage(des)
                .setTitle("新版本" + VersionName)
                .setIcon(R.drawable.update)
                .setCancelable(true)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //下载最新版本
                        download();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        enterHome();
                    }
                })
                //显示对话框
                .show();
    }

    /**
     * 3.下载最新版本wasdwasdwasdwasdwasdwasdwasdwasdwasdwasdwad
     *
     */

    private void download(View v) {

    }

        //跳转主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //移除splash界面
        finish();
    }

    /**
     * 提醒用户更新版本
     */
    private void update() {
        //链接服务器，查看是否有最新操作，联网操作，好耗时操作，4.0以后不予许在主线程中执行，要放到子线程
        new Thread() {
            public int startTime;

            @Override
            public void run() {
                Message message = new Message();
                //在连接之前获取一个时间
                startTime = (int) System.currentTimeMillis();
                try {

                    URL url = new URL("http://192.168.1.139/update.json");
                    //1.1.2获取链接操作
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();//Http协议，httpClient
                    //1.1.3设置超时时间
                    conn.setConnectTimeout(8000);//设置链接超时时间
                    //  conn.setReadTimeout(8000);//设置读取超时时间
                    //1.1.4请求方式“GTE”
                    conn.setRequestMethod("GET");//post
                    //1.1.5获取服务器返回的状态码
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        /**链接成功,获取服务器返回的数据，
                         * code,新版本的版本号
                         * aokurl：新版本路径
                         * des：描述信息，告诉用户更新了那些功能
                         * 获取数据之前，服务器是如何封装数据json
                         */
                        Log.v("TAG", "链接成功！");
                        //获取服务器返回流的信息
                        InputStream inputStream = conn.getInputStream();
                        //将获取到的信息流转化成字符串
                        String json = StreamUtil.parserStreamUtil(inputStream);
                        //解析json数据
                        JSONObject jsonObject = new JSONObject(json);
                        //解析完成获取数据
                        VersionName = jsonObject.getString("versonName");
                        VersonCode = jsonObject.getInt("versonCode");
                        des = jsonObject.getString("des");
                        apkurl = jsonObject.getString("apkurl");
                        Log.v("TAG", "versonName:" + VersionName + "\n" + "versonCode:" + VersonCode + "\n" + "des:" + des + "\n" + "apkurl:" + apkurl);
                        //判断服务器返回的新版本版本号和当前应用程序的版本是否一致，一致表示没有最新版本，不一致就表示有最新版本
                        if (VersionName.equals(getVersionName())) {
                            //没有最新版本
                            Log.v("TAG", "当前已是最新版本，无需更新");

                        } else {
                            //有最新版本
                            //2.弹出对话框，提醒用户更新版本
                            message.what = MSG_UPDATE_DIALOG;
                        }
                    } else {
                        //链接失败
                        Log.v("TAG", "链接失败！");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {//不管有没有异常都会执行
                    //在连接成功之后再去获取一个时间
                    //处理连接外网连接时间问题
                    int endTime = (int) System.currentTimeMillis();
                    int dTime = endTime - startTime;
                    if (dTime < 2000) {
                        //比较两个的时间差如果小于两秒就睡两秒，大于两秒不睡
                        //睡两秒钟
                        SystemClock.sleep(2000 - dTime);//始终都是睡两秒钟的时间
                    }
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * 获取版本名称
     */
    private String getVersionName() {
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
