import EndpointsConfig.CreditRelatedEndpoints;
import POJOs.Credit;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static Utils.TestListener.collectTestAttributes;

public class CreditTests extends BaseTest{
    private CreditRelatedEndpoints creditRelatedEndpoints;

    @BeforeClass(alwaysRun = true)
    public void setUp(){
        creditRelatedEndpoints = new CreditRelatedEndpoints();
    }

    @Test(groups = "e2e")
    public void topUpCustomersAccountCheck(){
        Credit creditToBeAdded = Credit.builder().customerId(createdCustomer.getId()).totalPrice(testData.getTopUpAmount()).build();
        Response response = creditRelatedEndpoints.topUpCustomersAccount(creditToBeAdded);
        collectTestAttributes(response, toJsonPretty(creditToBeAdded));
        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Test(groups = "e2e", dependsOnMethods = "topUpCustomersAccountCheck")
    public void getCreditHistoryCheck(){
        Response response = creditRelatedEndpoints.getCreditHistory(createdCustomer.getId());
        List<Credit> customersCreditHistory = creditRelatedEndpoints.getCreditHistoryAsList(createdCustomer.getId());
        collectTestAttributes(response);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(customersCreditHistory.getLast().getTotalPrice(), testData.getTopUpAmount());
        softAssert.assertEquals(customersCreditHistory.getLast().getTransactionType(), "CREDIT");
        softAssert.assertEquals(response.getStatusCode(), 200);
        softAssert.assertAll();
    }
}
