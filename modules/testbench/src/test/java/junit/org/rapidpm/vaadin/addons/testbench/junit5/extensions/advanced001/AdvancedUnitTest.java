package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.advanced001;

import junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.advanced001.demo.DemoTestPageObject;
import org.junit.jupiter.api.Test;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest.VaadinUnitTest;
import org.rapidpm.vaadin.addons.testbench.junit5.pageobject.PageObject;

/**
 *
 */

@VaadinUnitTest
public class AdvancedUnitTest {

  @Test
  void test001(@PageObject DemoTestPageObject pageObject) {
    pageObject.loadPage("advanced");
    pageObject.tfSurname.get().setValue("Vaadin");
    pageObject.button.get().click();
    pageObject.screenshot();
  }
}
