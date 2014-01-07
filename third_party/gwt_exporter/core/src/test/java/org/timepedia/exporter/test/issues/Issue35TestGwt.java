package org.timepedia.exporter.test.issues;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportConstructor;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.ExportPackage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class Issue35TestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.timepedia.exporter.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    GWT.create(CoordinateOverlay.class);
    GWT.create(GeometryOverlay.class);
  }
  
  public static class Coordinate {
    int x, y;
    public Coordinate() {
    }
    public Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
    }
    public String toString() {
      return x + "x" + y;
    }
  }
  
  public static class Geometry {
    public Geometry() {
    }
    public Geometry(String geometryType, int srid, int precision) {
    }
    Coordinate[] coordinates;
    public Coordinate[] getCoordinates() {
      return coordinates;
    }
    public void setCoordinates(Coordinate[] coordinates) {
      this.coordinates = coordinates;
    }
  }
  
  @Export("Coordinate")
  @ExportPackage("sp")
  public static class CoordinateOverlay implements ExportOverlay<Coordinate> {
    @ExportConstructor
    public static Coordinate constructor(int x, int y) {
      return new Coordinate(x, y);
    }
    public String toString() {
      return "";
    }
  }

  @Export("Geometry")
  @ExportPackage("sp")
  public static class GeometryOverlay implements ExportOverlay<Geometry> {
    @ExportConstructor
    public static Geometry constructor(String geometryType, int srid, int precision) {
      return new Geometry(geometryType, srid, precision);
    }
    public Coordinate[] getCoordinates() {
      return null;
    }
    public void setCoordinates(Coordinate[] coordinates) {
    }
  }
  
  public static native String runJs() /*-{
    var geometry = new $wnd.sp.Geometry("Point", 0, 0);
    geometry.setCoordinates([ new $wnd.sp.Coordinate(10, 10), new $wnd.sp.Coordinate(20, 20) ]);
    return "" + geometry.getCoordinates();
  }-*/;

  public void testIssue() {
    assertEquals("10x10,20x20", runJs());
  }
}
