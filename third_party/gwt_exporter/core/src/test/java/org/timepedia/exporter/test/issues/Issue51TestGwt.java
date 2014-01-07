package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Getter;
import org.timepedia.exporter.client.Setter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue51TestGwt extends GWTTestCase {

  @Override public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override protected void gwtSetUp() throws Exception {
    GWT.create(Test.class);
  }

  /**
   * An external interface that can't be Exportable by itself
   */
  public static interface ExternalInterface {
    public static final String MY_CONSTANT = "MY_CONSTANT";

    public String getName();
    public void setName(String s);
  }

  /**
   * A marker interface that extends the external interface an implements the Exportable to let the
   * system know that it does need to export all fields of this interface and the ones it extend.
   */
  public static interface MyInternalInterface extends ExternalInterface, Exportable {

  }

  @ExportPackage("tst")
  @Export("Test51")
  public static class Test implements MyInternalInterface {
    private String name = "";
    public String getName() {
      return name;
    }
    public void setName(String s) {
      this.name = s + "-exporter";
    }
  }

  public static native String runJs() /*-{
    var obj = new $wnd.tst.Test51();
    obj.setName($wnd.tst.Test51.MY_CONSTANT);
    return obj.getName();
  }-*/;

  public void testIssue() {
    assertEquals("MY_CONSTANT-exporter", runJs());
  }
}
