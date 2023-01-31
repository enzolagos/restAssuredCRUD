
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import jdk.jfr.Description;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TodolyApiTest{
    public static String mail;
    public static String password = "12345";
    public static Integer projectId;
    public static Integer itemId;
    public static String itemContent;

    @Test
    @Order(0)
    public void createUser(){
        System.out.println("===== POST =====");
        User usuario = new User(RandomString.getAlphaNumericString(5)+"@gmail.com",password,"Enzo Lakes");
        Response response = given()
            .baseUri("https://todo.ly/api")
            .contentType("application/json")
            .body(usuario)
        .when()
            .post("/user.json");

        JsonPath responseBody = response.jsonPath();
        mail = responseBody.get("Email");

        response
        .then()
            .assertThat()
                .statusCode(200)
                .header("Content-Type","application/json; charset=utf-8")
                .body("FullName", equalTo("Enzo Lakes"))
                .cookie("ASP.NET_SessionId")
            .log().all();
        System.out.println("=================\n");
    }

    @Test
    @Order(1)
    public void getUser() {
        System.out.println("===== GET =====");
        Response response = given()
                .baseUri("https://todo.ly/api")
                .contentType("application/json")
                .auth().preemptive().basic(mail, password)
                .when()
                .get("/user.json");

        response
        .then()
            .log().all()
                .assertThat()
                .body("Email", equalTo(mail));

        System.out.println("=================\n");
    }

    @Test
    @Order(2)
    public void getUserFail(){
        System.out.println("===== GET =====");
        Response response = given()
                .baseUri("https://todo.ly/api")
                .auth().preemptive().basic("enzoenzo@gmail.com", "wrongPassword")
                .when()
                .get("/user.json");

        response
                .then()
                .assertThat()
                .statusCode(200)
                .body("$",not(hasKey("ErrorCode")))
                .log().all();
        System.out.println("=================\n");
    }

    @Test
    @Order(3)
    public void createProject(){
        System.out.println("===== POST =====");
        JSONObject projectContent = new JSONObject();
        projectContent.put("Content","Test"+RandomString.getAlphaNumericString(3));

        Response response = given()
                .baseUri("https://todo.ly/api")
                .contentType("application/json")
                .auth().preemptive().basic(mail, password)
                .body(projectContent)
                .when()
                .post("/projects.json");

        JsonPath responseBody = response.jsonPath();
        projectId = responseBody.get("Id");

        response.then()
                .assertThat()
                .statusCode(200)
                .log().all();

    }

    @Test
    @Order(4)
    public void getProjectById(){
        System.out.println("===== GET =====");
        Response response = given()
                .baseUri("https://todo.ly/api")
                .contentType("application/json")
                .auth().preemptive().basic(mail,password)
                .when()
                .get("/projects/"+projectId.toString()+".json");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("Id",equalTo(projectId))
                .log().all();
    }

    @Test
    @Order(5)
    public void createIteminAProject(){
        System.out.println("===== POST =====");

        JSONObject bodyItemCreation = new JSONObject();
        bodyItemCreation.put("Content", "Item"+RandomString.getAlphaNumericString(2));
        bodyItemCreation.put("ProjectId", projectId.toString());

        Response response = given()
                .baseUri("https://todo.ly/api")
                .contentType("application/json")
                .auth().preemptive().basic(mail,password)
                .body(bodyItemCreation)
                .when()
                .post("/items.json");

        JsonPath responseBody = response.jsonPath();
        itemId = responseBody.get("Id");
        itemContent = responseBody.get("Content");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("ProjectId", equalTo(projectId))
                .log().all();
    }

    @Test
    @Order(6)
    public void updateItemById(){
        System.out.println("===== PUT =====");

        JSONObject bodyItemUpdate = new JSONObject();
        bodyItemUpdate.put("Priority", 3);
        bodyItemUpdate.put("Content","contentChanged");

        Response response = given()
                .baseUri("https://todo.ly/api")
                .contentType("application/json")
                .auth().preemptive().basic(mail,password)
                .body(bodyItemUpdate)
                .when()
                .put("/items/"+itemId+".json");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("Content",equalTo("contentChanged"))
                .body("Content", not(equalTo(itemContent)))
                .log().all();
    }

    @Test
    @Order(7)
    public void deleteItemByid(){
        System.out.println("===== DELETE =====");
        Response response = given()
                .baseUri("https://todo.ly/api")
                .contentType("application/json")
                .auth().preemptive().basic(mail,password)
                .when()
                .delete("items/"+itemId+".json");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("Deleted",equalTo(true))
                .log().all();


    }
}
