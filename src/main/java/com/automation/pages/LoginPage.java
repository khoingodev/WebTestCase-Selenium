package com.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.automation.base.BasePage;

/**
 * LoginPage - Page Object cho trang đăng nhập Văn phòng điện tử UTC.
 * Dùng nhiều locator fallback để tăng độ ổn định khi DOM thay đổi.
 */
public class LoginPage extends BasePage {

    // ===== Locator fallback cho username =====
    // Thứ tự ưu tiên: id -> name -> placeholder -> css
    private static final By[] USERNAME_LOCATORS = {
            By.id("username"),
            By.id("Username"),
            By.id("user_name"),
            By.id("UserName"),
            By.name("username"),
            By.name("UserName"),
            By.cssSelector("input[type='text']"),
            By.cssSelector("input[placeholder*='Tên' i]"),
            By.cssSelector("input[placeholder*='username' i]"),
            By.cssSelector("input[formcontrolname='username']"),
            By.cssSelector("input[ng-reflect-name='username']")
    };

    // ===== Locator fallback cho password =====
    private static final By[] PASSWORD_LOCATORS = {
            By.id("password"),
            By.id("Password"),
            By.id("pass"),
            By.name("password"),
            By.name("Password"),
            By.cssSelector("input[type='password']"),
            By.cssSelector("input[placeholder*='Mật' i]"),
            By.cssSelector("input[placeholder*='password' i]"),
            By.cssSelector("input[formcontrolname='password']"),
            By.cssSelector("input[ng-reflect-name='password']")
    };

