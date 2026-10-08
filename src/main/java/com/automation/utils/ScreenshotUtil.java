package com.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtil - Chụp ảnh màn hình khi test fail.
 */
public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "screenshots/";

    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            File dir = new File(SCREENSHOT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(SCREENSHOT_DIR + testName + "_" + timestamp + ".png");
            FileHandler.copy(src, dest);
            System.out.println(">> Screenshot saved: " + dest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Loi chup man hinh: " + e.getMessage());
        }
    }
}
