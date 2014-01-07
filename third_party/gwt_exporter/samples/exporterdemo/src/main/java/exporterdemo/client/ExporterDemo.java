package exporterdemo.client;

import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ExporterDemo implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    
    try {
      ExporterUtil.exportAll();
    } catch (Exception e) {
      Window.alert("Exception " + e.toString());
    }
  }
}
