package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;
import org.timepedia.exporter.client.test.JsTestUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class Issue48TestGwt extends GWTTestCase {

  JsTestUtil jsTest = new JsTestUtil(Issue48TestGwt.class);
  
  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }
  
  public void test() {
    ExporterUtil.exportAll();
    runJsTests(jsTest);
    if (jsTest.isFailed()) fail(jsTest.getFailed());
  }

  public native JavaScriptObject runJsTests(JsTestUtil jsTest) /*-{
    // Utility function to assert equals numbers
    var assertEq = function(a, b) {jsTest.@org.timepedia.exporter.client.test.JsTestUtil::assertEqualsNumber(*)(a, b);}

    // We increase this ver each time the handler is called
    var i = 0;
    var login_handler = function() {i++};

    // Register the handler
    var reg_handler = $wnd.My.Handlers.addLoginEventHandler(login_handler);
    assertEq(0, i);
    
    // each call to fireLogin should increment the counter
    $wnd.My.Handlers.fireLoginEvent();
    assertEq(1, i);
    
    $wnd.My.Handlers.fireLoginEvent();
    assertEq(2, i);
    
    // We remove the handler from the eventBus, so the counter should not be incremented
    reg_handler.removeHandler();
    
    // The counter should remain the same
    $wnd.My.Handlers.fireLoginEvent();
    assertEq(2, i);
  }-*/;

  @Export
  @ExportClosure
  @ExportPackage("_internal")
  public static interface JsFunction extends Exportable {
    public void execute();
  }

  @ExportPackage("My")
  @Export("Handlers")
  public static class HandlersExporter implements Exportable {
    private static EventBus eventBus = new SimpleEventBus();
    
    public static HandlerRegistration addLoginEventHandler(final JsFunction function) {
      return eventBus.addHandler(LoginEvent.TYPE, new LogInEventHandler() {
        public void onLogIn(LoginEvent event) {
          function.execute();
        }
      });
    }

    public static void fireLoginEvent() {
      eventBus.fireEvent(new LoginEvent());
    }
  }

  @Export
  @ExportPackage("_internal")
  public interface JsHandlerRegistration extends ExportOverlay<HandlerRegistration> {
    void removeHandler();
  }

  public interface LogInEventHandler extends EventHandler {
    void onLogIn(LoginEvent event);
  }

  public static class LoginEvent extends Event<LogInEventHandler> {
    public static final Type<LogInEventHandler> TYPE = new Type<LogInEventHandler>();

    public Type<LogInEventHandler> getAssociatedType() {
      return TYPE;
    }

    protected void dispatch(LogInEventHandler handler) {
      handler.onLogIn(null);
    }
  }
}
