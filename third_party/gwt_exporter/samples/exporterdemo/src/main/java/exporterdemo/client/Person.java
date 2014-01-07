package exporterdemo.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.StructuralType;

/**
 * Whitelist demo, manually export
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
@StructuralType
public class Person implements Exportable {

  private String firstName;

  private String lastName;

//  @Export
//  public enum TestEnum implements Exportable { 
//    
//    @Export INSTANCE {
//      Employee getSomeClass() {
//        return some;
//      }
//    };
//    public Employee some;
//    @Export
//    abstract Employee getSomeClass();
//  }
  /**
   */
  @Export
  public Person(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Person() {
  }

  /**
   */
  @Export
  public String getLastName() {
    return lastName;
  }

  /**
   */
  @Export
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   */
  @Export
  public String getFirstName() {
    return firstName;
  }

  /**
   */
  @Export
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   */
  @Export
  public String fullTitle() {
    return firstName + " " + lastName;
  }

  /**
   */
  @Export
  public Person getSelf() {
    return this;
  }
  
  @Export
  public static Person salute(String name) {
    return new Person();
  }
  
  @Export
  public static String salute(int x) {
    return "Hello " + x;
  }
}
