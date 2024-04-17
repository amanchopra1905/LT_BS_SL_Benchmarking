package extra.benchmarking;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
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
import java.util.Map.Entry;
import java.util.Set;

public class LTTest extends LTHelper {
  private org.apache.logging.log4j.Logger ltLogger = LogManager.getLogger(LTTest.class);
  private String ltUserName = EnvSetup.CURRENT_USER_USERNAME.get();
  private String ltAccessKey = EnvSetup.CURRENT_USER_KEY.get();
  private String ltTestHubUrl = ltHubUrl();
  private String hyperTest = HTTPS + ltUserName + ":" + ltAccessKey + "@localhost:4449/wd/hub";
  private RemoteWebDriver driver;
  private String driverStartTime;
  private String driverQuitTime;
  private String testRunTime;
  private String testName;
  Map<String, String> gCap;
  ApiHelper apiH = new ApiHelper();
  SessionId sessionId;

  public String ltHubUrl() {
    String gridUrl = "";
    if (!System.getProperty(CUSTOM_USER_GRID_URL, "").equalsIgnoreCase("")) {
      gridUrl = HTTPS + ltUserName + ":" + ltAccessKey + "@" + System.getProperty(CUSTOM_USER_GRID_URL);
    } else {
      gridUrl = "https://" + ltUserName + ":" + ltAccessKey + "@" + EnvSetup.TEST_ENV_HUB_URL + "/wd/hub";
    }
    ltLogger.info("LTTest Grid URL:- {}", gridUrl);
    return gridUrl;
  }

  public DesiredCapabilities getcaps(String givenCaps) {

    String capFixIP = System.getProperty("CAPS_FOR_FIXIP", "");
    if (!capFixIP.contains("=")) {
      gCap = getHasMapFromString(givenCaps);
    } else {
      gCap = getHasMapFromString(givenCaps + "," + capFixIP);
    }
    Map<String, Object> ltOptions = new HashMap<>();
    DesiredCapabilities caps = new DesiredCapabilities();
    ltOptions.put("build", "Benchmarking Build" + EnvSetup.JENKINS_JOB_IDENTIFIER.get());
    ltOptions.put("name", "Benchmarking Test" + EnvSetup.JENKINS_JOB_IDENTIFIER.get());
    ltOptions.put("platform", "Windows 10");
    ltOptions.put("browserName", "Chrome");
    ltOptions.put("version", "latest");
    ltOptions.put("resolution", "1920x1080");
    ltOptions.put("network", true);
    ltOptions.put("idleTimeout", 300);
    if (EnvSetup.CURRENT_USER_TUNNEL_NAME.get() != null) {
      ltOptions.put("tunnelName", EnvSetup.CURRENT_USER_TUNNEL_NAME.get());
    }

    Set<Entry<String, String>> eset = gCap.entrySet();

    for (Entry<String, String> ent : eset) {
      ltOptions.put(ent.getKey(), ent.getValue());
    }
    testName = ltOptions.get("name").toString();
    caps.setCapability("lt:options", ltOptions);
    return caps;
  }

  public String setURL() {
    if (!System.getProperty(CUSTOM_USER_GRID_URL, "").equalsIgnoreCase("")) {
      return HTTPS + EnvSetup.CURRENT_USER_USERNAME.get() + ":" + EnvSetup.CURRENT_USER_KEY.get() + "@" + System.getProperty(
        CUSTOM_USER_GRID_URL);
    } else {
      return (System.getProperty("TEST_IDENTIFIER", "").equalsIgnoreCase("HYP_hyperexecute")) ?
        hyperTest :
        ltTestHubUrl;
    }
  }

  public void setDriver(String givenCaps) {
    DesiredCapabilities finalCaps = getcaps(givenCaps);
    StopWatch driverStart = new StopWatch();
    String gridUrl = setURL();
    ltLogger.info("gridUrl:- {} ", gridUrl);
    ClientConfig config = ClientConfig.defaultConfig().connectionTimeout(Duration.ofMinutes(20))
      .readTimeout(Duration.ofMinutes(20));
    driverStart.start();
    driver = (RemoteWebDriver) RemoteWebDriver.builder().oneOf(finalCaps).address(gridUrl).config(config).build();
    driver.getCurrentUrl();
    driverStart.stop();
    sessionId = driver.getSessionId();
    driverStartTime = String.valueOf(driverStart.getTime() / 1000.00);
    EnvSetup.TEST_ENV_TEST_SESSION_ID.set(sessionId.toString());
  }

  public RemoteWebDriver getTestDriver() {
    return driver;
  }

  // This method accepts the status, reason and WebDriver instance and marks the
  // test on BrowserStack
  public void markTestStatus(String status, WebDriver driver) {
    JavascriptExecutor jse = (JavascriptExecutor) driver;
    jse.executeScript("lambda-status=" + status);
  }

  public void quitDriver() {
    StopWatch driverQuit = new StopWatch();
    driverQuit.start();
    this.driver.quit();
    driverQuit.stop();
    driverQuitTime = String.valueOf(driverQuit.getTime() / 1000.00);
  }

  public void addLTTestValuesToCSVAndDB(String downloadSpeedStr, String cloudfareDownloadSpeed,
    String cloudfareUploadSpeed, String cloudfarePingSpeed, String cloudfareJitterSpeed) {
    double downloadSpeed = 0;
    if (!downloadSpeedStr.equalsIgnoreCase("")) {
      downloadSpeed = Double.parseDouble(downloadSpeedStr);
    }
    String[] csvRowValue = new String[] { testName, "Lambdatest", "add it manually", driverStartTime, testRunTime,
      driverQuitTime, getSessionStatus(), sessionId.toString(), "", String.valueOf(downloadSpeed),
      String.valueOf(cloudfareDownloadSpeed), String.valueOf(cloudfareUploadSpeed), String.valueOf(cloudfarePingSpeed),
      String.valueOf(cloudfareJitterSpeed) };
    addValuesInCSVFile(EnvSetup.JENKINS_JOB_IDENTIFIER.get() + "_LT_" + BENCHMARKING_STATICS_FILE_NAME,
      BENCHMARKING_STATICS_CSV_HEADER, csvRowValue);
    Map<String, String> jsonObjectPayload = new LinkedHashMap<>();
    jsonObjectPayload.put("TestName", testName);
    jsonObjectPayload.put("Provider", "Lambdatest");
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
    String uri = "https://" + ltUserName + ":" + ltAccessKey + "@" + EnvSetup.TEST_ENV_API_URL + "/automation/api/v1/sessions/" + sessionId;
    String status = "";
    for (int i = 0; i < 10; i++) {
      Map<Object, Object> sessionDetails = apiH.getReqJsonPath(uri).getMap("$.");
      Map<Object, Object> innerSessionDetails = (Map<Object, Object>) sessionDetails.get("data");
      if (innerSessionDetails != null && innerSessionDetails.get("status_ind") != null) {
        ltLogger.info("uri:- {} and innerSessionDetails :- {}", uri, innerSessionDetails);
        status = innerSessionDetails.get("status_ind").toString();
        return status;
      }
      waitForTime(10);
    }
    assert !status.equalsIgnoreCase("") : "status_ind not found in innerSessionDetails even after 10 retries";
    return status;
  }
}
