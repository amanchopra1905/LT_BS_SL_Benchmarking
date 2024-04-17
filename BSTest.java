package extra.benchmarking;

import org.apache.commons.lang3.time.StopWatch;
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

public class BSTest extends LTHelper {
  public static final String AUTOMATE_USERNAME = System.getProperty("BS_USERNAME");
  public static final String AUTOMATE_ACCESS_KEY = System.getProperty("BS_KEY");
  public static final String URL = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";
  private RemoteWebDriver driver;
  private String driverStartTime;
  private String driverQuitTime;
  private String testRunTime;
  private String testName;
  private String deviceName;
  ApiHelper apiH = new ApiHelper();
  Map<String, String> gCap;

  SessionId sessionId;

  public DesiredCapabilities getcaps(String givenCaps) {
    String capFixIP = System.getProperty("CAPS_FOR_FIXIP", "");
    if (!capFixIP.contains("=")) {
      gCap = getHasMapFromString(givenCaps);
    } else {
      gCap = getHasMapFromString(givenCaps + "," + capFixIP);
    }
    HashMap<String, Object> bstackOptions = new HashMap<>();
    DesiredCapabilities caps = new DesiredCapabilities();
    bstackOptions.put("buildName", "Touchstone Build");// CI/CD job or build name
    bstackOptions.put("sessionName", "Touchstone Test"); // test name
    bstackOptions.put("networkLogs", "true");

    if (gCap.getOrDefault("deviceTest", "false").equalsIgnoreCase("true")) {
      bstackOptions.put("realMobile", gCap.get("realMobile"));
      bstackOptions.put("device", gCap.get("device"));
      bstackOptions.put("osVersion", gCap.get("osVersion"));
      deviceName = bstackOptions.get("device").toString();
    } else {
      bstackOptions.put("osVersion", "10");
      bstackOptions.put("resolution", "1920x1080");
      bstackOptions.put("os", "Windows");
      bstackOptions.put("browserName", "Chrome");
      bstackOptions.put("browserVersion", "latest");
    }

    if (EnvSetup.CURRENT_USER_TUNNEL_NAME.get() != null) {
      bstackOptions.put("localIdentifier", EnvSetup.CURRENT_USER_TUNNEL_NAME.get());
    }
    bstackOptions.put("idleTimeout", 300);
    Set<Entry<String, String>> eset = gCap.entrySet();

    for (Entry<String, String> ent : eset) {
      bstackOptions.put(ent.getKey(), ent.getValue());
    }
    testName = "benchmarking Test" + EnvSetup.JENKINS_JOB_IDENTIFIER.get();
    caps.setCapability("bstack:options", bstackOptions);
    return caps;
  }

  public void setDriver(String givenCaps) {
    DesiredCapabilities finalCaps = getcaps(givenCaps);
    StopWatch driverStart = new StopWatch();
    ClientConfig config = ClientConfig.defaultConfig().connectionTimeout(Duration.ofMinutes(20))
      .readTimeout(Duration.ofMinutes(20));
    driverStart.start();
    driver = (RemoteWebDriver) RemoteWebDriver.builder().oneOf(finalCaps).address(URL).config(config).build();
    driverStart.stop();
    sessionId = driver.getSessionId();
    driverStartTime = String.valueOf(driverStart.getTime() / 1000.00);
  }

  public RemoteWebDriver getTestDriver() {
    return driver;
  }

  // This method accepts the status, reason and WebDriver instance and marks the
  // test on BrowserStack
  public void markTestStatus(String status, String reason, WebDriver driver) {
    JavascriptExecutor jse = (JavascriptExecutor) driver;
    jse.executeScript(
      "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
  }

  public void quitDriver() {
    StopWatch driverQuit = new StopWatch();
    driverQuit.start();
    this.driver.quit();
    driverQuit.stop();
    driverQuitTime = String.valueOf(driverQuit.getTime() / 1000.00);
  }

  public void addBSTestValuesToCSVAndDB(String downloadSpeedStr, String cloudfareDownloadSpeed,
    String cloudfareUploadSpeed, String cloudfarePingSpeed, String cloudfareJitterSpeed) {
    double downloadSpeed = 0;
    if (downloadSpeedStr != null && !downloadSpeedStr.equalsIgnoreCase("")) {
      downloadSpeed = Double.parseDouble(downloadSpeedStr);
    }
    String[] csvRowValue = new String[] { testName, "Browserstack", "add it manually", driverStartTime, testRunTime,
      driverQuitTime, getSessionStatus(), sessionId.toString(), deviceName, String.valueOf(downloadSpeed),
      String.valueOf(cloudfareDownloadSpeed), String.valueOf(cloudfareUploadSpeed), String.valueOf(cloudfarePingSpeed),
      String.valueOf(cloudfareJitterSpeed) };
    addValuesInCSVFile(EnvSetup.JENKINS_JOB_IDENTIFIER.get() + "_BS_" + BENCHMARKING_STATICS_FILE_NAME,
      BENCHMARKING_STATICS_CSV_HEADER, csvRowValue);
    Map<String, String> jsonObjectPayload = new LinkedHashMap<>();
    jsonObjectPayload.put("TestName", testName);
    jsonObjectPayload.put("Provider", "Browserstack");
    jsonObjectPayload.put("Region", "add it manually");
    jsonObjectPayload.put("Setup Time", driverStartTime);
    jsonObjectPayload.put("Test Time", testRunTime);
    jsonObjectPayload.put("Tear Time", driverQuitTime);
    jsonObjectPayload.put("Status", getSessionStatus());
    jsonObjectPayload.put("SessionId", sessionId.toString());
    jsonObjectPayload.put("deviceName", deviceName);
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
    String uri = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY + "@api.browserstack.com/automate/sessions/" + sessionId + ".json";
    Map<Object, Object> sessionDetails = apiH.getReqJsonPath(uri).getMap("$.");
    sessionDetails = (Map<Object, Object>) sessionDetails.get("automation_session");
    System.out.println(sessionDetails.get("status"));
    return sessionDetails.get("status").toString();
  }
}
