package org.tiefaces.datademo;


import java.util.ArrayList;
import java.util.List;

public class Department {
    private String name;
    private Employee chief = new Employee();
    private List<Employee> staff = new ArrayList<>();
    private String link;
    private int headAccount;

    
    
    

    /**
     * 
     */
    public Department() {
        super();
    }

    public Department(String name) {
        this.name = name;
    }

    public Department(String name, Employee chief, List<Employee> staff) {
        this.name = name;
        this.chief = chief;
        this.staff = staff;
    }


    public void addEmployee(Employee employee) {
        staff.add(employee);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Employee getChief() {
        return chief;
    }

    public void setChief(Employee chief) {
        this.chief = chief;
    }

    public List<Employee> getStaff() {
        return staff;
    }

    public void setStaff(List<Employee> staff) {
        this.staff = staff;
    }

  

    public int getHeadAccount() {
		return headAccount;
	}

	public void setHeadAccount(int headAccount) {
		this.headAccount = headAccount;
	}

	@Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", chief=" + chief +
                ", staff.size=" + staff.size() +
                '}';
    }
}
