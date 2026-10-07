package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe DriverManager using ThreadLocal to enable seamless parallel test execution.
 * Ensures zero static shared WebDriver state and clean resource lifecycle management.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Prevent instantiation
    }

    /**
     * Retrieves the WebDriver instance associated with the current executing thread.
     *
     * @return WebDriver instance for current thread
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized for thread: " + Thread.currentThread().getName());
        }
        return driver;
    }

    /**
     * Sets the WebDriver instance for the current executing thread.
     *
     * @param driver WebDriver instance
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    /**
     * Initializes the WebDriver based on browser name and headless configuration.
     * Supported browsers: chrome, edge, firefox.
     *
     * @param browser  name of browser (e.g. "edge", "chrome", "firefox")
     * @param headless whether to run in headless mode
     */
    public static void initDriver(String browser, boolean headless) {
        if (browser == null || browser.trim().isEmpty()) {
            browser = ConfigReader.getBrowser();
        }

        WebDriver driver;
        String normalizedBrowser = browser.trim().toLowerCase();

        switch (normalizedBrowser) {
            case "chrome":
                try {
                    WebDriverManager.chromedriver().setup();
                } catch (Exception e) {
                    System.out.println("Notice: WebDriverManager setup fallback to Selenium Manager: " + e.getMessage());
                }
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-search-engine-choice-screen");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "edge":
            case "msedge":
                try {
                    WebDriverManager.edgedriver().setup();
                } catch (Exception e) {
                    System.out.println("Notice: WebDriverManager setup fallback to Selenium Manager: " + e.getMessage());
                }
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                edgeOptions.addArguments("--window-size=1920,1080");
                edgeOptions.addArguments("--disable-gpu");
                edgeOptions.addArguments("--no-sandbox");
                edgeOptions.addArguments("--disable-dev-shm-usage");
                edgeOptions.addArguments("--remote-allow-origins=*");
                driver = new EdgeDriver(edgeOptions);
                break;

            case "firefox":
                try {
                    WebDriverManager.firefoxdriver().setup();
                } catch (Exception e) {
                    System.out.println("Notice: WebDriverManager setup fallback to Selenium Manager: " + e.getMessage());
                }
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser specified: '" + browser + "'. Supported browsers are: chrome, edge, firefox.");
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        if (!headless) {
            driver.manage().window().maximize();
        }

        setDriver(driver);
    }

    /**
     * Safely quits the WebDriver and removes it from ThreadLocal storage to prevent memory leaks.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Warning: Error occurred while quitting WebDriver for thread " + Thread.currentThread().getName() + ": " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
