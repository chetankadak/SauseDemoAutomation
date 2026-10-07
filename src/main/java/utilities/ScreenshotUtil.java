package utilities;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for capturing and storing screenshots for test reporting and failure analysis.
 */
public final class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "test-output" + File.separator + "screenshots";

    private ScreenshotUtil() {
        // Prevent instantiation
    }

    /**
     * Captures screenshot from current WebDriver instance and saves it to test-output/screenshots/ directory.
     * Also returns the screenshot byte array for Allure attachment.
     *
     * @param testName name of the test method
     * @return byte array of the captured screenshot, or empty byte array on failure
     */
    public static byte[] captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) {
                return new byte[0];
            }

            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);
            File sourceFile = ts.getScreenshotAs(OutputType.FILE);

            File targetDir = new File(SCREENSHOT_DIR);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = sanitizedTestName + "_" + timestamp + ".png";
            File destinationFile = new File(targetDir, fileName);

            FileUtils.copyFile(sourceFile, destinationFile);

            return screenshotBytes;
        } catch (Exception e) {
            System.err.println("Warning: Failed to capture screenshot for test '" + testName + "': " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Captures screenshot and directly attaches it to Allure.
     *
     * @param attachmentName name of attachment in Allure
     * @param testName       test method name
     * @return byte array of screenshot
     */
    public static byte[] captureAndAttachScreenshot(String attachmentName, String testName) {
        byte[] screenshotBytes = captureScreenshot(testName);
        if (screenshotBytes.length > 0) {
            AllureUtility.attachScreenshot(attachmentName, screenshotBytes);
        }
        return screenshotBytes;
    }
}
