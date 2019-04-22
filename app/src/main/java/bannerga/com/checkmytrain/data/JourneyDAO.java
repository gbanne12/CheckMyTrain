package bannerga.com.checkmytrain.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface JourneyDAO {

    @Query("SELECT * FROM journey")
    List<Journey> getAll();

    @Query("SELECT * FROM journey WHERE id IN (:Ids)")
    List<Journey> loadAllByIds(int[] Ids);

    @Query("SELECT * FROM journey WHERE origin LIKE :stationCode LIMIT 1")
    Journey findByOrigin(String stationCode);

    @Insert
    void insertAll(Journey... journeys);

    @Delete
    void delete(Journey journey);
}