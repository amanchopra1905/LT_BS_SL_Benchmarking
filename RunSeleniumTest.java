package extra.benchmarking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.testng.Assert;
import utility.utility.LTHelper;
import utility.utility.WebDriverHelper;

public class RunSeleniumTest extends LTHelper {

  WebDriverHelper wdHelper;
  RemoteWebDriver testDriver;
  RunHerokuTest runHerokuApp = new RunHerokuTest();

  private org.apache.logging.log4j.Logger ltLogger = LogManager.getLogger(RunSeleniumTest.class);
  String ltSampleUrl = "https://lambdatest.github.io/sample-todo-app/";
  String cloudFareSpeedTestUrl = "https://speed.cloudflare.com/";
  private Map<String, String> sessionResults = new HashMap<>();

  // locators start
  static final String HEROKU_CHECKBOX = "http://the-internet.herokuapp.com/checkboxes";
  final String[] firstCheckbox = { CSS, "#checkboxes input:nth-child(3)" };
  final String[] secondCheckbox = { CSS, "#checkboxes input:nth-child(1)" };

  final String[] geoLocation = { XPATH, "//td[text()='Country']//following-sibling::td" };
  final String[] geoLocation2 = { ID, "geotargetly_country_name" };
  final String[] list1 = { NAME, "li1" };
  final String[] list2 = { NAME, "li2" };
  final String[] toDOTextInput = { ID, "sampletodotext" };
  final String[] headingLtSampleSite = { CSS, ".container h2" };
  final String[] addButton = { ID, "addbutton" };
  final String[] enteredTextLoc = { XPATH, "/html/body/div/div/div/ul/li[6]/span" };
  final String[] ltTunnelSiteVarification = { XPATH, "//pre[contains(text(),\"connect: connection refused\")]" };
  final String[] localServerHeading = { XPATH, "//h1[contains(text(),'Directory listing for')]" };
  final String[] currentBrowserTime = { ID, "theTime" };
  final String[] amPmTextWorldTimeServer = { XPATH,
    "//div[@id=\"theTime\" and (contains(text(),AM) or contains(text(), 'PM'))]" };
  // fast.com locators
  final String[] fastComRefresh = { CSS, ".oc-icon-refresh" };
  final String[] fastComDownloadSpeed = { CSS, ".speed-results-container.succeeded" };
  final String[] fastComDownloadSpeedUnit = { CSS, ".speed-units-container.succeeded" };

  final String[] list1RealDevice = { XPATH, "//input[@name='li1']" };
  final String[] list2RealDevice = { XPATH, "//input[@name='li2']" };
  final String[] addButtonRealDevice = { XPATH, "//input[@type=\"submit\"]" };

  // Speed Test Locators
  static final String SPEED_TEST_URL = "https://www.speedtest.net/";
  final String[] goSpeedTest = { CSS, ".start-text" };
  final String[] speedTestComDownloadSpeed = { CSS, ".download-speed:not([data-download-status-value='NaN'])" };
  final String[] speedTestComDownloadSpeedUnit = { CSS, ".result-item-download .result-data-unit" };

  // local test
  static final String LOCAL_URL = "http://localhost:8000/";
  final String[] localSiteHeading = { CSS, "body h1" };
  final String[] cloudFareSpeedTestDownloadBtn = { CSS, ".hj" };
  final String[] cloudFareSpeedTestResumeBtn = { XPATH, "//div[text()='Resume']/parent::button" };
  final String[] cloudFareDownloadSpeed = { XPATH, "(//div[text()='Mbps']//preceding-sibling::div)[1]" };
  final String[] cloudFareUploadSpeed = { XPATH, "(//div[text()='Mbps']//preceding-sibling::div)[2]" };
  final String[] cloudFarePingSpeed = { XPATH, "(//div[text()='ms']//preceding-sibling::div)[1]" };
  final String[] cloudFareJitterSpeed = { XPATH, "(//div[text()='ms']//preceding-sibling::div)[2]" };

