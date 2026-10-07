package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Reusable utility for attaching screenshots, videos, text, logs, and stack traces to Allure reports.
 */
public final class AllureUtility {

    private AllureUtility() {
        // Prevent instantiation
    }

    /**
     * Attaches screenshot byte array to Allure report using @Attachment annotation.
     *
     * @param name            Attachment name
     * @param screenshotBytes Raw screenshot bytes
     * @return screenshot byte array
     */
    @Attachment(value = "{0}", type = "image/png")
    public static byte[] attachScreenshot(String name, byte[] screenshotBytes) {
        return screenshotBytes;
    }

    /**
     * Attaches plain text / logs to Allure report using @Attachment annotation.
     *
     * @param name    Attachment name
     * @param message Text content to attach
     * @return message text
     */
    @Attachment(value = "{0}", type = "text/plain")
    public static String attachText(String name, String message) {
        return message;
    }

    /**
     * Attaches a video file (e.g. .avi or .mp4) to the Allure report.
     *
     * @param name      Attachment name
     * @param videoFile Recorded video file
     */
    public static void attachVideo(String name, File videoFile) {
        if (videoFile != null && videoFile.exists()) {
            try {
                byte[] videoBytes = FileUtils.readFileToByteArray(videoFile);
                Allure.addAttachment(name, "video/avi", new ByteArrayInputStream(videoBytes), "avi");
            } catch (IOException e) {
                System.err.println("Warning: Failed to attach video to Allure report: " + e.getMessage());
            }
        }
    }

    /**
     * Attaches arbitrary file content with custom MIME type.
     *
     * @param name    Attachment name
     * @param type    MIME type (e.g. "application/json", "text/plain")
     * @param content Raw byte content
     */
    public static void attachFile(String name, String type, byte[] content) {
        if (content != null) {
            Allure.addAttachment(name, type, new ByteArrayInputStream(content), "");
        }
    }

    /**
     * Captures and attaches full failure details and stack trace to Allure.
     *
     * @param throwable Caught exception/error
     */
    @Attachment(value = "Failure Details & Stacktrace", type = "text/plain")
    public static String attachFailureDetails(Throwable throwable) {
        if (throwable == null) {
            return "No exception details available.";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
