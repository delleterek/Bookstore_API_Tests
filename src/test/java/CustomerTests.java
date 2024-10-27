import EndpointsConfig.CustomersRelatedEndpoints;
import POJOs.Customer;
import Utils.DataReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static Utils.TestListener.collectTestAttributes;

public class CustomerTests extends BaseTest{
    private CustomersRelatedEndpoints customersRelatedEndpoints;

    @BeforeClass(alwaysRun = true)
    public void setUp(){
        customersRelatedEndpoints = new CustomersRelatedEndpoints();
    }

    @DataProvider(name = "CreatedCustomerData")
    private Object[][] getCreateCustomerData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\CustomerData.xlsx", "Create");
    }

    @DataProvider(name = "UpdateCustomerData")
    private Object[][] getUpdateCustomerData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\CustomerData.xlsx", "Update");
    }

    @DataProvider(name = "DeleteCustomerData")
    private Object[][] getDeleteCustomerData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\CustomerData.xlsx", "Delete");
    }

    @Test(groups = "CRUD")
    public void getCustomersCheck(){
        Response response = customersRelatedEndpoints.getCustomers();
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "createCustomerCheck", groups = "CRUD")
    public void getCustomerCheck(){
        Response response = customersRelatedEndpoints.getCustomer(createdCustomer.getId());
        collectTestAttributes(response);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(createdCustomer.getFirstName(), testData.getCustomersFirstName());
        softAssert.assertEquals(createdCustomer.getLastName(), testData.getCustomersLastName());
        softAssert.assertEquals(createdCustomer.getUsername(), testData.getCustomersUsername());
        softAssert.assertEquals(response.getStatusCode(), 200);
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "createCustomerCheck", groups = "CRUD")
    public void updateCustomerCheck(){
        Customer customerDataToUpdate = Customer.builder().firstName(testData.getUpdatedFirstName()).lastName(testData.getUpdatedLastName()).username(testData.getUpdatedUsername()).build();
        Response response = customersRelatedEndpoints.updateCustomer(createdCustomer.getId(), customerDataToUpdate);
        collectTestAttributes(response);
        Customer updatedCustomer = customersRelatedEndpoints.getCustomerAsClass(createdCustomer.getId());
        SoftAssert softAssert= new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), 204);
        softAssert.assertEquals(updatedCustomer.getFirstName(), testData.getUpdatedFirstName());
        softAssert.assertEquals(updatedCustomer.getLastName(), testData.getUpdatedLastName());
        softAssert.assertEquals(updatedCustomer.getUsername(), testData.getUpdatedUsername());
        softAssert.assertAll();
    }

    @Test(dataProvider = "UpdateCustomerData")
    public void updateCustomerBadRequestParametrizedCheck(String firstName, String lastName, String username, String id, String path, int expectedStatusCode, String expectedMessage){
        Customer updatedCustomer = Customer.builder().firstName(firstName).lastName(lastName).username(username).build();
        Response response = customersRelatedEndpoints.updateCustomer(id, updatedCustomer);
        collectTestAttributes(response, toJsonPretty(updatedCustomer));
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(groups = {"e2e", "CRUD"})
    public void createCustomerCheck(){
        Customer customerToBeAdded = Customer.builder().firstName(testData.getCustomersFirstName()).lastName(testData.getCustomersLastName()).username(testData.getCustomersUsername()).build();
        Response response = customersRelatedEndpoints.addCustomer(customerToBeAdded);
        createdCustomer = customersRelatedEndpoints.getCustomerAsClass(response.jsonPath().getString("customerId"));
        collectTestAttributes(response, toJsonPretty(customerToBeAdded));
        verifyStatusCodeAndMessage(response, 201, "message", "Customer saved successfully");
    }

    @Test(dataProvider = "CreateCustomerData", groups = "CRUD")
    public void createCustomerBadRequestParametrizedCheck(String firstName, String lastName, String username, String path, String expectedStatusCodeAsString, String expectedMessage){
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Customer customerToBeAdded = Customer.builder().firstName(firstName).lastName(lastName).username(username).build();
        Response response = customersRelatedEndpoints.addCustomer(customerToBeAdded);
        collectTestAttributes(response, toJsonPretty(customerToBeAdded));
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(dependsOnMethods = "updateCustomerCheck", groups = "CRUD")
    public void deleteCustomerCheck(){
        Response response = customersRelatedEndpoints.deleteCustomer(createdCustomer.getId());
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dataProvider = "DeleteCustomerData", groups = "CRUD")
    public void deleteCustomerBadRequestParametrizedCheck(String id, String path, String expectedStatusCodeAsString, String expectedMessage){
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Response response = customersRelatedEndpoints.deleteCustomer(id);
        collectTestAttributes(response);
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(groups = "Database cleanup", enabled = false)
    public void deleteAllCustomersCheck(){
        Response response = customersRelatedEndpoints.deleteAllCustomers();
        collectTestAttributes(response);
        Assert.assertEquals(customersRelatedEndpoints.getListOfCustomers().size(), 0, "Database cleanup probably went not as expected, there are still some customers listed");
    }
}
