package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.WaitUtil;

/**
 * Page Object representing SauceDemo Checkout Step One: Your Information.
 * Encapsulates input fields for customer details, continue to overview, and error messaging.
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators
    private final By pageTitle = By.cssSelector("span.title");
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Verify Checkout Step One Page is loaded")
    public boolean isCheckoutPageLoaded() {
        return waitUtil.waitForVisibility(pageTitle).isDisplayed() &&
                getPageTitle().equalsIgnoreCase("Checkout: Your Information");
    }

    @Step("Get page title")
    public String getPageTitle() {
        return waitUtil.waitForVisibility(pageTitle).getText().trim();
    }

    @Step("Enter First Name: '{firstName}'")
    public CheckoutPage enterFirstName(String firstName) {
        waitUtil.waitForVisibility(firstNameInput).clear();
        if (firstName != null && !firstName.isEmpty()) {
            driver.findElement(firstNameInput).sendKeys(firstName);
        }
        return this;
    }

    @Step("Enter Last Name: '{lastName}'")
    public CheckoutPage enterLastName(String lastName) {
        waitUtil.waitForVisibility(lastNameInput).clear();
        if (lastName != null && !lastName.isEmpty()) {
            driver.findElement(lastNameInput).sendKeys(lastName);
        }
        return this;
    }

    @Step("Enter Postal Code: '{postalCode}'")
    public CheckoutPage enterPostalCode(String postalCode) {
        waitUtil.waitForVisibility(postalCodeInput).clear();
        if (postalCode != null && !postalCode.isEmpty()) {
            driver.findElement(postalCodeInput).sendKeys(postalCode);
        }
        return this;
    }

    @Step("Fill checkout customer details: '{firstName}', '{lastName}', '{postalCode}'")
    public CheckoutPage fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return this;
    }

    @Step("Click Continue button")
    public CheckoutOverviewPage clickContinue() {
        waitUtil.waitForClickability(continueButton).click();
        return new CheckoutOverviewPage(driver);
    }

    @Step("Click Cancel button")
    public CartPage clickCancel() {
        waitUtil.waitForClickability(cancelButton).click();
        return new CartPage(driver);
    }

    @Step("Get checkout validation error message")
    public String getErrorMessage() {
        return waitUtil.waitForVisibility(errorMessage).getText().trim();
    }

    @Step("Check if error message is displayed")
    public boolean isErrorMessageDisplayed() {
        try {
            return waitUtil.waitForVisibility(errorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
