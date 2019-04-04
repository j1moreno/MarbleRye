package com.j1.marblerye;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About");
        setContentView(R.layout.activity_about);
        WebView webView = findViewById(R.id.aboutActivity_webView_license);
        webView.loadDataWithBaseURL(null, getString(R.string.apache_license), "html", Xml.Encoding.US_ASCII.name(), null);
    }
}
