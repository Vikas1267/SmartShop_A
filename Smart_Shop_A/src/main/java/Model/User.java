package Model;

public class User extends Person {
    private final int userId;
    private final String username;

    public User(int userId, String firstName, String lastName, String username, String city, String email, String mobile) {
        super(firstName, lastName, city, email, mobile);
        this.userId = userId;
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
