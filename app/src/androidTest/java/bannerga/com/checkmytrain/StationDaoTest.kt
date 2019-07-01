package bannerga.com.checkmytrain

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import bannerga.com.checkmytrain.data.AppDatabase
import bannerga.com.checkmytrain.data.Station
import bannerga.com.checkmytrain.data.StationDAO
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

class StationDaoTest {

    private var staionDao: StationDAO? = null

    @Before
    @Throws(IOException::class, JSONException::class)
    fun setup() {
        val db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java).build()
        staionDao = db.stationDao()

        val stationArray: JSONArray = stationJson
        for (i in 0 until stationArray.length()) {
            val stationDetails = stationArray.get(i) as JSONObject
            val stationName = stationDetails.get("stationName").toString()
            val stationCode = stationDetails.get("crsCode").toString()
            val station = Station()
            station.name = stationName
            station.crs = stationCode
            staionDao?.insertAll(station)
        }
    }

    @Test
    fun canFindStationByName() {
        val station = staionDao!!.findByName("Muirend")
        assertEquals("MUI", station.crs)
    }

    @Test
    fun canFindStationByPartialName() {
        val stationList = staionDao!!.findByPartialName("Muir")
        assertEquals(4, stationList.size)
        for (station in stationList) {
            assertTrue(station.crs == "MUI" ||
                    station.crs == "DMR" ||
                    station.crs == "MOO" ||
                    station.crs == "SDM")
        }
    }

    private val stationJson: JSONArray
        @Throws(IOException::class, JSONException::class)
        get() {
            val responseString = StringBuilder()
            val huxleyAddress = "http://huxley.apphb.com/crs"
            val url = URL(huxleyAddress)
            val connection: URLConnection = url.openConnection()

            val br = BufferedReader(InputStreamReader(connection.getInputStream()))
            var line: String? = br.readLine()
            while (line != null) {
                responseString.append(line)
                line = br.readLine()
            }
            return JSONArray(responseString.toString())
        }

}
