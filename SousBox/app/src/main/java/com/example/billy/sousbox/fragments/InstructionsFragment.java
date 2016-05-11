package com.example.billy.sousbox.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.billy.sousbox.R;

/**
 * Created by Billy on 5/6/16.
 */
public class InstructionsFragment extends Fragment {

    private WebView instructionsWebView;
    private ProgressBar progress;
    private String htmlSaveForLater;
    private String instructionURL;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.instruction_fragment_layout, container, false);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        instructionsWebView = (WebView) v.findViewById(R.id.instruction_web_view);
        progress = (ProgressBar) v.findViewById(R.id.progress_bar);
        Bundle instructionBundle = getArguments();
        instructionURL = instructionBundle.getString(IngredientsFragment.URL_KEY);
        WebSettings webSettings = instructionsWebView.getSettings();
        instructionsWebView.setWebViewClient(new WebViewClientDemo()); //opens url in app, not in default browser
        webSettings.setJavaScriptEnabled(true); //turn js on for hacking and giving better ux
        instructionsWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        instructionsWebView.loadUrl(instructionURL);


        return v;

    }

    private class WebViewClientDemo extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            progress.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            instructionsWebView.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            progress.setVisibility(View.GONE);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);
        }
    }

    public class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(String html) {
            htmlSaveForLater = html;
        }
    }



}
