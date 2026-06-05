import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Project - Project 5  -  FileManager
 * This program is used to add, edit, and remove items from the shopping cart files
 * that will have to be managed for each customer.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */
public class FileManager {
    private String fileName;//name of product
    ArrayList<Product> products; // arraylist of products currently available
    ArrayList<Integer> buyAmounts; //arraylist of amount of items being purchased

    public FileManager(String fileName) {
        this.fileName = fileName;
        File f = new File(fileName);
        try { //creates a new file if there isn't one already
            f.createNewFile();
            readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getFileName() {
        return fileName;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Integer> getBuyAmounts() {
        return buyAmounts;
    }

    public void setBuyAmounts(ArrayList<Integer> buyAmounts) {
        this.buyAmounts = buyAmounts;
    }

    public ArrayList<Integer> readFile() throws FileNotFoundException {
        /*   From handout:
        "Sellers can import or export products for their stores using a csv file.
        All product details should be included, with one row per product."   */

        File f = new File(getFileName());
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>(); //arraylist of products in the file
        buyAmounts = new ArrayList<>(); //how many of each product that was bought

        try {
            String line = bfr.readLine();
            while (line != null) {
                list.add(line);
                line = bfr.readLine();
            }

            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    products.add(new Product());
                    Product current = products.get(i); //current product being worked on

                    current.setAccountID(list.get(i).split(",")[0]);
                    current.setStore(list.get(i).split(",")[1]);
                    current.setName(list.get(i).split(",")[2]);
                    current.setDescription(list.get(i).split(",")[3]);
                    buyAmounts.add(Integer.parseInt(list.get(i).split(",")[4]));
                    current.setPrice(Double.parseDouble(list.get(i).split(",")[5]));
                    current.setQuantityAvailable(Integer.parseInt(list.get(i).split(",")[6]));
                }
            }

            setProducts(products);
            bfr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return (buyAmounts);
    }

    public void addProduct(Product product, int buyAmount) throws IOException {
        try {
            products.add(product);
            setProducts(products);
        } catch (NullPointerException e) {
            products = new ArrayList<>();
            products.add(product);
            setProducts(products);
        }

        File f = new File(getFileName());
        FileWriter fw = new FileWriter(f);
        BufferedWriter bfw = new BufferedWriter(fw);

        try {
            bfw.write(product.getAccountID() + ",");
            bfw.write(product.getStore() + ",");
            bfw.write(product.getName() + ",");
            bfw.write(product.getDescription() + ",");
            bfw.write(buyAmount + ",");
            bfw.write(product.getPrice() + ",");
            bfw.write(product.getQuantityAvailable() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred when adding the product! " +
                    "Please try again.", "Shopping Cart", JOptionPane.ERROR_MESSAGE);
        }

        bfw.close();
    }

    public void removeProduct(int index) {
        ArrayList<Product> products = getProducts(); //product object arraylist being edited
        products.remove(index);
        setProducts(products);
    }

    public void logout(String accountID) throws IOException {
        File f = new File(accountID + ".txt");
        f.createNewFile();
        FileWriter fw = new FileWriter(f);
        BufferedWriter bfw = new BufferedWriter(fw);

        try {
            for (int i = 0; i < products.size(); i++) {
                bfw.write(products.get(i).getAccountID() + ",");
                bfw.write(products.get(i).getStore() + ",");
                bfw.write(products.get(i).getName() + ",");
                bfw.write(products.get(i).getDescription() + ",");
                bfw.write(buyAmounts.get(i) + ",");
                bfw.write(String.format("%.2f", products.get(i).getPrice()) + ",");
                bfw.write(products.get(i).getQuantityAvailable() + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred when logging out! ",
                    "Shopping Cart", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException ignored) {
        }
        bfw.close();
    }
}
