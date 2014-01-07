package org.timepedia.exporter.test;

import java.util.ArrayList;
import java.util.Date;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportConstructor;
import org.timepedia.exporter.client.ExportInstanceMethod;
import org.timepedia.exporter.client.ExportJsInitMethod;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportStaticMethod;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;
import org.timepedia.exporter.client.NoExport;
import org.timepedia.exporter.client.test.JsTestUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Label;

public class CoreTestGwt extends GWTTestCase {

  JsTestUtil jsTest = new JsTestUtil(CoreTestGwt.class);
  
  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }
  
  public void test() {
    GWT.create(C.class);
    runJsTests1(jsTest);
    if (jsTest.isFailed()) fail(jsTest.getFailed());
    
    ExporterUtil.export(false);
    runJsTests2(jsTest);
    if (jsTest.isFailed()) fail(jsTest.getFailed());
  }
  
  ///////////////////// Classes used to test types, arrays, static, public, override
  @ExportPackage("gwt")
  @Export
  public static class HelloAbstract implements Exportable {
    public String helloAbstract(){
      return this.getClass().getName();
    }
    
    @NoExport
    public String noHelloAbstract(){
      return this.getClass().getName();
    }
  }
  
  @ExportPackage("gwt")
  @Export("HelloClass")
  public static class HelloClass extends HelloAbstract implements Exportable {
    public String hello(){
      return this.getClass().getName();
    }

    public static String[] test0(char c, byte b, int i, double d, float f, String s, Object o, Exportable e) {
      String[] ret = new String[8];
      ret[0] = "" + (int)c;
      ret[1] = "" + b;
      ret[2] = "" + i;
      ret[3] = "" + d;
      ret[4] = "" + f;
      ret[5] = "" + s;
      ret[6] = "" + o.getClass().getName();
      ret[7] = "" + e.getClass().getName();
      return ret;
    }

    public static int[] test1(char[]c, byte[] b, int[] i, double[]d, float[] f, long[] l, String[] s, Object[] o, Exportable[] e) {
      int[] ret = new int[9];
      ret[0] = c.length;
      ret[1] = b.length;
      ret[2] = i.length;
      ret[3] = d.length;
      ret[4] = f.length;
      ret[5] = l.length;
      ret[6] = s.length;
      ret[7] = o.length;
      ret[8] = e.length;
      return ret;
    }

    public static long[] test2() {
      return new long[]{1l,2l};
    }
    
    public static Exportable[] test3() {
      return new HelloClass[]{new HelloClass()};
    }
    
    public static char test4() {
      return 1;
    }
    
    public static byte test5() {
      return 2;
    }
    
    public static int test6() {
      return 3;
    }
    
    public static double test7() {
      return 4;
    }
    
    public static float test8() {
      return 5;
    }
    
    public static String test9() {
      return "A";
    }
    
    public static JavaScriptObject test10() {
      return new Label("").getElement();
    }
    
    public static long test11() {
      return 6;
    }
    
    public static String test12(long l){
      return "" + l;
    }
    
    public static long test13(long l, double d) {
      return l + (long)d;
    }
    
    public long test14(long l, double d, long[] a) {
      return l + (long)d + a[0];
    }
    
    public long[] test15(long[] a) {
      return a;
    }

    public static String test16(long l) {
      return "" + l;
    }

    public static long test16(long a, long b) {
      return (a + b);
    }

    public String test17(long l) {
      return "" + l;
    }

    public long test17(long a, long b) {
      return (a + b);
    }
    
    public static String test18(String a, String[] b) {
      return a + "_" + b.length;
    }

    public static String test18(String a, String b, String[] c) {
      return a + "_" + b + "_" + c.length;
    }
    
    public String test19(String a, String[] b) {
      return test18(a, b);
    }

    public String test19(String a, String b, String[] c) {
      return test18(a, b, c);
    }
    
    public static String test20(String a, long b, String...c) {
      return a + "_" + b + "_" + c.length;
    }

    public String test21(String a, long b, String...c) {
      return test20(a, b, c);
    }
    
    public static Date test22(Date d) {
      return d;
    }

    @SuppressWarnings("deprecation")
    public String test23(Date...ds) {
      String ret = "";
      for (Date d : ds) {
        ret += d.getYear() + "-";
      }
      return ret;
    }
    
    public Date[] test24() {
      Date[] ret = new Date[1];
      ret[0] = new Date(0);
      return ret;
    }    
    

    public boolean test25(boolean a) {
      return a;
    }

    public boolean test26(long l) {
      return l > 2;
    }

    public boolean test27(boolean a) {
      return a;
    }

    public boolean test27(boolean a, boolean b, long l) {
      return a && b && l > 2;
    }
  }
  
  ///////////////////// Classes used to test closures
  @ExportPackage("gwt")
  @Export("Foo")
  public static class Foo implements Exportable {
    
    String n = "foo";
    public Foo() {
    }
    public Foo(String id) {
      n= id;
    }
    public Foo(String id, String a) {
      n= id + a;
    }
    public String toString(){
      return n;
    }
    public String toString(String a){
      return n + ">" + a;
    }
    
    @ExportClosure
    public interface Closure extends Exportable {
      public String execute(String par1, String par2);
    }
    
    public String executeJsClosure(Closure closure){
       return closure.execute("Hello", "Friend");
    }
  }
  
  ///////////////////// Classes used to test that we can mark methods in interfaces
  public static interface MInterface extends Exportable {
    @Export
    String m1();
    String m1(int a);
    @Export
    String m1(int a, int b);
  }
  
  public static abstract class MBase implements MInterface {
    @Export
    public String m0() {
      return "m0";
    }
    public String m1() {
      return "m1";
    }
    public String m1(int a) {
      return "m1-" + a;
    }
    public String m1(int a, int b) {
      return "m1-" + a + b;
    }
    @Export
    public String m2() {
      return "m2";
    }
  }
  
  ///////////////////// Classes used to test that unused parent classes are not exported
  @ExportPackage("gwt")
  public static class MClass extends MBase {
    @Export
    public String m0() {
      return "om0";
    }
    @Export
    public String m3() {
      return "m3";
    }
    public String m4() {
      return "m4";
    }
    @Export
    public final String f() {
      return "final";
    }
  }
  
  @ExportPackage("gwt")
  public static class A implements Exportable {
    public B convertToB() {
      return new B();
    }
    @Export
    public String foo() {
      return "foo";
    }
    @Export
    public String toString() {
      return this.getClass().getName().replaceAll("^.+[\\.\\$]", "");
    }
  }

  @ExportPackage("gwt")
  public static class B extends A {
    public C convertToC() {
      return new C();
    }
    public String toString() {
      return "A";
    }
  }

  @ExportPackage("gwt")
  public static class C extends A {
    @Export
    public A convertToA() {
      return new A();
    }
  }
  
  ///////////////////// Classes used to test exported methods in parent classes if export.all = true. And name-spaces
  public static class Parent {
    public String m(String a) {
      return a;
    }
    public String getParentName(Parent p) {
      return p.getClass().getName().replaceAll("^.*[\\.\\$]", "");
    }
    public Parent parent() {
      return this;
    }
  }
  
  @Export(value = "$$", all = true)
  @ExportPackage("")
  public static class Son extends Parent implements Exportable {
    @Export
    public String f = "F";
    
    @Export("sonName")
    public String getSonName(Son c) {
      return super.getParentName(c);
    }
    
    @Export("$wnd.$$$")
    public static String $() {
      return "$$$";
    }
  }
  
  ///////////////////// Classes used to test export overlay
  public static class Func {
    public boolean f(Element e) {
      return true;
    }
  }
  
  public static class GQ implements Exportable {
    private String echoMsg = "empty";

    @ExportConstructor
    public static GQ $(String s) {
      GQ ret = new GQ();
      ret.echoMsg = s;
      return ret;
    }
    public String echo() {
      return echoMsg;
    };
    public Element element() {
      return Document.get().getDocumentElement();
    };
    public Element[] elements() {
      ArrayList<Element> e = new ArrayList<Element>();
      e.add(element());
      return e.toArray(new Element[0]);
    };
    public String countElements(Element... elms) {
      return "" + elms.length;
    }
    public GQ[] exports() {
      ArrayList<GQ> j = new ArrayList<GQ>();
      j.add(this);
      return j.toArray(new GQ[0]);
    }
    public GQ gq() {
      return this;
    }
    
    public boolean executeFunction(Func... f) {
      boolean ret = false;
      for (Func ff : f) {
        ret = ret || ff.f(element());
      }
      return ret;
    }
    
    public boolean executeFunction(String s, Func... f) {
      boolean ret = executeFunction(f);
      return ret;
    }
    
    public GQ executeFunction2(Func... f) {
      echoMsg = "ret-" + executeFunction(f);
      return this;
    }
    
    public String executeClosure(Clos f) {
      return f.execute("A", "B");
    }
    
    public String executeClosure(int i, Clos f) {
      return f.execute("A", "B");
    }
  }
  
  @ExportPackage("gwt")
  @Export("j")
  public static class JQ implements  ExportOverlay<GQ> {
    public String echo() {return null;}

    public Element element(){return null;}
    public Element[] elements(){return null;}
    public String countElements(Element... elms){return null;}
    
    public GQ[] exports() {return null;}
    
    public GQ gq() {return null;}
    
    public boolean executeFunction(Func... f) {return false;}
    public boolean executeFunction(String s, Func... f) {return false;}
    public GQ executeFunction2(Func... f) {return null;}
    
    public String executeClosure(Clos f) {return null;}
    public String executeClosure(int a, Clos f) {return null;}

    @Export("$wnd.$")
    public static GQ $(String s){return null;};
  }
  
  @ExportClosure
  public interface Clos extends Exportable {
    public String execute(String par1, String par2);
  }
  
  @ExportClosure()
  public interface FuncClos extends ExportOverlay<Func>  {
    public boolean f(Element e);
  }
  
  ///////////////////// Class used to test static constructors
  @ExportPackage("gwt")
  @Export("c")
  public static class TestConstructors implements Exportable {
    private static TestConstructors instance;
    private String msg;

    @ExportConstructor
    public static TestConstructors constructor(String msg) {
      if (instance == null) {
        instance = new TestConstructors();
        instance.msg = msg;
      }
      return instance;
    }
    // Constructor is private
    private TestConstructors() {
    }
    public String echo() {
      return msg;
    }
  }
  
  // ExportOverlay issue_30
  public static class Child {
    private String name;
    public Child(String cname) { name = cname; }
    public String getName1() { return name; }
    public String getName2(long l) { return name + l; }
    public String getName3(Child c) { return c==null? "NULL" :c.getName1(); }

    public String wrapped_method(long l) {return "" + l;}
  }

  public static class Mother {
    Child child;
    public void setChild(Child c) {child = c;}
    public Child getChild() {return child;}
  }

  @ExportPackage("ex")
  @Export("Child")
  public static class XChild implements ExportOverlay<Child>{
    public XChild(String s){}
    @Export("name")
    public String getName1() {return null;}
    @Export("name")
    public String getName2(long l) {return null;}
    @Export("name")
    public String getName3(Child c) {return null;}

    @ExportConstructor
    public static Child constructor(String name, String surname) {
      return new Child(name + " " + surname);
    }

    @ExportInstanceMethod("foo")
    public static String instanceMethod(Child instance, String name, String surname, long l) {
      return name + "-" + surname + "-Foo-" + l;
    }
    @ExportInstanceMethod("foo")
    public static String instanceMethod(Child instance, String name) {
      return name + "-Caa";
    }
    
    @ExportStaticMethod("sfoo")
    public static String staticMethod(String name)  {
      return name + "-Cas";
    }
    @ExportStaticMethod("sfoo")
    public static String staticMethod(long l)  {
      return l + "-Cas";
    }
    
    public String wrapped_method(long l) {return null;}
  }

  @ExportPackage("ex")
  @Export("Mother")
  public static class XMother implements ExportOverlay<Mother>{
    public void setChild(Child c) {}
    public Child getChild() {return null;}
  }
  
  public static class Q {
    private NodeList<Element> nodeList = JavaScriptObject.createArray().cast();
    public Q() {
      nodeList = Document.get().<Element>cast().getElementsByTagName("body");
    }
    public int size() {
      return nodeList.getLength();
    }
    public NodeList<Element> get() {
      return nodeList;
    }
  }
  
  @ExportPackage("")
  @Export("JQ")
  public static class EJQ implements ExportOverlay<Q> {
    public int size(){return -1;}
    @ExportJsInitMethod
    public NodeList<Element> get(){return null;}
  }
  
  public native JavaScriptObject runJsTests1(JsTestUtil jsTest) /*-{
    var assertEq = function(a, b) {jsTest.@org.timepedia.exporter.client.test.JsTestUtil::assertEquals(*)(a, b);}
    try {
      var c = new $wnd.gwt.C();
      assertEq("C", c); 
      assertEq("C", c.toString()); 
      var a = c.convertToA();
      assertEq("A", a);
      a = new $wnd.gwt.A();
      assertEq("A", a);
      
      // GWT.create(C) should not export B
      var c = $wnd.gwt.B ? "defined" : "undefined";
      assertEq("undefined", c);
    } catch(e) {
      assertEq(null, "JS Exception: " + e);
    }; 
  }-*/;

  public native JavaScriptObject runJsTests2(JsTestUtil jsTest) /*-{
    var assertEq = function(a, b) {jsTest.@org.timepedia.exporter.client.test.JsTestUtil::assertEquals(*)(a, b);}
    
    try {
      var h = new $wnd.gwt.HelloClass();
      assertEq("1,2,3,4,5,S,JavaScriptObject,HelloClass", $wnd.gwt.HelloClass.test0(1, 2, 3, 4, 5, "S", window.document, h));
      assertEq("1,1,1,1,1,2,2,2,1", $wnd.gwt.HelloClass.test1([0], [0], [0], [0], [0], [1,2], ["a","b"], [window,document], [h]));
      assertEq("1,2", $wnd.gwt.HelloClass.test2());
      assertEq("HelloClass", $wnd.gwt.HelloClass.test3()[0].hello());
      assertEq("HelloClass", $wnd.gwt.HelloClass.test3()[0].helloAbstract());
      assertEq("undefined", "" + $wnd.gwt.HelloClass.test3()[0].noHelloAbstract);
      
      assertEq("1", $wnd.gwt.HelloClass.test4(1, "A"));
      assertEq("2", $wnd.gwt.HelloClass.test5());
      assertEq("3", $wnd.gwt.HelloClass.test6());
      assertEq("4", $wnd.gwt.HelloClass.test7());
      assertEq("5", $wnd.gwt.HelloClass.test8());
      assertEq("A", $wnd.gwt.HelloClass.test9());
      assertEq("div", $wnd.gwt.HelloClass.test10().tagName.toLowerCase());
      assertEq("6", $wnd.gwt.HelloClass.test11());
      assertEq("1", $wnd.gwt.HelloClass.test12(1));
      assertEq("5", $wnd.gwt.HelloClass.test13(2, 3));
      assertEq("4", $wnd.gwt.HelloClass.test16(4));
      assertEq("14", $wnd.gwt.HelloClass.test16(4, 10));
      assertEq("a_2", $wnd.gwt.HelloClass.test18("a", ["b", "c"]));
      assertEq("a_b_1", $wnd.gwt.HelloClass.test18("a", "b", ["c"]));
      assertEq("a_1_0", $wnd.gwt.HelloClass.test20("a", 1));
      assertEq("a_1_3", $wnd.gwt.HelloClass.test20("a", 1, "a", "e", "i"));   
      assertEq("1970", "" + ($wnd.gwt.HelloClass.test22(new Date(0)).getYear() + 1900));
      
      var h = new $wnd.gwt.HelloClass();
      assertEq("102", h.test14(1, 1, [100]));
      assertEq("100,200", h.test15([100, 200]));
      assertEq("5", h.test17(5));
      assertEq("15", h.test17(5,10));
      assertEq("a_2", h.test19("a", ["b", "c"]));
      assertEq("a_b_1", h.test19("a", "b", ["c"]));
      assertEq("a_1_0", h.test21("a", 1));
      assertEq("a_1_3", h.test21("a", 1, "a", "e", "i")); 
      assertEq("70-111-", h.test23(new Date(50680), new Date(1309777010000)));
      assertEq("70", "" + h.test24()[0].getYear());   
      assertEq("true", h.test25(true)); 
      assertEq("false", h.test25(false)); 
      assertEq("true", h.test26(3)); 
      assertEq("true", h.test27(true, true, 3));    
      
      var v1 = new $wnd.gwt.Foo();
      assertEq("foo", v1);
      var v2 = new $wnd.gwt.Foo("foo2");
      assertEq("foo2", v2);
      var v3 = new $wnd.gwt.Foo("foo3", "bbb");
      assertEq("foo3bbb", v3);
      assertEq("foo3bbb>ccc", v3.toString("ccc"));
      assertEq("Hello,Friend", v3.executeJsClosure(function(arg1, arg2) {
          return arg1 + "," + arg2;
      }));
      
      var m = new $wnd.gwt.MClass();
      assertEq("om0", m.m0());
      assertEq("m1", m.m1());
      assertEq("m1-23", m.m1(2, 3));
      assertEq("m2", m.m2());
      assertEq("m2", m.m2());
      assertEq("m3", m.m3());
      var m5 = $wnd.gwt.MClass.m5 ? "defined" : "undefined";
      assertEq("undefined", m5);    
      assertEq("final", m.f());
      
      // exportAll must export B
      var c = $wnd.gwt.B ? "defined" : "undefined";
      assertEq("defined", c); 
      
      var ch = new $wnd.$$();
      assertEq("Son", ch.sonName(ch));
      assertEq("Son", ch.getParentName(ch.parent()));
      assertEq("$$$", $wnd.$$$());
      
      // export overlay
      var gq = new  $wnd.$('hello'); 
      assertEq("hello", gq.echo());
      assertEq("hello", gq.gq().echo());
       
      var ex = gq.exports();
      assertEq("hello", ex[0].echo());
      
      assertEq("0", gq.countElements());
      assertEq("1", gq.countElements(document));
      assertEq("2", gq.countElements([document, window]));
      
      assertEq("object", (typeof gq.element()));
      assertEq("object", (typeof gq.elements()[0]));
      
      assertEq('whatever', gq.executeClosure(function(){return 'whatever';}));
      assertEq('false', gq.executeFunction(function(e){return e == null;}));
      assertEq('true', gq.executeFunction(function(e){return false;}, function(e){return e != null;}));
      assertEq('ret-true', gq.executeFunction2(function(e){return true;}).gq().echo());
      assertEq('ret-false', gq.executeFunction2(function(e){return false;}).echo());
      
      // export static constructors
      var cs1 = new $wnd.gwt.c('hello');
      assertEq("hello", cs1.echo());
      var cs2 = new $wnd.gwt.c('by');
      assertEq("hello", cs2.echo());
      
      // more tests for exportoverlay
      var child = new $wnd.ex.Child("Bill");
      var mother = new $wnd.ex.Mother();
      mother.setChild(child);
      assertEq("Bill", mother.getChild().name());
      assertEq("Bill2", mother.getChild().name(2));
      assertEq("Joe", child.name(new $wnd.ex.Child("Joe")));
      assertEq("s1-s2-Foo-2", child.foo('s1', 's2', 2));
      assertEq("s1-Caa", child.foo('s1'));
      assertEq("null-Caa", child.foo(null));
      assertEq("s-Cas", $wnd.ex.Child.sfoo('s'));
      assertEq("2-Cas", $wnd.ex.Child.sfoo(2));    
      
      // tests for ExportJsInit
      var jq = new $wnd.JQ();
      assertEq("1", "" + jq.length);
      assertEq("1", "" + jq.size());
    } catch(e) {
      assertEq(null, "JS Exception: " + e);
    }; 
  }-*/;
  
  
  
  
  public static class Person {
    private String name;

    public Person(String cname) {
      name = cname;
    }

    public String getName() {
      return name;
    }
  }

  @ExportPackage("ex")
  @Export("Man")
  public static class XPerson implements ExportOverlay<Person>{
    
    public XPerson(String s) {
    }

    public String getName() {
      return null;
    }

    @ExportConstructor
    public static Person constructor(String name, String surname) {
      return new Person(name + " " + surname);
    }

    @ExportInstanceMethod("foo")
    public static String instanceMethod(Person instance, String surname) {
      return instance.getName() + "-" + surname + " foo";
    }
    
    @ExportStaticMethod("sfoo")
    public static String staticMethod(String name)  {
      return name + " sfoo";
    }
  }
  
}
