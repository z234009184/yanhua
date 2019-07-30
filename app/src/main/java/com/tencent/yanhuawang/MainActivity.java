package com.tencent.yanhuawang;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import com.just.agentweb.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.just.agentweb.AgentWeb;
import com.king.zxing.Intents;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity {

    private AgentWeb webView;

    public WebChromeClient mWebChromeClient = new WebChromeClient() {

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置状态栏颜色
        StatusBarUtil.setColor(MainActivity.this, 0xFFE32323, 0);

        // 构建WebView
        LinearLayout linearLayout = findViewById(R.id.lin_web);
        webView = AgentWeb.with(this)
                .setAgentWebParent(linearLayout, new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setWebChromeClient(mWebChromeClient)
                .createAgentWeb()
                .ready()
//                .go("https://v.qq.com/x/page/q0842cihiac_0.html");
                .go("http://119.3.219.52:8080/firework/static/index.html#/");


        //"http://119.3.219.52:8080/firework/static/index.html#/"
//        webView.getWebCreator().getWebView().loadUrl("file:///android_asset/demo.html");
        // 注册Java接口 等待javaScript调用
        webView.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(webView, this));

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data!=null){
            String result = data.getStringExtra(Intents.Scan.RESULT);
            Toast.makeText(this,result, Toast.LENGTH_SHORT).show();
            webView.getJsAccessEntrace().quickCallJs("scanResult", result);
        }

    }



    /**
     * javaScript调用Java接口类
     * */
    private class AndroidInterface {

        private AgentWeb agent;
        private Context context;

        public AndroidInterface(AgentWeb agent, Context context) {
            this.agent = agent;
            this.context = context;
        }

        @JavascriptInterface
        public void scanQRCode() {
            // 检查权限 && 进行扫码
            checkCameraPermissions();
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        webView.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        webView.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        webView.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra("key_title",title);
        intent.putExtra("key_continuous_scan",false);
        ActivityCompat.startActivityForResult(this,intent,0X01,optionsCompat.toBundle());
    }


    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(0X01)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(EasyCaptureActivity.class, "扫一扫");
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera),
                    0X01, perms);
        }
    }


}
