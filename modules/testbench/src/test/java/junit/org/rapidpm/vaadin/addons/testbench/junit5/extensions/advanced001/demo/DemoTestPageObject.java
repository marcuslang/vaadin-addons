package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.advanced001.demo;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import org.openqa.selenium.WebDriver;
import org.rapidpm.vaadin.addons.testbench.junit5.pageobject.AbstractVaadinPageObject;

import java.util.function.Supplier;

public class DemoTestPageObject extends AbstractVaadinPageObject {

  public DemoTestPageObject(WebDriver webDriver) {
    super(webDriver);
  }

  public Supplier<TextFieldElement> tfSurname = () -> textField().id(DemoTestUI.TEXTFIELD_SURNAME_ID);

  public Supplier<ButtonElement> button = () -> btn().id(DemoTestUI.BUTTON_ID);

}
