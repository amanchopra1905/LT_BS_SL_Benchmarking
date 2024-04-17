package extra.benchmarking;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import utility.utility.LTHelper;
import utility.utility.WebDriverHelper;

public class RunHerokuTest extends LTHelper {

  WebDriverHelper wdHelper;
  RemoteWebDriver testDriver;

  // locators start
  protected static final String[] ABTEST_TEXT = { CSS, "h3" };
  protected static final String[] ADD_ELEMENT = { CSS, "[onclick='addElement()']" };
  protected static final String[] DELETE_ELEMENT = { CSS, "[onclick='deleteElement()']" };
  protected static final String[] BASIC_AUTH_TEXT = { CSS, "p" };
  protected static final String[] CHECKBOX_CLICK = { CSS, "input:first-child" };
  protected static final String[] CONTEXT_MENU_BOX = { ID, "hot-spot" };
  protected static final String[] A_ELEMENT = { ID, "column-a" };
  protected static final String[] B_ELEMENT = { ID, "column-b" };
  protected static final String[] FIRST_HEADER = { CSS, "#columns div:first-child header" };
  protected static final String[] BROKEN_IMAGES = { CSS, "img" };
  protected static final String[] DROPDOWN_OPT = { ID, "dropdown" };
  protected static final String[] OPTION_1 = { CSS, "[value='1']" };
  protected static final String[] STATIC_CLICK = { CSS, "p a" };
  protected static final String[] STATIC_IMAGE = { CSS, "#content > div:first-child.row img" };
  protected static final String[] STATIC_PARAGRAPH = { CSS, "#content > div:first-child.row > div.large-10" };
  protected static final String[] CHECKBOX_REMOVE_BUTTON = { CSS, "[onclick='swapCheckbox()']" };
  protected static final String[] MESSAGE = { CSS, "#message" };
  protected static final String[] TEXTBOX_ENABLED_BUTTON = { CSS, "[onclick='swapInput()']" };
  protected static final String[] AD_MODAL_BOX = { CSS, ".modal-title" };
  protected static final String[] USERNAME_TEXT = { ID, "username" };
  protected static final String[] PASSWORD_TEXT = { ID, "password" };
  protected static final String[] LOGIN_BUTTON = { CSS, ".radius" };
  protected static final String[] SUBHEADER_TEXT = { CSS, ".subheader" };
  protected static final String[] LOGOUT_BUTTON = { CSS, ".button" };
  protected static final String[] LOGIN_TEXT = { CSS, "h2" };

  // locators end
  protected static final String HEROKU_URL = "http://the-internet.herokuapp.com/";
  protected static final String ABTEST_TEST_URL = "abtest";
  protected static final String ADD_REMOVE_ELEMENT_URL = "add_remove_elements/";
  protected static final String CHECKBOX_URL = "checkboxes";
  protected static final String BROKEN_IMAGES_URL = "broken_images";
  protected static final String DROPDOWN_URL = "dropdown";
  protected static final String DYNAMIC_CONTENT_URL = "dynamic_content";
  protected static final String DYNAMIC_CONTROL_URL = "dynamic_controls";
  protected static final String LOGIN_URL = "login";

  public void setTestDriver(RemoteWebDriver testDriver) {
    this.testDriver = testDriver;
    wdHelper = new WebDriverHelper(this.testDriver);
  }

  public void runHerokuApp() {
    abTest();
    addRemoveElement();
    basicAuth();
    brokenImages();
    checkBox();
    dynamicContent();
    dynamicControls();
    login();
  }

  public void abTest() {
    wdHelper.getURL(HEROKU_URL + ABTEST_TEST_URL);
    String abTestString = wdHelper.getText(ABTEST_TEXT);
    assert abTestString.contains("A/B Test") : "A/B Test Failed";
  }

