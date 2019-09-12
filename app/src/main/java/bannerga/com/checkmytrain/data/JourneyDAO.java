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

    @Query("SELECT * FROM journey " +
            "WHERE origin LIKE :origin " +
            "AND destination LIKE :destination " +
            "AND hour LIKE :hour " +
            "AND minute LIKE :minute")
    List<Journey> getJourney(String origin, String destination, int hour, int minute);

    @Query("SELECT * FROM station WHERE name LIKE '%' || :name || '%'")
    List<Station> findByPartialName(String name);

    @Insert
    void insertAll(Journey... journeys);

    @Delete
    void delete(Journey journey);
}