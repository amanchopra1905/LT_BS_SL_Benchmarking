package extra.benchmarking;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.http.ClientConfig;
import testrunner.testrunner.EnvSetup;
import utility.utility.ApiHelper;
import utility.utility.Constant;
import utility.utility.LTHelper;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SLTest extends LTHelper {
  public static final String SAUCE_USERNAME = System.getProperty("SAUCE_USERNAME");
  public static final String SAUCE_ACCESS_KEY = System.getProperty("SAUCE_ACCESS_KEY");
  public static final String URL = "https://" + SAUCE_USERNAME + ":" + SAUCE_ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";
  private RemoteWebDriver driver;
  private String driverStartTime;
  private String driverQuitTime;
  private String testRunTime;
  private String testName;
  Map<String, String> gCap;
  ApiHelper apiH = new ApiHelper();
  SessionId sessionId;

  public ChromeOptions getCaps() {

    ChromeOptions browserOptions = new ChromeOptions();

    browserOptions.setPlatformName("Windows 10");
    browserOptions.setBrowserVersion("latest");
    browserOptions.setAcceptInsecureCerts(true);

    HashMap<String, Object> slOptions = new HashMap<>();
    DesiredCapabilities caps = new DesiredCapabilities();
    slOptions.put("browserName", "Chrome");
    slOptions.put("os", "Windows 10");
    slOptions.put("version", "latest");
    slOptions.put("build", "Benchmarking Build" + EnvSetup.JENKINS_JOB_IDENTIFIER.get());
    slOptions.put("name", "Benchmarking Test" + EnvSetup.JENKINS_JOB_IDENTIFIER.get());
    slOptions.put("screenResolution", "1920x1080");
    slOptions.put("extendedDebugging", true);
    slOptions.put("idleTimeout", 300);
    browserOptions.setCapability("sauce:options", slOptions);
    testName = slOptions.get("name").toString();
    caps.setCapability("sauce:options", slOptions);
    return browserOptions;
  }

  public void setDriver() {
    StopWatch driverStart = new StopWatch();
    ClientConfig config = ClientConfig.defaultConfig().connectionTimeout(Duration.ofMinutes(20))
      .readTimeout(Duration.ofMinutes(20));
    driverStart.start();
    driver = (RemoteWebDriver) RemoteWebDriver.builder().oneOf(getCaps()).address(URL).config(config).build();
    driverStart.stop();
    sessionId = driver.getSessionId();
    driverStartTime = String.valueOf(driverStart.getTime() / 1000.00);
  }

  public RemoteWebDriver getTestDriver() {
    return driver;
  }

  // This method accepts the status, reason and WebDriver instance and marks the
  // test on BrowserStack
  public void markTestStatus(String status, WebDriver driver) {
    JavascriptExecutor jse = (JavascriptExecutor) driver;
    jse.executeScript("sauce:job-result=" + status);
  }

  public void quitDriver() {
    StopWatch driverQuit = new StopWatch();
    driverQuit.start();
    this.driver.quit();
    driverQuit.stop();
    driverQuitTime = String.valueOf(driverQuit.getTime() / 1000.00);
  }

  public void addSLTestValuesToCSVAndDB(String downloadSpeedStr, String cloudfareDownloadSpeed,
    String cloudfareUploadSpeed, String cloudfarePingSpeed, String cloudfareJitterSpeed) {
    double downloadSpeed = 0;
    if (downloadSpeedStr != null && !downloadSpeedStr.equalsIgnoreCase("")) {
      downloadSpeed = Double.parseDouble(downloadSpeedStr);
    }
    String[] csvRowValue = new String[] { testName, "Saucelabs", "add it manually", driverStartTime, testRunTime,
      driverQuitTime, getSessionStatus(), sessionId.toString(), "", String.valueOf(downloadSpeed),
      String.valueOf(cloudfareDownloadSpeed), String.valueOf(cloudfareUploadSpeed), String.valueOf(cloudfarePingSpeed),
      String.valueOf(cloudfareJitterSpeed) };
    addValuesInCSVFile(EnvSetup.JENKINS_JOB_IDENTIFIER.get() + "_SL_" + BENCHMARKING_STATICS_FILE_NAME,
      BENCHMARKING_STATICS_CSV_HEADER, csvRowValue);
    Map<String, String> jsonObjectPayload = new LinkedHashMap<>();
    jsonObjectPayload.put("TestName", testName);
    jsonObjectPayload.put("Provider", "Saucelabs");
    jsonObjectPayload.put("Region", "add it manually");
    jsonObjectPayload.put("Setup Time", driverStartTime);
    jsonObjectPayload.put("Test Time", testRunTime);
    jsonObjectPayload.put("Tear Time", driverQuitTime);
    jsonObjectPayload.put("Status", getSessionStatus());
    jsonObjectPayload.put("SessionId", sessionId.toString());
    jsonObjectPayload.put("DownloadSpeed", String.valueOf(downloadSpeed));
    jsonObjectPayload.put("cloudfareDownloadSpeed", String.valueOf(cloudfareDownloadSpeed));
    jsonObjectPayload.put("cloudfareUploadSpeed", String.valueOf(cloudfareUploadSpeed));
    jsonObjectPayload.put("cloudfarePingSpeed", String.valueOf(cloudfarePingSpeed));
    jsonObjectPayload.put("cloudfareJitterSpeed", String.valueOf(cloudfareJitterSpeed));
    apiH.postReqWithJSONResponseSumo(Constant.SUMOLOGIC_COLLECTOR_URL, jsonObjectPayload, null, 200);
  }

  public String getTestRunTime() {
    return testRunTime;
  }

  public void setTestRunTime(String testRunTime) {
    this.testRunTime = testRunTime;
  }

  @SuppressWarnings("unchecked")
  public String getSessionStatus() {
    String uri = "https://" + SAUCE_USERNAME + ":" + SAUCE_ACCESS_KEY + "@saucelabs.com/rest/v1/" + SAUCE_USERNAME + "/jobs/" + sessionId;
    Map<Object, Object> sessionDetails = apiH.getReqJsonPath(uri).getMap("$.");
    return sessionDetails.get("status").toString();
  }

}
