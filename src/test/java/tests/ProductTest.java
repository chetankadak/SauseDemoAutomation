package tests;

import base.BaseClass;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailsPage;
import utilities.JsonReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Epic("Product Catalog")
@Feature("Product Browsing and Sorting")
public class ProductTest extends BaseClass {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        String username = JsonReader.getTestData("validUser.username");
        String password = JsonReader.getTestData("validUser.password");
        inventoryPage = loginPage.login(username, password);
        Assert.assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page failed to load during pre-test login.");
    }

    @Test(priority = 1, groups = {"smoke", "sanity", "regression"}, description = "Verify product catalog displays items")
    @Story("Product Listing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that products are properly listed in the inventory catalog with non-zero count.")
    public void testProductListingDisplayed() {
        int productCount = inventoryPage.getProductCount();
        Assert.assertTrue(productCount > 0, "No products were found on the inventory page.");
        Assert.assertEquals(productCount, 6, "Expected exactly 6 default products on SauceDemo.");
    }

    @Test(priority = 2, groups = {"sanity", "regression"}, description = "Verify navigation to product details page and content verification")
    @Story("Product Details View")
    @Severity(SeverityLevel.NORMAL)
    @Description("Click a product from the inventory page, verify details page content matches JSON test data, and navigate back.")
    public void testProductDetailsNavigation() {
        String expectedName = JsonReader.getTestData("products.backpack.name");
        String expectedPrice = JsonReader.getTestData("products.backpack.price");
        String expectedDescription = JsonReader.getTestData("products.backpack.description");

        ProductDetailsPage detailsPage = inventoryPage.clickProduct(expectedName);

        Assert.assertEquals(detailsPage.getProductName(), expectedName, "Product name does not match expected name.");
        Assert.assertEquals(detailsPage.getProductPrice(), expectedPrice, "Product price does not match expected price.");
        Assert.assertTrue(detailsPage.getProductDescription().contains("carry.allTheThings()"), "Product description does not match.");
        Assert.assertTrue(detailsPage.isAddToCartButtonDisplayed(), "Add to Cart button is not displayed on details page.");

        InventoryPage backPage = detailsPage.clickBackToProducts();
        Assert.assertTrue(backPage.isInventoryPageLoaded(), "Did not return to Inventory page upon clicking Back to Products.");
    }

    @Test(priority = 3, groups = {"regression"}, description = "Verify sorting products by Name (A to Z)")
    @Story("Sort Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Select 'Name (A to Z)' sort order and verify product names are strictly sorted in ascending alphabetical order.")
    public void testSortProductsByNameAZ() {
        inventoryPage.selectSortOption("Name (A to Z)");
        List<String> actualNames = inventoryPage.getProductNames();

        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames);

        Assert.assertEquals(actualNames, expectedNames, "Products are not sorted alphabetically A to Z.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "Verify sorting products by Name (Z to A)")
    @Story("Sort Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Select 'Name (Z to A)' sort order and verify product names are strictly sorted in descending alphabetical order.")
    public void testSortProductsByNameZA() {
        inventoryPage.selectSortOption("Name (Z to A)");
        List<String> actualNames = inventoryPage.getProductNames();

        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(Collections.reverseOrder());

        Assert.assertEquals(actualNames, expectedNames, "Products are not sorted alphabetically Z to A.");
    }

    @Test(priority = 5, groups = {"regression"}, description = "Verify sorting products by Price (low to high)")
    @Story("Sort Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Select 'Price (low to high)' sort order and verify product prices are strictly sorted in ascending numeric order.")
    public void testSortProductsByPriceLowToHigh() {
        inventoryPage.selectSortOption("Price (low to high)");
        List<Double> actualPrices = inventoryPage.getProductPrices();

        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);

        Assert.assertEquals(actualPrices, expectedPrices, "Products are not sorted by price low to high.");
    }

    @Test(priority = 6, groups = {"regression"}, description = "Verify sorting products by Price (high to low)")
    @Story("Sort Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Select 'Price (high to low)' sort order and verify product prices are strictly sorted in descending numeric order.")
    public void testSortProductsByPriceHighToLow() {
        inventoryPage.selectSortOption("Price (high to low)");
        List<Double> actualPrices = inventoryPage.getProductPrices();

        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        expectedPrices.sort(Collections.reverseOrder());

        Assert.assertEquals(actualPrices, expectedPrices, "Products are not sorted by price high to low.");
    }
}
