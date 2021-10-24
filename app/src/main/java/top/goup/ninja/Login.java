package top.goup.ninja;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.json.JSONObject;

public class Login {
    public static JSONObject doIt(Cookie cookie){
        String pt_key = cookie.getPt_key();
        String pt_pin = cookie.getPt_pin();

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("pt_key", pt_key);
            jsonObject.put("pt_pin", pt_pin);


            HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod("http://ninja.goup.top:6002/api/cklogin");
            postMethod.addRequestHeader("Content-Type", "application/json");
            int code = 0;
            StringRequestEntity requestEntity = new StringRequestEntity(jsonObject.toString(),"application/json","utf-8");
            postMethod.setRequestEntity(requestEntity);
            code = httpClient.executeMethod(postMethod);
            if(code==200){
                byte[] res = postMethod.getResponseBody();
                String string= EncodingUtil.getString(res,"utf-8");

                JSONObject response =new JSONObject(string);
                return response;
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
}