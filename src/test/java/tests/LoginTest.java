package tests;

import base.BaseClass;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utilities.JsonReader;

@Epic("Authentication")
@Feature("User Login and Logout")
public class LoginTest extends BaseClass {

    @Test(priority = 1, groups = {"smoke", "sanity", "regression"}, description = "Verify successful login with valid credentials")
    @Story("Valid Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Authenticate using valid username and password from JSON test data and verify user lands on Products page.")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Login page failed to load.");

        String username = JsonReader.getTestData("validUser.username");
        String password = JsonReader.getTestData("validUser.password");

        InventoryPage inventoryPage = loginPage.login(username, password);

        Assert.assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page did not load after successful login.");
        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Page title does not match 'Products'.");
    }

    @Test(priority = 2, groups = {"sanity", "regression"}, description = "Verify error message when logging in with locked out user")
    @Story("Locked Out User Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Attempt login with locked out user credentials from JSON data and assert proper error message is displayed.")
    public void testLockedOutUser() {
        LoginPage loginPage = new LoginPage(getDriver());

        String username = JsonReader.getTestData("lockedOutUser.username");
        String password = JsonReader.getTestData("lockedOutUser.password");
        String expectedError = JsonReader.getTestData("lockedOutUser.expectedErrorMessage");

        loginPage.login(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message container is not displayed for locked out user.");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError, "Locked out error message does not match expected text.");
    }

    @Test(priority = 3, groups = {"regression"}, description = "Verify error message for invalid credentials")
    @Story("Invalid Credentials Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempt login with non-existent user credentials from JSON data and verify error message.")
    public void testInvalidCredentials() {
        LoginPage loginPage = new LoginPage(getDriver());

        String username = JsonReader.getTestData("invalidUser.username");
        String password = JsonReader.getTestData("invalidUser.password");
        String expectedError = JsonReader.getTestData("invalidUser.expectedErrorMessage");

        loginPage.login(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message is not displayed for invalid credentials.");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError, "Error message does not match expected text.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "Verify error message for empty username and password")
    @Story("Empty Fields Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Click login button with blank username and password and verify required field validation message.")
    public void testEmptyUsername() {
        LoginPage loginPage = new LoginPage(getDriver());

        String username = JsonReader.getTestData("emptyUser.username");
        String password = JsonReader.getTestData("emptyUser.password");
        String expectedError = JsonReader.getTestData("emptyUser.expectedErrorMessage");

        loginPage.login(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message is not displayed for empty credentials.");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError, "Error message does not match expected text.");
    }

    @Test(priority = 5, groups = {"sanity", "regression"}, dependsOnMethods = {"testValidLogin"}, description = "Verify successful logout from application")
    @Story("User Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Log in, open sidebar menu, click Logout, and verify user is redirected to Login page.")
    public void testLogout() {
        LoginPage loginPage = new LoginPage(getDriver());
        String username = JsonReader.getTestData("validUser.username");
        String password = JsonReader.getTestData("validUser.password");

        InventoryPage inventoryPage = loginPage.login(username, password);
        Assert.assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page failed to load prior to logout.");

        LoginPage loggedOutPage = inventoryPage.clickLogout();
        Assert.assertTrue(loggedOutPage.isLoginPageLoaded(), "User was not redirected to Login page after logout.");
    }
}
