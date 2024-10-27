import EndpointsConfig.BookstoresRelatedEndpoints;
import POJOs.Bookstore;
import Utils.DataReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static Utils.TestListener.collectTestAttributes;

public class BookstoreTests extends BaseTest{
    private BookstoresRelatedEndpoints bookstoresRelatedEndpoints;

    @BeforeClass(alwaysRun = true)
    public void setUp(){
        bookstoresRelatedEndpoints = new BookstoresRelatedEndpoints();
    }

    @DataProvider(name = "CreatedBookstoreData")
    private Object[][] getCreateBookstoreData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\BookstoreData.xlsx", "Create");
    }

    @DataProvider(name = "UpdateBookstoreData")
    private Object[][] getUpdateBookstoreData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\BookstoreData.xlsx", "Update");
    }

    @DataProvider(name = "DeleteBookstoreData")
    private Object[][] getDeleteBookstoreData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\BookstoreData.xlsx", "Delete");
    }

    @Test(groups = "CRUD")
    public void getBookstoresCheck(){
        Response response = bookstoresRelatedEndpoints.getBookstores();
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "createBookstoreCheck", groups = "CRUD")
    public void getBookstoreCheck(){
        Response response = bookstoresRelatedEndpoints.getBookstore(createdBookstore.getId());
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(groups = {"e2e", "CRUD"})
    public void createBookstoreCheck(){
        Bookstore bookstoreToBeAdded = Bookstore.builder().name(testData.getBookstoreName()).isActive(testData.getIsBookstoreActive()).build();
        Response response = bookstoresRelatedEndpoints.addBookstore(bookstoreToBeAdded);
        String createdBookstoreId = response.getBody().asString().replaceAll("\"", "");
        createdBookstore = bookstoresRelatedEndpoints.getBookstoreAsClass(createdBookstoreId);
        collectTestAttributes(response, toJsonPretty(bookstoreToBeAdded));
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), 201);
        softAssert.assertEquals(createdBookstore.getName(), testData.getBookstoreName());
        softAssert.assertEquals(createdBookstore.getIsActive(), testData.getIsBookstoreActive());
        softAssert.assertAll();
    }

    @Test(dataProvider = "CreateBookstoreData", groups = "CRUD")
    public void createBookstoreBadRequestParametrizedCheck(String isActiveAsString, String name, String path, String expectedStatusCodeAsString, String expectedMessage){
        Boolean isActive = (isActiveAsString.isEmpty()) ? null : Boolean.parseBoolean(isActiveAsString);
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Bookstore bookstoreToBeAdded = Bookstore.builder().name(name).build();
        if(isActive!=null) bookstoreToBeAdded.setIsActive(isActive);
        Response response = bookstoresRelatedEndpoints.addBookstore(bookstoreToBeAdded);
        collectTestAttributes(response, toJsonPretty(bookstoreToBeAdded));
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(dependsOnMethods = "createBookstoreCheck", groups = "CRUD")
    public void updateBookstoreCheck(){
        Bookstore bookstoreDataToBeUpdated = Bookstore.builder().name(testData.getUpdatedBookstoreName()).isActive(testData.getIsUpdatedBookstoreActive()).build();
        Response response = bookstoresRelatedEndpoints.updateBookstore(createdBookstore.getId(), bookstoreDataToBeUpdated);
        collectTestAttributes(response, toJsonPretty(bookstoreDataToBeUpdated));
        Bookstore updatedBookstore = bookstoresRelatedEndpoints.getBookstoreAsClass(createdBookstore.getId());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), 204);
        softAssert.assertEquals(updatedBookstore.getName(), testData.getUpdatedBookstoreName());
        softAssert.assertEquals(updatedBookstore.getIsActive(), testData.getIsUpdatedBookstoreActive());
        softAssert.assertAll();
    }

    @Test(dataProvider = "UpdateBookstoreData", groups = "CRUD")
    public void updateBookstoreBadRequestParametrizedCheck(String bookstoreId, String isActiveAsString, String name, String path, String expectedStatusCodeAsString, String expectedMessage){
        Boolean isActive = (isActiveAsString.isEmpty()) ? null : Boolean.parseBoolean(isActiveAsString);
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Bookstore bookstoreDataToBeUpdated = Bookstore.builder().name(name).build();
        if(isActive!=null) bookstoreDataToBeUpdated.setIsActive(isActive);
        Response response = bookstoresRelatedEndpoints.updateBookstore(bookstoreId, bookstoreDataToBeUpdated);
        collectTestAttributes(response, toJsonPretty(bookstoreDataToBeUpdated));
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(dependsOnMethods = "updateBookstoreCheck", groups = "CRUD", enabled = false)
    public void deleteBookstoreCheck(){
        Response response = bookstoresRelatedEndpoints.deleteBookstore(createdBookstore.getId());
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(),204);
    }

    @Test(dataProvider = "DeleteBookstoreData", groups = "CRUD")
    public void deleteBookstoreBadRequestParametrizeCheck(String id, String path, String expectedStatusCodeAsString, String expectedMessage){
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Response response = bookstoresRelatedEndpoints.deleteBookstore(id);
        collectTestAttributes(response);
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(groups = "Database cleanup", enabled = false)
    public void deleteAllBookstoresCheck(ITestContext context){
        Response response = bookstoresRelatedEndpoints.deleteAllBookstores();
        collectTestAttributes(response, context);
        Assert.assertEquals(bookstoresRelatedEndpoints.getListOfBookstores().size(), 0, "Database cleanup probably went not as expected, there are still some bookstores listed");
    }
}
