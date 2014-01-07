package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.junit.client.GWTTestCase;

public class Issue49TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    ExporterUtil.exportAll();
  }

  @Export
  public interface ObjectInterface extends Exportable {
    public String method();
  }

  @ExportPackage("tst")
  @Export("ObjectFactory")
  public static class ObjectFactory implements Exportable {
    public ObjectInterface create() {
      return new ObjectImpl();
    }
  }

  @ExportPackage("tst")
  @Export("ObjectUser")
  public static class ObjectUser implements Exportable {
    public String executeMethod(ObjectInterface object) {
      return object.method();
    }
  }

  public static class ObjectImpl implements ObjectInterface, Exportable {
    @Override
    public String method() {
      return "object";
    }
  }

  public static native String runJs() /*-{
		var factory = new $wnd.tst.ObjectFactory();
		var ret = "";
		var object = factory.create();
		var objectUser = new $wnd.tst.ObjectUser();
		ret += objectUser.executeMethod(object);
		return ret;
  }-*/;

  public void testIssue() {
    assertEquals("object", runJs());
  }
}