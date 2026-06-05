import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Project 5 - Login
 * This program is used to log into to sign up for our program.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */

public class Login {
    ArrayList<String[]> accounts;
    Account finalAccount;
    Boolean newAccount = false;
    BufferedReader reader;
    PrintWriter writer;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public Login(BufferedReader reader, PrintWriter writer, ObjectOutputStream oos, ObjectInputStream ois) { // Constructor for the class
        this.reader = reader;
        this.writer = writer;
        this.oos = oos;
        this.ois = ois;
        this.accounts = getAccounts();
    }

    public boolean editAccount() throws IOException {
        int userChoice;
        String[] options = {"Account Name", "Password", "Email", "Delete Account", "Exit"};
        do {
            userChoice = JOptionPane.showOptionDialog(null,
                    "What part of the account would you like to edit?",
                    "Edit Account", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            switch (userChoice) {
                case 0:
                    String newName;
                    do {
                        newName = JOptionPane.showInputDialog("What would you like your new username to be? (No commas)");
                        if (newName == null) {
                            break;
                        }
                        if (newName.isEmpty() || newName.contains(",")) {
                            JOptionPane.showMessageDialog(null, "Invalid Input, please try again");
                        }
                    } while (newName.isEmpty() || newName.contains(","));
                    if (newName == null) {
                        break;
                    }
                    // Here the logic is written to make NetworkIO to server to update username
                    // Command pattern: 0,0,accountId,newUsername
                    String command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.UPDATE_USERNAME
                            + "," + finalAccount.getAccountID() + "," + newName;
                    oos.writeObject(command);
                    this.finalAccount.setAccountName(newName);
                    JOptionPane.showMessageDialog(null, "Your username has been changed!");
                    break;
                case 1:
                    String newPassword;
                    do {
                        newPassword = JOptionPane.showInputDialog("What would you like your new password to be?");
                        if (newPassword == null) {
                            break;
                        }
                        if (newPassword.isEmpty() || newPassword.contains(",")) {
                            JOptionPane.showMessageDialog(null, "Invalid Input, please try again");
                        }
                    } while (newPassword.isEmpty() || newPassword.contains(","));
                    if (newPassword == null) {
                        break;
                    }
                    // Here the logic is written to make NetworkIO to server to update password
                    // Command pattern: 0,1,accountId,newPassword
                    command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.UPDATE_PASSWORD
                            + "," + finalAccount.getAccountID() + "," + newPassword;
                    oos.writeObject(command);
                    JOptionPane.showMessageDialog(null, "Your password has been changed!");
                    break;
                case 2:
                    String newEmail;
                    do {
                        newEmail = JOptionPane.showInputDialog("What would you like your new email to be?");
                        if (newEmail == null) {
                            break;
                        }
                        if (newEmail.contains(",") || newEmail.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Invalid input, please try again");
                        }
                    } while (newEmail.contains(",") || newEmail.isEmpty());
                    if (newEmail == null) {
                        break;
                    }
                    // Here the logic is written to make NetworkIO to server to update email
                    // Command pattern: 0,2,accountId,newEmail
                    command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.UPDATE_EMAIL
                            + "," + finalAccount.getAccountID() + "," + newEmail;
                    oos.writeObject(command);
                    this.finalAccount.setEmail(newEmail);
                    JOptionPane.showMessageDialog(null, "Your email has been changed!");
                    break;
                case 3:
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete your account?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Here the logic is written to make NetworkIO to server to delete user account
                        // Command pattern: 0,3,accountId
                        command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.DELETE_ACCOUNT
                                + "," + finalAccount.getAccountID();
                        oos.writeObject(command);
                        JOptionPane.showMessageDialog(null, "Your account has been deleted");
                        return true;
                    }
                    break;
                case 4:
                    return false;
                default:
                    break;
            }
        } while (userChoice != 4);
        return false;
    }

    public Customer createCustomer() throws IOException {
        return new Customer(this.finalAccount.getAccountName(), this.finalAccount.getPassword(),
                this.finalAccount.getEmail(), this.finalAccount.isMerchant(), this.finalAccount.getAccountID(),
                this.oos, this.ois);
    }

