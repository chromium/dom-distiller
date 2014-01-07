package org.timepedia.exporter.test;

import junit.framework.Test;

import org.timepedia.exporter.test.issues.*;

import com.google.gwt.junit.tools.GWTTestSuite;
 public class ExporterGwtSuiteTest extends GWTTestSuite
  {
      public static Test suite()
      {
          GWTTestSuite suite = new GWTTestSuite( "Exporter Suite" );
          suite.addTestSuite(CoreTestGwt.class );
          suite.addTestSuite(Issue25aTestGwt.class);
          suite.addTestSuite(Issue25bTestGwt.class);
          suite.addTestSuite(Issue33TestGwt.class);
          suite.addTestSuite(Issue34TestGwt.class);
          suite.addTestSuite(Issue35TestGwt.class);
          suite.addTestSuite(Issue38TestGwt.class);
          suite.addTestSuite(Issue44TestGwt.class);
          suite.addTestSuite(Issue48TestGwt.class);
          suite.addTestSuite(Issue49TestGwt.class);
          suite.addTestSuite(Issue50TestGwt.class);
          suite.addTestSuite(Issue51TestGwt.class);
          return suite;
      }
}