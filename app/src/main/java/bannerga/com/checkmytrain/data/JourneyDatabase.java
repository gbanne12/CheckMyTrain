package bannerga.com.checkmytrain.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Journey.class}, version = 2, exportSchema = false)
public abstract class JourneyDatabase extends RoomDatabase {
    public abstract JourneyDAO dao();

}