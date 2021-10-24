package top.goup.ninja;

import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.json.JSONObject;

public class GetInfo {
    public static JSONObject doIt(Cookie cookie){
        HttpClient client = new HttpClient();

        GetMethod getMethod = new GetMethod("https://me-api.jd.com/user_new/info/GetJDUserInfoUnion");
        getMethod.addRequestHeader("Host","me-api.jd.com");
        getMethod.addRequestHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.1 Mobile/15E148 Safari/604.1");
        getMethod.addRequestHeader("Cookie",cookie.toString());

        try{

            int code = client.executeMethod(getMethod);
            byte[] res = getMethod.getResponseBody();
            String response= EncodingUtil.getString(res,"utf-8");
            JSONObject jsonObject = new JSONObject(response);
            Log.i("response",jsonObject.getString("msg")+" "+code);


            return jsonObject;
        }catch (Exception e){

        }
        return null;
    }
}