  // locators end
  private String downloadSpeed = "";
  private String cloudfareDownloadSpeed;
  private String cloudfareUploadSpeed;
  private String cloudfarePingSpeed;
  private String cloudfareJitterSpeed;

  public String getDownloadSpeed() {
    return downloadSpeed;
  }

  public String getCloudFareDownloadSpeed() {
    return cloudfareDownloadSpeed;
  }

  public String getCloudFareUploadSpeed() {
    return cloudfareUploadSpeed;
  }

  public String getCloudFarePingSpeed() {
    return cloudfarePingSpeed;
  }

  public String getCloudFareJitterSpeed() {
    return cloudfareJitterSpeed;
  }

  public void setTestDriver(RemoteWebDriver testDriver) {
    this.testDriver = testDriver;
    wdHelper = new WebDriverHelper(this.testDriver);
  }

  public void runTestActions(String[] testActions) {
    for (int i = 0; i < testActions.length; i++) {
      runTestAction(testActions[i]);
    }
  }

  public void runTestAction(String testAction) {
    switch (testAction) {
    case "geolocation":
      runGeolocationTestActivity();
      break;
    case "consoleLog":
      runConsoleLogTestActivity();
      break;
    case "timezone":
      runTimezoneTestActivity();
      break;
    case "SpeedTest":
      //      fetchAndSaveNetworkSpeedOfVm();
      break;
    case "realSample":
      runSampleTestActivityRealDevice();
      break;
    case "herokuApp":
      runHerokuApp.setTestDriver(this.testDriver);
      runHerokuApp.runHerokuApp();
      break;
    case "speedTestDotNet":
      //        speedTestViaSpeedtestSite();
      break;
    case "local":
      localTunnelTest();
      break;
    case "bandwidth":
      bandwidth();
      break;
    case "networklog":
    default:
      runSampleTestActivity();
      break;
    }
  }

  public void runGeolocationTestActivity() {
    String country;
    try {
      wdHelper.getURL(GEOLOCATION_TEST_URL);
      country = wdHelper.getText(geoLocation);
    } catch (Exception e) {
      wdHelper.getURL(GEOLOCATION_TEST_URL2);
      try {
        country = wdHelper.getText(geoLocation2);
      } catch (Exception e2) {
        country = "";
      }
    }
    ltLogger.info("geolocation for session id {} is {} ", testDriver.getSessionId(), country);
  }

  public void runConsoleLogTestActivity() {
    try {
      wdHelper.getURL("https://staging.lambdatest.com/blog/");
      TimeUnit.SECONDS.sleep(8);
    } catch (InterruptedException e) {
      ltLogger.info(e.getMessage());
    }
    wdHelper.getURL(ltSampleUrl);
    String consoleMsg = "console log is working fine";
    javascriptExecution("console.error('" + consoleMsg + " via error command" + "')", testDriver);
    javascriptExecution("console.warn('" + consoleMsg + " via warn command" + "')", testDriver);
  }

  public void runTimezoneTestActivity() {
    String timeOffset = executeJSFetchValue("return new Date().getTimezoneOffset();", testDriver);
    ltLogger.info(" timeOffset for given timezone capability is {}", timeOffset);
    Integer browserTimezon = Integer.parseInt(timeOffset);
    Integer browserTimezoneValue = valueOfInteger(browserTimezon);
    String timezoneHour = integerAsStringAppendZeros(browserTimezoneValue / 60, 2);
    String timezoneMiute = integerAsStringAppendZeros(browserTimezoneValue % 60, 2);
    String timezoneValue = timezoneHour + ":" + timezoneMiute;
    if (browserTimezon > 0) {
      timezoneValue = "UTC-" + timezoneValue;
    } else {
      timezoneValue = "UTC+" + timezoneValue;
    }
    ltLogger.info("timezone value for session with session id {} is {}", testDriver.getSessionId(), timezoneValue);
  }

