package org.timepedia.exporter.test.issues;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue25bTestGwt extends GWTTestCase{

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }
  
  @Override
  protected void gwtSetUp() throws Exception {
    GWT.create(MyExportable.class);
  }
  
  public native String evalJsTest(String jsCode) /*-{
    try {
      return eval(jsCode);
    } catch(e) {
      return String(e);
    }
  }-*/;
  
  public void test1() {
    String jsCode = "var v1 = new $wnd.gwt.MyExportable(); String(v1.getId());";
    assertEquals("no-id", evalJsTest(jsCode));
  }
  
  public void test2() {
    String jsCode = "var v1 = new $wnd.gwt.MyExportable('mid'); String(v1.getId());";
    assertEquals("mid", evalJsTest(jsCode));
  }
  
}
