package bannerga.com.checkmytrain.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

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