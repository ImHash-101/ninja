package top.goup.ninja;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CookieDao {
    @Query("SELECT * FROM Cookie")
    public List<Cookie> getAll();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCookie(Cookie... cookie);
    @Query("SELECT * FROM Cookie WHERE nickName LIKE :nickName")
    public Cookie getCookie(String nickName);
}
