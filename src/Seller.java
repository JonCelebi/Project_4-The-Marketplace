import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Project - Project 5  -  Seller
 * This program is used to create a seller that can interact with the market. Sellers can create stores and add products
 * to any of their stores. Products and stores can be removed or edited as needed by the seller.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */

public class Seller extends Account {
    private ArrayList<Store> allStores;
    private ArrayList<String[]> transactions;
    private boolean endMethod = false;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public Seller(String accountName, String password, String email, Boolean merchant, String accountID, Boolean
            status, ObjectOutputStream oos, ObjectInputStream ois) {
        super(accountName, password, email, merchant, accountID);
        allStores = new ArrayList<>();
        this.oos = oos;
        this.ois = ois;
        if (status) {//status is if it is old seller or new seller, true is new seller
            File file = new File(accountID + ".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            readFile();
        }

    }

    public ArrayList<Store> getAllStores() {
        return allStores;
    }

    public void setAllStores(ArrayList<Store> allStores) {
        this.allStores = allStores;
    }

    public String[] getAllProductNames(Store store) {
        ArrayList<String> productNames = new ArrayList<>();

        for (int i = 1; i < store.getProducts().size() + 1; i++) {
            productNames.add(String.format("%d. %s", (i), store.getProducts().get(i - 1).getName()));
        }
        return productNames.toArray(new String[0]);
    }

    public double totalProfit() {
        double profit = 0;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i)[1].equals(getAccountID())) {
                profit += Double.parseDouble(transactions.get(i)[6]);
            }
        }
        return profit;
    }

    public double profitFromStore(String storeName) {
        double profit = 0;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i)[1].equals(getAccountID())) {
                if (transactions.get(i)[2].equals(storeName)) {
                    profit += Double.parseDouble(transactions.get(i)[6]);
                }
            }
        }
        return profit;
    }

    public double profitFromProduct(String storeName, String productName) {
        double profit = 0;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i)[1].equals(getAccountID())) {
                if (transactions.get(i)[2].equals(storeName)) {
                    if (transactions.get(i)[3].equals(productName))
                        profit += Double.parseDouble(transactions.get(i)[6]);
                }
            }
        }
        return profit;
    }

    public int amountProductSold(String storeName, String productName) {
        int productsSold = 0;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i)[1].equals(getAccountID())) {
                if (transactions.get(i)[2].equals(storeName)) {
                    if (transactions.get(i)[3].equals(productName))
                        productsSold += Integer.parseInt(transactions.get(i)[5]);
                }
            }
        }
        return productsSold;
    }

    public String[] getOnlyStoreNames() {
        ArrayList<String> storeNames = new ArrayList<>();
        for (int i = 0; i < allStores.size(); i++) {
            storeNames.add(allStores.get(i).getName());
        }
        return storeNames.toArray(new String[0]);
    }

    public String[] getAllStoreNames() {
        ArrayList<String> storeNames = new ArrayList<>();

        for (int i = 1; i < allStores.size() + 1; i++) {
            storeNames.add(String.format("%d. %s", (i), allStores.get(i - 1).getName()));
        }
        return storeNames.toArray(new String[0]);
    }

    public void createProduct(String store, String name, String description, int quantityAvailable, double price) {
        int index = 0;
        for (int i = 0; i < allStores.size(); i++) {
            if (store.equals(allStores.get(i).getName())) index = i;
        }
        allStores.get(index).addProduct(getAccountID(), store, name, description, quantityAvailable, price);
    }

    public int[] findProduct(String store, String name) {
        int[] temp = new int[2];
        for (int i = 0; i < allStores.size(); i++) {
            if (allStores.get(i).getName().equals(store)) {
                temp[0] = i;
                for (int j = 0; j < allStores.get(i).getProducts().size(); j++) {
                    if (name.equals(allStores.get(i).getProducts().get(j).getName()))
                        temp[1] = j;
                }
            }
        }
        return temp;

    }

    public void createStore(String name) {
        allStores.add(new Store(getAccountID(), name));
    }

    public void editProduct(String store, String originalName, String editedName, String description, int quantity, double price) {
        for (int i = 0; i < allStores.size(); i++) {
            if (allStores.get(i).getName().equals(store)) {
                for (int j = 0; j < allStores.get(i).getProducts().size(); j++) {
                    if (originalName.equals(allStores.get(i).getProducts().get(j).getName()))
                        allStores.get(i).getProducts().get(j).setAll(store, editedName, description, quantity, price);
                }
            }
        }
    }

    public void deleteProduct(String store, String name, Product product) {
        for (int i = 0; i < allStores.size(); i++) {
            if (allStores.get(i).getName().equals(store)) {
                for (int j = 0; j < allStores.get(i).getProducts().size(); j++) {
                    if (name.equals(allStores.get(i).getProducts().get(j).getName()))
                        allStores.get(i).getProducts().remove(j);
                }
            }
        }
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> allProducts = new ArrayList<>();
        for (int i = 0; i < allStores.size(); i++) {
            for (int j = 0; j < allStores.get(i).getProducts().size(); j++) {
                allProducts.add(allStores.get(i).getProducts().get(j));
            }
        }
        return allProducts;
    }

    public void importProduct() {

        String importedFileName;
        do {
            importedFileName = JOptionPane.showInputDialog(null,
                    "What is the file called?(excluding the .csv extension)", "Seller - Importing Product",
                    JOptionPane.QUESTION_MESSAGE);
            if ((importedFileName == null)) {
                JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Importing Product", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (importedFileName.isEmpty() || (importedFileName.contains(" "))) {
                JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Importing Product", JOptionPane.INFORMATION_MESSAGE);
            }

        } while ((importedFileName.isEmpty()) || (importedFileName.contains(" ")));

        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to import " + importedFileName + " .csv?",
                "Seller - Importing Product", JOptionPane.YES_NO_OPTION);

        if (reply == 0) {
            ArrayList<String[]> allProducts = new ArrayList<>();
            try {
                File f = new File(importedFileName + ".csv");
                FileReader fr = new FileReader(f);
                BufferedReader bfr = new BufferedReader(fr);

                String line = bfr.readLine();
                while (line != null) {
                    allProducts.add(line.split(","));
                    line = bfr.readLine();
                }
                bfr.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "File does not exist", "Seller - Importing Product", JOptionPane.INFORMATION_MESSAGE);
            }

            boolean isStoreExists;
            Boolean productExists;
            for (int i = 0; i < allProducts.size(); i++) {
                isStoreExists = false;
                productExists = false;
                for (int j = 0; j < allStores.size(); j++) {
                    if (allProducts.get(i)[0].equals(allStores.get(j).getName())) {
                        isStoreExists = true;
                        for (int k = 0; k < allStores.get(j).getProducts().size(); k++) {
                            if (allProducts.get(i)[1].equals(allStores.get(j).getProducts().get(k).getName())) {
                                productExists = true;
                                break;
                            }
                        }
                    }
                }
                if (!(allProducts.get(i)[0].equals("null"))) {
                    if (!isStoreExists) {
                        createStore(allProducts.get(i)[0]);
                    }
                }

                if (!(allProducts.get(i)[1].equals("null"))) {
                    if (!productExists) {
                        createProduct(allProducts.get(i)[0], allProducts.get(i)[1], allProducts.get(i)[2], Integer.parseInt(allProducts.get(i)[3]), Double.parseDouble(allProducts.get(i)[4]));
                    }
                }
            }

            JOptionPane.showMessageDialog(null, "Successfully Imported!", "Seller - Importing Product", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Importing Product", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public boolean endMethod() {
        return endMethod;
    }

    public void readFile() { // reads an already existing seller file and puts the products in their respective stores.
        ArrayList<String[]> allProducts = new ArrayList<>();
        for (int i = 0; i < allStores.size(); i++) {
            allStores.remove(i);
        }
        try {
            // creating new file, file reader, buffered reader
            File f = new File(getAccountID() + ".txt"); // "getEmail().txt" is a csv file where allProducts are stored from the seller
            f.createNewFile();
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);

            String line = bfr.readLine();
            while (line != null) {
                allProducts.add(line.split(","));
                line = bfr.readLine();
            }
            bfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isStoreExists;

        for (int i = 0; i < allProducts.size(); i++) {
            isStoreExists = false;
            for (int j = 0; j < allStores.size(); j++) {
                if (allProducts.get(i)[0].equals(allStores.get(j).getName()))
                    isStoreExists = true;
            }
            if (!isStoreExists) {
                createStore(allProducts.get(i)[0]);
            }
            if (!(allProducts.get(i)[1].equals("null"))) {
                createProduct(allProducts.get(i)[0], allProducts.get(i)[1], allProducts.get(i)[2], Integer.parseInt(allProducts.get(i)[3]), Double.parseDouble(allProducts.get(i)[4]));
            }

        }
        readTransactions();
    }

    public void export() {

        String exportedFileName = JOptionPane.showInputDialog(null, "What would you like to call the exported file (Without .csv extension)?",
                "Seller - Exporting", JOptionPane.QUESTION_MESSAGE);
        if (exportedFileName == null) {
            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Exporting",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirmation;
        confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you would like to call your file " + exportedFileName + ".csv?",
                "Seller - Exporting", JOptionPane.YES_NO_OPTION);
        if (confirmation == 0) {
            try {
                oos.writeObject("2,0," + exportedFileName + "," + getAccountID());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JOptionPane.showMessageDialog(null, "File successfully exported!", "Seller - Exporting",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (confirmation != 0) {
            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Exporting",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void readTransactions() {
        ArrayList<String[]> allTransactions = new ArrayList<>(); // add this line to store transactions

        try {
            File f = new File("transactions.txt");
            f.createNewFile();
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);
            String line = bfr.readLine();
            while (line != null) {
                allTransactions.add(line.split(","));
                line = bfr.readLine();
            }
            bfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < allTransactions.size(); i++) {
            if (!(allTransactions.get(i)[1].equals(getAccountID())))
                allTransactions.remove(i);
        }

        transactions = allTransactions;

    }

    public boolean printSellerDashboard() {

        String userChoice;
        do {
            String[] choices = new String[8];
            choices[0] = "1. Create Store";
            choices[1] = "2. Edit/Delete Store";
            choices[2] = "3. Create Product/Import Products";
            choices[3] = "4. Edit Product";
            choices[4] = "5. View Statistics";
            choices[5] = "6. Edit Account";
            choices[6] = "7. Export Products";
            choices[7] = "8. Log out";
            userChoice = (String) JOptionPane.showInputDialog(null, String.format("What would you like to do?"),
                    "Seller - Dashboard", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
            if (userChoice != null) {
                switch (userChoice) {
                    case "1. Create Store" -> {
                        String newStoreName;
                        Boolean storeExists;
                        do {
                            newStoreName = JOptionPane.showInputDialog(null, "What would you like to call your new store? (No Commas Allowed)",
                                    "Seller - Creating Store", JOptionPane.QUESTION_MESSAGE);
                            storeExists = false;
                            if (newStoreName == null)
                                break;
                            for (int i = 0; i < allStores.size(); i++) {
                                if (newStoreName.equals(allStores.get(i).getName())) {
                                    storeExists = true;
                                    JOptionPane.showMessageDialog(null, "Store already exists", "Seller - Creating Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }
                            }
                            if (newStoreName.contains(",") || newStoreName.isEmpty())
                                JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Creating Store",
                                        JOptionPane.INFORMATION_MESSAGE);
                        } while (newStoreName.contains(",") || newStoreName.isEmpty() || storeExists);

                        if (newStoreName != null) {
                            readFile();
                            createStore(newStoreName);
                            saveToServer(oos, ois);
                            JOptionPane.showMessageDialog(null, "Store successfully created!", "Seller - Creating Store",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                    }

                    case "2. Edit/Delete Store" -> {
                        String[] option2Choices = new String[3];
                        option2Choices[0] = "1. Edit Store Name";
                        option2Choices[1] = "2. Delete Store";
                        option2Choices[2] = "3. Go Back";


                        String response;
                        do {
                            response = (String) JOptionPane.showInputDialog(null, "Please choose one of the following",
                                    "Seller - Editing Store", JOptionPane.QUESTION_MESSAGE,
                                    null, option2Choices, option2Choices[0]);
                            if (response == null) {
                                break;
                            }

                            if (response.equals("1. Edit Store Name")) {

                                String[] stores = getAllStoreNames();
                                if (stores.length == 0) {
                                    JOptionPane.showMessageDialog(null, "There are no stores to edit", "Seller - Editing Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }
                                String placeholder = (String) JOptionPane.showInputDialog(null, "Which store would you like to edit?",
                                        "Seller - Editing Store", JOptionPane.QUESTION_MESSAGE,
                                        null, stores, stores[0]);
                                if (placeholder == null) {
                                    break;
                                }
                                int optionStore = Integer.parseInt(placeholder.substring(0, placeholder.indexOf("."))) - 1;
                                String responseEditStore;
                                Boolean storeExists;
                                do {
                                    responseEditStore = JOptionPane.showInputDialog(null, "What do you like to edit its name to be?(No commas)",
                                            "Seller - Editing Store", JOptionPane.QUESTION_MESSAGE);
                                    if (responseEditStore == null) {
                                        break;
                                    }
                                    storeExists = false;
                                    for (int i = 0; i < allStores.size(); i++) {
                                        if (allStores.get(optionStore).getName().equals(allStores.get(i).getName())) {
                                            storeExists = true;
                                            JOptionPane.showMessageDialog(null, "Store already exists", "Seller - Editing Store",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                    }
                                    if (responseEditStore.contains(",") || responseEditStore.isEmpty())
                                        JOptionPane.showMessageDialog(null, "Invalid input, please try again without commas", "Seller - Editing Store",
                                                JOptionPane.INFORMATION_MESSAGE);

                                } while (responseEditStore.contains(",") || responseEditStore.isEmpty() || storeExists);
                                if (responseEditStore == null) {
                                    break;
                                }
                                readFile();
                                allStores.get(optionStore).setName(responseEditStore);
                                saveToServer(oos, ois);
                                JOptionPane.showMessageDialog(null, "Store successfully edited!", "Seller - Editing Store",
                                        JOptionPane.INFORMATION_MESSAGE);


                            } else if (response.equals("2. Delete Store")) {
                                String[] stores = getAllStoreNames();
                                if (stores.length == 0) {
                                    JOptionPane.showMessageDialog(null, "There are no stores to delete", "Seller - Editing Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }
                                String storeToDelete;
                                storeToDelete = (String) JOptionPane.showInputDialog(null, "Which store would you like to delete?",
                                        "Seller - Editing Store", JOptionPane.QUESTION_MESSAGE,
                                        null, stores, stores[0]);
                                if (storeToDelete == null) {
                                    JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Editing Store", JOptionPane.INFORMATION_MESSAGE);
                                }
                                readFile();
                                allStores.remove(Integer.parseInt(storeToDelete.substring(0, storeToDelete.indexOf("."))) - 1);
                                saveToServer(oos, ois);
                                JOptionPane.showMessageDialog(null, "Store successfully deleted", "Seller - Editing Store",
                                        JOptionPane.INFORMATION_MESSAGE);

                            } else {
                                JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Editing Store", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }
                            break;
                        } while (false);
                    }

                    case "3. Create Product/Import Products" -> {
                        String[] option3Choices = new String[3];
                        option3Choices[0] = "1. Create New Product";
                        option3Choices[1] = "2. Import Product";
                        option3Choices[2] = "3. Go Back";

                        String responseCreateImport;
                        do {
                            responseCreateImport = (String) JOptionPane.showInputDialog(null, "Would you like to import product or create new one?",
                                    "Seller - Editing Store", JOptionPane.QUESTION_MESSAGE,
                                    null, option3Choices, option3Choices[0]);
                            if (responseCreateImport == null) {
                                break;
                            }
                            switch (responseCreateImport) {
                                case "1. Create New Product" -> {
                                    String[] stores = getAllStoreNames();
                                    if (stores.length == 0) {
                                        JOptionPane.showMessageDialog(null, "There are no stores to put a product in. Create a store first.", "Seller - Adding Product",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    String productName;
                                    do {
                                        productName = JOptionPane.showInputDialog(null, "What name would you like to give to this product(No commas)",
                                                "Seller - Creating Product", JOptionPane.QUESTION_MESSAGE);
                                        if (productName == null) {
                                            break;
                                        }
                                        if (productName.contains(",") || productName.equals("") || productName.indexOf(" ") == 0)
                                            JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Adding Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                    } while (productName.contains(",") || productName.equals("") || productName.indexOf(" ") == 0);
                                    if (productName == null) {
                                        break;
                                    }
                                    String productDescription;
                                    do {
                                        productDescription = JOptionPane.showInputDialog(null, "What description would you like to give to this product",
                                                "Seller - Creating Product", JOptionPane.QUESTION_MESSAGE);
                                        if (productDescription == null) {
                                            break;
                                        }
                                        if (productDescription.contains(",") || productDescription.equals("") || productDescription.indexOf(" ") == 0)
                                            JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Adding Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                    } while (productDescription.contains(",") || productDescription.equals("") || productDescription.indexOf(" ") == 0);
                                    if (productDescription == null) {
                                        break;
                                    }

                                    String amountAvailable;
                                    Boolean exceptionError;
                                    do {
                                        exceptionError = false;
                                        amountAvailable = JOptionPane.showInputDialog(null, "How much of this product would you like to sell?",
                                                "Seller - Creating Product", JOptionPane.QUESTION_MESSAGE);
                                        if (amountAvailable == null) {
                                            break;
                                        }
                                        try {
                                            if (Integer.parseInt(amountAvailable) <= 0) {
                                                JOptionPane.showMessageDialog(null, "Type a number above 0", "Seller - Edit Product",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                                exceptionError = true;
                                            }
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null, "Please type an integer", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            exceptionError = true;
                                        }
                                    } while (exceptionError);

                                    if (amountAvailable == null) {
                                        break;
                                    }

                                    String cost;
                                    do {
                                        exceptionError = false;
                                        cost = JOptionPane.showInputDialog(null, "How much would you like to sell this product for? ($ and , not needed)",
                                                "Seller - Creating Product", JOptionPane.QUESTION_MESSAGE);
                                        if (cost == null) {
                                            break;
                                        }
                                        try {
                                            if (Double.parseDouble(cost) <= 0) {
                                                JOptionPane.showMessageDialog(null, "Type a number above 0", "Seller - Adding Product",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                                exceptionError = true;
                                            }
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null, "Please type an Integer", "Seller - Adding Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            exceptionError = true;
                                        }
                                    } while (exceptionError);

                                    if (cost == null) {
                                        break;
                                    }

                                    String placeholder;
                                    Boolean productExists = false;
                                    int optionStore = 1;
                                    do {
                                        placeholder = (String) JOptionPane.showInputDialog(null, "Which store would you like to put this product in?",
                                                "Seller - Creating Product", JOptionPane.QUESTION_MESSAGE, null, stores, stores[0]);
                                        if (placeholder == null) {
                                            break;
                                        }
                                        if (placeholder.contains(",") || placeholder.equals("") || placeholder.indexOf(" ") == 0 || placeholder.equals("0")) {
                                            JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Adding Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            optionStore = Integer.parseInt(placeholder.substring(0, placeholder.indexOf(".")));
                                            productExists = false;
                                            for (int i = 0; i < allStores.get(optionStore - 1).getProducts().size(); i++) {
                                                if (productName.equals(allStores.get(optionStore - 1).getProducts().get(i).getName())) {
                                                    productExists = true;
                                                    JOptionPane.showMessageDialog(null, "There is already a product with the same name in this store", "Seller - Adding Product",
                                                            JOptionPane.INFORMATION_MESSAGE);
                                                    break;
                                                }
                                            }
                                        }

                                    } while (placeholder.contains(",") || placeholder.equals("") || placeholder.indexOf(" ") == 0 || placeholder.equals("0") || productExists);

                                    if (placeholder == null) {
                                        break;
                                    }
                                    readFile();
                                    createProduct(allStores.get(optionStore - 1).getName(), productName, productDescription, Integer.parseInt(amountAvailable), Double.parseDouble(cost));
                                    saveToServer(oos, ois);
                                    JOptionPane.showMessageDialog(null, "Product successfully created and placed in store!", "Seller - Adding Product",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                                case "2. Import Product" -> {
                                    readFile();
                                    JOptionPane.showMessageDialog(null, "File successfully saved!", "Seller - Saving data",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    importProduct();
                                    saveToServer(oos, ois);
                                }
                            }
                            break;
                        } while (false);
                    }
                    case "4. Edit Product" -> {
                        do {
                            String[] stores = getAllStoreNames();
                            if (stores.length == 0) {
                                JOptionPane.showMessageDialog(null, "There are no products to edit", "Seller - Editing Store",
                                        JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }
                            String optionStore = (String) JOptionPane.showInputDialog(null, "What store would you like to edit a product from?",
                                    "Seller - Edit Product", JOptionPane.QUESTION_MESSAGE, null, stores, stores[0]);
                            if ((optionStore == null)) {
                                break;
                            }
                            optionStore = optionStore.substring(0, optionStore.indexOf("."));
                            String[] products = getAllProductNames(allStores.get(Integer.parseInt(optionStore) - 1));
                            if (products.length == 0) {
                                JOptionPane.showMessageDialog(null, "No products are in this store", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            } else {
                                String optionProduct = (String) JOptionPane.showInputDialog(null, "What product would you like to edit?",
                                        "Seller - Edit Product", JOptionPane.QUESTION_MESSAGE, null, products, products[0]);
                                if ((optionProduct == null)) {
                                    break;
                                }
                                optionProduct = optionProduct.substring(0, optionProduct.indexOf("."));

                                String[] option4Choices = new String[7];
                                option4Choices[0] = "1. Change Store of Product";
                                option4Choices[1] = "2. Name of Product";
                                option4Choices[2] = "3. Description of Product";
                                option4Choices[3] = "4. Amount Available of Product";
                                option4Choices[4] = "5. Price of Product";
                                option4Choices[5] = "6. Delete Product";
                                option4Choices[6] = "7. Go back";
                                String editOption;

                                editOption = (String) JOptionPane.showInputDialog(null, "What do you want to edit?",
                                        "Seller - Edit Product", JOptionPane.QUESTION_MESSAGE,
                                        null, option4Choices, option4Choices[0]);
                                if (editOption == null) {
                                    JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }
                                readFile();
                                if (editOption.equals("1. Change Store of Product")) {


                                    Boolean isInStore;
                                    Boolean productExists;
                                    String optionStore2;
                                    do {
                                        optionStore2 = (String) JOptionPane.showInputDialog(null, "What is the new store you want to store the product in?",
                                                "Seller - Edit Product", JOptionPane.QUESTION_MESSAGE, null, stores, stores[0]);
                                        if ((optionStore2 == null)) {
                                            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        optionStore2 = optionStore2.substring(0, optionStore2.indexOf("."));

                                        isInStore = false;
                                        productExists = false;

                                        for (int j = 0; j < allStores.get(Integer.parseInt(optionStore) - 1).getProducts().size(); j++) {
                                            if (allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(j).getName().equals(allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName()))
                                                productExists = true;
                                        }
                                        if (optionStore2.equals(optionStore)) {
                                            JOptionPane.showMessageDialog(null, "The product is already in this store", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            isInStore = true;
                                        } else if (productExists) {
                                            JOptionPane.showMessageDialog(null, "There is already a product with the same name in this store", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            readFile();
                                            createProduct(allStores.get(Integer.parseInt(optionStore) - 1).getName(),
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(),
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getDescription(),
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getQuantityAvailable(),
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getPrice());
                                            allStores.get(Integer.parseInt(optionStore) - 1).getProducts().remove((Integer.parseInt(optionProduct) - 1));
                                            saveToServer(oos, ois);
                                        }

                                    } while (isInStore || productExists);


                                }
                                if (editOption.equals("2. Name of Product")) {

                                    String newName;
                                    do {
                                        newName = JOptionPane.showInputDialog(null, String.format("What should be the new name of %s%n",
                                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName()),
                                                "Seller - Creating Store", JOptionPane.QUESTION_MESSAGE);
                                        if (newName == null) {
                                            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        if (newName.contains(",") || newName.isEmpty())
                                            JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                    } while (newName.contains(",") || newName.isEmpty());
                                    if (newName == null) {
                                        break;
                                    }

                                    String confirmation = JOptionPane.showConfirmDialog(null, String.format("Do you want to change %s to %s%n",
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(), newName),
                                            "Seller - Edit Account", JOptionPane.YES_NO_OPTION) + "";

                                    if (confirmation.equals("0")) {
                                        readFile();
                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).setAll(
                                                allStores.get(Integer.parseInt(optionStore) - 1).getName(),
                                                newName,
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getDescription(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getQuantityAvailable(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getPrice());
                                        saveToServer(oos, ois);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }


                                }
                                if (editOption.equals("3. Description of Product")) {


                                    String newDescription;
                                    do {
                                        newDescription = JOptionPane.showInputDialog(null, String.format("What should be the new description of %s%n",
                                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName()),
                                                "Seller - Creating Store", JOptionPane.QUESTION_MESSAGE);
                                        if (newDescription == null) {
                                            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        if (newDescription.contains(",") || newDescription.isEmpty())
                                            JOptionPane.showMessageDialog(null, "Invalid input, please try again", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                    } while (newDescription.contains(",") || newDescription.isEmpty());
                                    if (newDescription == null) {
                                        break;
                                    }

                                    String confirmation = JOptionPane.showConfirmDialog(null, String.format("Do you want to change %s to %s%n",
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(), newDescription),
                                            "Seller - Edit Account", JOptionPane.YES_NO_OPTION) + "";

                                    if (confirmation.equals("0")) {
                                        readFile();
                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).setAll(
                                                allStores.get(Integer.parseInt(optionStore) - 1).getName(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(),
                                                newDescription,
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getQuantityAvailable(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getPrice());
                                        saveToServer(oos, ois);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Cancelling changes, returning " +
                                                "to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }

                                }
                                if (editOption.equals("4. Amount Available of Product")) {

                                    Boolean exceptionError = false;
                                    String newQuantity;
                                    do {
                                        newQuantity = JOptionPane.showInputDialog(null, String.format("What should be the new quantity avaliable of %s%n",
                                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName()),
                                                "Seller - Creating Store", JOptionPane.QUESTION_MESSAGE);
                                        if (newQuantity == null) {
                                            JOptionPane.showMessageDialog(null, "Cancelling changes, " +
                                                    "returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        try {//TODO: does customer still display product if there is 0 left
                                            if (Integer.parseInt(newQuantity) <= 0) {
                                                JOptionPane.showMessageDialog(null, "Type a number above 0", "Seller - Edit Product",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                                exceptionError = true;
                                            }
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null, "Please type an integer", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            exceptionError = true;
                                        }
                                    } while (newQuantity.contains(",") || newQuantity.isEmpty() || exceptionError);
                                    if (newQuantity == null) {
                                        break;
                                    }

                                    String confirmation = JOptionPane.showConfirmDialog(null, String.format("Do you want to change %s to %s%n",
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(), newQuantity),
                                            "Seller - Edit Account", JOptionPane.YES_NO_OPTION) + "";

                                    if (confirmation.equals("0")) {
                                        readFile();
                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).setAll(
                                                allStores.get(Integer.parseInt(optionStore) - 1).getName(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getDescription(),
                                                Integer.parseInt(newQuantity),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getPrice());
                                        saveToServer(oos, ois);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }

                                }
                                if (editOption.equals("5. Price of Product")) {


                                    Boolean exceptionError = false;
                                    String newPrice;
                                    do {
                                        newPrice = JOptionPane.showInputDialog(null, String.format("What should be the new price of %s%n",
                                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName()),
                                                "Seller - Creating Store", JOptionPane.QUESTION_MESSAGE);
                                        if (newPrice == null) {
                                            JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        try {
                                            if (Double.parseDouble(newPrice) <= 0) {
                                                JOptionPane.showMessageDialog(null, "Type a number above 0", "Seller - Edit Product",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                                exceptionError = true;
                                            }
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null, "Please type an integer", "Seller - Edit Product",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            exceptionError = true;
                                        }
                                    } while (newPrice.contains(",") || newPrice.isEmpty() || exceptionError);
                                    if (newPrice == null) {
                                        break;
                                    }

                                    String confirmation = JOptionPane.showConfirmDialog(null, String.format("Do you want to change %s to %s%n",
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(), newPrice),
                                            "Seller - Edit Account", JOptionPane.YES_NO_OPTION) + "";

                                    if (confirmation.equals("0")) {
                                        readFile();
                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).setAll(
                                                allStores.get(Integer.parseInt(optionStore) - 1).getName(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getDescription(),
                                                allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getQuantityAvailable(),
                                                Double.parseDouble(newPrice));
                                        saveToServer(oos, ois);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }

                                }
                                if (editOption.equals("6. Delete Product")) {

                                    String confirmation = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to delete %s%n?",
                                                    allStores.get(Integer.parseInt(optionStore) - 1).getProducts().get(Integer.parseInt(optionProduct) - 1).getName()),
                                            "Seller - Edit Account", JOptionPane.YES_NO_OPTION) + "";

                                    if (confirmation.equals("0")) {
                                        readFile();
                                        allStores.get(Integer.parseInt(optionStore) - 1).getProducts().remove((Integer.parseInt(optionProduct) - 1));
                                        saveToServer(oos, ois);

                                    } else {
                                        JOptionPane.showMessageDialog(null, "Cancelling changes, returning to dashboard.", "Seller - Edit Product", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                }
                            }
                        } while (false);
                    }
                    case "5. View Statistics" -> {
                        //TODO: If I ever come back to this assignment,things I wish to do:
                        // I never sorted the dashboard for statistics,
                        // I didn't include list of customers and the number of items they purchased,
                        // I also never fixed the error with customer where when it tried to get the statistics, it didn't display the store correclty and the name of the account was just merchant1 and not the actual name
                        // and there was probably a bunch of other errors that I didn't account for, but most things work, and could clean stuff up so there are not 68 warnings in my code and things don't go past 120 characters
                        String[] option2Choices = new String[6];
                        option2Choices[0] = "1. Account Stats";
                        option2Choices[1] = "2. Current Store/Product Profit";
                        option2Choices[2] = "3. All Transactions";
                        option2Choices[3] = "4. View Stores";
                        option2Choices[4] = "5. View Products";
                        option2Choices[5] = "6. Go Back";

                        String responseStatistics;
                        do {
                            //TODO: if a store is deleted, you still need the stats for that
                            responseStatistics = (String) JOptionPane.showInputDialog(null,
                                    "Please choose one of the following", "Seller - Statistic",
                                    JOptionPane.QUESTION_MESSAGE, null, option2Choices, option2Choices[0]);
                            readFile();
                            if (responseStatistics == null) {
                                break;
                            } else if (responseStatistics.equals("1. Account Stats")) {
                                String appendedString = "~~~~~~~~~~~~~\n";
                                appendedString += String.format("Amount of Stores: %d%nAmount of Products: %d%nTotal Amount of Profit: $%.2f%n",
                                        allStores.size(), getProducts().size(), totalProfit());
                                appendedString += "~~~~~~~~~~~~~\n";
                                JOptionPane.showMessageDialog(null, appendedString, "Seller - Statistic", JOptionPane.INFORMATION_MESSAGE);

                                break;
                            } else if (responseStatistics.equals("2. Current Store/Product Profit")) {

                                String appendedString = "Click a store for more product profit\n~~~~~~~~~~~~~\n";
                                String tempString = "";

                                for (int i = 0; i < allStores.size(); i++) {
                                    tempString = String.format("Store: %s%nNumber of Products: %d%nProfit from Store: $%.2f%n",
                                            allStores.get(i).getName(), allStores.get(i).getProducts().size(), profitFromStore(allStores.get(i).getName()));
                                    appendedString += tempString + "~~~~~~~~~~~~~\n";
                                }

                                String responseStore = (String) JOptionPane.showInputDialog(null,
                                        appendedString, "Seller - Statistic",
                                        JOptionPane.QUESTION_MESSAGE, null, getAllStoreNames(), getAllStoreNames()[0]);
                                if (responseStore == null) {
                                    break;
                                }
                                int storeIndex = Integer.parseInt(responseStore.substring(0, responseStore.indexOf("."))) - 1;
                                String moreStats = String.format("Store: %s%nProducts Sold: ", allStores.get(storeIndex).getName());
                                if (allStores.get(storeIndex).getProducts().size() == 0) {
                                    moreStats += "None";
                                } else {
                                    for (int i = 0; i < allStores.get(storeIndex).getProducts().size(); i++) {
                                        if (i < allStores.get(storeIndex).getProducts().size() - 1) {
                                            moreStats += allStores.get(storeIndex).getProducts().get(i).getName() + ", ";
                                        } else {
                                            moreStats += allStores.get(storeIndex).getProducts().get(i).getName();
                                        }
                                    }
                                    moreStats += "\nProduct Stats:\n";
                                    for (int i = 0; i < allStores.get(storeIndex).getProducts().size(); i++) {
                                        moreStats += String.format("%s profit: $%.2f%nAmount Sold: %d%n",
                                                allStores.get(storeIndex).getProducts().get(i).getName(),
                                                profitFromProduct(allStores.get(storeIndex).getName(), allStores.get(storeIndex).getProducts().get(i).getName()),
                                                amountProductSold(allStores.get(storeIndex).getName(), allStores.get(storeIndex).getProducts().get(i).getName()));
                                    }

                                }

                                JOptionPane.showMessageDialog(null, moreStats, "Seller - Statistic", JOptionPane.INFORMATION_MESSAGE);
                                break;

                            } else if(responseStatistics.equals("3. All Transactions")){


                                String appendedString="";
                                Boolean isPastTransactions=false;
                                for (int i = 0; i < transactions.size(); i++) {
                                    if(transactions.get(i)[1].equals(getAccountID())){
                                                appendedString+="~~~~~~~~~~~~~\n";
                                                appendedString+=String.format("Store: %s%nProduct: %s%nDescription: %s%nAmount Sold: %s%nProfit: %s%n",transactions.get(i)[2],transactions.get(i)[3],transactions.get(i)[4],transactions.get(i)[5],transactions.get(i)[6]);
                                                isPastTransactions=true;
                                    }
                                }
                                if(isPastTransactions){
                                    appendedString+="~~~~~~~~~~~~~";
                                    JOptionPane.showMessageDialog(null, appendedString, "Seller - Statistic", JOptionPane.INFORMATION_MESSAGE);
                                }else{
                                    JOptionPane.showMessageDialog(null, "None", "Seller - Statistic", JOptionPane.INFORMATION_MESSAGE);
                                }

                            }
                            else if (responseStatistics.equals("4. View Stores")) {
                                String printString = "~~~~~~~~~~~~~\n";
                                for (int i = 0; i < allStores.size(); i++) {
                                    printString += String.format("Store: %s%nProducts Sold: ", allStores.get(i).getName());
                                    if (allStores.get(i).getProducts().size() == 0) {
                                        printString += "None";
                                    } else {
                                        for (int j = 0; j < allStores.get(i).getProducts().size(); j++) {
                                            if (j < allStores.get(i).getProducts().size() - 1) {
                                                printString += allStores.get(i).getProducts().get(j).getName() + ", ";
                                            } else {
                                                printString += allStores.get(i).getProducts().get(j).getName();
                                            }
                                        }
                                    }
                                    printString += "\n~~~~~~~~~~~~~";
                                }

                                JOptionPane.showMessageDialog(null, printString, "Seller - Statistic", JOptionPane.INFORMATION_MESSAGE);

                                break;

                            } else if (responseStatistics.equals("5. View Products")) {
                                String printString = "~~~~~~~~~~~~~\n";
                                for (int i = 0; i < getProducts().size(); i++) {
                                    printString += String.format("Store: %s%nProduct Name: %s%nDescription: %s%nAmount Available: %d%nPrice: %.2f%n~~~~~~~~~~~~~%n",
                                            getProducts().get(i).getStore(),
                                            getProducts().get(i).getName(),
                                            getProducts().get(i).getDescription(),
                                            getProducts().get(i).getQuantityAvailable(),
                                            getProducts().get(i).getPrice());
                                }
                                JOptionPane.showMessageDialog(null, printString, "Seller - Statistic", JOptionPane.INFORMATION_MESSAGE);
                                break;

                            } else if (responseStatistics.equals("6. Go Back")) {
                                break;
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid input, please " +
                                        "try again", "Seller - Dashboard", JOptionPane.INFORMATION_MESSAGE);
                            }
                            if (responseStatistics == null) {
                                break;
                            }
                        } while (false);
                    }
                    case "6. Edit Account" -> {
                        String responseEditAccount;
                        do {
                            responseEditAccount = JOptionPane.showConfirmDialog(null, "Are " +
                                    "you sure you would like edit your account?", "Seller - Edit " +
                                    "Account", JOptionPane.YES_NO_OPTION) + "";

                            if (responseEditAccount.equals("0")) {
                                return true;
                            } else if (!responseEditAccount.equals("1")) {
                                JOptionPane.showMessageDialog(null, "Invalid input, please " +
                                        "try again", "Seller - Editing Account", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } while ((!(responseEditAccount.equals("0"))) && (!(responseEditAccount.equals("1"))));
                    }
                    case "7. Export Products" -> {
                        readFile();
                        export();
                    }
                    case "8. Log out" -> {
                        endMethod = true;
                    }
                    default -> {
                        JOptionPane.showMessageDialog(null, "Invalid input, please try again",
                                "Seller - Dashboard", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                endMethod = true;
                break;
            }
            readTransactions();
            readFile();
        } while (!(userChoice.equals("8. Log out")));
        return false;
    }

    public void saveToServer(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            oos.writeObject("2,1," + getAccountID());
            oos.flush();
            oos.writeObject(getOnlyStoreNames());
            oos.writeObject(getProducts());
            oos.flush();
            ois.readObject();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error Saving",
                    "Seller - Dashboard", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error Saving",
                    "Seller - Dashboard", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
