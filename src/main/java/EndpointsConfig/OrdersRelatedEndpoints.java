package EndpointsConfig;

import POJOs.Order;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrdersRelatedEndpoints extends BasicEndpointConfiguration{
    public static final String createPurchaseEndpoint = "orders";
    public static final String getOrderStatusEndpoint = "orders/{orderTrackingId}";
    public static final String paymentsEndpoint = "payments/{orderId}";

    public Response createPurchaseOrder(Order order){
        return given().spec(requestSpecification).body(order).when().post(createPurchaseEndpoint);
    }

    public Response getOrderStatus(String orderTrackingId){
        return given().spec(requestSpecification).pathParam("orderTrackingId", orderTrackingId).when().get(getOrderStatusEndpoint);
    }

    public Response getPaymentInformation(String orderId){
        return given()
                .spec(requestSpecification)
                .pathParam("orderId", orderId)
                .get(paymentsEndpoint);
    }
}
