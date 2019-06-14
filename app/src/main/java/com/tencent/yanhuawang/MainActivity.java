package com.tencent.yanhuawang;


import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.jaeger.library.StatusBarUtil;
import com.just.agentweb.AgentWeb;

public class MainActivity extends AppCompatActivity {

    private AgentWeb webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置状态栏颜色
        StatusBarUtil.setColor(MainActivity.this, 0xFFC32A19, 0);

        // 构建WebView
        LinearLayout linearLayout = findViewById(R.id.lin_web);
        webView = AgentWeb.with(this)
                .setAgentWebParent(linearLayout, new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .createAgentWeb()
                .ready()
                .go("http://119.3.219.52:8080/firework/static/index.html#/");

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


}
