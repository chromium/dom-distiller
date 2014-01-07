package org.gquery.ewidget.client;
import static com.google.gwt.query.client.GQuery.$;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * App example to show how to mix gquery and gwtexporter
 * 
 * After compiling this app and loading in your html document, 
 * you can call exported methods from javascript:
 <pre>
   label = js.getLabel(".gwt-Label");
   label.setText("Hi");
   
   widget = js.getWidget(".gwt-Label");
   label = new js.Label(widget);
   label.setText("Bye");
   
   button = js.getButtons("*")[0];
   button.click();

   widget = js.getWidget(".gwt-Button");
   button = new js.Button(widget);
   button.click();
 </pre>
 */
public class ExportWidget implements EntryPoint {
  
  public void onModuleLoad() {
    ExporterUtil.exportAll();
    
    final Label label = new Label("Hello world");
    final Button button = new Button("Click me", new ClickHandler() {
      public void onClick(ClickEvent event) {
        $(label)
          .animate("fontSize: '+=10px', color:'blue'")
          .delay(1000)
          .animate("fontSize: '-=10px', color: 'black'");
      }
    });
    
    RootPanel.get().add(label);
    RootPanel.get().add(button);
  }
  
  /**
   * We use a class to expose some static methods which
   * will return the widgets we want calling to gquery.
   */
  @ExportPackage("")
  @Export("js")
  public static class Exposer implements Exportable {
    public static Label getLabel(String selector) {
      return $(selector).widgets(Label.class).get(0);
    }

    public static Button[] getButtons(String selector) {
      return $(selector).widgets(Button.class).toArray(new Button[0]);
    }
    
    public static Widget getWidget(String selector) {
      return $(selector).widget();
    }
    
    public static Widget[] getWidgets(String selector) {
      return $(selector).widgets().toArray(new Widget[0]);
    }
  }
  
  /**
   * We export the widgets we want to use so as we can
   * access their instance methods.
   * 
   * We can use either interfaces or abstract classes.
   */
  @ExportPackage("js")
  @Export
  public static abstract class LabelOverLay implements ExportOverlay<Label> {
    public abstract String getText();
    public abstract void setText(String s);
  }

  @ExportPackage("js")
  @Export
  public static interface ButtonOverLay extends ExportOverlay<Button> {
    void setText(String s);
    void click();
  }
}
