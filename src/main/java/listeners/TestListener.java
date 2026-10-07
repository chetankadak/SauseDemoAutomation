package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utilities.AllureUtility;
import utilities.ConfigReader;
import utilities.ScreenshotUtil;

/**
 * Custom TestNG Listener implementing ITestListener.
 * Automatically captures screenshots on test failure, logs execution lifecycle events,
 * and attaches failure details and stack traces to Allure reports.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("==================================================");
        System.out.println("Starting Test Suite: " + context.getName());
        System.out.println("Total tests to be executed: " + context.getAllTestMethods().length);
        System.out.println("==================================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("==================================================");
        System.out.println("Finished Test Suite: " + context.getName());
        System.out.println("Passed tests: " + context.getPassedTests().size());
        System.out.println("Failed tests: " + context.getFailedTests().size());
        System.out.println("Skipped tests: " + context.getSkippedTests().size());
        System.out.println("==================================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("--> Starting Test: " + testName + " | Thread ID: " + Thread.currentThread().threadId());
        AllureUtility.attachText("Test Started", "Starting execution of: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("[PASS] Test Passed: " + testName);
        AllureUtility.attachText("Test Result", "Test PASSED: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.err.println("[FAIL] Test Failed: " + testName);

        if (ConfigReader.isScreenshotOnFailure()) {
            ScreenshotUtil.captureAndAttachScreenshot("Failure Screenshot - " + testName, testName);
        }

        if (result.getThrowable() != null) {
            AllureUtility.attachFailureDetails(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("[SKIP] Test Skipped: " + testName);

        if (result.getThrowable() != null) {
            AllureUtility.attachText("Skip Reason", "Test was skipped due to: " + result.getThrowable().getMessage());
            AllureUtility.attachFailureDetails(result.getThrowable());
        }
    }
}
