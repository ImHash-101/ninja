package top.goup.ninja;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = Cookie.class,version = 1,exportSchema = false)
abstract public class CookieDatabase extends RoomDatabase {
    abstract public CookieDao getDao();
}
