package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue34TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    GWT.create(Test.class);
  }
  
  @ExportPackage("tst")
  @Export("Test34")
  public static class Test implements Exportable {
    public int[] method(int i) { return new int[]{1}; }
    public int[] method(String s) { return new int[]{2}; }
  }
  
   public static native String runJs() /*-{
     var ret = ""
     ret += (new $wnd.tst.Test34()).method(1)[0];
     ret += '-';
     ret += (new $wnd.tst.Test34()).method('')[0];
		return ret;
  }-*/;

  public void testIssue() {
    assertEquals("1-2", runJs());
  }
}
