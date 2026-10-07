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
import pages.CartPage;
import pages.CheckoutOverviewPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;
import utilities.JsonReader;

@Epic("E-Commerce Purchase Flow")
@Feature("Checkout Process")
public class CheckoutTest extends BaseClass {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        String username = JsonReader.getTestData("validUser.username");
        String password = JsonReader.getTestData("validUser.password");
        inventoryPage = loginPage.login(username, password);
        Assert.assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page failed to load during pre-test login.");
    }

    @Test(priority = 1, groups = {"smoke", "sanity", "regression"}, description = "Verify end-to-end checkout flow from product selection to order confirmation")
    @Story("Complete Checkout Flow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Add product to cart, proceed to checkout, enter customer information, verify payment and tax details on overview page, finish order, and verify confirmation message.")
    public void testCompleteCheckoutFlow() {
        String productName = JsonReader.getTestData("products.backpack.name");
        String firstName = JsonReader.getTestData("checkoutData.firstName");
        String lastName = JsonReader.getTestData("checkoutData.lastName");
        String postalCode = JsonReader.getTestData("checkoutData.postalCode");
        String expectedHeader = JsonReader.getTestData("checkoutData.completeHeader");
        String expectedText = JsonReader.getTestData("checkoutData.completeText");

        // Add product and navigate to Cart
        inventoryPage.addProductToCart(productName);
        CartPage cartPage = inventoryPage.clickCartIcon();
        Assert.assertTrue(cartPage.isItemInCart(productName), "Item not in cart.");

        // Proceed to Checkout
        CheckoutPage checkoutPage = cartPage.clickCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(), "Checkout page did not load.");

        // Fill customer information and continue
        checkoutPage.fillCheckoutInformation(firstName, lastName, postalCode);
        CheckoutOverviewPage overviewPage = checkoutPage.clickContinue();
        Assert.assertTrue(overviewPage.isOverviewPageLoaded(), "Checkout Overview page did not load.");

        // Verify summary details
        Assert.assertTrue(overviewPage.getItemNames().contains(productName), "Selected item missing from overview summary.");
        Assert.assertFalse(overviewPage.getPaymentInfo().isEmpty(), "Payment information is empty.");
        Assert.assertFalse(overviewPage.getShippingInfo().isEmpty(), "Shipping information is empty.");
        Assert.assertTrue(overviewPage.getTotalAmount() > 0, "Order total is zero or negative.");

        // Finalize order
        overviewPage.clickFinish();
        Assert.assertTrue(overviewPage.isOrderComplete(), "Order is not marked as complete.");
        Assert.assertEquals(overviewPage.getCompleteHeader(), expectedHeader, "Order completion header mismatch.");
        Assert.assertEquals(overviewPage.getCompleteMessage(), expectedText, "Order completion description mismatch.");

        // Return Home
        InventoryPage homePage = overviewPage.clickBackHome();
        Assert.assertTrue(homePage.isInventoryPageLoaded(), "Did not return to Products page after order completion.");
    }

    @Test(priority = 2, groups = {"regression"}, description = "Verify checkout validation when First Name is missing")
    @Story("Checkout Form Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempt to continue checkout without entering first name and verify error message.")
    public void testCheckoutMissingFirstName() {
        String productName = JsonReader.getTestData("products.backpack.name");
        String lastName = JsonReader.getTestData("invalidCheckoutData.emptyFirstName.lastName");
        String postalCode = JsonReader.getTestData("invalidCheckoutData.emptyFirstName.postalCode");
        String expectedError = JsonReader.getTestData("invalidCheckoutData.emptyFirstName.expectedErrorMessage");

        inventoryPage.addProductToCart(productName);
        CartPage cartPage = inventoryPage.clickCartIcon();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.fillCheckoutInformation("", lastName, postalCode);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message was not displayed when First Name was empty.");
        Assert.assertEquals(checkoutPage.getErrorMessage(), expectedError, "Error message text mismatch for empty first name.");
    }

    @Test(priority = 3, groups = {"regression"}, description = "Verify checkout validation when Last Name is missing")
    @Story("Checkout Form Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempt to continue checkout without entering last name and verify error message.")
    public void testCheckoutMissingLastName() {
        String productName = JsonReader.getTestData("products.backpack.name");
        String firstName = JsonReader.getTestData("invalidCheckoutData.emptyLastName.firstName");
        String postalCode = JsonReader.getTestData("invalidCheckoutData.emptyLastName.postalCode");
        String expectedError = JsonReader.getTestData("invalidCheckoutData.emptyLastName.expectedErrorMessage");

        inventoryPage.addProductToCart(productName);
        CartPage cartPage = inventoryPage.clickCartIcon();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.fillCheckoutInformation(firstName, "", postalCode);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message was not displayed when Last Name was empty.");
        Assert.assertEquals(checkoutPage.getErrorMessage(), expectedError, "Error message text mismatch for empty last name.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "Verify checkout validation when Postal Code is missing")
    @Story("Checkout Form Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempt to continue checkout without entering postal code and verify error message.")
    public void testCheckoutMissingPostalCode() {
        String productName = JsonReader.getTestData("products.backpack.name");
        String firstName = JsonReader.getTestData("invalidCheckoutData.emptyPostalCode.firstName");
        String lastName = JsonReader.getTestData("invalidCheckoutData.emptyPostalCode.lastName");
        String expectedError = JsonReader.getTestData("invalidCheckoutData.emptyPostalCode.expectedErrorMessage");

        inventoryPage.addProductToCart(productName);
        CartPage cartPage = inventoryPage.clickCartIcon();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.fillCheckoutInformation(firstName, lastName, "");
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message was not displayed when Postal Code was empty.");
        Assert.assertEquals(checkoutPage.getErrorMessage(), expectedError, "Error message text mismatch for empty postal code.");
    }

    @Test(priority = 5, groups = {"regression"}, description = "Verify cancelling checkout returns user to Cart page")
    @Story("Checkout Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Click Cancel on checkout step one page and verify return to cart page.")
    public void testCancelCheckout() {
        String productName = JsonReader.getTestData("products.backpack.name");

        inventoryPage.addProductToCart(productName);
        CartPage cartPage = inventoryPage.clickCartIcon();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        CartPage returnedCartPage = checkoutPage.clickCancel();
        Assert.assertTrue(returnedCartPage.isCartPageLoaded(), "User was not returned to Cart page upon cancelling checkout.");
        Assert.assertTrue(returnedCartPage.isItemInCart(productName), "Item was lost from cart after cancelling checkout.");
    }
}
