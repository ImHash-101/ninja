package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    Map<String,String> cookie=new HashMap<>();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button enter = findViewById(R.id.enter);
        WebView webView = findViewById(R.id.webView);
        TextView textView = findViewById(R.id.textView);
        Button getCookie = findViewById(R.id.getCookie);
        Button login = findViewById(R.id.login);
        context =getApplicationContext();

        WebSettings settings =webView.getSettings();
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pt_key = cookie.get("pt_key");
                String pt_pin = cookie.get("pt_pin");

                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("pt_key", pt_key);
                    jsonObject.put("pt_pin", pt_pin);

//                    webView.postUrl("http://192.168.3.83:8081/api/cklogin",EncodingUtils.getBytes(jsonObject.toString(),"utf-8"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpClient = new HttpClient();
                            PostMethod postMethod = new PostMethod("http://192.168.3.83:8081/api/cklogin");
                            postMethod.addRequestHeader("Content-Type", "application/json");
                            int code = 0;
                            try {
                                StringRequestEntity requestEntity = new StringRequestEntity(jsonObject.toString(),"application/json","utf-8");
                                postMethod.setRequestEntity(requestEntity);
                                code = httpClient.executeMethod(postMethod);
                                if(code==200){
                                    byte[] res = postMethod.getResponseBody();
                                    String string=EncodingUtil.getString(res,"utf-8");
                                    Log.i("info",string);

                                    JSONObject response =new JSONObject(string);

                                    String message;
                                    message = response.getString("message");
                                    Looper.prepare();
                                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                                    Looper.loop();


                                }

                            } catch (IOException |JSONException  e) {
                                e.printStackTrace();
                                Log.e("error","error");
                            }
                        }
                    }).start();

                } catch (JSONException e){
                    e.printStackTrace();
                }


            }
        });



        getCookie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(webViewClient.cookie==null){
                   Toast.makeText(context,"页面未加载完成，请稍后重试",Toast.LENGTH_SHORT).show();
                   return;
                }
                int i=0;
                for (String ss : webViewClient.cookie.split(";")) {
                    if(i>=2) {
                        Toast.makeText(context,"获取成功",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(ss.contains("pt_key")||ss.contains("pt_pin")){
                        String[] s = ss.split("=");
                        cookie.put(s[0].trim(),s[1].trim());
                        i++;
                    }

                }
            }
        });


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = textView.getText().toString();
                Toast.makeText(context,url,Toast.LENGTH_SHORT).show();

                webView.loadUrl(url);

            }
        });
        webView.loadUrl("https://plogin.m.jd.com/login/login");

    }


}
class MyWebViewClient extends WebViewClient {

    public String cookie;
    public void onPageFinished(WebView view, String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String CookieStr = cookieManager.getCookie(url);
        cookie=CookieStr;
        super.onPageFinished(view, url);
    }
}