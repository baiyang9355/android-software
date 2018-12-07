package pojo;

import java.io.Serializable;

public class Job implements Serializable {

    private String department; // 部门
    private String address; // 地址
    private Double salary; // 薪水

    public Job() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Job{" +
                "department='" + department + '\'' +
                ", address='" + address + '\'' +
                ", salary=" + salary +
                '}';
    }
}
