package top.goup.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import top.goup.update.BaseDialog;

public class Web extends AppCompatActivity {
    WebView webView;
    final String dialog_title = "注意";
    final String dialog_content = "请使用验证码登录，登录成功返回软件主界面即可。";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        webView = findViewById(R.id.webView);

        WebClient webClient = new WebClient();
        webView.setWebViewClient(webClient);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(false);

        /**清除Cookie**/
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        CookieManager.getInstance().removeAllCookie();
        new BaseDialog(this,dialog_title,dialog_content).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        webView.loadUrl("https://plogin.m.jd.com/login/login");
        Intent i=new Intent();
        i.putExtra("key",new String("keys"));
        setResult(1,i);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode== KeyEvent.KEYCODE_BACK)&&webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    class WebClient extends WebViewClient {

        public String cookie;
        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookie= cookieManager.getCookie(url);
            Intent i = new Intent();
            i.putExtra("cookie",cookie);
            setResult(RESULT_OK,i);
            super.onPageFinished(view, url);
        }


    }

}
