import java.io.Serializable;

/**
 * Project - Project 5  -  Product
 * This program is used to create a product with the name of the product, the name of the store selling the product,
 * the description, the quantity available for purchase, the price, and the seller's ID.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */
public class Product implements Serializable{
    private String store;
    private String name; //name of product
    private String description; //description of product
    private int quantityAvailable; //quantity available of the product
    private double price; //The price of product
    private String accountID; //account ID of seller who listed the product on the market


    public Product(String accountID, String store, String name, String description, int quantityAvailable, double price){
        this.store = store;
        this.name = name;
        this.description = description;
        this.quantityAvailable = quantityAvailable;
        this.price = price;
        this.accountID = accountID;
    }

    public Product() {
        this.name = null;
        this.description = null;
        this.quantityAvailable = 0;
        this.price = 0;
    }

    public void setAll(String store, String name, String description, int quantityAvailable, double price) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.quantityAvailable = quantityAvailable;
        this.price = price;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getName() {
        return name;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
