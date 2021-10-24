package top.goup.ninja;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.room.Room;

import org.json.JSONObject;

public class Detail extends AppCompatActivity {
    TextView allBean,nickNameW,pt_pin;
    CookieDatabase database;
    CookieDao dao;
    Button updateInfo,rmUser;
    Cookie cookie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        updateInfo = findViewById(R.id.updateInfo);
        rmUser = findViewById(R.id.rmUser);

        rmUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dao.rmCookie(cookie);
                    }
                }).start();
                finish();
            }
        });

        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
            }
        });

        Intent intent = getIntent();
        String nickName = intent.getStringExtra("nickName");
        if(nickName==null)finish();

        database = Room.databaseBuilder(getApplicationContext(),CookieDatabase.class,"Cookie").build();
        dao = database.getDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cookie = dao.searchWithNick(nickName);
                Message msg = new Message();
                msg.what = Constant.SEARCH_OK;
                uiHandler.sendMessage(msg);
            }
        }).start();

        allBean = findViewById(R.id.allBean);
        nickNameW = findViewById(R.id.nickName);
        pt_pin = findViewById(R.id.pt_pin);


    }
    private final Handler uiHandler;

    {
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.JSON_OK:
                        JSONObject json = (JSONObject) msg.obj;
                        try {
                            nickNameW.setText(cookie.getNickName());
                            pt_pin.setText(cookie.getPt_pin());
                            allBean.setText(json.getJSONObject("data").getJSONObject("assetInfo").getString("beanNum"));

                        }catch (Exception e){
                            allBean.setText(e.getMessage());
                        }
                        break;
                    case Constant.SEARCH_OK:
                        getInfo();
                        break;
                }
            }
        };
    }



    void getInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = GetInfo.doIt(cookie);
                if(json!=null){
                    try {
                        Log.i("response",json.getJSONObject("data").getJSONObject("assetInfo").getString("beanNum")+" ");

                        Message msg = new Message();
                        msg.obj = json;
                        msg.what = Constant.JSON_OK;
                        uiHandler.sendMessage(msg);

                    }catch (Exception e){
                        allBean.setText(e.getMessage());
                    }
                }
            }
        }).start();
    }

}