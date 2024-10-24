package EndpointsConfig;

import POJOs.Customer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;

import java.lang.reflect.Type;
import java.util.List;

import static io.restassured.RestAssured.given;

public class CustomersRelatedEndpoints extends BasicEndpointConfiguration {
    private static final String addGetCustomerEndpoint = "customers";
    private static final String getUpdateDeleteCustomerEndpoint = "customers/{customerId}";

    public Response getCustomer(String customerId){
        return given().spec(requestSpecification).pathParam("customerId", customerId).when().get(getUpdateDeleteCustomerEndpoint);
    }

    public Customer getCustomerAsClass(String customerId) {
        return getCustomer(customerId).as(Customer.class);
    }

    public Response getCustomers() {
        return given().spec(requestSpecification).when().get(addGetCustomerEndpoint);
    }

    public List<Customer> getListOfCustomers(){
        Response response = getCustomers();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Customer>>() {}.getType();
        return gson.fromJson(response.asString(), type);
    }

    public Response updateCustomer(String customerId, Customer newCustomer){
        return given()
                .spec(requestSpecification)
                .pathParam("customerId", customerId)
                .body(newCustomer)
                .when()
                .put(getUpdateDeleteCustomerEndpoint);
    }

    public Response deleteCustomer(String customerId){
        return given().spec(requestSpecification).pathParam("customerId", customerId).when().delete(getUpdateDeleteCustomerEndpoint);
    }

    public Response addCustomer(Customer customer){
        return given().spec(requestSpecification).body(customer).when().post(addGetCustomerEndpoint);
    }

    public Response deleteAllCustomers(){
        Response response = null;
        List<Customer> customersList = getListOfCustomers();
        for (Customer customer : customersList){
            response = deleteCustomer(customer.getId());
        }
        return response;
    }
}
