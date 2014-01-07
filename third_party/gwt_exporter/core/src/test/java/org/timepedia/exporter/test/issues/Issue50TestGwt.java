package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Getter;
import org.timepedia.exporter.client.Setter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue50TestGwt extends GWTTestCase {

  @Override public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override protected void gwtSetUp() throws Exception {
    GWT.create(Test.class);
  }

  @ExportPackage("tst")
  @Export("Test50")
  public static class Test implements Exportable {
    private String name = "";

    @Getter public String getName() {
      return name;
    }
    @Setter public void setName(String s) {
      this.name = s + "-exporter";
    }
  }

  public static native String runJs() /*-{
    var obj = new $wnd.tst.Test50();
    obj.name = "gwt";
    return obj.name;
  }-*/;

  public void testIssue() {
    assertEquals("gwt-exporter", runJs());
  }
}
