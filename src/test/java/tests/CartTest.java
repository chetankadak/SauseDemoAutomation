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
import pages.InventoryPage;
import pages.LoginPage;
import utilities.JsonReader;

@Epic("Shopping Cart")
@Feature("Cart Operations")
public class CartTest extends BaseClass {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        String username = JsonReader.getTestData("validUser.username");
        String password = JsonReader.getTestData("validUser.password");
        inventoryPage = loginPage.login(username, password);
        Assert.assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page failed to load during pre-test login.");
    }

    @Test(priority = 1, groups = {"smoke", "sanity", "regression"}, description = "Verify adding a single product to the cart")
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add an item from the inventory catalog, verify the shopping cart badge updates to 1, and ensure item appears in Cart.")
    public void testAddSingleProductToCart() {
        String productName = JsonReader.getTestData("products.backpack.name");

        inventoryPage.addProductToCart(productName);
        Assert.assertEquals(inventoryPage.getCartItemCount(), 1, "Cart badge count is not 1 after adding one product.");

        CartPage cartPage = inventoryPage.clickCartIcon();
        Assert.assertTrue(cartPage.isCartPageLoaded(), "Cart page failed to load.");
        Assert.assertTrue(cartPage.isItemInCart(productName), "Added item '" + productName + "' was not found in cart.");
    }

    @Test(priority = 2, groups = {"sanity", "regression"}, description = "Verify adding multiple products to the cart")
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add multiple distinct items to the shopping cart, verify badge count increments accurately, and check cart contents.")
    public void testAddMultipleProductsToCart() {
        String item1 = JsonReader.getTestData("products.backpack.name");
        String item2 = JsonReader.getTestData("products.bikeLight.name");

        inventoryPage.addProductToCart(item1);
        inventoryPage.addProductToCart(item2);

        Assert.assertEquals(inventoryPage.getCartItemCount(), 2, "Cart badge count is not 2 after adding two products.");

        CartPage cartPage = inventoryPage.clickCartIcon();
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Cart page does not contain 2 items.");
        Assert.assertTrue(cartPage.isItemInCart(item1), item1 + " not in cart.");
        Assert.assertTrue(cartPage.isItemInCart(item2), item2 + " not in cart.");
    }

    @Test(priority = 3, groups = {"regression"}, description = "Verify removing product directly from inventory catalog")
    @Story("Remove from Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Add a product, then remove it using the button on the inventory page, and assert badge count reverts.")
    public void testRemoveProductFromInventoryPage() {
        String productName = JsonReader.getTestData("products.backpack.name");

        inventoryPage.addProductToCart(productName);
        Assert.assertTrue(inventoryPage.isRemoveButtonDisplayed(productName), "Remove button not shown after adding item.");

        inventoryPage.removeProductFromCart(productName);
        Assert.assertEquals(inventoryPage.getCartItemCount(), 0, "Cart badge count did not reset to 0 after removal.");
        Assert.assertTrue(inventoryPage.isAddToCartButtonDisplayed(productName), "Add to Cart button did not reappear after removal.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "Verify removing product from inside the cart page")
    @Story("Remove from Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Add a product, navigate to the cart page, click Remove, and verify item is removed and cart is empty.")
    public void testRemoveProductFromCartPage() {
        String productName = JsonReader.getTestData("products.backpack.name");

        inventoryPage.addProductToCart(productName);
        CartPage cartPage = inventoryPage.clickCartIcon();
        Assert.assertTrue(cartPage.isItemInCart(productName), "Product not in cart before removal.");

        cartPage.removeItem(productName);
        Assert.assertFalse(cartPage.isItemInCart(productName), "Product still in cart after removal.");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart item count is not 0 after removing all items.");
    }

    @Test(priority = 5, groups = {"regression"}, description = "Verify navigation back from cart page using 'Continue Shopping'")
    @Story("Cart Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Open cart page and click 'Continue Shopping' to ensure seamless navigation back to product catalog.")
    public void testContinueShoppingFromCart() {
        CartPage cartPage = inventoryPage.clickCartIcon();
        Assert.assertTrue(cartPage.isCartPageLoaded(), "Cart page did not load.");

        InventoryPage returnPage = cartPage.clickContinueShopping();
        Assert.assertTrue(returnPage.isInventoryPageLoaded(), "Failed to return to Inventory page from Cart.");
    }
}
