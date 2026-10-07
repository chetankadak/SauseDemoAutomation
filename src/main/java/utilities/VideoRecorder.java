package utilities;

import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

/**
 * Screen recorder utility utilizing Monte Media Library.
 * Saves recorded AVI videos to test-output/videos/ and attaches them to Allure reports.
 * Engineered so that any video recording error or graphical limitation NEVER fails the actual test execution.
 */
public final class VideoRecorder {

    private static final String VIDEO_DIR = "test-output" + File.separator + "videos";
    private static final ThreadLocal<ScreenRecorder> screenRecorderThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> currentTestNameThreadLocal = new ThreadLocal<>();

    private VideoRecorder() {
        // Prevent instantiation
    }

    /**
     * Custom Monte ScreenRecorder that names the output file with the test name and timestamp.
     */
    private static class SpecializedScreenRecorder extends ScreenRecorder {
        private final String customFileName;

        public SpecializedScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea,
                                         Format fileFormat, Format screenFormat,
                                         Format mouseFormat, Format audioFormat,
                                         File folder, String customFileName) throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, folder);
            this.customFileName = customFileName;
        }

        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            }
            return new File(movieFolder, customFileName + ".avi");
        }
    }

    /**
     * Starts screen recording for the given test if video recording is enabled and display is available.
     *
     * @param testName name of test method
     */
    public static void startRecording(String testName) {
        if (!ConfigReader.isVideoRecordingEnabled()) {
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Notice: Screen recording skipped for test '" + testName + "' because environment is headless.");
            return;
        }

        try {
            File movieDir = new File(VIDEO_DIR);
            if (!movieDir.exists()) {
                movieDir.mkdirs();
            }

            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle captureArea = new Rectangle(0, 0, screenSize.width, screenSize.height);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = sanitizedTestName + "_" + timestamp;

            ScreenRecorder recorder = new SpecializedScreenRecorder(
                    gc,
                    captureArea,
                    new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null,
                    movieDir,
                    fileName
            );

            recorder.start();
            screenRecorderThreadLocal.set(recorder);
            currentTestNameThreadLocal.set(sanitizedTestName);
        } catch (Throwable t) {
            System.err.println("Warning: Video recording could not be started for test '" + testName + "': " + t.getMessage());
        }
    }

    /**
     * Stops screen recording, attaches video to Allure, and cleans up.
     * Video failure will NEVER fail the actual test.
     *
     * @return File of recorded video, or null if no recording was active
     */
    public static File stopRecording() {
        ScreenRecorder recorder = screenRecorderThreadLocal.get();
        String testName = currentTestNameThreadLocal.get();

        if (recorder == null) {
            return null;
        }

        File recordedFile = null;
        try {
            recorder.stop();
            List<File> createdFiles = recorder.getCreatedMovieFiles();
            if (createdFiles != null && !createdFiles.isEmpty()) {
                recordedFile = createdFiles.get(0);
                if (recordedFile.exists()) {
                    AllureUtility.attachVideo("Execution Video: " + (testName != null ? testName : "Test"), recordedFile);
                }
            }
        } catch (Throwable t) {
            System.err.println("Warning: Error occurred while stopping video recording: " + t.getMessage());
        } finally {
            screenRecorderThreadLocal.remove();
            currentTestNameThreadLocal.remove();
        }

        return recordedFile;
    }
}
