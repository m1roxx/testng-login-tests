package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import utils.ExtentReportManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginTest {

    private static final Logger logger = LogManager.getLogger(LoginTest.class);
    private static ExtentReports extent = ExtentReportManager.getInstance();
    private ExtentTest extentTest;
    private WebDriver driver;
    private static final String BASE_URL = "https://practicetestautomation.com/practice-test-login/";
    private static final String VALID_USERNAME = "student";
    private static final String VALID_PASSWORD = "Password123";

    @BeforeClass
    public void setUp() {
        logger.info("Initializing Chrome WebDriver");
        extentTest = extent.createTest("Login Test");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        logger.info("Chrome WebDriver initialized and window maximized");
        extentTest.log(Status.INFO, "Chrome WebDriver initialized and window maximized");
    }

    @BeforeMethod
    public void openLoginPage() {
        logger.info("Opening login page: {}", BASE_URL);
        extentTest.log(Status.INFO, "Opening login page: " + BASE_URL);
        driver.get(BASE_URL);
        logger.info("Login page opened successfully");
        extentTest.log(Status.INFO, "Login page opened successfully");
    }

    @Test
    public void testSuccessfulLogin() {
        try {
            logger.info("Starting login test");
            extentTest.log(Status.INFO, "Starting login test");

            logger.info("Entering username: {}", VALID_USERNAME);
            extentTest.log(Status.INFO, "Entering username: " + VALID_USERNAME);
            WebElement usernameField = driver.findElement(By.id("username"));
            usernameField.sendKeys(VALID_USERNAME);

            logger.info("Entering password");
            extentTest.log(Status.INFO, "Entering password");
            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.sendKeys(VALID_PASSWORD);

            logger.info("Clicking login button");
            extentTest.log(Status.INFO, "Clicking login button");
            WebElement loginButton = driver.findElement(By.id("submit"));
            loginButton.click();

            logger.info("Verifying login success");
            extentTest.log(Status.INFO, "Verifying login success");
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("logged-in-successfully"),
                    "URL should contain 'logged-in-successfully'");

            logger.info("Login test completed successfully");
            extentTest.log(Status.PASS,
                    MarkupHelper.createLabel("Login test completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            logger.error("Error occurred during login test: {}", e.getMessage(), e);
            extentTest.log(Status.FAIL, "Test failed: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}", result.getName());
            String screenshotPath = captureScreenshot(result.getMethod().getMethodName());
            try {
                extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
                extentTest.addScreenCaptureFromPath(screenshotPath);
                extentTest.log(Status.FAIL, MarkupHelper.createLabel("Test Case Failed", ExtentColor.RED));
            } catch (Exception e) {
                logger.error("Failed to attach screenshot to report: {}", e.getMessage());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test passed: {}", result.getName());
            extentTest.log(Status.PASS, MarkupHelper.createLabel("Test Case Passed", ExtentColor.GREEN));
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing WebDriver");
            extentTest.log(Status.INFO, "Closing WebDriver");
            driver.quit();
            logger.info("WebDriver closed successfully");
            extentTest.log(Status.INFO, "WebDriver closed successfully");
        }
        ExtentReportManager.flushReport();
    }

    private String captureScreenshot(String testMethodName) {
        String screenshotDir = "screenshots";
        try {
            Path dirPath = Paths.get(screenshotDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String screenshotName = testMethodName + "_" + System.currentTimeMillis() + ".png";
            String screenshotPath = screenshotDir + File.separator + screenshotName;

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(screenshotPath);

            Files.copy(sourceFile.toPath(), destinationFile.toPath());
            logger.info("Screenshot saved: {}", screenshotPath);

            return screenshotPath;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
            return "";
        }
    }
}
