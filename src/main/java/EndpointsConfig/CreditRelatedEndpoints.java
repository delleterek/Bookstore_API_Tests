package EndpointsConfig;

import POJOs.Credit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;

import java.lang.reflect.Type;
import java.util.List;

import static io.restassured.RestAssured.*;

public class CreditRelatedEndpoints {
    private static final String topUpEndpoint = "payments/credit";
    private static final String historyEndpoint = "payments/credit/history/{customerId}";

    public Response topUpCustomersAccount(Credit credit){
        return given().spec(requestSpecification).body(credit).when().post(topUpEndpoint);
    }

    public Response getCreditHistory(String customerId){
        return given().spec(requestSpecification).pathParam("customerId", customerId).when().get(historyEndpoint);
    }

    public List<Credit> getCreditHistoryAsList(String customerId){
        Response response = getCreditHistory(customerId);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Credit>>() {}.getType();
        return gson.fromJson(response.asString(), type);
    }
}
