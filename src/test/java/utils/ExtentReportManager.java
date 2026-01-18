package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final String REPORT_PATH = "test-output/ExtentReport.html";

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static void createInstance() {
        extent = new ExtentReports();

        File reportDir = new File("test-output");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
        sparkReporter.config().setDocumentTitle("Test Automation Report");
        sparkReporter.config().setReportName("Login Test Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}
