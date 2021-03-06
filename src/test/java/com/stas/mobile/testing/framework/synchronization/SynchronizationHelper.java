
package com.stas.mobile.testing.framework.synchronization;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.stas.mobile.testing.framework.device.controls.BaseDeviceControl;
import com.stas.mobile.testing.framework.queryhelpers.DeviceElementQueryHelper;
import com.stas.mobile.testing.framework.util.TestHelper;
import com.stas.mobile.testing.framework.util.environment.EnvironmentUtil;
import com.stas.mobile.testing.framework.util.logger.LogController;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class SynchronizationHelper
{
    private DeviceElementQueryHelper _queryHelper;
    private AppiumDriver _driver;
    private LogController _logger = new LogController(SynchronizationHelper.class);
    private EnvironmentUtil _envUtil = EnvironmentUtil.getInstance();

    public SynchronizationHelper(AppiumDriver driver)
    {
        _queryHelper = new DeviceElementQueryHelper(driver);
        _driver = driver;
    }

    public int getElementCount(String selector)
    {
        List<MobileElement> elements = _queryHelper.findElements(selector);
        return elements.size();
    }

    public void waitForElementToAppear(String selector)
    {
        long end = TestHelper.getCurrentGMT6Time() + 30000L;

        WebElement element = null;
        boolean nseLogMessage = true;
        while (TestHelper.getCurrentGMT6Time() < end)
        {
            try
            {
                element = _queryHelper.findElement(selector);
                if (element == null)
                {
                    suspend(2000);
                    _logger.info("Waiting for " + selector + " to appear");
                }
                else
                {
                    break;
                }
            }
            catch (NoSuchElementException nse)
            {
                if (nseLogMessage)
                {
                    _logger.debug("Element [" + selector + "] wasn't located, waiting and rerunning loop");

                    nseLogMessage = false;
                }
            }
        }
        if (element == null)
        {
            Assert.fail("Element [" + selector + "] was not found before timeout.");
        }
    }

    public String waitForOneOfTwoElementsToAppear(String selector1, String selector2)
    {
        long end = TestHelper.getCurrentGMT6Time() + 30000L;

        WebElement element1 = null;
        WebElement element2 = null;
        boolean nseLogMessage = true;
        while (TestHelper.getCurrentGMT6Time() < end)
        {
            try
            {
                element1 = _queryHelper.findElement(selector1);
                if (element1 != null)
                {
                    return selector1;
                }
            }
            catch (NoSuchElementException localNoSuchElementException)
            {
            }
            try
            {
                element2 = _queryHelper.findElement(selector2);
                if (element2 != null)
                {
                    return selector2;
                }
            }
            catch (NoSuchElementException localNoSuchElementException1)
            {
            }
            if (nseLogMessage)
            {
                suspend(500);
                _logger.debug("Element [" + selector1 + " or " + selector2 + "] wasn't located, waiting and rerunning loop");

                nseLogMessage = false;
            }
        }
        if ((element1 == null) && (element2 == null))
        {
            Assert.fail("Element [" + selector1 + "] or Element [" + selector2 + "] was not found before timeout.");
        }
        return null;
    }

    public void waitForElementToAppear(BaseDeviceControl element)
    {
        long end = TestHelper.getCurrentGMT6Time() + 30000L;

        boolean displayed = true;
        while (TestHelper.getCurrentGMT6Time() < end)
        {
            if (element.isDisplayed())
            {
                displayed = true;
                break;
            }
            displayed = false;
        }
        if (!displayed)
        {
            Assert.fail(String.format("Element [%s] was not found before timeout.", new Object[]{element

                .getSelector()}));
        }
    }

    public void waitForElementToDisappear(String selector)
    {
        long end = TestHelper.getCurrentGMT6Time() + 30000L;

        WebElement element = null;
        for (;;)
        {
            if (TestHelper.getCurrentGMT6Time() < end)
            {
                try
                {
                    element = _queryHelper.findElement(selector);
                    if ((element != null) && (element.isDisplayed()))
                    {
                        suspend(2000);
                        _logger.info("Waiting for " + selector + " to disappear");
                    }
                    else if ((element != null) && (element.isDisplayed()))
                    {
                        Assert.fail("Element [" + selector + "] was still found");
                    }
                }
                catch (NoSuchElementException nse)
                {
                    _logger.debug("Element [" + selector + "] wasn't located");
                }
            }
        }
    }

    public void waitForTextToAppear(final MobileElement element, final String text)
    {
        WebDriverWait wait = new WebDriverWait(_driver, 30000L);
        boolean result;
        try
        {
            wait.until((ExpectedCondition) dr -> Boolean.valueOf((element.isDisplayed()) &&
                (element.getText().contains(text))));
            result = true;
        }
        catch (Exception e)
        {
            result = false;
        }
        if (!result)
        {
            Assert.fail("Element does not have the expected text : " + text);
        }
    }

    public void waitForTextToDisappear(final MobileElement element, final String text)
    {
        WebDriverWait wait = new WebDriverWait(_driver, 30000L);
        boolean result;
        try
        {
            wait.until(ExpectedConditions.not(dr -> Boolean.valueOf((element.isDisplayed()) &&
                (element.getText().contains(text)))));
            result = true;
        }
        catch (Exception e)
        {
            result = false;
        }
        if (!result)
        {
            Assert.fail("Element still has the given text : " + text);
        }
    }

    public void suspend(int millis)
    {
        _logger.debug("Suspending thread " + millis + "ms.");
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isElementDisplayed(String selector)
    {
        return isElementDisplayed(selector, 1000);
    }

    public boolean isElementDisplayed(String selector, int timeout)
    {
        long end = TestHelper.getCurrentGMT6Time() + timeout;
        WebElement element = null;
        boolean nseLogMessage = true;
        while (TestHelper.getCurrentGMT6Time() < end)
        {
            try
            {
                element = _queryHelper.findElement(selector);
                if (element == null)
                {
                    suspend(500);
                    _logger.info("Waiting for " + selector + " to appear");
                }
                else
                {
                    break;
                }
            }
            catch (NoSuchElementException nse)
            {
                if (nseLogMessage)
                {
                    _logger.debug("Element [" + selector + "] wasn't located, waiting and rerunning loop");

                    nseLogMessage = false;
                }
            }
        }
        if (element == null)
        {
            return false;
        }
        if (!element.isDisplayed())
        {
            return false;
        }
        return true;
    }
}
