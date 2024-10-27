import EndpointsConfig.CustomersRelatedEndpoints;
import EndpointsConfig.ProductsRelatedEndpoints;
import POJOs.Product;
import Utils.DataReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static Utils.TestListener.collectTestAttributes;

public class ProductTests extends BaseTest{
    private ProductsRelatedEndpoints productsRelatedEndpoints;

    @BeforeClass(alwaysRun = true)
    public void setUp(){
        productsRelatedEndpoints = new ProductsRelatedEndpoints();
    }

    @DataProvider(name = "CreatedProductData")
    private Object[][] getCreateProductData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\ProductData.xlsx", "Create");
    }

    @DataProvider(name = "UpdateProductData")
    private Object[][] getUpdateProductData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\ProductData.xlsx", "Update");
    }

    @DataProvider(name = "DeleteProductData")
    private Object[][] getDeleteProductData(){
        DataReader dataReader = new DataReader();
        return dataReader.readExcelData("C:\\Projects\\Bookstore_API_Tests\\ProductData.xlsx", "Delete");
    }

    @Test(groups = "CRUD")
    public void getProductsCheck(){
        Response response = productsRelatedEndpoints.getProducts();
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "createProductCheck", groups = "CRUD")
    public void getProductCheck(){
        Response response = productsRelatedEndpoints.getProduct(createdProduct.getId());
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(groups = {"e2e", "CRUD"})
    public void createProductCheck(){
        Product productToBeAdded = Product.builder().name(testData.getProductName()).price(testData.getProductPrice()).available(testData.getIsProductAvailable()).build();
        Response response = productsRelatedEndpoints.postProduct(createdBookstore.getId(), productToBeAdded);
        String newProductId = response.getBody().asString().replaceAll("\"", "");
        createdProduct = productsRelatedEndpoints.getProductAsClass(newProductId);
        collectTestAttributes(response, toJsonPretty(productToBeAdded));
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), 201);
        softAssert.assertEquals(createdProduct.getName(), testData.getProductName());
        softAssert.assertEquals(createdProduct.getPrice(), testData.getProductPrice());
        softAssert.assertEquals(createdProduct.getAvailable(), testData.getIsProductAvailable());
        softAssert.assertAll();
    }

    @Test(dataProvider = "CreateProductData", groups = "CRUD")
    public void createProductBadRequestParametrizedCheck(String bookstoreId, String availableAsString, String name, String priceAsString, String path, String expectedStatusCodeAsString, String expectedMessage){
        Boolean available = (availableAsString.isEmpty()) ? null : Boolean.parseBoolean(availableAsString);
        Double price = (priceAsString.isEmpty()) ? null : Double.parseDouble(priceAsString);
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Product productToBeAdded = Product.builder().name(name).build();
        if(available!=null) productToBeAdded.setAvailable(available);
        if(price!=null) productToBeAdded.setPrice(price);
        Response response = productsRelatedEndpoints.postProduct(bookstoreId, productToBeAdded);
        collectTestAttributes(response, toJsonPretty(productToBeAdded));
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(dependsOnMethods = "createProductCheck", priority = 1, groups = "CRUD")
    public void updateProductCheck(){
        Product productToBeUpdated = Product.builder().name(testData.getUpdatedProductName()).price(testData.getUpdatedProductPrice()).available(testData.getIsUpdatedProductAvailable()).build();
        Response response = productsRelatedEndpoints.updateProduct(createdProduct.getId(), productToBeUpdated);
        collectTestAttributes(response, toJsonPretty(productToBeUpdated));
        Product updatedProduct = productsRelatedEndpoints.getProductAsClass(createdProduct.getId());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), 204);
        softAssert.assertEquals(updatedProduct.getName(), testData.getUpdatedProductName());
        softAssert.assertEquals(updatedProduct.getPrice(), testData.getUpdatedProductPrice());
        softAssert.assertEquals(updatedProduct.getAvailable(), testData.getIsUpdatedProductAvailable());
        softAssert.assertAll();
    }

    @Test(dataProvider = "UpdateProductData", groups = "CRUD")
    public void updateProductBadRequestParametrizedCheck(String productId, String isAvailableAsString, String name, String priceAsString, String path, String expectedStatusCodeAsString, String expectedMessage){
        Boolean isAvailable = (isAvailableAsString.isEmpty()) ? null : Boolean.parseBoolean(isAvailableAsString);
        Double price = (priceAsString.isEmpty()) ? null : Double.parseDouble(priceAsString);
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Product productToBeUpdated = Product.builder().name(name).build();
        if (isAvailable!=null) productToBeUpdated.setAvailable(isAvailable);
        if (price!=null) productToBeUpdated.setPrice(price);
        Response response = productsRelatedEndpoints.updateProduct(productId, productToBeUpdated);
        collectTestAttributes(response, toJsonPretty(productToBeUpdated));
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(dependsOnMethods = "updateProductCheck", groups = "CRUD")
    public void deleteProductCheck(){
        Response response = productsRelatedEndpoints.deleteProduct(createdBookstore.getId(), createdProduct.getId());
        collectTestAttributes(response);
        Assert.assertEquals(response.getStatusCode(), 204);
    }

    @Test(dataProvider = "DeleteProductData", groups = "CRUD")
    public void deleteProductBadRequestParametrizedCheck(String bookstoreId, String productId, String path, String expectedStatusCodeAsString, String expectedMessage){
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeAsString);
        Response response = productsRelatedEndpoints.deleteProduct(bookstoreId, productId);
        collectTestAttributes(response);
        verifyStatusCodeAndMessage(response, expectedStatusCode, path, expectedMessage);
    }

    @Test(groups = "Database cleanup", enabled = false)
    public void deleteAllProductCheck(ITestContext context){
        Response response = productsRelatedEndpoints.deleteAllProducts();
        collectTestAttributes(response, context);
        Assert.assertEquals(productsRelatedEndpoints.getListOfProducts().size(), 0, "Database cleanup probably went not as expected, there are still some products listed");
    }

}