    public Seller createMerchant() {
        return new Seller(this.finalAccount.getAccountName(), this.finalAccount.getPassword(),
                this.finalAccount.getEmail(), this.finalAccount.isMerchant(), this.finalAccount.getAccountID(),
                this.newAccount, this.oos, this.ois);
    }

    public boolean startCode() throws IOException {
        String[] options = {"Login", "Sign Up"};
        int choice;
        boolean successful = true; //will determine if the login is successful and the code needs to continue
        do {
            choice = JOptionPane.showOptionDialog(null, "Please select one of the following:",
                    "Login or Sign Up", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
            if (choice == 0) {
                do {
                    try {
                        accounts.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(null, "There are currently no accounts" +
                                ", please sign up first");
                        choice = 1;
                        break;
                    }
                    int index = login();
                    // Here the logic is written to make NetworkIO to server to get account from the server
                    // Command pattern: 0,6,index
                    if (index >= 0) {
                        String command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.GET_ACCOUNT
                                + "," + index;
                        oos.writeObject(command);
                        String[] accountInfo;
                        try {
                            accountInfo = (String[]) ois.readObject();
                        } catch (ClassNotFoundException e) {
                            accountInfo = new String[4];
                        }
                        this.finalAccount = createAccount(accountInfo, false);
                    } else {
                        successful = false;
                    }
                } while (false);
            }
            if (choice == 1) {
                accounts = getAccounts();
                String[] signUpInfo;
                try {
                    signUpInfo = signup(accounts);
                } catch (Exception e) {
                    successful = false;
                    break;
                }
                this.finalAccount = createAccount(signUpInfo, true);
                accounts.add(signUpInfo);
            } else if (choice != 0 && choice != 1) {
                JOptionPane.showMessageDialog(null, "Invalid Response, Please try again");
            }
        } while (choice != 0 && choice != 1);

        return successful;
    }

    public String[] signup(ArrayList<String[]> accounts) throws Exception {
        String[] result = new String[5]; // index 0 is username, 1 is password, 2 is email, 3 is merchant, 4 is ID
        result[0] = "";
        result[4] = "";
        boolean taken;

        result[0] = JOptionPane.showInputDialog(null, "What would you like your username to be?");
        while (result[0].contains(",") || result[0].isEmpty()) {
            result[0] = JOptionPane.showInputDialog(null, "Invalid Input, please try again");
        }
        if (!result[0].equals("")) {
            if (accounts != null) {
                do {
                    taken = false;
                    for (String[] account : accounts) {
                        if (account[0].equals(result[0])) {
                            taken = true;
                            result[0] = JOptionPane.showInputDialog(null, "Unfortunately that " +
                                    "username is taken, please try another one:");
                        }
                    }
                } while (taken);
                if (result[0] == null) {
                    throw new Exception();
                }
            }
            JOptionPane.showMessageDialog(null, "Congrats, your username has been confirmed");

            result[1] = JOptionPane.showInputDialog(null, "What would you like your password to be:");
            while (result[1].contains(",") || result[1].isEmpty()) {
                result[1] = JOptionPane.showInputDialog(null, "Invalid input, please try again");
            }
            JOptionPane.showMessageDialog(null, "Congrats, your password has been confirmed");

            result[2] = JOptionPane.showInputDialog(null, "Please enter the email you " +
                    "would like to use:");
            while (result[2].contains(",") || result[2].isEmpty()) {
                result[2] = JOptionPane.showInputDialog(null, "Invalid input, please try again");
            }
            JOptionPane.showMessageDialog(null, "Your email has been confirmed");

            String[] options = {"Customer Account", "Merchant Account"};
            int choice = JOptionPane.showOptionDialog(null, "Please select one of the following:",
                    "Account", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                result[3] = "false";
            } else if (choice == 1) {
                result[3] = "true";
            } else {
                throw new Exception();
            }
            try {
                accounts.get(0);
            } catch (IndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "There are currently no Seller accounts or shops, account registered as seller account instead.");
                result[3] = "true";
            }
        }
        return result;
    }


