package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import utilities.ConfigReader;
import utilities.DriverManager;
import utilities.VideoRecorder;

import java.lang.reflect.Method;

/**
 * BaseClass for all test classes.
 * Manages WebDriver initialization, browser lifecycle, configuration loading,
 * and test execution video recording across test methods.
 */
public abstract class BaseClass {

    /**
     * Initializes WebDriver, starts video recording (if enabled), and navigates to the target application URL.
     * Allows optional TestNG XML parameter override for browser.
     *
     * @param browserParam optional browser name from testng.xml
     * @param method       executing test method metadata
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@org.testng.annotations.Optional("") String browserParam, org.testng.ITestContext context, Method method) {
        String testName = method.getName();

        String browser = (browserParam != null && !browserParam.trim().isEmpty())
                ? browserParam.trim()
                : (context.getCurrentXmlTest() != null && context.getCurrentXmlTest().getParameter("browser") != null
                    ? context.getCurrentXmlTest().getParameter("browser")
                    : ConfigReader.getBrowser());

        boolean headless = ConfigReader.isHeadless();

        // Start screen recording before browser launches
        VideoRecorder.startRecording(testName);

        // Initialize ThreadLocal WebDriver instance
        DriverManager.initDriver(browser, headless);

        // Navigate to the application URL
        String applicationUrl = ConfigReader.getUrl();
        DriverManager.getDriver().get(applicationUrl);
    }

    /**
     * Stops video recording and closes WebDriver after each test method.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            VideoRecorder.stopRecording();
        } finally {
            DriverManager.quitDriver();
        }
    }

    /**
     * Convenience method to retrieve current thread's WebDriver instance.
     *
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}
