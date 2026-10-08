package com.automation.base;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automation.config.ConfigReader;
import com.automation.utils.ScreenshotUtil;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * BaseTest - Class cha cho tất cả test class.
 * Khởi tạo/đóng WebDriver trước và sau mỗi test.
 * Tự động chụp ảnh khi test fail (dùng TestWatcher).
 */
public class BaseTest implements TestWatcher {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private String currentTestName = "";

    @BeforeEach
    public void setUp(org.junit.jupiter.api.TestInfo testInfo) {
        currentTestName = testInfo.getDisplayName();
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");

        switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                // Tắt các dịch vụ nền có thể gây treo/timeout
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-infobars");
                // Tắt CDP (Chrome DevTools Protocol) để tránh warning CDP version mismatch
                options.addArguments("--disable-features=DevTools");
                // Kích thước cố định thay vì --start-maximized (ổn định hơn khi headless)
                options.addArguments("--window-size=1366,768");
                options.addArguments("--remote-allow-origins=*");
                options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);

                driver = new ChromeDriver(options);
                // Set size thay vì maximize để tránh xung đột màn hình
                try {
                    driver.manage().window().setSize(new Dimension(1366, 768));
                } catch (Exception ignore) {
                    // một số môi trường headless không cho setSize, bỏ qua
                }
            }
            default -> throw new RuntimeException("Browser chua duoc ho tro: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));
        // Dùng Duration cho script timeout — tránh treo vô tận khi page load chậm
        driver.manage().timeouts().scriptTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));
        // Bỏ qua lỗi JS console, không ném exception khi page có lỗi
        try {
            ((JavascriptExecutor) driver).executeScript("window.onerror = function(){return true;};");
        } catch (Exception ignore) {
            // ignore
        }

        driver.get(ConfigReader.get("app.url"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Loi khi quit driver: " + e.getMessage());
            }
        }
    }

    // ===== TestWatcher: chụp ảnh khi test fail =====
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (driver != null) {
            String name = context.getDisplayName().replaceAll("[^a-zA-Z0-9_\\-]", "_");
            ScreenshotUtil.takeScreenshot(driver, name);
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // no-op
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        // no-op
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        // no-op
    }
}
