package exporterdemo.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.ExportPackage;

/**
 *
 */
@ExportPackage("sample")
public interface ExportManager extends ExportOverlay<Manager> {

  @Export("getEmp")
  Employee getEmployee();

  @Export
  String getManagerEmployeeTitle(Manager m);

  @Export
  Manager getSelf();
}
