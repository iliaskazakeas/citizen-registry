package gr.registry.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CitizenApiIT {

    private static HttpServer server;

    @BeforeAll
    static void startServer() {
        // Σηκώνουμε server ΜΟΝΟ για τα tests (μην τρέχει και άλλος server στην 8080)
        server = CitizenServer.startServer();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
    }

    @AfterAll
    static void stopServer() {
        if (server != null) server.shutdownNow();
    }

    // Για να μην “σπάνε” τεστ από παλιές εγγραφές στη DB
    @AfterEach
    void cleanup() {
        given().when().delete("/citizens/IT123456").then().statusCode(anyOf(is(200), is(404)));
        given().when().delete("/citizens/IT654321").then().statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(1)
    void testCreateCitizen_POST_201() {
        String payload = """
        {
          "idNumber": "IT123456",
          "firstName": "Nikos",
          "lastName": "Papadopoulos",
          "gender": "MALE",
          "birthDate": "1995-11-12",
          "afm": "123456789",
          "address": "Athens"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/citizens")
        .then()
            .statusCode(201)
            .body("idNumber", equalTo("IT123456"))
            .body("firstName", equalTo("Nikos"));
    }

    @Test
    @Order(2)
    void testGetCitizen_GET_200() {
        // ensure exists
        testCreateCitizen_POST_201();

        given()
        .when()
            .get("/citizens/IT123456")
        .then()
            .statusCode(200)
            .body("lastName", equalTo("Papadopoulos"))
            .body("gender", equalTo("MALE"));
    }

    @Test
    @Order(3)
    void testUpdateCitizen_PUT_200() {
        // ensure exists
        testCreateCitizen_POST_201();

        String updates = """
        {
          "afm": "987654321",
          "address": "Thessaloniki"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(updates)
        .when()
            .put("/citizens/IT123456")
        .then()
            .statusCode(200)
            .body("afm", equalTo("987654321"))
            .body("address", equalTo("Thessaloniki"));
    }

    @Test
    @Order(4)
    void testDeleteCitizen_DELETE_200() {
        // ensure exists
        testCreateCitizen_POST_201();

        given()
        .when()
            .delete("/citizens/IT123456")
        .then()
            .statusCode(200)
            .body("message", notNullValue());
    }

    @Test
    @Order(5)
    void testSearchCitizens_GET_200() {
        // βάλε 2 πολίτες ώστε να υπάρχει αποτέλεσμα
        String p1 = """
        {"idNumber":"IT123456","firstName":"Nikos","lastName":"Papadopoulos","gender":"MALE","birthDate":"1995-11-12"}
        """;
        String p2 = """
        {"idNumber":"IT654321","firstName":"Nikos","lastName":"Test","gender":"MALE","birthDate":"1994-10-10"}
        """;

        given().contentType(ContentType.JSON).body(p1).when().post("/citizens")
                .then().statusCode(anyOf(is(201), is(409)));
        given().contentType(ContentType.JSON).body(p2).when().post("/citizens")
                .then().statusCode(anyOf(is(201), is(409)));

        given()
        .when()
            .get("/citizens/search?firstName=Nikos")
        .then()
            .statusCode(200)
            .body("$", not(empty()));
    }
}
