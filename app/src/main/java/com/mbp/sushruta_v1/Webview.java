package com.mbp.sushruta_v1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Webview extends AppCompatActivity {
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Bundle bundle=getIntent().getExtras();
        String url=bundle.getString("Url");
        webview=(WebView)findViewById(R.id.webview1);
        webview.loadUrl(url);
    }
}
