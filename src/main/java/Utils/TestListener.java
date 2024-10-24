
package Utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.Map;
import java.util.stream.Collectors;

public class TestListener implements ITestListener {

    private final ExtentReports extent = new ExtentReports();

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("The following test case execution has started: " + result.getName()):
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        result.getTestContext().setAttribute("ExtentTest", test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("The following test case execution has been finished successfully: " + result.getName());
        TestInfo testInfo = (TestInfo) result.getTestContext().getAttribute("testInfo");
        ExtentTest test = (ExtentTest) result.getTestContext().getAttribute("ExtentTest");
        if (testInfo != null) {
            test.pass("Request Headers:<br>" + formatHeaders(testInfo.getRequestHeaders()));
            test.pass("Request Body:<br><pre>" + testInfo.getRequestBody() + "</pre>");
            test.pass("Response Status: " + testInfo.getResponseStatus());
            test.pass("Response Time: " + testInfo.getResponseTime() + "ms");
            test.pass("Response Headers:<br>" + formatHeaders(testInfo.getResponseHeaders()));
            test.pass("Response Body:<br><pre>" + testInfo.getResponseBody() + "</pre>");
        }
        test.pass("Test PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("The following test case execution has failed: " + result.getName());
        TestInfo testInfo = (TestInfo) result.getTestContext().getAttribute("testInfo");
        ExtentTest test = (ExtentTest) result.getTestContext().getAttribute("ExtentTest");
        if (testInfo != null) {
            test.fail("Request Headers:<br>" + formatHeaders(testInfo.getRequestHeaders();
            test.fail("Request Body:<br><pre> " + testInfo.getRequestBody() + " </pre>");
            test.fail(" Response Status: " + testInfo.getResponseStatus());
            test.fail("Response Time:" + testInfo.getResponseTime() + " ms");
            test.fail(" Response Headers:<br> " + formatHeaders(testInfo.getResponseHeaders()));
            test.fail("Response Body:<br><pre> " + testInfo.getResponseBody() + "</pre>");
        }
        test.fail("Test FAILED: " + result.getName());
        test.fail(result.getThrowable());
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("The following test case execution has been skipped: " + result.getName());
        ExtentTest test = (ExtentTest) result.getTestContext().getAttribute("ExtentTest");
        test.skip("Test SKIPPED: " + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result){
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result){
        this.onTestFailure(result);
    }

    @Override
    public void onStart(ITestContext context){
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(context.getSuite().getName() + ".html");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Automation Tests Report");
        sparkReporter.config().setReportName(context.getSuite().getName());
        extent.attachReporter(sparkReporter);
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private StringBuilder formatHeaders(Map<String, String> headers) {
        StringBuilder formattedHeaders = new StringBuilder("<div>");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            formattedHeaders.append(header.getKey()).append(": ").append(header.getValue()).append("<br>");
        }
        formattedHeaders.append("</div>");
        return formattedHeaders;
    }

    public static void collectTestAttributes(Response response, String body) {
        TestInfo testInfo = new TestInfo();
        testInfo.setRequestBody(body);
        testInfo.setRequestHeaders(response.getHeaders().asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue, (value1, value2) -> value1 + ", " + value2)));
        testInfo.setResponseStatus(response.getStatusCode());
        testInfo.setResponseHeaders(response.getHeaders().asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue, (value1, value2) -> value1 + ", " + value2)));
        testInfo.setResponseBody(response.getBody().prettyPrint());
        testInfo.setResponseTime(response.getTime());
        ITestContext context = Reporter.getCurrentTestResult().getTestContext();
        context.setAttribute("testInfo", testInfo);
    }

    public static void collectTestAttributes(Response response) {
        TestInfo testInfo = new TestInfo();
        testInfo.setRequestHeaders(response.getHeaders().asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue, (value1, value2) -> value1 + ", " + value2)));
        testInfo.setResponseStatus(response.getStatusCode());
        testInfo.setResponseHeaders(response.getHeaders().asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue, (value1, value2) -> value1 + ", " + value2)));
        testInfo.setResponseBody(response.getBody().prettyPrint());
        testInfo.setResponseTime(response.getTime());
        ITestContext context = Reporter.getCurrentTestResult().getTestContext();
        context.setAttribute("testInfo", testInfo);
    }

    public static void collectTestAttributes(Response response, ITestContext context) {
        TestInfo testInfo = new TestInfo();
        testInfo.setRequestHeaders(response.getHeaders().asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue, (value1, value2) -> value1 + ", " + value2)));
        testInfo.setResponseStatus(response.getStatusCode());
        testInfo.setResponseHeaders(response.getHeaders().asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue, (value1, value2) -> value1 + ", " + value2)));
        testInfo.setResponseBody(response.getBody().prettyPrint());
        testInfo.setResponseTime(response.getTime());
        context.setAttribute("testInfo", testInfo);
    }

}


