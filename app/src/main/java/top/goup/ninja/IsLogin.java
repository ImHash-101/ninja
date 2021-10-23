package top.goup.ninja;


import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.json.JSONObject;

import java.io.IOException;

public class IsLogin {


    public static boolean check(String cookie){
        HttpClient httpClient = new HttpClient();
        GetMethod  getMethod = new GetMethod("https://plogin.m.jd.com/cgi-bin/ml/islogin");
        getMethod.addRequestHeader("User-Agent", "jdapp;iPhone;10.1.2;15.0;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1");
        getMethod.addRequestHeader("Cookie",cookie);
        try {
            int code = httpClient.executeMethod(getMethod);
            byte[] res = getMethod.getResponseBody();
            String response= EncodingUtil.getString(res,"utf-8");
            JSONObject jsonObject = new JSONObject(response);
            return  jsonObject.getString("islogin").equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
