package org.timepedia.exporter.rebind;

public class Property {

	private String name;
	private JExportableMethod getter;
	private JExportableMethod setter;

	public Property(String name) {
		this.name = name;
	}

	public void setGetter(JExportableMethod getter) {
		this.getter = getter;
	}

	public void setSetter(JExportableMethod setter) {
		this.setter = setter;
	}

	public String getName() {
		return name;
	}

	public JExportableMethod getGetter() {
		return getter;
	} 
	
	public JExportableMethod getSetter() {
		return setter;
	}
	
}
