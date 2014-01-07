package org.timepedia.exporter.client;

/**
 * Implements a decoupled Exportable interface. Type T will be exported.
 * 
 * Classes implementing this interface, should be marked with Export annotation and
 * have to implement stub methods and constructor of the methods in the original class
 * to export.
 * 
 * This classes could have static implementations of constructors and methods of the
 * original class which should be marked with ExportConstructor or ExportInstanceMethod.
 * 
 */
public interface ExportOverlay<T> extends Exportable {

}
