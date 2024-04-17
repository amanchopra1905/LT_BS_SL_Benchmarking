package extra.benchmarking;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import testrunner.testrunner.EnvSetup;
import utility.utility.ApiHelper;
import utility.utility.Constant;
import utility.utility.LTHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalTest extends LTHelper {
  public static final String LOCALTEST = "http://localhost:4444/wd/hub";
  private RemoteWebDriver driver;
  private String driverStartTime;
  private String driverQuitTime;
  private String testRunTime;
  private String testName;
  ApiHelper apiH = new ApiHelper();
  SessionId sessionId;

  public DesiredCapabilities getCaps() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    String localBrowser = System.getProperty("LOCAL_BROWSER", "").toLowerCase();
    switch (localBrowser) {
    case "chrome":
      System.setProperty("webdriver.chrome.driver",
        System.getProperty(Constant.DRIVER_PATH_KEY, "drivers/chromedriver"));
      //      capabilities = DesiredCapabilities.chrome();
      capabilities.setBrowserName("chrome");
      break;
    case "firefox":
      System.setProperty("webdriver.gecko.driver", System.getProperty(Constant.DRIVER_PATH_KEY, "drivers/geckodriver"));
      //      capabilities = DesiredCapabilities.firefox();
      capabilities.setBrowserName("firefox");
      break;
    default:
      System.out.print("Set LOCAL_BROWSER in your command");
      break;
    }
    return capabilities;
  }

  public void setDriver() {
    DesiredCapabilities finalCaps = getCaps();
    StopWatch driverStart = new StopWatch();

    try {
      driverStart.start();
      driver = new RemoteWebDriver(new URL(LOCALTEST), finalCaps);
      driverStart.stop();
      sessionId = driver.getSessionId();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    driverStartTime = String.valueOf(driverStart.getTime() / 1000.00);
  }

  public RemoteWebDriver getTestDriver() {
    return driver;
  }

  public void quitDriver() {
    StopWatch driverQuit = new StopWatch();
    driverQuit.start();
    driver.quit();
    driverQuit.stop();
    driverQuitTime = String.valueOf(driverQuit.getTime() / 1000.00);
  }

  public void addLocalTestValuesToCSV() {
    String[] csvRowValue = new String[] { testName, "Lambdatest", "add it manually", driverStartTime, testRunTime,
      driverQuitTime, "Passed", sessionId.toString() };
    addValuesInCSVFile(EnvSetup.JENKINS_JOB_IDENTIFIER.get() + "_LocalTest_" + BENCHMARKING_STATICS_FILE_NAME,
      BENCHMARKING_STATICS_CSV_HEADER, csvRowValue);
    Map<String, String> jsonObjectPayload = new LinkedHashMap<>();
    jsonObjectPayload.put("TestName", "Test_Local_Benchmarking_Sumo");
    jsonObjectPayload.put("Provider", "Local");
    jsonObjectPayload.put("Region", "add it manually");
    jsonObjectPayload.put("Setup Time", driverStartTime);
    jsonObjectPayload.put("Test Time", testRunTime);
    jsonObjectPayload.put("Tear Time", driverQuitTime);
    apiH.postReqWithJSONResponseSumo(Constant.SUMOLOGIC_COLLECTOR_URL, jsonObjectPayload, null, 200);

  }

  public String getTestRunTime() {
    return testRunTime;
  }

  public void setTestRunTime(String testRunTime) {
    this.testRunTime = testRunTime;
  }

}
