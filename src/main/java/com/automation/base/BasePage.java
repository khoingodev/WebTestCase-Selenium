package com.automation.base;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * BasePage - Class cha cho tất cả Page Object.
 * Cung cấp các hàm tiện ích: wait, click, type, getText, scroll, js-executor.
 * Mọi thao tác đều có timeout giới hạn, tránh treo vô tận.
 */
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    /**
     * Thử tìm element bằng danh sách locator, trả về WebElement đầu tiên tìm thấy.
     * Nếu không tìm thấy locator nào, ném RuntimeException với thông tin rõ ràng.
     */
    protected WebElement findAny(By... locators) {
        for (By loc : locators) {
            try {
                List<WebElement> elements = driver.findElements(loc);
                if (!elements.isEmpty()) {
                    return elements.get(0);
                }
            } catch (Exception ignore) {
                // tiếp tục thử locator tiếp theo
            }
        }
        String tried = java.util.Arrays.stream(locators)
                .map(By::toString).collect(Collectors.joining(" | "));
        throw new RuntimeException("Khong tim thay element voi cac locator: " + tried);
    }

    /**
     * Thử tìm NHIỀU element khả dĩ (kết quả nhiều phần tử), trả về list không rỗng đầu tiên.
     */
    protected List<WebElement> findAnyList(By... locators) {
        for (By loc : locators) {
            try {
                List<WebElement> elements = driver.findElements(loc);
                if (!elements.isEmpty()) {
                    return elements;
                }
            } catch (Exception ignore) {
                // tiếp tục
            }
        }
        return java.util.Collections.emptyList();
    }

    /** Chờ element có thể hiển thị (tối đa theo timeout đã set). */
    public WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /** Chờ element có trong DOM (không cần visible) — nhanh hơn visibility. */
    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /** Chờ element có thể click. */
    public WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /** Click an toàn (chờ clickable rồi mới click). Nếu click thường fail, fallback JS click. */
    public void safeClick(WebElement element) {
        try {
            waitForClickable(element).click();
        } catch (Exception e) {
            // Fallback: click bằng JS
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception e2) {
                throw new RuntimeException("Khong the click element: " + e2.getMessage(), e2);
            }
        }
    }

    /**
     * Nhập text an toàn. Thử clear + sendKeys; nếu thất bại, fallback JS.
     * Tránh treo vô tận: dùng wait ngắn cho visibility.
     */
    public void safeType(WebElement element, String text) {
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            el = element; // thử dùng element thô
        }
        try {
            el.clear();
        } catch (Exception ignore) {
            // một số element không clear được, bỏ qua
        }
        if (text != null && !text.isEmpty()) {
            try {
                el.sendKeys(text);
            } catch (Exception e) {
                // Fallback: set value bằng JS + fire event
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];"
                                + " arguments[0].dispatchEvent(new Event('input',{bubbles:true}));"
                                + " arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                        el, text);
            }
        }
    }

    /** Lấy text sau khi đợi visible. Nếu timeout trả về chuỗi rỗng. */
    public String getText(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy text của element đầu tiên khớp với bất kỳ locator nào. Trả về "" nếu không thấy. */
    public String getTextByAny(By... locators) {
        List<WebElement> elements = findAnyList(locators);
        if (elements.isEmpty()) {
            return "";
        }
        try {
            return elements.get(0).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy title trang hiện tại. */
    public String getPageTitle() {
        try {
            return driver.getTitle();
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy URL hiện tại. */
    public String getCurrentUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "";
        }
    }

    /** Cuộn đến element. */
    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);
        } catch (Exception ignore) {
            // ignore
        }
    }

    /** Thực thi JS, trả về null nếu fail. */
    public Object executeJS(String script, Object... args) {
        try {
            return ((JavascriptExecutor) driver).executeScript(script, args);
        } catch (Exception e) {
            return null;
        }
    }

    /** Chờ URL chứa 1 chuỗi cụ thể (tối đa timeout đã set). */
    public void waitForUrlContains(String fragment) {
        try {
            wait.until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException ignore) {
            // không ném, để test tự kiểm tra
        }
    }

    /** Chờ URL KHÔNG còn chứa 1 chuỗi cụ thể. */
    public void waitForUrlNotContains(String fragment) {
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains(fragment)));
        } catch (TimeoutException ignore) {
            // không ném
        }
    }
}
