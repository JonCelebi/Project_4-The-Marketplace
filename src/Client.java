import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Project - Project 5  -  Client
 * This class is the central location that houses all runnable class methods. They are run in this class and allow the
 * user to interact with the market using either a customer or seller profile. This serves as our client class.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */
public class Client{

    public static void main(String[] args) {

        BufferedReader reader = null;
        PrintWriter writer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            Socket socket = new Socket("localhost", 4242);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error! The client and server could not " +
                    "create a connection. Please try again.", "Client", JOptionPane.ERROR_MESSAGE);
        }

        Login newLogin = new Login(reader, writer, oos, ois); //Creates an account or logs in as an account in accounts.txt
        boolean successful = false; //determines if login was successful and code needs to continue
        try {
            successful = newLogin.startCode();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error! A problem occurred when trying to " +
                    "login. Please try again.", "Client", JOptionPane.ERROR_MESSAGE);
        }

        boolean isSeller; //determines if new login is seller or customer
        if (successful) {
            isSeller = newLogin.getFinalAccount().isMerchant();

            if (isSeller) {
                Seller seller = newLogin.createMerchant(); //save method, export, edit account
                do {
                    if (seller.printSellerDashboard())
                        try {
                            if (newLogin.editAccount()) {
                                break;
                            }
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                    "when trying to edit your account. Please try again.",
                                    "Client", JOptionPane.ERROR_MESSAGE);
                        }
                } while (!seller.endMethod());
            } else {
                try {
                    Customer customer = newLogin.createCustomer();
                    customer.mainMenu(newLogin);
                } catch (Exception ignored) {
                }
            }
            try {
                oos.writeObject("3,0");
            } catch (IOException ignored) {
            }

        }
        JOptionPane.showMessageDialog(null, "Thank you for visiting the MarketPlace, " +
                "come again!", "Purchase History", JOptionPane.INFORMATION_MESSAGE);
        }
}