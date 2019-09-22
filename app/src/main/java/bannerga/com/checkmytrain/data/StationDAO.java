package bannerga.com.checkmytrain.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StationDAO {

    @Query("SELECT * FROM station")
    List<Station> getAll();

    @Query("SELECT * FROM station WHERE name LIKE :name LIMIT 1")
    Station findByName(String name);

    @Query("SELECT * FROM station WHERE name LIKE '%' || :name || '%'")
    List<Station> findByPartialName(String name);

    @Query("DELETE FROM station")
    void wipeTable();

    @Insert
    void insertAll(Station... stations);

}