

This is the project ExportWidget which uses GwtQuery and GwtExporter libraries.

It tries to show how to look for widgets from js and how to use methods in 
those found widgets.

- Assuming you have installed maven, compile and install it just running:
$ mvn clean install

- Run it in development mode:
$ mvn gwt:run

- Import and run in Eclipse:

 The archetype generates a project ready to be used in eclipse, 
 but before importing it you have to install the following plugins:

    * Google plugin for eclipse (update-site: http://dl.google.com/eclipse/plugin/3.7 or 3.6 or 3.5)
    * Sonatype Maven plugin (update-site: http://m2eclipse.sonatype.org/site/m2e)

 Then you can import the project in your eclipse workspace:

    * File -> Import -> Existing Projects into Workspace 

 Finally you should be able to run the project in development mode and to run the gwt test unit.

    * Right click on the project -> Run as -> Web Application
    * Right click on the test class -> Run as -> GWT JUnit Test 

- Although the project has the files .classpath and .project, you could generate them running any 
 of the following commands:

$ mvn eclipse:m2eclipse  (if you like to use m2eclipse)
$ mvn eclipse:eclipse    (to use the project without m2eclipse)
