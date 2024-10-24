package EndpointsConfig;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BasicEndpointConfiguration {
    private final static String uri = "http://localhost:8024";
    private final static ContentType contentType = ContentType.JSON;
    private final static String basePath = "rekindle";
    public RequestSpecification requestSpecification;

    public BasicEndpointConfiguration(){
        requestSpecification = new RequestSpecBuilder().setBaseUri(uri).setBasePath(basePath).setContentType(contentType).build();
    }
}
