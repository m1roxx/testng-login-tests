package utils;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public final class DriverFactory {

    private static final String REMOTE_PROP = "RUN_REMOTE";
    private static final String HUB_URL = "https://hub-cloud.browserstack.com/wd/hub";
    private static final String ENV_USERNAME = "BROWSERSTACK_USERNAME";
    private static final String ENV_ACCESS_KEY = "BROWSERSTACK_ACCESS_KEY";

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        if (isRemoteRun()) {
            return createRemoteDriver();
        }
        return new ChromeDriver();
    }

    private static boolean isRemoteRun() {
        return "true".equalsIgnoreCase(System.getProperty(REMOTE_PROP, ""));
    }

    private static WebDriver createRemoteDriver() {
        String username = System.getenv(ENV_USERNAME);
        String accessKey = System.getenv(ENV_ACCESS_KEY);

        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                    "BrowserStack remote run requires " + ENV_USERNAME + " to be set. " +
                            "Example: RUN_REMOTE=true BROWSERSTACK_USERNAME=your_user BROWSERSTACK_ACCESS_KEY=your_key mvn test -Dtest=LoginTest");
        }
        if (accessKey == null || accessKey.isBlank()) {
            throw new IllegalStateException(
                    "BrowserStack remote run requires " + ENV_ACCESS_KEY + " to be set. " +
                            "Example: RUN_REMOTE=true BROWSERSTACK_USERNAME=your_user BROWSERSTACK_ACCESS_KEY=your_key mvn test -Dtest=LoginTest");
        }

        URL hubUrl;
        try {
            hubUrl = new URL("https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack hub URL", e);
        }

        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        capabilities.setCapability("bstack:options", Map.<String, Object>of(
                "os", "Windows",
                "osVersion", "11",
                "projectName", "Assignment 6",
                "buildName", "BrowserStack Build 1",
                "sessionName", "LoginTest"
        ));

        return new RemoteWebDriver(hubUrl, capabilities);
    }
}
