package bannerga.com.checkmytrain

import bannerga.com.checkmytrain.json.Timetable
import junit.framework.Assert.assertEquals
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import java.io.*

class TimetableTest {

    private var dummyJson: JSONArray? = null
    private val destination: String = "Glasgow Central"

    @Test
    @Throws(Exception::class)
    fun canGetHuxleyResponseTest() {
        val timetable = Timetable()
        val response: JSONArray = timetable.getAllJourneys("MUI")
        Assert.assertNotNull(response)
    }

    @Test
    @Throws(Exception::class)
    fun canGetMapForOnTimeTrain() {
        dummyJson = getDummyJsonArray("on-time.json")
        val nextTrain = Timetable().getNextJourney(dummyJson, destination)

        val time = nextTrain["time"]?.toString()
        val delayedStatus = nextTrain["delayed"]?.toString()
        val cancelledStatus = nextTrain["cancelled"]?.toString()

        assertEquals("15:11", time)
        assertEquals("On time", delayedStatus)
        assertEquals("false", cancelledStatus)
    }

    @Test
    @Throws(Exception::class)
    fun canGetMapForDelayedTrain() {
        dummyJson = getDummyJsonArray("delayed.json")
        val nextTrain = Timetable().getNextJourney(dummyJson, destination)

        val time = nextTrain["time"]?.toString()
        val delayedStatus = nextTrain["delayed"]?.toString()
        val cancelledStatus = nextTrain["cancelled"]?.toString()

        assertEquals("08:20", time)
        assertEquals("08:35", delayedStatus)
        assertEquals("false", cancelledStatus)
    }

    @Throws(IOException::class, JSONException::class)
    private fun getDummyJsonArray(filename: String): JSONArray {
        val responseString = StringBuilder()
        val jsonFile = File("src/test/res/$filename")
        val stream = FileInputStream(jsonFile)
        val bufferedReader = BufferedReader(InputStreamReader(stream))
        var line: String? = bufferedReader.readLine()
        while (line != null) {
            responseString.append(line)
            line = bufferedReader.readLine()
        }
        val jsonOb = JSONObject(responseString.toString())
        return jsonOb.get("trainServices") as JSONArray
    }

}
