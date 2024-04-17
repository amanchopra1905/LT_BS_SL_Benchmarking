package extra.benchmarking;

import io.cucumber.java.en.Then;
import org.testng.Assert;
import testrunner.testrunner.EnvSetup;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BrowserstackLamabdatestSaucelabStepdef {
  RD_Benchmarking bmark = new RD_Benchmarking();
  Benchmarking benchmarking = new Benchmarking();

  @Then("^start browserstack Test to test ([a-zA-Z0-9_=,:.+\\-() ]+) with ([a-zA-Z0-9_=,:.+\\-() ]+) capabilities$")
  public void startBSTestForGivenCaps(String sessionType, String capabilities) {
    String[] sessionActions = sessionType.split(",");
    benchmarking.startBSTestForBenchmarking(capabilities, sessionActions);
  }

  @Then("^start browserstack Test to test ([a-zA-Z0-9_=,:.+\\-() ]+) with ([a-zA-Z0-9_=,:.+\\-() ]+) capabilities with test activity repeat count as ([0-9_ ]+)$")
  public void startBSTestForGivenCapsWithRepeat(String sessionType, String capabilities, Integer repeatCount) {
    String[] sessionActions = sessionType.split(",");
    benchmarking.startBSTestForBenchmarking(capabilities, sessionActions, repeatCount);
  }

  @Then("^start Lambdatest Test to test ([a-zA-Z0-9_=,:.+\\-() ]+) with ([a-zA-Z0-9_=,:.+\\-() ]+) capabilities with test activity repeat count as ([0-9_ ]+)$")
  public void startLTTestForGivenCapsWithRepeat(String sessionType, String capabilities, Integer repeatCount) {
    String[] sessionActions = sessionType.split(",");
    bmark.startLTTestForBenchmarking(capabilities, sessionActions, repeatCount);
  }

  @Then("^start saucelabs Test to test ([a-zA-Z0-9_=,:.+\\-() ]+) with ([a-zA-Z0-9_=,:.+\\-() ]+) capabilities with test activity repeat count as ([0-9_ ]+)$")
  public void startSLTestForGivenCapsWithRepeat(String sessionType, String capabilities, Integer repeatCount) {
    String[] sessionActions = sessionType.split(",");
    bmark.startSLTestForBenchmarking(capabilities, sessionActions, repeatCount);
  }

  @Then("^start Local Test to test ([a-zA-Z0-9_=,:.+\\-() ]+) with test activity repeat count as ([0-9_ ]+)$")
  public void startLocalTestForGivenCapsWithRepeat(String sessionType, Integer repeatCount) {
    String[] sessionActions = sessionType.split(",");
    bmark.startLocalTestForBenchmarking(sessionActions, repeatCount);
  }

  @Then("^start browserstack tunnel$")
  public void startBsTunnel() {
    boolean tunnelStarted = false;
    tunnelStarted = startTunnelBs(0);
    Assert.assertTrue(tunnelStarted,
      "unable to start tunnel with tunnelname as " + EnvSetup.CURRENT_USER_TUNNEL_NAME.get());
  }

  public boolean startTunnelBs(int retry) {

    StartBsTunnelBinary Tunnelbs = new StartBsTunnelBinary();
    Random rand = new Random();
    int sleepTime = (rand.nextInt(10000) * 10000) / 10000;
    try {
      TimeUnit.MILLISECONDS.sleep(sleepTime);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    Tunnelbs.start();
    boolean tunnelStarted = true;
    for (int i = 0; i < 15; i++) {
      if (Tunnelbs.isTunnelStarted()) {
        tunnelStarted = true;
        break;
      }
      try {
        TimeUnit.SECONDS.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    if (retry > 0) {
      startTunnelBs(retry - 1);
    }
    return tunnelStarted;
  }
}
