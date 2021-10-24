package top.goup.ninja;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cookie",indices = @Index(value = {"pt_pin"},unique = true))
public class Cookie {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String pt_pin;
    private String pt_key;
    private String nickName;

    public Cookie(){}
    @Ignore
    public Cookie(String pt_pin,String pt_key){
        this.pt_pin = pt_pin;
        this.pt_key = pt_key;
        nickName = pt_pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPt_pin() {
        return pt_pin;
    }

    public void setPt_pin(String pt_pin) {
        this.pt_pin = pt_pin;
    }

    public String getPt_key() {
        return pt_key;
    }

    public void setPt_key(String pt_key) {
        this.pt_key = pt_key;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "pt_key="+pt_key+";pt_pin="+pt_pin+";";
    }
}
