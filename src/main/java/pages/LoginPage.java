package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.WaitUtil;

/**
 * Page Object representing SauceDemo Login Page.
 * Encapsulates elements and user actions on the login screen.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators
    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");
    private final By loginLogo = By.className("login_logo");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Verify Login Page is loaded")
    public boolean isLoginPageLoaded() {
        return waitUtil.waitForVisibility(loginButton).isDisplayed();
    }

    @Step("Enter username: '{username}'")
    public LoginPage enterUsername(String username) {
        waitUtil.waitForVisibility(usernameInput).clear();
        if (username != null && !username.isEmpty()) {
            driver.findElement(usernameInput).sendKeys(username);
        }
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        waitUtil.waitForVisibility(passwordInput).clear();
        if (password != null && !password.isEmpty()) {
            driver.findElement(passwordInput).sendKeys(password);
        }
        return this;
    }

    @Step("Click Login button")
    public void clickLogin() {
        waitUtil.waitForClickability(loginButton).click();
    }

    @Step("Log in with credentials: '{username}'")
    public InventoryPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new InventoryPage(driver);
    }

    @Step("Get login error message")
    public String getErrorMessage() {
        return waitUtil.waitForVisibility(errorMessage).getText().trim();
    }

    @Step("Check if login error message is displayed")
    public boolean isErrorMessageDisplayed() {
        try {
            return waitUtil.waitForVisibility(errorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
