package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.advanced001.demo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import static org.rapidpm.vaadin.addons.testbench.ComponentIDGenerator.buttonID;
import static org.rapidpm.vaadin.addons.testbench.ComponentIDGenerator.textfieldID;

/**
 *
 */
public class DemoTestUI extends UI {

  public static final String TEXTFIELD_SURNAME_ID = textfieldID().apply(DemoTestUI.class, "textfieldID");
  public static final String BUTTON_ID = buttonID().apply(DemoTestUI.class, "buttonID");

  @Override
  protected void init(VaadinRequest request) {
    setContent(initLayout());
  }

  private Component initLayout(){
    final VerticalLayout layout = new VerticalLayout();

    final TextField tfSurname = new TextField(TEXTFIELD_SURNAME_ID);
    tfSurname.setId(TEXTFIELD_SURNAME_ID);

    final Button button = new Button(BUTTON_ID);
    button.setId(BUTTON_ID);
    button.addClickListener(event -> System.out.println("Hello " + tfSurname.getValue()));

    layout.addComponent(tfSurname);
    layout.addComponent(button);

    return layout;
  }
}
