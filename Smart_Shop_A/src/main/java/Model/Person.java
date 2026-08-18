package Model;
public abstract class Person {
    private final String firstName;
    private final String lastName;
    private final String city;
    private final String email;
    private final String mobile;

    protected Person(String firstName, String lastName, String city, String email, String mobile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.email = email;
        this.mobile = mobile;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }
}
