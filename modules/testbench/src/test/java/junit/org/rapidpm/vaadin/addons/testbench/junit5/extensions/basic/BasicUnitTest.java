package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic;

import junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo.BasicTestPageObject;
import org.junit.jupiter.api.Test;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest.VaadinUnitTest;
import org.rapidpm.vaadin.addons.testbench.junit5.pageobject.PageObject;

/**
 *
 */

@VaadinUnitTest
public class BasicUnitTest {

  @Test
  void test001(@PageObject BasicTestPageObject pageObject) {
    pageObject.loadPage("basic");
    pageObject.button.get().click();
    pageObject.screenshot();
  }
}
