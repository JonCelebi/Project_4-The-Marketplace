/**
 * Project - Project 5  -  Account
 * This is a class within project 4 that maintains every seller and customer account. The users can edit their
 * information and also delete their account here.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */
public class Account {
    private String accountName;
    private String password;
    private String email;
    private boolean merchant;
    private final String accountID;

    public Account(String accountName, String password, String email, boolean merchant, String accountID) {
        this.accountName = accountName;
        this.password = password;
        this.email = email;
        this.merchant = merchant;
        this.accountID = accountID;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isMerchant() {
        return merchant;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
