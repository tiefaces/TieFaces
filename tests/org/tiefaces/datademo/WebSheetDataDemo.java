package org.tiefaces.datademo;

import java.util.ArrayList;
import java.util.List;


public class WebSheetDataDemo {
	
	
	
    private WebSheetDataDemo() {
		super();
	}

	public static List<Department> createDepartments()  {
        List<Department> departments = new ArrayList<>();
        Department department = new Department("IT");
		Employee chief;
		String url = "http://tiefaces.org";
		chief = new Employee("Derek", 15.0, 3000.0, 0.30, "1980/02/01","M");
		department.setChief(chief);
		department.setLink(url);
		department.addEmployee(new Employee("Elsa", 10.25, 1500.0, 0.15, "1974/03/02","F"));
		department.addEmployee(new Employee("Oleg", 20.0, 2300.0, 0.25, "1972/04/18","M"));
		department.addEmployee(new Employee("Neil", 18.0, 2500.0, 0.00, "1981/05/12","M"));
		department.addEmployee(new Employee("Maria", 15.2, 1700.0, 0.15, "1982/06/18","F"));
		department.addEmployee(new Employee("John", 20.0, 2800.0, 0.20, "1983/07/19","M"));
		departments.add(department);
		department = new Department("HR");
		chief = new Employee("Betsy", 22.0, 2200.0, 0.30, "1980/02/01","M");
		department.setChief(chief);
		department.setLink(url);
		department.addEmployee(new Employee("Olga", 21.0, 1400.0, 0.20, "1965/05/18","F"));
		department.addEmployee(new Employee("Helen", 20.0, 2100.0, 0.10, "1973/06/18","F"));
		department.addEmployee(new Employee("Keith", 20.25, 1800.0, 0.15, "1968/09/12","M"));
		department.addEmployee(new Employee("Cat", 22.0, 1900.0, 0.15, "1977/12/01","F"));
		departments.add(department);
		department = new Department("BA");
		chief = new Employee("Wendy", 20.0, 2900.0, 0.35, "1982/06/01","M");
		department.setChief(chief);
		department.setLink(url);
		department.addEmployee(new Employee("Denise", 20.1, 2400.0, 0.20, "1975/06/23","M"));
		department.addEmployee(new Employee("LeAnn", 18.0, 2200.0, 0.15, "1978/07/21","F"));
		department.addEmployee(new Employee("Natali", 18.25, 2600.0, 0.10, "1969/12/15","M"));
		department.addEmployee(new Employee("Martha", 20.1, 2150.0, 0.25, "1983/08/12","F"));
		departments.add(department);
        return departments;
    }
	

}
