package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.junit.client.GWTTestCase;

public class Issue38TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  // Should be exported as pkg.Person1
  @ExportPackage("pkg")
  @Export(all = true, value = "Person1")
  public static class Person implements Exportable {

    // Should be exported as pkg.Person1.Foo
    @Export
    public static class Foo implements Exportable {
      public String bar() {
        return "foo-bar,";
      }
    }
    
    private String name;
    public Person(String name) {
      this.name = name;
    }
    public String method() {
      return "Hello: " + name + ",";
    }
    
    /**
     * Just an empty method used to test JsDoclet
     * @param theName Any string should work
     * @return always null.
     */
    public String method(String theName) {
      return null;
    }
  }
  
  // Should be exported as pkg.Person2
  @ExportPackage("pkg")
  @Export(all = true)
  public static class Person2 extends Person {
    /**
     * This comment is here to test JsDoclet
     * 
     * @param name the name of the person
     */
    public Person2(String name) {
      super(name);
    }
  }

  // Should be exported as org.timepedia.exporter.test.issues.Issue38TestGwt.Person3
  @Export(all = true)
  public static class Person3 extends Person {
    public Person3(String name) {
      super(name);
    }
  }

  public static native String runJs() /*-{
    try {
      ret = "";
      if ($wnd.pkg && $wnd.pkg.Person2) {
        ret += new $wnd.pkg.Person1("A").method();
        ret += new $wnd.pkg.Person1.Foo().bar();
        ret += new $wnd.pkg.Person2("B").method();
        ret += new $wnd.org.timepedia.exporter.test.issues.Issue38TestGwt.Person3("C").method();
      }
      return ret;
    } catch(e) {
      return("Js Exception : " + e);
    }
  }-*/;

  public void testIssue() {
    // export(false) export classes which are default-instantiable (excluding interfaces, abstract, no-zero-constructor)
    ExporterUtil.export(false);
    assertEquals("", runJs());

    // exportAll exports all Exportable classes marked with @Export
    ExporterUtil.exportAll();
    assertEquals("Hello: A,foo-bar,Hello: B,Hello: C,", runJs());
  }
}