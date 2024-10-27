import EndpointsConfig.OrdersRelatedEndpoints;
import POJOs.Address;
import POJOs.Item;
import POJOs.Order;
import POJOs.Purchase;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Utils.TestListener.collectTestAttributes;
import static org.awaitility.Awaitility.await;

public class OrderTests extends BaseTest{
    private OrdersRelatedEndpoints ordersRelatedEndpoints;

    @BeforeClass(alwaysRun = true)
    public void setUp(){
        ordersRelatedEndpoints = new OrdersRelatedEndpoints();
    }

    @Test(groups = "e2e")
    public void createPurchaseOrderCheck(){
        Address address = new Address(testData.getCity(), testData.getPostalCode(), testData.getStreet());
        List<Item> items = new ArrayList<>();
        Item item = new Item(String.valueOf(createdProduct.getPrice()), createdProduct.getId(), "1", String.valueOf(createdProduct.getPrice()));
        items.add(item);
        Order orderToBeCreated = new Order(address, createdBookstore.getId(), createdCustomer.getId(), items, String.valueOf(createdProduct.getPrice()));
        Response response = ordersRelatedEndpoints.createPurchaseOrder(orderToBeCreated);
        createdPurchase = response.as(Purchase.class);
        collectTestAttributes(response, toJsonPretty(orderToBeCreated));
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.jsonPath().getString("orderStatus"), "PENDING");
        softAssert.assertEquals(response.jsonPath().getString("message"), "Order created successfully");
        softAssert.assertEquals(response.getStatusCode(), 200);
        softAssert.assertAll();
    }

    @Test(groups = "e2e", dependsOnMethods = "createPurchaseOrderCheck")
    public void getOrderStatusCheck(){
        SoftAssert softAssert = new SoftAssert();
        verifyCurrentOrderStatus(softAssert, "PENDING");
        awaitForGivenResult("PAID");
        verifyCurrentOrderStatus(softAssert, "PAID");
        awaitForGivenResult("APPROVED");
        Response finalResponse = verifyCurrentOrderStatus(softAssert, "APPROVED");
        collectTestAttributes(finalResponse);
        softAssert.assertAll();
    }

    private void awaitForGivenResult(String status){
        await().atMost(5, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .until(() -> {
                    Response response = ordersRelatedEndpoints.getOrderStatus(createdPurchase.getOrderTrackingId());
                    String orderStatus = response.jsonPath().getString("orderStatus");
                    return orderStatus.equals(status);
                });
    }

    private Response verifyCurrentOrderStatus(SoftAssert softAssert, String status){
        Response currentResponse = ordersRelatedEndpoints.getOrderStatus(createdPurchase.getOrderTrackingId());
        String initialOrderStatus = currentResponse.jsonPath().getString("orderStatus");
        softAssert.assertEquals(initialOrderStatus, status);
        return currentResponse;
    }
}
