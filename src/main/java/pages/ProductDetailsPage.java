package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.WaitUtil;

import java.util.List;

/**
 * Page Object representing SauceDemo Product Details Page.
 * Encapsulates details view of an individual inventory product.
 */
public class ProductDetailsPage {

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators
    private final By productName = By.cssSelector(".inventory_details_name");
    private final By productDesc = By.cssSelector(".inventory_details_desc");
    private final By productPrice = By.cssSelector(".inventory_details_price");
    private final By addToCartButton = By.cssSelector("button[id^='add-to-cart'], [data-test^='add-to-cart']");
    private final By removeButton = By.cssSelector("button[id^='remove'], [data-test^='remove']");
    private final By backButton = By.id("back-to-products");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");

    public ProductDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Get product name from details page")
    public String getProductName() {
        return waitUtil.waitForVisibility(productName).getText().trim();
    }

    @Step("Get product description from details page")
    public String getProductDescription() {
        return waitUtil.waitForVisibility(productDesc).getText().trim();
    }

    @Step("Get product price from details page")
    public String getProductPrice() {
        return waitUtil.waitForVisibility(productPrice).getText().trim();
    }

    @Step("Add product to cart from details page")
    public ProductDetailsPage addToCart() {
        waitUtil.waitForClickability(addToCartButton).click();
        return this;
    }

    @Step("Remove product from cart from details page")
    public ProductDetailsPage removeFromCart() {
        waitUtil.waitForClickability(removeButton).click();
        return this;
    }

    @Step("Check if Add to Cart button is displayed on details page")
    public boolean isAddToCartButtonDisplayed() {
        try {
            return driver.findElement(addToCartButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Check if Remove button is displayed on details page")
    public boolean isRemoveButtonDisplayed() {
        try {
            return driver.findElement(removeButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Click Back to Products button")
    public InventoryPage clickBackToProducts() {
        waitUtil.waitForClickability(backButton).click();
        return new InventoryPage(driver);
    }

    @Step("Click cart icon from details page")
    public CartPage clickCartIcon() {
        waitUtil.waitForClickability(cartLink).click();
        return new CartPage(driver);
    }

    @Step("Get cart badge item count from details page")
    public int getCartItemCount() {
        try {
            List<WebElement> badges = driver.findElements(cartBadge);
            if (badges.isEmpty() || !badges.get(0).isDisplayed()) {
                return 0;
            }
            return Integer.parseInt(badges.get(0).getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
