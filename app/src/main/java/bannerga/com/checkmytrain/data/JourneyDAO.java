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

    @Query("SELECT * FROM journey ORDER by id desc LIMIT 1")
    Journey getLast();

    @Query("SELECT * FROM journey WHERE id IN (:Ids)")
    List<Journey> loadAllByIds(int[] Ids);

    @Query("SELECT * FROM journey WHERE origin LIKE :stationCode LIMIT 1")
    Journey findByOrigin(String stationCode);

    @Insert
    void insertAll(Journey... journeys);

    @Delete
    void delete(Journey journey);
}