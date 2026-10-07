package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utilities.WaitUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing SauceDemo Products / Inventory Page.
 * Encapsulates elements and actions for catalog browsing, sorting, adding to cart, and navigation.
 */
public class InventoryPage {

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators
    private final By pageTitle = By.cssSelector("span.title");
    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By inventoryItemNames = By.cssSelector(".inventory_item_name");
    private final By inventoryItemPrices = By.cssSelector(".inventory_item_price");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By sortDropdown = By.cssSelector("[data-test='product-sort-container']");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By resetLink = By.id("reset_sidebar_link");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Verify Inventory Page is loaded")
    public boolean isInventoryPageLoaded() {
        return waitUtil.waitForVisibility(pageTitle).isDisplayed() &&
                getPageTitle().equalsIgnoreCase("Products");
    }

    @Step("Get page header title")
    public String getPageTitle() {
        return waitUtil.waitForVisibility(pageTitle).getText().trim();
    }

    @Step("Get total number of products displayed")
    public int getProductCount() {
        return waitUtil.waitForAllVisible(inventoryItems).size();
    }

    @Step("Get list of all product names")
    public List<String> getProductNames() {
        List<WebElement> elements = waitUtil.waitForAllVisible(inventoryItemNames);
        List<String> names = new ArrayList<>();
        for (WebElement element : elements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    @Step("Get list of all product prices as numeric values")
    public List<Double> getProductPrices() {
        List<WebElement> elements = waitUtil.waitForAllVisible(inventoryItemPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement element : elements) {
            String text = element.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(text));
        }
        return prices;
    }

    @Step("Add product '{productName}' to cart")
    public InventoryPage addProductToCart(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By addToCartButton = By.cssSelector("[data-test='add-to-cart-" + formattedName + "'], button[id^='add-to-cart-" + formattedName + "']");
        
        // If specific data-test locator doesn't match, locate dynamically by card container
        try {
            waitUtil.waitForClickability(addToCartButton).click();
        } catch (Exception e) {
            WebElement itemCard = driver.findElement(By.xpath("//div[@class='inventory_item'][.//div[text()='" + productName + "']]"));
            WebElement button = itemCard.findElement(By.xpath(".//button[contains(text(),'Add to cart')]"));
            button.click();
        }
        return this;
    }

    @Step("Remove product '{productName}' from cart")
    public InventoryPage removeProductFromCart(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By removeButton = By.cssSelector("[data-test='remove-" + formattedName + "'], button[id^='remove-" + formattedName + "']");

        try {
            waitUtil.waitForClickability(removeButton).click();
        } catch (Exception e) {
            WebElement itemCard = driver.findElement(By.xpath("//div[@class='inventory_item'][.//div[text()='" + productName + "']]"));
            WebElement button = itemCard.findElement(By.xpath(".//button[contains(text(),'Remove')]"));
            button.click();
        }
        return this;
    }

    @Step("Check if 'Add to cart' button is displayed for '{productName}'")
    public boolean isAddToCartButtonDisplayed(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By addToCartButton = By.cssSelector("[data-test='add-to-cart-" + formattedName + "']");
        try {
            return driver.findElement(addToCartButton).isDisplayed();
        } catch (Exception e) {
            try {
                WebElement itemCard = driver.findElement(By.xpath("//div[@class='inventory_item'][.//div[text()='" + productName + "']]"));
                return itemCard.findElement(By.xpath(".//button[contains(text(),'Add to cart')]")).isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        }
    }

    @Step("Check if 'Remove' button is displayed for '{productName}'")
    public boolean isRemoveButtonDisplayed(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By removeButton = By.cssSelector("[data-test='remove-" + formattedName + "']");
        try {
            return driver.findElement(removeButton).isDisplayed();
        } catch (Exception e) {
            try {
                WebElement itemCard = driver.findElement(By.xpath("//div[@class='inventory_item'][.//div[text()='" + productName + "']]"));
                return itemCard.findElement(By.xpath(".//button[contains(text(),'Remove')]")).isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        }
    }

    @Step("Get cart badge item count")
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

    @Step("Click product '{productName}' to view details")
    public ProductDetailsPage clickProduct(String productName) {
        By productLink = By.xpath("//div[@class='inventory_item_name ' and text()='" + productName + "'] | //div[text()='" + productName + "']");
        waitUtil.waitForClickability(productLink).click();
        return new ProductDetailsPage(driver);
    }

    @Step("Click cart icon")
    public CartPage clickCartIcon() {
        waitUtil.waitForClickability(cartLink).click();
        return new CartPage(driver);
    }

    @Step("Select sort option: '{sortOption}'")
    public InventoryPage selectSortOption(String sortOption) {
        WebElement dropdown = waitUtil.waitForVisibility(sortDropdown);
        Select select = new Select(dropdown);
        try {
            select.selectByVisibleText(sortOption);
        } catch (Exception e) {
            select.selectByValue(sortOption);
        }
        return this;
    }

    @Step("Open side navigation menu")
    public InventoryPage openMenu() {
        waitUtil.waitForClickability(menuButton).click();
        waitUtil.waitForVisibility(logoutLink);
        return this;
    }

    @Step("Click Logout from side menu")
    public LoginPage clickLogout() {
        openMenu();
        waitUtil.waitForClickability(logoutLink).click();
        return new LoginPage(driver);
    }

    @Step("Reset application state")
    public InventoryPage resetAppState() {
        openMenu();
        waitUtil.waitForClickability(resetLink).click();
        return this;
    }
}
