package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue33TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    GWT.create(EvenMoreSpecificType.class);
    GWT.create(FurtherSubClass.class);
  }

  @ExportPackage("tst")
  @Export
  public static class Test implements Exportable {
    public int[] method(int i) {
      return new int[]{1};
    }

    public int[] method(String s) {
      return new int[]{1};
    }
  }

  public static class SomeType {
    public String getType() {
      return "type";
    }
  }

  public static class SomeMoreSpecificType extends SomeType {
  }

  @ExportPackage("tst")
  @Export(value="EvenMoreSpecificType", all = true)
  public static class EvenMoreSpecificType extends SomeMoreSpecificType
      implements Exportable {
  }

  public static abstract class SuperClass<T extends SomeType> {
    public String doSomething(T t) {
      return t.getType();
    }
  }

  public static abstract class SubClass<T extends SomeMoreSpecificType> extends
      SuperClass<SomeMoreSpecificType> {
    @Override
    public String doSomething(SomeMoreSpecificType t) {
      return super.doSomething(t);
    }
  }

  @ExportPackage("tst")
  @Export(value="FurtherSubClass", all = true)
  public static class FurtherSubClass extends SubClass<EvenMoreSpecificType>
      implements Exportable {
    @Override
    public String doSomething(SomeMoreSpecificType t) {
      return super.doSomething(t);
    }
  }

  public static native String runJs() /*-{
		var e = new $wnd.tst.EvenMoreSpecificType();
		var v = new $wnd.tst.FurtherSubClass();
		return v.doSomething(e);
  }-*/;

  public void testIssue() {
    assertEquals("type", runJs());
  }
}
