package bannerga.com.checkmytrain.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Journey.class}, version = 1, exportSchema = false)
public abstract class JourneyDatabase extends RoomDatabase {
    public abstract JourneyDAO wordDao();

}