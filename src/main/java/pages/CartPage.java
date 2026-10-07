package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.WaitUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing SauceDemo Shopping Cart Page.
 * Encapsulates elements and actions for reviewing cart items, removing items,
 * continuing shopping, or proceeding to checkout.
 */
public class CartPage {

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators
    private final By pageTitle = By.cssSelector("span.title");
    private final By cartItems = By.cssSelector(".cart_item");
    private final By cartItemNames = By.cssSelector(".inventory_item_name");
    private final By cartItemPrices = By.cssSelector(".inventory_item_price");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Verify Cart Page is loaded")
    public boolean isCartPageLoaded() {
        return waitUtil.waitForVisibility(pageTitle).isDisplayed() &&
                getPageTitle().equalsIgnoreCase("Your Cart");
    }

    @Step("Get cart page title")
    public String getPageTitle() {
        return waitUtil.waitForVisibility(pageTitle).getText().trim();
    }

    @Step("Get total number of items in cart")
    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    @Step("Get names of all items in cart")
    public List<String> getCartItemNames() {
        List<WebElement> elements = driver.findElements(cartItemNames);
        List<String> names = new ArrayList<>();
        for (WebElement element : elements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    @Step("Get prices of all items in cart")
    public List<Double> getCartItemPrices() {
        List<WebElement> elements = driver.findElements(cartItemPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement element : elements) {
            String priceText = element.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(priceText));
        }
        return prices;
    }

    @Step("Check if '{productName}' is present in cart")
    public boolean isItemInCart(String productName) {
        return getCartItemNames().contains(productName);
    }

    @Step("Remove item '{productName}' from cart")
    public CartPage removeItem(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By removeButton = By.cssSelector("[data-test='remove-" + formattedName + "'], button[id^='remove-" + formattedName + "']");

        try {
            waitUtil.waitForClickability(removeButton).click();
        } catch (Exception e) {
            WebElement cartItem = driver.findElement(By.xpath("//div[@class='cart_item'][.//div[text()='" + productName + "']]"));
            cartItem.findElement(By.xpath(".//button[contains(text(),'Remove')]")).click();
        }
        return this;
    }

    @Step("Click Continue Shopping button")
    public InventoryPage clickContinueShopping() {
        waitUtil.waitForClickability(continueShoppingButton).click();
        return new InventoryPage(driver);
    }

    @Step("Click Checkout button")
    public CheckoutPage clickCheckout() {
        waitUtil.waitForClickability(checkoutButton).click();
        return new CheckoutPage(driver);
    }
}