    // ===== Locator fallback cho nút Đăng nhập =====
    private static final By[] LOGIN_BUTTON_LOCATORS = {
            By.cssSelector("button[type='submit']"),
            By.cssSelector("input[type='submit']"),
            By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'đăng nhập')]"),
            By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'dang nhap')]"),
            By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]"),
            By.cssSelector("button.btn-primary"),
            By.cssSelector("button.login-btn"),
            By.cssSelector(".login-form button")
    };

    // ===== Locator fallback cho checkbox "Ghi nhớ" =====
    private static final By[] REMEMBER_LOCATORS = {
            By.id("rememberMe"),
            By.id("remember"),
            By.name("rememberMe"),
            By.cssSelector("input[type='checkbox']")
    };

    // ===== Locator fallback cho link SSO (e-mail UTC) =====
    private static final By[] SSO_LOCATORS = {
            By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'e-mail')]"),
            By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'email utc')]"),
            By.xpath("//a[contains(@href, 'Email')]"),
            By.xpath("//a[contains(@href, 'email')]")
    };

    // ===== Locator fallback cho "Quên mật khẩu" =====
    private static final By[] FORGOT_LOCATORS = {
            By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'quên')]"),
            By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'quen')]"),
            By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'forgot')]"),
            By.xpath("//a[contains(@href, 'ForgotPassword')]"),
            By.xpath("//a[contains(@href, 'forgot')]"),
            By.xpath("//a[contains(@href, 'reset')]")
    };

    // ===== Locator fallback cho thông báo lỗi =====
    private static final By[] ERROR_LOCATORS = {
            By.cssSelector(".alert-danger"),
            By.cssSelector(".alert-error"),
            By.cssSelector(".error"),
            By.cssSelector(".text-danger"),
            By.cssSelector(".field-validation-error"),
            By.cssSelector("[class*='error' i]"),
            By.cssSelector("[class*='invalid' i]"),
            By.cssSelector(".validation-summary-errors"),
            By.xpath("//*[contains(@class, 'toast') and contains(@class, 'error')]"),
            By.xpath("//div[@role='alert']")
    };

    // ===== PageFactory elements (giữ để tương thích với code cũ) =====
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[contains(., 'Đăng nhập') or contains(., 'Dang nhap') or @type='submit']")
    private WebElement loginButton;

    @FindBy(id = "rememberMe")
    private WebElement rememberCheckbox;

    @FindBy(xpath = "//a[contains(., 'e-mail UTC') or contains(., 'email UTC')]")
    private WebElement ssoLink;

    @FindBy(xpath = "//a[contains(., 'quên mật khẩu') or contains(., 'quen mat khau')]")
    private WebElement forgotPasswordLink;

    @FindBy(css = ".alert-danger, .error, .text-danger, .field-validation-error, [class*='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ===== Lấy element động bằng fallback =====
    private WebElement resolveUsername() {
        return findAny(USERNAME_LOCATORS);
    }

    private WebElement resolvePassword() {
        return findAny(PASSWORD_LOCATORS);
    }

    private WebElement resolveLoginButton() {
        return findAny(LOGIN_BUTTON_LOCATORS);
    }

    private WebElement resolveRemember() {
        return findAny(REMEMBER_LOCATORS);
    }

    // ===== Actions =====
    public void enterUsername(String username) {
        WebElement el = resolveUsername();
        scrollToElement(el);
        safeType(el, username);
    }

    public void enterPassword(String password) {
        WebElement el = resolvePassword();
        scrollToElement(el);
        safeType(el, password);
    }

    public void clickLogin() {
        WebElement el = resolveLoginButton();
        scrollToElement(el);
        safeClick(el);
    }

    public void tickRememberMe() {
        try {
            WebElement el = resolveRemember();
            if (!el.isSelected()) {
                safeClick(el);
            }
        } catch (Exception ignore) {
            // không tìm thấy checkbox -> bỏ qua
        }
    }

    public void clickSsoLink() {
        List<WebElement> links = findAnyList(SSO_LOCATORS);
        if (!links.isEmpty()) {
            scrollToElement(links.get(0));
            safeClick(links.get(0));
        } else {
            throw new RuntimeException("Khong tim thay link SSO (e-mail UTC)");
        }
    }

    public void clickForgotPassword() {
        List<WebElement> links = findAnyList(FORGOT_LOCATORS);
        if (!links.isEmpty()) {
            scrollToElement(links.get(0));
            safeClick(links.get(0));
        } else {
            throw new RuntimeException("Khong tim thay link Quen mat khau");
        }
    }

    /** Thực hiện login đầy đủ. */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /** Lấy thông báo lỗi hiển thị (nếu có), trả về "" nếu không có. */
    public String getErrorMessage() {
        List<WebElement> errors = findAnyList(ERROR_LOCATORS);
        if (errors.isEmpty()) {
            return "";
        }
        try {
            String txt = errors.get(0).getText();
            return txt == null ? "" : txt.trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy text tiêu đề trang login. */
    public String getPageHeading() {
        // Thử lấy thẻ h1/h2 hoặc element có class "brand" / "title"
        List<WebElement> candidates = findAnyList(
                By.cssSelector("h1"), By.cssSelector("h2"),
                By.cssSelector(".brand"), By.cssSelector(".title"),
                By.cssSelector(".login-title"));
        for (WebElement el : candidates) {
            try {
                String txt = el.getText();
                if (txt != null && !txt.trim().isEmpty()) {
                    return txt.trim();
                }
            } catch (Exception ignore) {
                // tiếp element tiếp theo
            }
        }
        return "";
    }

    /** Lấy giá trị hiện tại của ô username. */
    public String getUsernameInputValue() {
        try {
            return resolveUsername().getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy giá trị hiện tại của ô password. */
    public String getPasswordInputValue() {
        try {
            return resolvePassword().getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    /** Nhấn phím Enter khi đang focus vào ô password. */
    public void pressEnterOnPassword() {
        WebElement el = resolvePassword();
        scrollToElement(el);
        try {
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(el));
        } catch (Exception ignore) {
            // bỏ qua, dùng el thô
        }
        el.sendKeys(Keys.ENTER);
    }
}
