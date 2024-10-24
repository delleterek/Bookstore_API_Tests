package EndpointsConfig;

import POJOs.Bookstore;
import POJOs.Product;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.restassured.response.Response;

import java.lang.reflect.Type;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ProductsRelatedEndpoints {
    private static final String getProductsEndpoint = "bookstores/product";
    private static final String getUpdateProductEndpoint = "bookstores/product/{productId}";
    private static final String deleteProductEndpoint = "bookstores/{bookstoreId}/product/{productId}";
    private static final String postProductEndpoint = "bookstores/{bookstoreId}/product";

    public Response getProducts() {
        return given().spec(requestSpecification).when().get(getProductsEndpoint);
    }

    public List<Product> getListOfProducts(){
        Response response = getProducts();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(response.asString(), type);
    }

    public Response getProduct(String productId){
        return given().spec(requestSpecification).pathParam("productId", productId).when().get(getUpdateProductEndpoint);
    }

    public Product getProductAsClass(String productId){
        return getProduct(productId).as(Product.class);
    }

    public Response updateProduct(String productId, Product newProduct){
        return given().spec(requestSpecification).pathParam("productId", productId).body(newProduct).when().put(getUpdateProductEndpoint);
    }

    public Response postProduct(String bookstoreId, Product newProduct){
        return given().spec(requestSpecification).pathParam("bookstoreId", bookstoreId).body(newProduct).when().post(postProductEndpoint);
    }

    public Response deleteProduct(String bookstoreId, String productId){
        return given().spec(requestSpecification).pathParams("bookstoreId", bookstoreId, "productId", productId).when().delete(deleteProductEndpoint);
    }

    public Response deleteAllProducts(){
        Response response = null;
        BookstoresRelatedEndpoints bookstoresRelatedEndpoints = new BookstoresRelatedEndpoints();
        List<Product> productsList = getListOfProducts();
        List<Bookstore> bookstoresList = bookstoresRelatedEndpoints.getListOfBookstores();
        for (Bookstore bookstore : bookstoresList){
            for (Product product : productsList){
                response = deleteProduct(bookstore.getId(), product.getId());
            }
        }
        return response;
    }
}
