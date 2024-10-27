import POJOs.Bookstore;
import POJOs.Customer;
import POJOs.Product;
import POJOs.Purchase;
import Utils.TestData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.oauth2;

public class BaseTest {
    protected static Bookstore createdBookstore;
    protected static Customer createdCustomer;
    protected static Product createdProduct;
    protected static Purchase createdPurchase;
    protected TestData testData = new TestData();

    @BeforeSuite(alwaysRun = true)
    public void getToken() throws OAuthSystemException, OAuthProblemException {
        String clientID = "internal-ms";
        String clientSecret = "Nr4lsn5o";
        String tokenURL = "http://localhost:8023/oauth2/token";
        String token;
        String encodedValue = getBase64Encoded(clientID, clientSecret);

        OAuthClient client = new OAuthClient(new URLConnectionClient());
        OAuthClientRequest request = OAuthClientRequest.tokenLocation(tokenURL)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .buildBodyMessage();

        request.addHeader("Authorization", "Basic " + encodedValue);
        OAuthJSONAccessTokenResponse oAuthResponse = client.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class);
        token = oAuthResponse.getAccessToken();
        RestAssured.authentication = oauth2(token);
    }

    private static String getBase64Encoded(String clientId, String clientSecret){
        return Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    }

    protected static String toJsonPretty(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }

    protected static void verifyStatusCodeAndMessage(Response response, int expectedStatusCode, String messagePath, String expectedMessage){
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode);
        if(response.getStatusCode()!=204) softAssert.assertEquals(response.jsonPath().getString(messagePath), expectedMessage);
        softAssert.assertAll();
    }

}
