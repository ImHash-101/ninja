package top.goup.ninja;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CookieDao {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insert(Cookie...cookies);
    @Query("SELECT * FROM Cookie WHERE nickName LIKE :nickName")
    Cookie searchWithNick(String nickName);
    @Query("SELECT * FROM Cookie")
    List<Cookie> getAll();
    @Update
    void update(Cookie...cookies);
    @Delete
    void rmCookie(Cookie...cookies);

}
