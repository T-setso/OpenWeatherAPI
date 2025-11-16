package openWeather;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class StationsCrudTest extends BaseTest {
    //to store the station ID created during tests
    private static String stationId;

    //---------Test to create a new weather station---------
    @Test(priority = 1)
    public void createStationTest() {

        //generate random external ID and station name using faker
        String externalId = "ST_" + faker.number().digits(6);
        String stationName = "Station_" + faker.address().cityName();

        //generate random latitude, longitude, and altitude values
        double lat = randLat();
        double lon = randLon();
        int alt = randAlt();

        //construct the JSON body for the create station request
        String body = String.format("""
            {
              "external_id": "%s",
              "name": "%s",
              "latitude": %s,
              "longitude": %s,
              "altitude": %s
            }""",  externalId, stationName, lat, lon, alt);

        //send POST request to create the station and validate the response
        Response res = given().contentType(JSON()).body(body)
                .when().post("/stations" + appIdQuery())
                .then()
                .statusCode(201)
                //.body("ID", notNullValue())
                .body("name", equalTo(stationName))
                .extract().response();

        assertHasIdOrID(res);//custom assertion to check for ID presence
        stationId = extractStationId(res);//extract and store the station ID for later use
        //print the created station ID for debugging
        System.out.println("Created station ID: " + stationId);
        //final check to ensure stationId is not null
        Assert.assertNotNull(stationId, "stationId should be present in POST response (id/ID)");
    }

    //---------Test to retrieve the created weather station---------
   @Test(priority = 2, dependsOnMethods = "createStationTest")
    public void getStation() {
        //send GET request to retrieve the station by ID and validate the response
        given().when().get("/stations/" + stationId + appIdQuery())
                .then()
                .statusCode(200)
                .body("id", equalTo(stationId))
                .body("$", hasKey("name"));
    }}
