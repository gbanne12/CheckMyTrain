package bannerga.com.checkmytrain.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Journey.class, Station.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JourneyDAO wordDao();

    public abstract StationDAO stationDao();
}