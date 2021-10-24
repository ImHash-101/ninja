package top.goup.ninja;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CookieDatabase database;
    CookieDao dao;
    List<Cookie> cookies;
    List<String> strS;
    ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = Room.databaseBuilder(getApplicationContext(), CookieDatabase.class, "Cookie").build();
        dao = database.getDao();

        strS = new ArrayList<>();



        new Thread(new Runnable() {
            @Override
            public void run() {
                Cookie cookie = new Cookie("jd_KNXptIPgfuGY","AAJhaZ5zADBcyxiSaIdayK-GZfPz-fgHEAoTKzRl6wkVTJjI4y_aThS2P0PBOuPrcFkPXOQ11ag");
                dao.insert(cookie);
            }
        }).start();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strS);
        listView = findViewById(R.id.lisView);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),Detail.class);
                intent.putExtra("nickName",strS.get(i));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.addUser:
                Intent intent = new Intent(getApplicationContext(),Web.class);
                startActivityForResult(intent,1);
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }).start();
    }

    //需在子线程运行
    private void loadData() {
        strS.clear();
        cookies = dao.getAll();
        for (int i = 0; i < cookies.size(); i++) {
            strS.add(cookies.get(i).getNickName());
        }
        updateAdapter();

    }
    private void updateAdapter(){
        Message msg = new Message();
        msg.what = Constant.UPDATE_ADAPTER;
        uiHandler.sendMessage(msg);
    }

    //需在子线程运行
    private void addCookie(Cookie cookie) {
        dao.insert(cookie);
    }
    private final Handler uiHandler;

    {
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.UPDATE_ADAPTER:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&data!=null){
            String c = data.getStringExtra("cookie");
            if(c==null){
                Toast.makeText(this,"无操作",Toast.LENGTH_SHORT).show();
                return;
            }
            Cookie cookie = delCookie(c);
            if(cookie!=null){
                Toast.makeText(this,"成功添加"+cookie.getPt_pin(),Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = Login.doIt(cookie);
                        Looper.prepare();
                        try {
                            String nickName = json.getJSONObject("data").getString("nickName");
                            cookie.setNickName(nickName);
                            Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
                        }catch (Exception e){

                        }
                        dao.insert(cookie);
                        loadData();
                        Looper.loop();
                    }
                }).start();
            }else {
                Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();

            }
        }
    }

    public Cookie delCookie(String cookie){
        if(cookie.contains("pt_key")||cookie.contains("pt_pin")){
            int i = 0;
            Cookie r = new Cookie();
            for (String ss : cookie.split(";")) {
                if(r.getPt_pin()!=null&&r.getPt_key()!=null)return r;
                if(ss.contains("pt_key")){
                    r.setPt_key(ss.split("=")[1].trim());
                }else if(ss.contains("pt_pin")){
                    r.setPt_pin(ss.split("=")[1].trim());
                }
            }
        }
        return null;
    }
}