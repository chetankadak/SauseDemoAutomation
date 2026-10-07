package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.WaitUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing SauceDemo Checkout Step Two (Overview) and Confirmation (Complete).
 * Encapsulates order summary review, totals verification, order finalization, and completion status.
 */
public class CheckoutOverviewPage {

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Overview Locators
    private final By pageTitle = By.cssSelector("span.title");
    private final By cartItems = By.cssSelector(".cart_item");
    private final By cartItemNames = By.cssSelector(".inventory_item_name");
    private final By paymentInfo = By.cssSelector("[data-test='payment-info-value'], .summary_value_label:nth-of-type(1)");
    private final By shippingInfo = By.cssSelector("[data-test='shipping-info-value'], .summary_value_label:nth-of-type(2)");
    private final By itemTotal = By.cssSelector(".summary_subtotal_label");
    private final By taxLabel = By.cssSelector(".summary_tax_label");
    private final By totalLabel = By.cssSelector(".summary_total_label");
    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");

    // Complete / Confirmation Locators
    private final By completeHeader = By.cssSelector(".complete-header");
    private final By completeText = By.cssSelector(".complete-text");
    private final By backHomeButton = By.id("back-to-products");

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Verify Checkout Overview Page is loaded")
    public boolean isOverviewPageLoaded() {
        return waitUtil.waitForVisibility(pageTitle).isDisplayed() &&
                getPageTitle().equalsIgnoreCase("Checkout: Overview");
    }

    @Step("Get page header title")
    public String getPageTitle() {
        return waitUtil.waitForVisibility(pageTitle).getText().trim();
    }

    @Step("Get list of items on overview page")
    public List<String> getItemNames() {
        List<WebElement> elements = driver.findElements(cartItemNames);
        List<String> names = new ArrayList<>();
        for (WebElement element : elements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    @Step("Get Payment Information")
    public String getPaymentInfo() {
        return waitUtil.waitForVisibility(paymentInfo).getText().trim();
    }

    @Step("Get Shipping Information")
    public String getShippingInfo() {
        return waitUtil.waitForVisibility(shippingInfo).getText().trim();
    }

    @Step("Get Item Subtotal text")
    public String getItemTotalText() {
        return waitUtil.waitForVisibility(itemTotal).getText().trim();
    }

    @Step("Get Tax text")
    public String getTaxText() {
        return waitUtil.waitForVisibility(taxLabel).getText().trim();
    }

    @Step("Get Total text")
    public String getTotalText() {
        return waitUtil.waitForVisibility(totalLabel).getText().trim();
    }

    @Step("Get parsed numeric Total amount")
    public double getTotalAmount() {
        String text = getTotalText();
        String numeric = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(numeric);
    }

    @Step("Click Finish button")
    public CheckoutOverviewPage clickFinish() {
        waitUtil.waitForClickability(finishButton).click();
        return this;
    }

    @Step("Verify Order Complete status")
    public boolean isOrderComplete() {
        return waitUtil.waitForVisibility(completeHeader).isDisplayed() &&
                getCompleteHeader().equalsIgnoreCase("Thank you for your order!");
    }

    @Step("Get confirmation order header text")
    public String getCompleteHeader() {
        return waitUtil.waitForVisibility(completeHeader).getText().trim();
    }

    @Step("Get confirmation dispatch message")
    public String getCompleteMessage() {
        return waitUtil.waitForVisibility(completeText).getText().trim();
    }

    @Step("Click Back Home button")
    public InventoryPage clickBackHome() {
        waitUtil.waitForClickability(backHomeButton).click();
        return new InventoryPage(driver);
    }

    @Step("Click Cancel button on overview")
    public InventoryPage clickCancel() {
        waitUtil.waitForClickability(cancelButton).click();
        return new InventoryPage(driver);
    }
}
