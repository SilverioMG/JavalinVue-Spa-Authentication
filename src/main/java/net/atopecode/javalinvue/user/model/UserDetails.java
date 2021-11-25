package net.atopecode.javalinvue.user.model;

public class UserDetails {

    private String dateOfBirth;
    private Double salary;

    public UserDetails(String dateOfBirth, Double salary) {
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
    }

    public UserDetails(UserDetails otherUserDetails){
        this.dateOfBirth = otherUserDetails.dateOfBirth;
        this.salary = otherUserDetails.salary;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "dateOfBirth='" + dateOfBirth + '\'' +
                ", salary=" + salary +
                '}';
    }
}
