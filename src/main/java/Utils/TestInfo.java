package Utils;

import lombok.*;

import java.util.Map;

@Data
public class TestInfo {
    private Map<String, String> requestHeaders;
    private String requestBody;
    private Map<String, String> requestParameters;
    private int responseStatus;
    private Map<String, String> responseHeaders;
    private String responseBody;
    private long responseTime;
}