package extra.benchmarking;

import org.apache.commons.lang3.time.StopWatch;
import utility.utility.LTHelper;

public class Benchmarking extends LTHelper {
  BSTest bsTest = new BSTest();
  RunSeleniumTest runTest = new RunSeleniumTest();
  LTTest ltTest = new LTTest();
  SLTest slTest = new SLTest();
  LocalTest localTest = new LocalTest();
  String downloadSpeed = "";
  String cloudfareDownloadSpeed = "";
  String cloudfareUploadSpeed = "";
  String cloudfarePingSpeed = "";
  String cloudfareJitterSpeed = "";

  public void startBSTestForBenchmarking(String capabilities, String[] sessionActions) {
    startBSTestForBenchmarking(capabilities, sessionActions, 1);
  }

  public void startBSTestForBenchmarking(String capabilities, String[] sessionActions, Integer repeatCount) {
    if (!System.getProperty("VM_REPEAT_COUNT", "").equalsIgnoreCase("")) {
      repeatCount = Integer.parseInt(System.getProperty("VM_REPEAT_COUNT"));
    }
    // start session
    bsTest.setDriver(capabilities);
    runTest.setTestDriver(bsTest.getTestDriver());
    // execute actions
    StopWatch testRunCounter = new StopWatch();
    testRunCounter.start();
    for (int i = 0; i < repeatCount; i++) {
      runTest.runTestActions(sessionActions);
    }
    testRunCounter.stop();
    bsTest.setTestRunTime(String.valueOf(testRunCounter.getTime() / 1000.00));
    downloadSpeed = runTest.getDownloadSpeed();
    cloudfareDownloadSpeed = runTest.getCloudFareDownloadSpeed();
    cloudfareUploadSpeed = runTest.getCloudFareUploadSpeed();
    cloudfarePingSpeed = runTest.getCloudFarePingSpeed();
    cloudfareJitterSpeed = runTest.getCloudFareJitterSpeed();

    bsTest.markTestStatus("passed", "test run success", bsTest.getTestDriver());
    // quit session
    bsTest.quitDriver();
    bsTest.addBSTestValuesToCSVAndDB(downloadSpeed, cloudfareDownloadSpeed, cloudfareUploadSpeed, cloudfarePingSpeed,
      cloudfareJitterSpeed);
  }

  public void startLTTestForBenchmarking(String capabilities, String[] sessionActions, Integer repeatCount) {
    if (!System.getProperty("VM_REPEAT_COUNT", "").equalsIgnoreCase("")) {
      repeatCount = Integer.parseInt(System.getProperty("VM_REPEAT_COUNT"));
    }
    // start session
    ltTest.setDriver(capabilities);
    runTest.setTestDriver(ltTest.getTestDriver());
    // execute actions
    StopWatch testRunCounter = new StopWatch();
    testRunCounter.start();
    for (int i = 0; i < repeatCount; i++) {
      runTest.runTestActions(sessionActions);
    }
    testRunCounter.stop();
    ltTest.setTestRunTime(String.valueOf(testRunCounter.getTime() / 1000.00));
    downloadSpeed = runTest.getDownloadSpeed();
    cloudfareDownloadSpeed = runTest.getCloudFareDownloadSpeed();
    cloudfareUploadSpeed = runTest.getCloudFareUploadSpeed();
    cloudfarePingSpeed = runTest.getCloudFarePingSpeed();
    cloudfareJitterSpeed = runTest.getCloudFareJitterSpeed();
    ltTest.markTestStatus("passed", ltTest.getTestDriver());
    // quit session
    ltTest.quitDriver();
    ltTest.addLTTestValuesToCSVAndDB(downloadSpeed, cloudfareDownloadSpeed, cloudfareUploadSpeed, cloudfarePingSpeed,
      cloudfareJitterSpeed);
  }

  public void startSLTestForBenchmarking(String capabilities, String[] sessionActions, Integer repeatCount) {
    if (!System.getProperty("VM_REPEAT_COUNT", "").equalsIgnoreCase("")) {
      repeatCount = Integer.parseInt(System.getProperty("VM_REPEAT_COUNT"));
    }
    // start session
    slTest.setDriver();
    runTest.setTestDriver(slTest.getTestDriver());
    // execute actions
    StopWatch testRunCounter = new StopWatch();
    testRunCounter.start();
    for (int i = 0; i < repeatCount; i++) {
      runTest.runTestActions(sessionActions);
    }
    testRunCounter.stop();
    slTest.setTestRunTime(String.valueOf(testRunCounter.getTime() / 1000.00));
    downloadSpeed = runTest.getDownloadSpeed();
    cloudfareDownloadSpeed = runTest.getCloudFareDownloadSpeed();
    cloudfareUploadSpeed = runTest.getCloudFareUploadSpeed();
    cloudfarePingSpeed = runTest.getCloudFarePingSpeed();
    cloudfareJitterSpeed = runTest.getCloudFareJitterSpeed();
    slTest.markTestStatus("passed", slTest.getTestDriver());
    // quit session
    slTest.quitDriver();
    slTest.waitForTime(15);
    slTest.addSLTestValuesToCSVAndDB(downloadSpeed, cloudfareDownloadSpeed, cloudfareUploadSpeed, cloudfarePingSpeed,
      cloudfareJitterSpeed);
  }

  public void startLocalTestForBenchmarking(String[] sessionActions, Integer repeatCount) {
    if (!System.getProperty("VM_REPEAT_COUNT", "").equalsIgnoreCase("")) {
      repeatCount = Integer.parseInt(System.getProperty("VM_REPEAT_COUNT"));
    }

    // start session
    localTest.setDriver();
    runTest.setTestDriver(localTest.getTestDriver());

    // execute actions
    StopWatch testRunCounter = new StopWatch();
    testRunCounter.start();
    for (int i = 0; i < repeatCount; i++) {
      runTest.runTestActions(sessionActions);
    }
    testRunCounter.stop();
    localTest.setTestRunTime(String.valueOf(testRunCounter.getTime() / 1000.00));

    // quit session
    localTest.quitDriver();

    localTest.waitForTime(15);

    localTest.addLocalTestValuesToCSV();

  }
}
