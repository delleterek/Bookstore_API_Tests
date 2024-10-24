package EndpointsConfig;

import POJOs.Bookstore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;

import java.lang.reflect.Type;
import java.util.List;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;

public class BookstoresRelatedEndpoints extends BasicEndpointConfiguration {
    private static final String addGetBookstoresEndpoint = "bookstores";
    private static final String getUpdateDeleteBookstoreEndpoint = "bookstores/{bookstoreId}";

    public Response getBookstores() {
        return given().spec(requestSpecification).when().get(addGetBookstoresEndpoint);
    }

    public List<Bookstore> getListOfBookstores(){
        Response response = getBookstores();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Bookstore>>() {}.getType();
        return gson.fromJson(response.asString(), type);
    }

    public Response addBookstore(Bookstore bookstore){
        return given().spec(requestSpecification).body(bookstore).when().post(addGetBookstoresEndpoint);
    }

    public Response getBookstore(String bookstoreId){
        return given().spec(requestSpecification).pathParam("bookstoreId", bookstoreId).when().get(getUpdateDeleteBookstoreEndpoint);
    }

    public Bookstore getBookstoreAsClass(String id) {
        return getBookstore(id).as(Bookstore.class);
    }

    public Response updateBookstore(String bookstoreId, Bookstore newBookstore){
        return given()
                .spec(requestSpecification)
                .pathParam("bookstoreId", bookstoreId)
                .body(newBookstore)
                .when()
                .put(getUpdateDeleteBookstoreEndpoint);
    }

    public Response deleteBookstore(String bookstoreId){
        return given().spec(requestSpecification).pathParam("bookstoreId", bookstoreId).when().delete(getUpdateDeleteBookstoreEndpoint);
    }

    public Response deleteAllBookstores(){
        Response response = null;
        List<Bookstore> bookstoreList = getListOfBookstores();
        for (Bookstore bookstore : bookstoreList){
            response = deleteBookstore(bookstore.getId());
        }
        return response;
    }

}
