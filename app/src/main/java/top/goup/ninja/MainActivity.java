package top.goup.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String cookie = "pt_key=AAJhalP4ADCNtzj1e2ujz_JHEx5JN9jmxz4l6PNlxxlRj2h4GNRYnzQVHablw20JyCD2K0aqH_o;pt_pin=jd_6031b8c0872cb;";
    ArrayAdapter<String> adapter;
    CookieDao dao;
    CookieDatabase database;
    ListView list_test;
    List<String> strS;
    List<View> checked = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(this,CookieDatabase.class,"this").build();
        dao = database.getDao();

        strS = new ArrayList<>();
        //创建ArrayAdapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,strS);


        for (int i = 0; i < 5; i++) {
            strS.add(i+"_");
        }
        add();

        list_test = (ListView) findViewById(R.id.listView);

        list_test.setAdapter(adapter);



//        list_test.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView textView = (CheckedTextView) view;
                if(textView.isChecked()){
                    checked.remove(view);
                    ((CheckedTextView) view).setChecked(false);
                }else {
                    checked.add(view);
                    ((CheckedTextView) view).setChecked(true);
                }
            }
        });


    }

    void add(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Cookie c : dao.getAll()) {
                    strS.add(c.getNickName());
                }
                adapter.notifyDataSetChanged();
            }
        }).start();

    }


    /***
    public boolean onCreateOptionsMenu(Menu menu)
    return true 显示 false 不显示
     ***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUser:
                Intent intent = new Intent(this,Web.class);
                startActivityForResult(intent,0);
                break;
            case R.id.check:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean ok = IsLogin.check(cookie);
                        String r ="已失效";
                        if(ok){
                            r = "有效";
                        }
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),r,Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
                break;
            case R.id.test:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cookie c = dao.getCookie("jd_KNXptIPgfuGY");
                        Log.i("test",c.getNickName()+" ");
                        Log.i("test",c.getPt_key()+" ");
                    }
                }).start();

                break;
            case R.id.rmSelectedUser:
                int count = checked.size();

                while (checked.size()>0){
                    Log.i("count",count+" ");
                    CheckedTextView view = (CheckedTextView) checked.get(0);
                    strS.remove(view.getText().toString());
                    adapter.notifyDataSetChanged();
                    checked.remove(view);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&data!=null){
            String c = data.getStringExtra("cookie");
            if(c==null)return;
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
                        dao.insertCookie(cookie);
                        
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