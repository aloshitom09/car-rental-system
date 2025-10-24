package projectui.model;

public class Company {
    private String companyName;
    private String email;
    private String phone;
    private String password;
    private String address;

    public Company(String companyName, String email, String phone, String password, String address) {
        this.companyName = companyName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
    }

    // Add getters if needed
    public String getCompanyName() { return companyName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
}
