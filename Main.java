package com.dbeef.webscraper.ogame;

import lombok.extern.java.Log;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Log
public class Main {
    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        log.info("Started application.");
        FirefoxProfile profile = new FirefoxProfile();

        log.info("Loading geckodriver.");
        System.setProperty("webdriver.gecko.driver", "/home/dbeef/Downloads/geckodriver");
        profile.setPreference("webdriver.gecko.driver", "/home/dbeef/Downloads/geckodriver");

        //
        log.info("Loading AdBlock.");
        File f = new File("/home/dbeef/addon-1865-latest.xpi");
        profile.addExtension(f);

        log.info("Setting preferences.");
        //0 is for "no proxy"
        profile.setPreference("network.proxy.type", 0);
        profile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/html");


        log.info("Opening Firefox.");
        Dimension dimension = new Dimension(1200, 800);

        WebDriver driver = new FirefoxDriver(profile);

        driver.manage().window().setSize(dimension);
        driver.manage().window().setPosition(new Point(10, 10));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get("https://pl.ogame.gameforge.com/");

        WebDriverWait wait = new WebDriverWait(driver, 40);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("loginBtn")));

        log.info("Clicking login button.");
        WebElement preLoginButton = driver.findElement(By.id("loginBtn"));
        preLoginButton.click();
        log.info("Username input.");
        WebElement textFieldLogin = driver.findElement(By.id("usernameLogin"));
        textFieldLogin.sendKeys("lunqrav@gmail.com");
        log.info("Password input.");
        WebElement textFieldPassword = driver.findElement(By.id("passwordLogin"));
        textFieldPassword.sendKeys("kosmosgwiazdystatki");
        log.info("Selecting uni.");
        WebElement select = driver.findElement(By.id("serverLogin"));
        Select dropDown = new Select(select);
        String selected = dropDown.getFirstSelectedOption().getText();

        ArrayList<WebElement> options = (ArrayList<WebElement>) dropDown.getOptions();
        for (WebElement option : options) {
            if (option.getText().equals("Oberon")) {
                option.click(); //select option here;
            }
        }

        log.info("Submitting.");
        WebElement buttonLogin = driver.findElement(By.id("loginSubmit"));
        buttonLogin.click();

        WebElement menuTable = driver.findElement(By.id("menuTable"));
        while (!menuTable.isDisplayed())
            Thread.sleep(1000);

        log.info("Logged in.");
        log.info("Moving to resources.");

        driver.get("https://s141-pl.ogame.gameforge.com/game/index.php?page=resources");

        Thread.sleep(3000);
        //http://darrellgrainger.blogspot.com/2012/06/staleelementexception.html
        //http://selenium-python.readthedocs.io/locating-elements.html
        //http://stackoverflow.com/questions/34115825/when-and-how-i-can-locate-element-by-tagname-using-selenium-webdriver-please-ex
        log.info("Searching for the power plant button.");

        ArrayList<WebElement> buildOptions = null;
        boolean result = false;
        int attempts = 0;
        while (attempts < 2) {
            try {
                buildOptions = (ArrayList<WebElement>) driver.findElements(By.className("buildingimg"));
                for (WebElement option : buildOptions) {
                    if (option.getText().contains("Elektrownia słoneczna")) {
                        option.click(); //select option here;
                    }

                }
                result = true;
                break;
            } catch (StaleElementReferenceException e) {
            }
            attempts++;
        }
        log.info("Found the powerplant button - " + result);

        Thread.sleep(3000);
        if(result) {
            log.info("Now clicking the powerplant button.");

            attempts = 0;
            result = false;
            while (attempts < 2) {
                try {
                    WebElement build = driver.findElement(By.className("build-it"));
                    build.click();
                    result = true;
                    break;
                } catch (StaleElementReferenceException e) {
                }
                attempts++;
            }
        }
        log.info("Exiting.");
    }
}