    private String assignID(ArrayList<String[]> accounts, String accountType, String username, boolean editFile) {
        if (accounts.size() != 0) {
            if (editFile) {
                int numberCreated = 1;
                for (int i = 0; i < accounts.size(); i++) {
                    if (accounts.get(i)[4].substring(0, 8).equals(accountType))
                        numberCreated++;
                }
                return (accountType + numberCreated);
            } else {
                String ID = "";
                for (int i = 0; i < accounts.size(); i++) {
                    if (accounts.get(i)[4].substring(0, 8).equals(accountType))
                        if (accounts.get(i)[0].equals(username))
                            ID = accounts.get(i)[4];

                }
                return ID;
            }
        }
        return (accountType + "1");
    }

    public Account createAccount(String[] accountInfo, boolean editFile) {
        Account account;
        accounts = getAccounts();
        if (Boolean.parseBoolean(accountInfo[3])) {
            accountInfo[4] = assignID(accounts, "merchant", accountInfo[0], editFile);
        } else {
            accountInfo[4] = assignID(accounts, "customer", accountInfo[0], editFile);
        }
        account = new Account(accountInfo[0], accountInfo[1], accountInfo[2], Boolean.parseBoolean(accountInfo[3]),
                accountInfo[4]);
        try {
            if (editFile) {
                // Here the logic is written to make NetworkIO to server to create account
                // Command pattern: 0,7,accountName,password,email,merchant,accountId
                String command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.CREATE_ACCOUNT
                        + "," + accountInfo[0] + "," + accountInfo[1] + "," + accountInfo[2] + "," + accountInfo[3]
                        + "," + accountInfo[4];
                oos.writeObject(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    public int login() throws IOException {
        int index = -10;
        boolean success;
        String username;
        do {
            username = JOptionPane.showInputDialog(null, "What is your username?",
                    "Login", JOptionPane.PLAIN_MESSAGE);
            if (username == null) {
                break; // break out of the loop
            } else if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Invalid input, please try again!",
                        "Login", JOptionPane.ERROR_MESSAGE);
                continue; // go to the next iteration of the loop
            }
            // Here the logic is written to make NetworkIO to server to check username
            // Command pattern: 0,4,username
            String command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.CHECK_USERNAME
                    + "," + username;
            oos.writeObject(command);
            try {
                index = (int) ois.readObject();
            } catch (ClassNotFoundException e) {
                index = -1;
            }
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "Invalid username, please try " +
                        "again!", "Login", JOptionPane.ERROR_MESSAGE);
            }
        } while (index == -1);

        if (username != null) {
            do {
                String password = JOptionPane.showInputDialog(null, "What is your " +
                        "password?", "Login", JOptionPane.PLAIN_MESSAGE);

                if (password == null) {
                    return -1;
                }
                // Here the logic is written to make NetworkIO to server to check password
                // Command pattern: 0,0,password,accountIndex
                String command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.CHECK_PASSWORD
                        + "," + password + "," + index;
                oos.writeObject(command);
                try {
                    success = (boolean) ois.readObject();
                } catch (ClassNotFoundException e) {
                    success = false;
                }
                if (!success) {
                    JOptionPane.showMessageDialog(null, "Invalid password, please try " +
                            "again!", "Login", JOptionPane.ERROR_MESSAGE);
                }
            } while (!success);
        }
        return index;
    }


    public Account getFinalAccount() {
        return finalAccount;
    }

    private ArrayList<String[]> getAccounts() {
        // Here the logic is written to make NetworkIO to server to get accounts of all the users
        // Command pattern: 0,0,accountId,newUsername
        String command = CommandConstants.LOGIN_COMMAND_PREFIX + "," + CommandConstants.GET_ACCOUNTS;

        try {
            oos.writeObject(command);
            accounts = (ArrayList<String[]>) ois.readObject();
        } catch (Exception e) {
            accounts = new ArrayList<>();
        }
        return accounts;
    }

    //Methods starting from here are called by server

