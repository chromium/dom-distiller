package exporterdemo.client;

/**
 * Non-exportable class
 */
public class Manager {

  private Employee employee;

  public Manager(Employee employee) {

    this.employee = employee;
  }

  public Manager() {
  }

  public Employee getEmployee() {
    return employee;
  }

  public String getManagerEmployeeTitle(Manager m) {
    return m.getEmployee().getTitle();
  }

  public Manager getSelf() {
    return this;
  }
}