  public void addRemoveElement() {
    wdHelper.getURL(HEROKU_URL + ADD_REMOVE_ELEMENT_URL);
    wdHelper.clickOnElement(ADD_ELEMENT);
    Boolean delete_button = wdHelper.isDisplayed(DELETE_ELEMENT);
    assert delete_button.equals(true) : "Delete button is not render";
    wdHelper.clickOnElement(DELETE_ELEMENT);
    delete_button = wdHelper.isDisplayed(DELETE_ELEMENT);
    assert delete_button.equals(false) : "Delete button is still render";
  }

  public void basicAuth() {
    wdHelper.getURL("http://admin:admin@the-internet.herokuapp.com/basic_auth");
    String basicAuthString = wdHelper.getText(BASIC_AUTH_TEXT);
    assert basicAuthString.contains(
      "Congratulations! You must have the proper credentials.") : "BASIC AUTH Test Failed";
  }

  public void brokenImages() {
    List<WebElement> al = new ArrayList<>();
    wdHelper.getURL(HEROKU_URL + BROKEN_IMAGES_URL);
    for (WebElement image : wdHelper.getElements(BROKEN_IMAGES)) {
      if (isImageBroken(image)) {
        al.add(image);
      }
    }
    Boolean image_status = al.isEmpty();
    assert image_status.equals(false) : "Broken images found";
  }

  public void checkBox() {
    wdHelper.getURL(HEROKU_URL + CHECKBOX_URL);
    wdHelper.clickOnElement(CHECKBOX_CLICK);
    Boolean checkbox_status = wdHelper.isSelected(CHECKBOX_CLICK);
    assert checkbox_status.equals(true) : "Checkbox Test Failed";
  }

  public void dropdown() {
    wdHelper.getURL(HEROKU_URL + DROPDOWN_URL);
    wdHelper.clickOnElement(DROPDOWN_OPT);
    wdHelper.waitForTime(2);
    wdHelper.clickOnElement(OPTION_1);
    String option_status = wdHelper.getAttributeValue(OPTION_1, "selected");
    assert option_status.equals("selected") : "Dropdown Test Failed";
  }

  public void dynamicContent() {
    wdHelper.getURL(HEROKU_URL + DYNAMIC_CONTENT_URL);
    wdHelper.clickOnElement(STATIC_CLICK);
    String dynamic_content_string_1 = wdHelper.getText(STATIC_PARAGRAPH);
    wdHelper.pageRefresh();
    String dynamic_content_string_2 = wdHelper.getText(STATIC_PARAGRAPH);
    assert dynamic_content_string_1.equals(dynamic_content_string_2) : "Dynamic Content Test Failed";
  }

  public void dynamicControls() {
    wdHelper.getURL(HEROKU_URL + DYNAMIC_CONTROL_URL);
    wdHelper.clickOnElement(CHECKBOX_REMOVE_BUTTON);
    assert wdHelper.getText(MESSAGE).equals("It's gone!");
    wdHelper.clickOnElement(CHECKBOX_REMOVE_BUTTON);
    assert wdHelper.getText(MESSAGE).equals("It's back!");
    wdHelper.clickOnElement(TEXTBOX_ENABLED_BUTTON);
    assert wdHelper.getText(MESSAGE).equals("It's enabled!");
    wdHelper.clickOnElement(TEXTBOX_ENABLED_BUTTON);
    assert wdHelper.getText(MESSAGE).equals("It's disabled!") : "Dynamic Control Test Failed";
  }

  public void login() {
    wdHelper.getURL(HEROKU_URL + LOGIN_URL);
    wdHelper.sendKeys(USERNAME_TEXT, "tomsmith");
    wdHelper.sendKeys(PASSWORD_TEXT, "SuperSecretPassword!");
    wdHelper.clickOnElement(LOGIN_BUTTON);
    assert wdHelper.getText(SUBHEADER_TEXT).equals("Welcome to the Secure Area. When you are done click logout below.");
    wdHelper.clickOnElement(LOGOUT_BUTTON);
    assert wdHelper.getText(LOGIN_TEXT).equals("Login Page") : "Login Test Failed";
  }
}
