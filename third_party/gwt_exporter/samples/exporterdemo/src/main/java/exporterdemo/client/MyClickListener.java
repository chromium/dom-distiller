package exporterdemo.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Example of closure exporting, gwt.export says export the class methods, and
 * gwt.exportClosure says auto-generate the makeClosure() method and closure
 * handling code for any methods which take this interface as a parameter
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
@Export
@ExportPackage("test")
@ExportClosure
public interface MyClickListener extends Exportable {

  /**
   * the call back
   */
  @Export
  public String onClick(Employee emp, String foo, int num);
}


