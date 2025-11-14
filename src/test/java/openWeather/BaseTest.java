package openWeather;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class BaseTest {
    //config properties object to be used in tests
    protected static final Properties CONFIG = new Properties();

    //base URL and app ID for OpenWeather API
    protected static String BASE_URL;
    protected static String APP_ID;

    //faker object to generate random test data
    protected static Faker faker = new Faker();

    @BeforeSuite
    public void globalSetUp() throws IOException{
        //load config properties from file
        try(FileInputStream fis = new FileInputStream("src/test/java/resources/config.properties")){
            CONFIG.load(fis);//load properties from file
        }
        //override properties with system properties if provided
        BASE_URL = System.getProperty("baseUrl", CONFIG.getProperty("baseUrl"));
        APP_ID   = System.getProperty("appid",   CONFIG.getProperty("appid"));

        //initialize faker with South African locale
        faker = new Faker(Locale.forLanguageTag("en-ZA"));

        //set up RestAssured with base URL and logging to help debug test failures
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    //helper methods to construct common query parameters
    protected String appIdQuery() { return "?appid=" + APP_ID; }
    protected ContentType JSON() { return ContentType.JSON; }//to specify JSON content type

    // ---- Helpers: numeric-safe generators ----
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

    //Helper to read "id" or "ID"
    protected String readIdCaseInsensitive(Properties props) {
        String id = props.getProperty("id");
        if (id == null) {
            id = props.getProperty("ID");
        }
        return id;
    }


}
