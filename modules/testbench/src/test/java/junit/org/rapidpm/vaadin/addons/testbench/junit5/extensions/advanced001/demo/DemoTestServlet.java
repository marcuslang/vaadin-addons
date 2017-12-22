package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.advanced001.demo;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import javax.servlet.annotation.WebServlet;

/**
 *
 */
@WebServlet(
    urlPatterns = "/advanced",
    name = "JumpstartServlet",
    displayName = "JumpstartServlet",
    asyncSupported = true,
    loadOnStartup = 1)
@VaadinServletConfiguration(productionMode = false, ui = DemoTestUI.class)
public class DemoTestServlet extends VaadinServlet { }