    public void editUsername(String loggedInUser, String username) {
        this.accounts = readFile();
        for (int i = 0; i < accounts.size(); i++) {
            String[] placeholder = accounts.get(i);
            if (placeholder[4].equals(loggedInUser)) {
                placeholder[0] = username;
                accounts.set(i, placeholder);
                break;
            }
        }

        try {
            File file = new File("accounts.txt");
            FileOutputStream fos = new FileOutputStream(file, false);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < accounts.size(); i++) {
                pw.write(String.format("%s,%s,%s,%s,%s\n", accounts.get(i)[0], accounts.get(i)[1], accounts.get(i)[2],
                        accounts.get(i)[3], accounts.get(i)[4]));
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editPassword(String loggedInUser, String password) {
        this.accounts = readFile();
        for (int i = 0; i < accounts.size(); i++) {
            String[] placeholder = accounts.get(i);
            if (placeholder[4].equals(loggedInUser)) {
                placeholder[1] = password;
                accounts.set(i, placeholder);
                break;
            }
        }

        try {
            File file = new File("accounts.txt");
            FileOutputStream fos = new FileOutputStream(file, false);
            PrintWriter pw = new PrintWriter(fos);
            for (String[] account : accounts) {
                pw.write(String.format("%s,%s,%s,%s,%s\n", account[0], account[1], account[2], account[3], account[4]));
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editEmail(String loggedInUser, String email) { // Method allowing users to edit their emails form anywhere needed
        this.accounts = readFile();
        for (int i = 0; i < accounts.size(); i++) {
            String[] placeholder = accounts.get(i);
            if (placeholder[4].equals(loggedInUser)) {
                placeholder[2] = email;
                accounts.set(i, placeholder);
                break;
            }
        }

        try {
            File file = new File("accounts.txt");
            FileOutputStream fos = new FileOutputStream(file, false);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < accounts.size(); i++) {
                pw.write(String.format("%s,%s,%s,%s,%s\n", accounts.get(i)[0], accounts.get(i)[1], accounts.get(i)[2],
                        accounts.get(i)[3], accounts.get(i)[4]));
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(String loggedInUser) { // Method allowing a user to delete their account from anywhere needed
        this.accounts = readFile();
        try { // Try is catching any possible errors that might come from reading file
            File file = new File("accounts.txt");
            FileOutputStream fos = new FileOutputStream(file, false);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < accounts.size(); i++) { //
                if (!accounts.get(i)[4].equals(loggedInUser)) {
                    pw.write(String.format("%s,%s,%s,%s,%s\n", accounts.get(i)[0], accounts.get(i)[1], accounts.get(i)[2],
                            accounts.get(i)[3], accounts.get(i)[4]));
                }
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int checkUsername(String username) { // Checking if username exists
        this.accounts = readFile();
        int index = -1;
        for (int i = 0; i < accounts.size(); i++) {
            if (username.equals(accounts.get(i)[0])) {
                index = i;
            }
        }
        return index;
    }

    public boolean checkPassword(String password, int index) {
        this.accounts = readFile();
        String correctPassword = accounts.get(index)[1];
        return (password.equals(correctPassword));
    }

    public String[] getAccount(int index) {
        this.accounts = readFile();
        return accounts.get(index);
    }

    public void createAccount(String[] accountInfo) throws FileNotFoundException {
        File file = new File("accounts.txt");
        FileOutputStream fos = new FileOutputStream(file, true);
        PrintWriter pw = new PrintWriter(fos);
        pw.write(String.format("%s,%s,%s,%s,%s\n", accountInfo[0], accountInfo[1], accountInfo[2],
                accountInfo[3], accountInfo[4]));
        pw.close();
    }

    public ArrayList<String[]> getAccountsServer() {
        ArrayList<String[]> allAccounts = readFile();
        for (int i = 0; i < allAccounts.size(); i++) {
            //This is to avoid sending real password to client. As client should not have access to real password of other users.
            allAccounts.get(i)[1] = "dummyPassword";
        }
        return allAccounts;
    }

    private ArrayList<String[]> readFile() { // Reading from accounts.txt, accounts will store this info,
        // each element represents a new account
        ArrayList<String[]> accounts = new ArrayList<>(0);
        try {
            // creating new file, file reader, buffered reader
            File f = new File("accounts.txt"); // "accounts.txt" is a csv file where account login info is
            // stored
            f.createNewFile();
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);

            String line = bfr.readLine();
            while (line != null) {
                accounts.add(line.split(","));
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }
}