  public void fetchAndSaveNetworkSpeedOfVm() {
    String downSpeed = "0";
    try {
      wdHelper.getURL(NETWORK_SPEEDTEST_URL);
      wdHelper.waitForElement(fastComRefresh, 60);
      downSpeed = wdHelper.getText(fastComDownloadSpeed) + wdHelper.getText(fastComDownloadSpeedUnit);
    } catch (Exception e) {
      ltLogger.info("issue with fast.com. we are unable to calculate speed");
    }
    ltLogger.info("download speed for session with session id {} is {} ", testDriver.getSessionId(), downSpeed);
  }

  public void runSampleTestActivityRealDevice() {
    wdHelper.getURL(ltSampleUrl);
    wdHelper.clickOnElement(list1RealDevice);
    wdHelper.clickOnElement(list2RealDevice);
    wdHelper.sendKeys(toDOTextInput, TODO_TEXT);
    wdHelper.clickOnElement(headingLtSampleSite);
    WebElement ele = wdHelper.getElement(addButtonRealDevice);
    wdHelper.jsClick(ele);
    wdHelper.javascriptExecution("document.getElementById(\"" + addButton[1] + "\").click();");
    String enteredText = wdHelper.getText(enteredTextLoc);
    javascriptExecution("console.error('console log is working fine via error command')", testDriver);
    assert enteredText.equalsIgnoreCase(
      "Yey, Let's add it to list") : "wrong text is shown for sample text. it is not equal to '" + TODO_TEXT + "'";
  }

  public void runSampleTestActivity() {
    wdHelper.getURL(HEROKU_CHECKBOX);
    wdHelper.clickOnElement(firstCheckbox);
    wdHelper.clickOnElement(secondCheckbox);
    wdHelper.clickOnElement(firstCheckbox);
    wdHelper.clickOnElement(secondCheckbox);
    wdHelper.clickOnElement(firstCheckbox);
    wdHelper.clickOnElement(secondCheckbox);
  }

  public void runSampleTestActivityLTtoDoApp() {
    wdHelper.getURL(ltSampleUrl);
    wdHelper.clickOnElement(list1);
    wdHelper.clickOnElement(list2);
    wdHelper.sendKeys(toDOTextInput, TODO_TEXT);
    wdHelper.clickOnElement(headingLtSampleSite);
    wdHelper.clickOnElement(addButton);
    wdHelper.javascriptExecution("document.getElementById(\"" + addButton[1] + "\").click();");
    String enteredText = wdHelper.getText(enteredTextLoc);
    javascriptExecution("console.error('console log is working fine via error command')", testDriver);
    assert enteredText.equalsIgnoreCase(
      "Yey, Let's add it to list") : "wrong text is shown for sample text. it is not equal to '" + TODO_TEXT + "'";
  }

  public void speedTestViaSpeedtestSite() {
    String downSpeed = "";
    try {
      wdHelper.getURL(SPEED_TEST_URL);
      wdHelper.waitForElement(goSpeedTest, 60);
      wdHelper.clickOnElement(goSpeedTest);
      wdHelper.waitForElement(speedTestComDownloadSpeed, 180);
      downSpeed = wdHelper.getText(speedTestComDownloadSpeed) + wdHelper.getText(speedTestComDownloadSpeedUnit);
    } catch (Exception e) {
      ltLogger.info("issue with sppedtest.net. we are unable to calculate speed");
    }
    downloadSpeed = getSpeedInMbps(downSpeed).toString();
  }

  public void localTunnelTest() {
    wdHelper.getURL(LOCAL_URL);
    Assert.assertEquals(wdHelper.getText(localSiteHeading), "Directory listing for /");
  }

  public void bandwidth() {
    wdHelper.getURL(LOCAL_URL);
    Assert.assertEquals(wdHelper.getText(localSiteHeading), "Directory listing for /");
    wdHelper.getURL(cloudFareSpeedTestUrl);
    wdHelper.waitForTime(180);
    cloudfareDownloadSpeed = wdHelper.getText(cloudFareDownloadSpeed);
    cloudfareUploadSpeed = wdHelper.getText(cloudFareUploadSpeed);
    cloudfarePingSpeed = wdHelper.getText(cloudFarePingSpeed);
    cloudfareJitterSpeed = wdHelper.getText(cloudFareJitterSpeed);
  }
}
