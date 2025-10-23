package projectui.model;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String address;

    public User(String fullName, String email, String phone, String password, String address) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
    }

    // Getters and setters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
}
