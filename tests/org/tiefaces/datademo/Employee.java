package org.tiefaces.datademo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Employee {
    private String name;
    private Double worktime;
    private Double payment;
    private Double bonus;
    private String birthDate;
    private Employee superior;
    private Double total;
    private String sex;

    static Random random = new Random(System.currentTimeMillis());
    static long current = System.currentTimeMillis();


    public Employee() {
        super();
    }


    public Employee(String name, Double worktime, Double payment, Double bonus, String birthDate, String sex) {
        this.name = name;
        this.worktime = worktime;
        this.payment = payment;
        this.bonus = bonus;
        this.birthDate = birthDate;
        this.sex = sex;
    }



    public static List<Employee> generate(int num){
        List<Employee> result = new ArrayList<>();
        for(int index = 0; index < num; index++){
            result.add( generateOne( Integer.toString(index)) );
        }
        return result;
    }
    


    public static Employee generateOne(String nameSuffix){
        SimpleDateFormat empDateformat = new SimpleDateFormat("yyyy/MM/dd");
        return new Employee("Employee " + nameSuffix, (double) random.nextInt(100), 1000 + random.nextDouble()*5000, random.nextInt(100)/100.0d, empDateformat.format(new Date(current - (1000000 + random.nextInt(1000000)))),"M");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Double getWorktime() {
		return worktime;
	}


	public void setWorktime(Double worktime) {
		this.worktime = worktime;
	}


	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Employee getSuperior() {
        return superior;
    }

    public void setSuperior(Employee superior) {
        this.superior = superior;
    }

    
    
    public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", birth date='" + birthDate + '\'' +
                ", worktime=" + worktime +
                ", payment=" + payment +
                '}';
    }
}
