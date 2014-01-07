package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue44TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    GWT.create(Impl.class);
    GWT.create(ServiceImpl.class);
  }

  @ExportPackage("tst")
  @Export("Interface")
  public interface Interface extends Exportable {
    public String method();
  }

  @ExportPackage("tst")
  @Export("Impl")
  public static class Impl implements Interface, Exportable {

    @Override
    public String method() {
      return "method";
    }

  }

  @ExportPackage("tst")
  @Export("Service")
  public interface Service extends Exportable {

    public String accept(Interface itf);
  }

  @ExportPackage("tst")
  @Export("ServiceImpl")
  public static class ServiceImpl implements Service, Exportable {

    @Override
    public String accept(Interface itf) {
      return itf.method();
    }

  }

  public static native String runJs() /*-{
		var service = new $wnd.tst.ServiceImpl();
		var ret = "";
		ret += service.accept(new $wnd.tst.Impl());
		return ret;
  }-*/;

  public void testIssue() {
    assertEquals("method", runJs());
  }
}