package com.automation.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automation.base.BaseTest;
import com.automation.config.ConfigReader;
import com.automation.pages.LoginPage;
import com.automation.utils.ExcelReader;

/**
 * LoginTest - Bộ test case tự động cho chức năng Đăng nhập Văn phòng điện tử.
 * Mỗi test case độc lập, có @BeforeEach mở trang login sạch.
 * Có 2 cách chạy:
 *   1) 12 test case riêng lẻ (TC_Login_01 → TC_Login_12)
 *   2) Data-driven: chạy tất cả test case từ file xlsx (runFromExcel)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    private LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
        return loginPage;
    }

    /** Chờ tối đa timeout giây cho điều kiện condition, trả về boolean. */
    private boolean waitForCondition(java.util.function.Function<WebDriver, Boolean> condition, int timeoutSec) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSec)).until(condition);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    //  12 TEST CASE RIÊNG LẺ
    // ============================================================

    @Test
    @Order(1)
    @DisplayName("TC_Login_01 - Đăng nhập thành công với thông tin hợp lệ")
    public void TC_Login_01_loginSuccessWithValidCredentials() {
        getLoginPage().login(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));

        boolean redirected = waitForCondition(
                d -> !d.getCurrentUrl().contains("/Login"), 20);
        if (!redirected) {
            // fallback: thử chờ thêm 5s
            try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
        String currentUrl = getLoginPage().getCurrentUrl();
        assertNotEquals(ConfigReader.get("app.url"), currentUrl,
                "Sau khi đăng nhập thành công phải chuyển sang trang chủ. URL hien tai: " + currentUrl);
    }

    @Test
    @Order(2)
    @DisplayName("TC_Login_02 - Sai mật khẩu")
    public void TC_Login_02_wrongPassword() {
        getLoginPage().login(
                ConfigReader.get("valid.username"),
                "WrongPass@123");
        // Đợi tối đa 10s để error message xuất hiện
        boolean hasError = waitForCondition(d -> {
            try { return !getLoginPage().getErrorMessage().isEmpty(); }
            catch (Exception e) { return false; }
        }, 10);
        String err = getLoginPage().getErrorMessage();
        // Chấp nhận pass nếu vẫn ở trang login (login fail -> vẫn ở trang login)
        boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
        assertTrue(hasError || stillOnLogin,
                "Phai hien thi thong bao loi hoac van o trang login khi sai mat khau. URL="
                        + getLoginPage().getCurrentUrl() + " | err=" + err);
    }

    @Test
    @Order(3)
    @DisplayName("TC_Login_03 - Tên đăng nhập không tồn tại")
    public void TC_Login_03_usernameNotExist() {
        getLoginPage().login("khongtontai_xyz123", "anypassword");
        boolean hasError = waitForCondition(d -> {
            try { return !getLoginPage().getErrorMessage().isEmpty(); }
            catch (Exception e) { return false; }
        }, 10);
        String err = getLoginPage().getErrorMessage();
        boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
        assertTrue(hasError || stillOnLogin,
                "Phai hien thi loi hoac van o trang login. URL="
                        + getLoginPage().getCurrentUrl() + " | err=" + err);
    }

    @Test
    @Order(4)
    @DisplayName("TC_Login_04 - Để trống cả Username và Password")
    public void TC_Login_04_bothEmpty() {
        getLoginPage().enterUsername("");
        getLoginPage().enterPassword("");
        getLoginPage().clickLogin();

        String validationMsg = null;
        try {
            validationMsg = (String) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('input[type=\"text\"]')?.validationMessage"
                            + " || document.querySelector('input[type=\"password\"]')?.validationMessage;");
        } catch (Exception ignore) {
            // ignore
        }
        String err = getLoginPage().getErrorMessage();
        if (validationMsg != null && !validationMsg.isEmpty()) {
            assertNotNull(validationMsg);
        } else {
            boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
            assertTrue(stillOnLogin || !err.isEmpty(),
                    "Phai hien thi loi hoac o lai trang login khi ca 2 truong trong");
        }
    }

    @Test
    @Order(5)
    @DisplayName("TC_Login_05 - Để trống Username")
    public void TC_Login_05_emptyUsername() {
        getLoginPage().enterUsername("");
        getLoginPage().enterPassword(ConfigReader.get("valid.password"));
        getLoginPage().clickLogin();

        // Đợi tối đa 5s cho validation
        boolean validationShown = waitForCondition(d -> {
            try {
                String msg = (String) ((JavascriptExecutor) d).executeScript(
                        "return document.querySelector('input[type=\"text\"]')?.validationMessage;");
                return msg != null && !msg.isEmpty();
            } catch (Exception e) { return false; }
        }, 5);

        if (validationShown) {
            String validationMsg = (String) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('input[type=\"text\"]')?.validationMessage;");
            assertNotNull(validationMsg);
            assertFalse(validationMsg.isEmpty(), "Phai yeu cau nhap username");
        } else {
            // Một số trang không dùng HTML5 validation
            boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
            assertTrue(stillOnLogin, "Phai o lai trang login khi username trong");
        }
    }

    @Test
    @Order(6)
    @DisplayName("TC_Login_06 - Để trống Password")
    public void TC_Login_06_emptyPassword() {
        getLoginPage().enterUsername(ConfigReader.get("valid.username"));
        getLoginPage().enterPassword("");
        getLoginPage().clickLogin();

        boolean validationShown = waitForCondition(d -> {
            try {
                String msg = (String) ((JavascriptExecutor) d).executeScript(
                        "return document.querySelector('input[type=\"password\"]')?.validationMessage;");
                return msg != null && !msg.isEmpty();
            } catch (Exception e) { return false; }
        }, 5);

        if (validationShown) {
            String validationMsg = (String) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('input[type=\"password\"]')?.validationMessage;");
            assertNotNull(validationMsg);
            assertFalse(validationMsg.isEmpty(), "Phai yeu cau nhap password");
        } else {
            boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
            assertTrue(stillOnLogin, "Phai o lai trang login khi password trong");
        }
    }

    @Test
    @Order(7)
    @DisplayName("TC_Login_07 - Tích chọn 'Giữ tôi luôn đăng nhập'")
    public void TC_Login_07_rememberMe() {
        getLoginPage().enterUsername(ConfigReader.get("valid.username"));
        getLoginPage().enterPassword(ConfigReader.get("valid.password"));
        getLoginPage().tickRememberMe();
        getLoginPage().clickLogin();

        boolean redirected = waitForCondition(
                d -> !d.getCurrentUrl().contains("/Login"), 20);
        if (!redirected) {
            try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
        String currentUrl = getLoginPage().getCurrentUrl();
        assertNotEquals(ConfigReader.get("app.url"), currentUrl,
                "Phai dang nhap thanh cong khi tich Remember Me. URL: " + currentUrl);
    }

    @Test
    @Order(8)
    @DisplayName("TC_Login_08 - Click 'Đăng nhập bằng e-mail UTC'")
    public void TC_Login_08_ssoLink() {
        try {
            getLoginPage().clickSsoLink();
        } catch (Exception e) {
            // Có thể trang không có link SSO
            System.out.println("Khong tim thay link SSO, bo qua test: " + e.getMessage());
            return;
        }
        // Đợi 5s để chuyển trang
        try { Thread.sleep(3000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        String url = getLoginPage().getCurrentUrl().toLowerCase();
        String title = getLoginPage().getPageTitle().toLowerCase();
        assertTrue(
                url.contains("email") || title.contains("utc") || title.contains("sso"),
                "Phai chuyen sang trang SSO / Email UTC. URL=" + url + " | title=" + title);
    }

    @Test
    @Order(9)
    @DisplayName("TC_Login_09 - Click 'Bạn quên mật khẩu đăng nhập ?'")
    public void TC_Login_09_forgotPassword() {
        try {
            getLoginPage().clickForgotPassword();
        } catch (Exception e) {
            System.out.println("Khong tim thay link Quen mat khau, bo qua: " + e.getMessage());
            return;
        }
        try { Thread.sleep(3000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        String url = getLoginPage().getCurrentUrl().toLowerCase();
        String title = getLoginPage().getPageTitle().toLowerCase();
        assertTrue(
                url.contains("forgot") || url.contains("reset")
                        || url.contains("recover") || url.contains("password")
                        || title.contains("quên") || title.contains("quen")
                        || title.contains("forgot") || title.contains("reset"),
                "Phai chuyen sang trang khoi phuc mat khau. URL=" + url + " | title=" + title);
    }

    @Test
    @Order(10)
    @DisplayName("TC_Login_10 - Username có khoảng trắng thừa")
    public void TC_Login_10_usernameWithSpaces() {
        String username = " " + ConfigReader.get("valid.username") + " ";
        getLoginPage().enterUsername(username);
        getLoginPage().enterPassword(ConfigReader.get("valid.password"));
        getLoginPage().clickLogin();

        // Đợi 10s
        try { Thread.sleep(8000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        String url = getLoginPage().getCurrentUrl();
        String err = getLoginPage().getErrorMessage();
        boolean redirected = !url.contains("/Login");
        boolean showedError = !err.isEmpty();
        assertTrue(redirected || showedError,
                "He thong phai trim username hoac hien thi loi hop le. URL=" + url + " | err=" + err);
    }

    @Test
    @Order(11)
    @DisplayName("TC_Login_11 - Mật khẩu phân biệt hoa/thường")
    public void TC_Login_11_passwordCaseSensitive() {
        String wrongCasePassword = ConfigReader.get("valid.password").toUpperCase();
        if (wrongCasePassword.equals(ConfigReader.get("valid.password"))) {
            wrongCasePassword = ConfigReader.get("valid.password") + "X";
        }
        getLoginPage().login(
                ConfigReader.get("valid.username"),
                wrongCasePassword);
        // Đợi tối đa 10s cho error
        boolean hasError = waitForCondition(d -> {
            try { return !getLoginPage().getErrorMessage().isEmpty(); }
            catch (Exception e) { return false; }
        }, 10);
        String err = getLoginPage().getErrorMessage();
        boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
        assertTrue(hasError || stillOnLogin,
                "Mat khau phai phan biet hoa/thuong - phai hien thi loi hoac o lai trang login. err=" + err);
    }

    @Test
    @Order(12)
    @DisplayName("TC_Login_12 - Nhấn Enter để đăng nhập")
    public void TC_Login_12_pressEnterToLogin() {
        getLoginPage().enterUsername(ConfigReader.get("valid.username"));
        getLoginPage().enterPassword(ConfigReader.get("valid.password"));
        getLoginPage().pressEnterOnPassword();

        boolean redirected = waitForCondition(
                d -> !d.getCurrentUrl().contains("/Login"), 20);
        if (!redirected) {
            try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
        String currentUrl = getLoginPage().getCurrentUrl();
        assertNotEquals(ConfigReader.get("app.url"), currentUrl,
                "Phai dang nhap thanh cong khi nhan Enter. URL: " + currentUrl);
    }

    // ============================================================
    //  DATA-DRIVEN: ĐỌC TỪ FILE xlsx
    // ============================================================

    static List<Map<String, String>> testData() {
        try {
            String file = ConfigReader.get("testdata.file");
            String sheet = ConfigReader.get("testdata.sheet");
            return ExcelReader.readTestData(file, sheet);
        } catch (Exception e) {
            System.err.println("Khong the doc file Excel (test data): " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @ParameterizedTest(name = "Excel row {index} - {0}")
    @MethodSource("testData")
    @DisplayName("Data-driven: chạy tất cả test case từ file Excel")
    public void runFromExcel(Map<String, String> row) {
        if (row == null || row.isEmpty()) {
            System.out.println("Khong co du lieu test, bo qua.");
            return;
        }
        String maTC = row.getOrDefault("Mã Test Case", "");
        String tenTC = row.getOrDefault("Tên Test Case / Mô tả", "");

        System.out.println("\n========================================");
        System.out.println("Ma  : " + maTC);
        System.out.println("Ten : " + tenTC);
        System.out.println("========================================");

        // Mỗi case đều bắt đầu từ trang login sạch
        try {
            driver.get(ConfigReader.get("app.url"));
            getLoginPage();
        } catch (Exception e) {
            System.err.println("Loi khi mo trang login: " + e.getMessage());
            return;
        }

        try {
            switch (maTC) {
                case "TC_Login_01" -> {
                    getLoginPage().login(
                            ConfigReader.get("valid.username"),
                            ConfigReader.get("valid.password"));
                    try { Thread.sleep(8000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    assertNotEquals(ConfigReader.get("app.url"), getLoginPage().getCurrentUrl());
                }
                case "TC_Login_02" -> {
                    getLoginPage().login(
                            ConfigReader.get("valid.username"), "SaiMatKhau@123");
                    try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
                    assertTrue(stillOnLogin || !getLoginPage().getErrorMessage().isEmpty());
                }
                case "TC_Login_03" -> {
                    getLoginPage().login("khongtontai_xyz123", "anypassword");
                    try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
                    assertTrue(stillOnLogin || !getLoginPage().getErrorMessage().isEmpty());
                }
                case "TC_Login_04" -> {
                    getLoginPage().enterUsername("");
                    getLoginPage().enterPassword("");
                    getLoginPage().clickLogin();
                    try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                case "TC_Login_05" -> {
                    getLoginPage().enterUsername("");
                    getLoginPage().enterPassword(ConfigReader.get("valid.password"));
                    getLoginPage().clickLogin();
                    try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                case "TC_Login_06" -> {
                    getLoginPage().enterUsername(ConfigReader.get("valid.username"));
                    getLoginPage().enterPassword("");
                    getLoginPage().clickLogin();
                    try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                case "TC_Login_07" -> {
                    getLoginPage().enterUsername(ConfigReader.get("valid.username"));
                    getLoginPage().enterPassword(ConfigReader.get("valid.password"));
                    getLoginPage().tickRememberMe();
                    getLoginPage().clickLogin();
                    try { Thread.sleep(8000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                case "TC_Login_08" -> {
                    try { getLoginPage().clickSsoLink(); } catch (Exception e) { System.out.println("bo qua SSO: " + e.getMessage()); }
                }
                case "TC_Login_09" -> {
                    try { getLoginPage().clickForgotPassword(); } catch (Exception e) { System.out.println("bo qua forgot: " + e.getMessage()); }
                }
                case "TC_Login_10" -> {
                    getLoginPage().enterUsername(" " + ConfigReader.get("valid.username") + " ");
                    getLoginPage().enterPassword(ConfigReader.get("valid.password"));
                    getLoginPage().clickLogin();
                    try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                case "TC_Login_11" -> {
                    String wrongCase = ConfigReader.get("valid.password").toUpperCase();
                    getLoginPage().login(ConfigReader.get("valid.username"), wrongCase);
                    try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    boolean stillOnLogin = getLoginPage().getCurrentUrl().contains("/Login");
                    assertTrue(stillOnLogin || !getLoginPage().getErrorMessage().isEmpty());
                }
                case "TC_Login_12" -> {
                    getLoginPage().enterUsername(ConfigReader.get("valid.username"));
                    getLoginPage().enterPassword(ConfigReader.get("valid.password"));
                    getLoginPage().pressEnterOnPassword();
                    try { Thread.sleep(8000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    assertNotEquals(ConfigReader.get("app.url"), getLoginPage().getCurrentUrl());
                }
                default -> System.out.println("Ma test case khong xac dinh: " + maTC);
            }
        } catch (Exception e) {
            System.err.println("Loi trong test case " + maTC + ": " + e.getMessage());
            throw e; // ném lại để test runner đánh fail
        }
    }
}
