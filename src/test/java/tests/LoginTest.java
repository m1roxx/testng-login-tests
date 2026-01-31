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
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;
import utils.DriverFactory;
import utils.ExcelReader;
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

    @BeforeClass
    public void setUp() {
        logger.info("Initializing WebDriver");
        extentTest = extent.createTest("Login Test");
        driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
        logger.info("WebDriver initialized and window maximized");
        extentTest.log(Status.INFO, "WebDriver initialized and window maximized");
    }

    @BeforeMethod
    public void openLoginPage() {
        logger.info("Opening login page: {}", BASE_URL);
        extentTest.log(Status.INFO, "Opening login page: " + BASE_URL);
        driver.get(BASE_URL);
        logger.info("Login page opened successfully");
        extentTest.log(Status.INFO, "Login page opened successfully");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        return ExcelReader.readSheet("testdata.xlsx", "Login");
    }

    @Test(dataProvider = "loginData")
    public void testLogin(String username, String password, String expectedResult) {
        try {
            logger.info("Starting login test for user: {}", username);
            extentTest.log(Status.INFO, "Starting login test for user: " + username);

            logger.info("Entering username: {}", username);
            extentTest.log(Status.INFO, "Entering username: " + username);
            WebElement usernameField = driver.findElement(By.id("username"));
            usernameField.sendKeys(username);

            logger.info("Entering password");
            extentTest.log(Status.INFO, "Entering password");
            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.sendKeys(password);

            logger.info("Clicking login button");
            extentTest.log(Status.INFO, "Clicking login button");
            WebElement loginButton = driver.findElement(By.id("submit"));
            loginButton.click();

            String currentUrl = driver.getCurrentUrl();
            boolean isSuccess = "success".equalsIgnoreCase(expectedResult.trim());

            if (isSuccess) {
                logger.info("Verifying login success");
                extentTest.log(Status.INFO, "Verifying login success");
                Assert.assertTrue(currentUrl.contains("logged-in-successfully"),
                        "URL should contain 'logged-in-successfully'");
                logger.info("Login test completed successfully");
                extentTest.log(Status.PASS,
                        MarkupHelper.createLabel("Login test completed successfully", ExtentColor.GREEN));
            } else {
                logger.info("Verifying login failure");
                extentTest.log(Status.INFO, "Verifying login failure");
                Assert.assertFalse(currentUrl.contains("logged-in-successfully"),
                        "URL should not contain 'logged-in-successfully' for failed login");
                logger.info("Login failure verified as expected");
                extentTest.log(Status.PASS,
                        MarkupHelper.createLabel("Login failure verified as expected", ExtentColor.GREEN));
            }
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
