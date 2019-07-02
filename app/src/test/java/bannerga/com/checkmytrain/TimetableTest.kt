package bannerga.com.checkmytrain

import bannerga.com.checkmytrain.json.Timetable
import bannerga.com.checkmytrain.notification.JourneyStatus
import junit.framework.Assert.assertEquals
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import java.io.*

class TimetableTest {

    private var dummyJson: JSONArray? = null

    @Test
    @Throws(Exception::class)
    fun canGetHuxleyResponseTest() {
        val timetable = Timetable()
        val response: JSONArray = timetable.getAllJourneys("MUI")
        Assert.assertNotNull(response)
    }

    @Test
    @Throws(Exception::class)
    fun canGetInfoForDestinationWhenOnTime() {
        dummyJson = getDummyJsonArray("on-time.json")
        val destination = "Glasgow Central"
        val journey: JourneyStatus = Timetable().getNextJourney(dummyJson, destination)

        assertEquals("15:11", journey.time)
        assertEquals("On time", journey.delayed)
        assertEquals(false, journey.cancelled)
    }

    @Test
    @Throws(Exception::class)
    fun canGetInfoForSubsequentWhenOnTime() {
        dummyJson = getDummyJsonArray("all-endpoint.json")
        val callingPoint = "Williamwood"
        val journey: JourneyStatus = Timetable().getNextJourney(dummyJson, callingPoint)

        assertEquals("13:41", journey.time)
        assertEquals("On time", journey.delayed)
        assertEquals(false, journey.cancelled)
    }

    @Test
    @Throws(Exception::class)
    fun canGetInfoForDelayedDestination() {
        dummyJson = getDummyJsonArray("delayed.json")
        val destination = "Glasgow Central"
        val journey: JourneyStatus = Timetable().getNextJourney(dummyJson, destination)

        assertEquals("08:20", journey.time)
        assertEquals("08:35", journey.delayed)
        assertEquals(false, journey.cancelled)
    }


    @Throws(IOException::class, JSONException::class)
    private fun getDummyJsonArray(filename: String): JSONArray {
        val responseString = StringBuilder()
        val jsonFile = File("src/test/res/json/$filename")
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
