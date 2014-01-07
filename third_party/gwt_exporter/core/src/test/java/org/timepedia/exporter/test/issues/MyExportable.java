package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

@ExportPackage("gwt")
@Export
public class MyExportable implements Exportable {

  String n = "no-id";

  public MyExportable() {
  }

  public MyExportable(String id) {
    n = id;
  }
  
  public String getId() {
    return n;
  }
}
