package bannerga.com.checkmytrain.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Journey.class, Station.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JourneyDAO dao();
    public abstract StationDAO stationDao();
}