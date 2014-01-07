package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.junit.client.GWTTestCase;

public class Issue45TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }
  
  public void test() {
    ExporterUtil.exportAll();
    publish();
    assertEquals("bar,java.lang.ClassCastException,bar,foo", runJs());
  }
  
  @ExportPackage("module")
  @Export(value="Chart")
  public static class Chart  implements Exportable {
    String name;
    public void setName(String name) {
      this.name = name;
    }
    public String getName() {
      return name;
    }

    public static Chart create() {
      return new Chart();
    }
  }
  
  public static class ChartManager  implements Exportable {
    public static void addChart(Chart chart) {
      chart.setName("foo");
    }
  }
  
  private static native void publish() /*-{
    // Export some Jsni objects without using gwt-exporter
    $wnd.ChartManager = {
       addChart : function(chrt) { $entry( @org.timepedia.exporter.test.issues.Issue45TestGwt.ChartManager::addChart(*)(chrt)); }
      ,newChart : function(){ return @org.timepedia.exporter.test.issues.Issue45TestGwt.Chart::create(); }
      ,addChartFailure: function(msg) {$wnd.alert('no implementation err: ' + msg); }
      ,addChartSuccess: function(chart) {$wnd.alert('addChartSuccess not implemented'); }
    };
  }-*/;
  
  private static native String runJs() /*-{
    var ret = "";
    
    // Create a new Chart exported by gwt-exporter
    // actually it is not a gwt object but a js object with
    // wraps a reference to the real gwt instance.
    var cht = new $wnd.module.Chart();
    cht.setName('bar');
    ret += cht.getName(); // bar
    
    // If we pass this gwt-exporter object to a jsni method it should raise
    // a cast exception 
    ret += ","
    try {
      $wnd.ChartManager.addChart(cht);
    } catch(e) {
      ret += e; //java.lang.ClassCastException
    }
    ret += ","
    ret += cht.getName(); // bar
    
    // We have to pass the appropriate gwt instance of the Chart object
    // The gwt instance is stored in the .g property of the js exported object.
    $wnd.ChartManager.addChart(cht.g);
    ret += ","
    ret += cht.getName(); // foo
    
    return ret;
  }-*/;  
}