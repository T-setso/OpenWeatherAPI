package openWeather;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;

import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasKey;

public class BaseTest {
    //config properties object to be used in tests
    protected static final Properties CONFIG = new Properties();

    //base URL and app ID for OpenWeather API
    protected static String BASE_URL;
    protected static String APP_ID;

    //faker object to generate random test data
    protected static Faker faker = new Faker();

    @BeforeSuite //runs once before all tests in the suite
    public void globalSetUp() throws IOException{

        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is == null) throw new IOException("config.properties not found on classpath (src/test/java/resources)");
            CONFIG.load(is);
        }
        //override properties with system properties if provided
        BASE_URL = System.getProperty("baseUrl", CONFIG.getProperty("baseUrl"));
        APP_ID   = System.getProperty("appid",   CONFIG.getProperty("appid"));

        //initialize faker with South African locale
        faker = new Faker(new Locale("en-ZA"));

        //set up RestAssured with base URL and logging to help debug test failures
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    //helper methods to construct common query parameters
    protected String appIdQuery() { return "?appid=" + APP_ID; }
    protected ContentType JSON() { return ContentType.JSON; }//to specify JSON content type

    // ---- Helpers: numeric-safe generators (avoid comma decimals) ----
    protected double randLat() {
        // randomDouble(maxDecimals, minInt, maxInt)
        return faker.number().randomDouble(6, -90, 90);
    }
    protected double randLon() {
        return faker.number().randomDouble(6, -180, 180);
    }
    protected int randAlt() {
        return faker.number().numberBetween(5, 5000);
    }

    // --- Robust extractor: accepts "id" or "ID" from API ---
    protected String extractStationId(Response r) {
        Map<String, Object> json = r.jsonPath().getMap("$");
        Object v = json.get("id");
        if (v == null) v = json.get("ID");  // POST/PUT sometimes returns "ID"
        return v == null ? null : v.toString();
    }

    // --- Assertion helper to accept either casing in response ---
    protected void assertHasIdOrID(Response r) {
        r.then().body("$", anyOf(hasKey("id"), hasKey("ID")));
    }


}
