package bannerga.com.checkmytrain.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface JourneyDAO {

    @Query("SELECT * FROM journey")
    List<Journey> getAll();

    @Insert
    void insertAll(Journey... journeys);

    @Delete
    void delete(Journey journey);
}