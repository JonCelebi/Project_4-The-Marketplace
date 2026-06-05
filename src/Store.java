import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project - Project 5  -  Store
 * This class allows for the creation of stores using the seller class. Sellers can maintain their stores and edit as
 * needed. Some parameters in this class are accessed in Customer to complete logic for certain methods.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */
public class Store implements Serializable {
    private String name;
    private String customerInformation;
    private int amtSold; // total amount of products sold for this specific store
    private ArrayList<Product> products;
    private String accountID;

    public Store(String accountID, String name) {
        this.accountID = accountID;
        this.name = name;
        this.customerInformation = null;
        this.amtSold = 0;
        this.products = new ArrayList<>(0);
    }

    public String getAccountID() {
        return accountID;
    }

    public int getAmtSold() {
        return amtSold;
    }


    public void setAmtSold(int amtSold) {
        this.amtSold = amtSold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addProduct(String accountID, String store, String name, String description, int quantity, double price) {
        products.add(new Product(accountID, store, name, description, quantity, price));
    }
    public void addProductObject(Product product){
        products.add(product);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
