import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.*;

/**
 * Project - Project 5  -  Customer
 * This program is used to create a customer that can interact with the market. Customers can add products to their
 * shopping carts, edit their profile, and edit their cart as needed. Their history will be saved and can be exported.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */

public class Customer extends Account {
    // server & client objects to pass and read information
    ObjectOutputStream oos;
    ObjectInputStream ois;

    private static final String[] storeDashboard = {"Number of products sold by store", "Stores you've previously " +
            "purchased from", "Return back to main menu"}; //store dashboard menu options to be used in JOptionPane
    private static final String[] cartMenu = {"View shopping cart", "Checkout with current cart items", "Remove item" +
            " from cart", "Return back to main menu"}; //shopping cart menu options to be used in JOptionPane
    private static final String[] listingPage = {"Price (low to high)", "Price (high to low)", "Quantity available",
            "Keyword search"};

    public Customer(String accountName, String password, String email, boolean merchant, String accountID,
                    ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        super(accountName, password, email, merchant, accountID);

        if (!accountID.equals("no")) {
            oos.writeObject("1,0");
            oos.writeObject(accountID); //name of their shopping cart file being sent to server
        }

        this.oos = oos;
        this.ois = ois;
    }

    public ArrayList<Boolean> getStoreHistory() throws IOException, ClassNotFoundException {
        File f = new File("transactions.txt");
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        ArrayList<String> list = new ArrayList<>(); //list of lines that have customer ID in them
        oos.writeObject("1,2,1");
        Store[] stores = (Store[]) ois.readObject(); //array containing all stores
        ArrayList<Boolean> storeHistory = new ArrayList<>(); //determines if a store is in this customer's store history
        ArrayList<Product> purchases = new ArrayList<>(); //arraylist of products previously purchased by this customer

        for (int i = 0; i < stores.length; i++) { //initializes length of the arraylist
            storeHistory.add(false);
        }

        try {
            String line = bfr.readLine();
            while (line != null) {
                if (line.split(",")[0].equals(getAccountID())) {
                    list.add(line);
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < list.size(); i++) {
            purchases.add(new Product());
            Product current = purchases.get(i); //current product being worked on
            current.setStore(list.get(i).split(",")[2]);

            for (int j = 0; j < stores.length; j++) {
                if (current.getStore().equals(stores[j].getName())) {
                    storeHistory.set(j, true);
                    break;
                }
            }
        }

        return storeHistory;
    }

    //the history parameter is used to determine if the customerHistory.txt file is needed
    public void purchaseHistory(String name) throws IOException {

        File f = new File("transactions.txt");
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        ArrayList<String> list = new ArrayList<>(); //list of lines that have customer ID in them
        ArrayList<Product> purchases = new ArrayList<>(); //arraylist of products previously purchased by this customer
        ArrayList<Integer> buyAmounts = new ArrayList<>(); //arraylist of amount purchased on each transaction

        try {
            String line = bfr.readLine();
            while (line != null) {
                if (line.split(",")[0].equals(getAccountID())) {
                    list.add(line);
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < list.size(); i++) {
            purchases.add(new Product());
            Product current = purchases.get(i); //current product being worked on

            current.setAccountID(list.get(i).split(",")[1]); //seller ID
            current.setStore(list.get(i).split(",")[2]);
            current.setName(list.get(i).split(",")[3]);
            current.setDescription(list.get(i).split(",")[4]);

            buyAmounts.add(Integer.valueOf(list.get(i).split(",")[5]));
            current.setPrice(Double.parseDouble(list.get(i).split(",")[6]));
        }

        oos.writeObject("1,0,7"); //runs historyFIle
        oos.writeObject(getAccountID());
        oos.writeObject(purchases);
        oos.writeObject(buyAmounts);
        oos.writeObject(name);

        bfr.close();
    }

    public void historyFile(ArrayList<Product> purchases, ArrayList<Integer> buyAmounts, String name)
            throws FileNotFoundException {
        File f = new File(("" + name + "customerHistory.txt"));
        try { //creates a new file if there isn't one already
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //reads through whole transaction file every time. must rewrite to not have duplicates
        FileOutputStream fos = new FileOutputStream(f, false);
        PrintWriter pw = new PrintWriter(fos);

        for (int i = 0; i < purchases.size(); i++) {
            pw.println("Product name: " + purchases.get(i).getName());
            pw.println("Product description: " + purchases.get(i).getDescription());
            pw.printf("%d %s products purchased at a price of $%.2f each\n",
                    (buyAmounts.get(i)), purchases.get(i).getName(), purchases.get(i).getPrice());
            pw.println("Purchased from seller " + purchases.get(i).getAccountID() +
                    "'s " + purchases.get(i).getStore() + " store");

            if (i < purchases.size() - 1) {
                pw.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
            }
        }

        pw.close();
    }

    private void storeDashboard() throws IOException, ClassNotFoundException {
        String option;  //menu option selected
        int optionIndex = -1; //index of the chosen option

        option = (String) JOptionPane.showInputDialog(null, "How would you like to " +
                        "sort the dashboard?", "Store Dashboard", JOptionPane.QUESTION_MESSAGE,
                null, storeDashboard, storeDashboard[0]);

        for (int i = 0; i < storeDashboard.length; i++) {
            if (storeDashboard[i].equals(option)) {
                optionIndex = i + 1;
                break;
            }
        }

        oos.writeObject("1,2,1");
        Store[] stores = (Store[]) ois.readObject(); //array containing all stores
        String[] storeDashboard = new String[stores.length * 3]; //dashboard info printed to JOptionPane
        ArrayList<Integer> revenues = new ArrayList<>(); //list of store revenues

        if (optionIndex == 1) {
            for (int i = 0; i < stores.length; i++) { //sets strings to storeDashboard
                for (int j = i * 3; j < (i * 3 + 3); j++) {
                    if (j % 3 == 0) {
                        storeDashboard[j] = "\n~ ~ ~ ~ ~ ~ ~ ~ ~ ~\nStore: " + stores[i];
                    } else if (j % 3 == 1) {
                        storeDashboard[j] = "\nSeller: " + stores[i].getAccountID();
                    } else {
                        storeDashboard[j] = "\nAmount of products sold: " + stores[i].getAmtSold();
                    }
                }
                revenues.add(stores[i].getAmtSold());
            }
            String dashboardString; //string to be put in JOptionPane
            dashboardString = Arrays.toString(storeDashboard).replace(",", "")
                    .replace("[", "").replace("]", "");

            revenues.sort(Collections.reverseOrder()); //todo: does this work? I feel like it shouldn't

            JOptionPane.showMessageDialog(null, "Here is a list of store descriptions, " +
                            "sorted from highest to lowest product sales." + dashboardString, "Store Dashboard",
                    JOptionPane.INFORMATION_MESSAGE);

        } else if (optionIndex == 2) {
            try {
                ArrayList<Boolean> storeHistory = getStoreHistory(); // stores this customer has previously shopped at
                Arrays.fill(storeDashboard, "");

                for (int i = 0; i < stores.length; i++) { //sets strings to storeDashboard
                    if (storeHistory.get(i)) {
                        for (int j = i * 3; j < (i * 3 + 3); j++) {
                            if (j % 3 == 0) {
                                storeDashboard[j] = "\n~ ~ ~ ~ ~ ~ ~ ~ ~ ~\nStore: " + stores[i];
                            } else if (j % 3 == 1) {
                                storeDashboard[j] = "\nSeller: " + stores[i].getAccountID();
                            } else {
                                storeDashboard[j] = "\nAmount of products sold: " + stores[i].getAmtSold();
                            }
                        }
                    }
                }

                String dashboardString; //string to be put in JOptionPane
                dashboardString = Arrays.toString(storeDashboard).replace(",", "")
                        .replace("[", "").replace("]", "");

                JOptionPane.showMessageDialog(null, "Here is a list of store descriptions " +
                                "from stores you've previously bought from." + dashboardString,
                        "Purchase History", JOptionPane.INFORMATION_MESSAGE);
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Error! You have no transaction history," +
                        " so there are no stores to display.", "Store Dashboard", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error! Something went wrong when trying" +
                        " to access your purchase history.", "Store Dashboard", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewShoppingCart() {
        ArrayList<Product> cartItems = new ArrayList<>(); //items currently in customer's cart
        ArrayList<Integer> buyAmounts = new ArrayList<>(); //# of specific items currently in the cart

        try {
            oos.writeObject("1,0,0");//changed something
            oos.writeObject(getAccountID());
            cartItems = (ArrayList<Product>) ois.readObject();
            buyAmounts = (ArrayList<Integer>) ois.readObject();
            if (cartItems.size() == 0 || buyAmounts.size() == 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error! Your shopping cart is empty, please" +
                    " add something to your cart to view.", "Shopping Cart", JOptionPane.ERROR_MESSAGE);
        }

        String[] currentItems = new String[cartItems.size() * 4]; //info to be printed to the JOptionPane

        for (int i = 0; i < cartItems.size(); i++) { //sets strings to currentItems
            for (int j = i * 4; j < (i * 4 + 4); j++) {
                if (j % 4 == 0) {
                    currentItems[j] = "\n~ ~ ~ ~ ~ ~ ~ ~ ~ ~\nProduct " + (i + 1) + ": " + cartItems.get(i).getName();
                } else if (j % 4 == 1) {
                    currentItems[j] = "\nQuantity bought: " + (buyAmounts.get(i));
                } else if (j % 4 == 2) {
                    currentItems[j] = "\nFrom store: " + cartItems.get(i).getStore();
                } else {
                    currentItems[j] = "\nDescription: " + cartItems.get(i).getDescription();
                }
            }
        }
        String currentItemsString; //string to be put in JOptionPane
        currentItemsString = Arrays.toString(currentItems).replace(",", "")
                .replace("[", "").replace("]", "");

        if (currentItems.length > 0) {
            JOptionPane.showMessageDialog(null, "Here are the current items in your cart." +
                    currentItemsString, "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void cartMenu() throws IOException, ClassNotFoundException {
        String choice; //menu option choice made by user
        int choiceIndex = 3; //index of menu option

        choice = (String) JOptionPane.showInputDialog(null, "Choose one of the following " +
                        "drop down options to continue.", "Shopping Cart Menu", JOptionPane.QUESTION_MESSAGE,
                null, cartMenu, cartMenu[0]);

        for (int i = 0; i < cartMenu.length; i++) {
            if (cartMenu[i].equals(choice)) {
                choiceIndex = i;
                break;
            }
        }

        if (choiceIndex == 0) {
            viewShoppingCart();
        } else if (choiceIndex == 1) {
            checkOut();
        } else if (choiceIndex == 2) {
            removeFromCart();
        }
    }

    private void checkOut() throws IOException, ClassNotFoundException {
        double totalCost = 0; //total cost of this customer's transaction
        ArrayList<Product> purchases = new ArrayList<>(); //items currently in customer's cart
        ArrayList<Integer> buyAmounts = new ArrayList<>(); //amount of items in cart, per item
        oos.writeObject("1,2,1");
        Store[] stores = (Store[]) ois.readObject(); //array containing all stores

        try {
            oos.writeObject("1,0,1");
            oos.writeObject(getAccountID());
            purchases = (ArrayList<Product>) ois.readObject();
            buyAmounts = (ArrayList<Integer>) ois.readObject();
            if (buyAmounts.size() == 0 || purchases.size() == 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error! There is nothing in your shopping " +
                    "cart, add an item to your cart to checkout.", "Shopping Cart", JOptionPane.ERROR_MESSAGE);
        }

        if (purchases != null && buyAmounts != null && !purchases.isEmpty() && !buyAmounts.isEmpty()) {

            for (int i = 0; i < purchases.size(); i++) {
                totalCost += purchases.get(i).getPrice() * buyAmounts.get(i);
                for (Store store : stores) { //updates store with new store amount sold
                    if (purchases.get(i).getStore().equals(store.getName())) { //finds product's store
                        store.setAmtSold(store.getAmtSold() + buyAmounts.get(i));
                    }
                }
            }
            oos.writeObject("1,0,2"); //updates shopping cart to null
            oos.writeObject(getAccountID());
            String cost = String.format("%.2f", totalCost); //string formatted order total

            JOptionPane.showMessageDialog(null, "You have successfully checked out, your order" +
                    " total was $" + cost + ".", "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeFromCart() throws IOException, ClassNotFoundException {
        int productChoice = -1; //index of item to be removed from cart
        int productsRemoved = 0; //amount of items subtracted from buyAmount if >1 when option 3 is selected
        ArrayList<Product> cartItems = new ArrayList<>(); //items currently in customer's cart
        ArrayList<Integer> buyAmounts = new ArrayList<>();  // # of specific items in their cart

        viewShoppingCart();

        try {
            oos.writeObject("1,0,0"); //gets items in customer's cart
            oos.writeObject(getAccountID());
            cartItems = (ArrayList<Product>) ois.readObject();
            buyAmounts = (ArrayList<Integer>) ois.readObject();
        } catch (Exception ignored) {
        }

        if (cartItems != null && cartItems.size() > 0) {
            String[] removeFromCart = new String[cartItems.size()]; //option choices for JOptionPane
            for (int i = 0; i < removeFromCart.length; i++) {
                removeFromCart[i] = cartItems.get(i).getName();
            }
            String choice; //option choice selected by user in JOptionPane

            choice = (String) JOptionPane.showInputDialog(null, "Choose a product from the list" +
                            " to remove it from your cart.", "Shopping Cart Menu", JOptionPane.QUESTION_MESSAGE,
                    null, removeFromCart, "None - return back to main menu");

            for (int i = 0; i <= removeFromCart.length; i++) { //accounts for "None" option
                if (removeFromCart[i].equals(choice)) {
                    productChoice = i;
                    break;
                }
            }

            if (productChoice > cartItems.size() || productChoice < 0) {
                JOptionPane.showMessageDialog(null, "Error! Product number must be a " +
                        "valid choice. Please try again.", "Shopping Cart", JOptionPane.ERROR_MESSAGE);
            }

            do { //IF BUY AMOUNT > 1, IT WILL ASK IF THE USER WANTS TO SUBTRACT LESS THAN THE FULL BUY AMOUNT
                try {
                    if (buyAmounts.get(productChoice) > 1) {
                        productsRemoved = Integer.parseInt(JOptionPane.showInputDialog(null, "How" +
                                " many would you like to remove?", "Shopping Cart", JOptionPane.QUESTION_MESSAGE));
                    }
                    if (productsRemoved > buyAmounts.get(productChoice) || productsRemoved < 0) {
                        JOptionPane.showMessageDialog(null, "Error! Product number must be a " +
                                "valid choice. Please try again.", "Shopping Cart", JOptionPane.ERROR_MESSAGE);
                        productsRemoved = -1;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error! Invalid product number chosen." +
                            " Please try again.", "Shopping Cart", JOptionPane.ERROR_MESSAGE);
                    productsRemoved = -1;
                }
            } while (productsRemoved < 0);

            if (productsRemoved == buyAmounts.get(productChoice)) { //checks if whole item will be removed from cart
                oos.writeObject("1,0,3"); //removes item from shopping cart
                oos.writeObject(getAccountID());
                oos.writeObject(productChoice);
            } else { //if not, it will only change the buy amount
                oos.writeObject("1,0,4"); //removes item from shopping cart
                oos.writeObject(getAccountID());
                oos.writeObject(productChoice);
                oos.writeObject(productsRemoved);
            }

            if (productsRemoved > 0) {
                JOptionPane.showMessageDialog(null, "The item was successfully removed from" +
                        " your cart.", "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
            }

            //these two lines update the product list to add the quantity available back up since removed from cart
            oos.writeObject("1,2,2,0"); //updates buyAmount of product chosen to be removed from cart
            cartItems = (ArrayList<Product>) ois.readObject(); //items currently in customer's cart
            oos.writeObject(productChoice);
            oos.writeObject(productsRemoved);
        }
    }

    private ArrayList<Product> keywordSearch(ArrayList<Product> productList) {
        ArrayList<Product> filteredProducts = new ArrayList<>(); //arraylist of products filtered by keyword

        do {
            // keyword is a keyword/phrase that will be used to filter and search products
            String keyword = JOptionPane.showInputDialog(null, "Type in a word or phrase to " +
                    "filter the available products.", "Listing Page", JOptionPane.QUESTION_MESSAGE);

            for (Product product : productList) {
                if (product.getName().contains(keyword)) {
                    filteredProducts.add(product);
                } else if (product.getDescription().contains(keyword)) {
                    filteredProducts.add(product);
                } else if (product.getStore().contains(keyword)) {
                    filteredProducts.add(product);
                }
            }

            if (filteredProducts.size() == 0) {
                JOptionPane.showMessageDialog(null, "Error! Key word or phrase did not result" +
                        " in any available products. Please try again.", "Listing Page", JOptionPane.ERROR_MESSAGE);
            }
        } while (filteredProducts.size() == 0);

        return (filteredProducts);
    }

    private void printListingPage() throws IOException, ClassNotFoundException {
        oos.writeObject("1,2,2");
        ArrayList<Product> productList = (ArrayList<Product>) ois.readObject(); //list of available products for sale
        ArrayList<Product> originalProductList = new ArrayList<>(productList);
        int optionIndex = -1; //option choice index selected for sorting
        int itemIndex = -1; //index of item in list to be purchased
        String option; //option choice selected for sorting

        option = (String) JOptionPane.showInputDialog(null, "Choose one of the following " +
                        "sorting options to continue.", "Listing Page", JOptionPane.QUESTION_MESSAGE, null,
                listingPage, listingPage[0]);

        for (int i = 0; i < listingPage.length; i++) {
            if (listingPage[i].equals(option)) {
                optionIndex = i;
                break;
            }
        }

        Comparator<Product> priceComparator = Comparator.comparingDouble(Product::getPrice); //http://bit.ly/3KGPXgU
        Comparator<Product> quantityComparator = Comparator.comparingInt(Product::getQuantityAvailable); //http://bit.ly/43sxWdN

        if (optionIndex == 0) {
            productList.sort(priceComparator);
        } else if (optionIndex == 1) {
            productList.sort(priceComparator);
            ArrayList<Product> placeholder = new ArrayList<>(); //holder variable to reverse the array
            for (int i = productList.size() - 1; i >= 0; i--) {
                placeholder.add(productList.get(i));
            }
            productList = placeholder;
        } else if (optionIndex == 2) {
            productList.sort(quantityComparator);
            ArrayList<Product> temp = new ArrayList<>(); //holder variable to reverse the array
            for (int i = productList.size() - 1; i >= 0; i--) {
                temp.add(productList.get(i));
            }
            productList = temp;
        } else if (optionIndex == 3) {
            productList = keywordSearch(productList); //sorts full product list based on key word or phrase
        }

        if (optionIndex != -1) {
            String[] currentItems = new String[productList.size() * 4]; //info to be printed to the JOptionPane
            String[] productNames = new String[productList.size()]; //names of products to be used in dropdown options

            for (int i = 0; i < productList.size(); i++) { //sets strings to currentItems
                for (int j = i * 4; j < (i * 4 + 4); j++) {
                    if (j % 4 == 0) {
                        currentItems[j] = "\n~ ~ ~ ~ ~ ~ ~ ~ ~ ~\nProduct " + (i + 1) + ": " + productList.get(i).getName();
                    } else if (j % 4 == 1) {
                        currentItems[j] = "\nQuantity available: " + productList.get(i).getQuantityAvailable();
                    } else if (j % 4 == 2) {
                        currentItems[j] = "\nFrom store: " + productList.get(i).getStore();
                    } else {
                        currentItems[j] = String.format("%nPrice: %.2f", productList.get(i).getPrice());
                    }
                }
                productNames[i] = productList.get(i).getName();
            }
            if (currentItems.length > 0) {
                String currentItemsString; //string to be put in JOptionPane
                currentItemsString = Arrays.toString(currentItems).replace(",", "")
                        .replace("[", "").replace("]", "");

                String chosen; //item chosen from the dropdown to learn more
                chosen = (String) JOptionPane.showInputDialog(null, "Here are the current" +
                                " items available for purchase. Select a product from the drop down to learn more." +
                                currentItemsString, "Listing Page", JOptionPane.QUESTION_MESSAGE, null,
                        productNames, productNames[0]);
                for (int i = 0; i < originalProductList.size(); i++) {
                    if (originalProductList.get(i).getName().equals(chosen)) {
                        itemIndex = i;
                        break;
                    }
                }
                customerListingPage(itemIndex, originalProductList);
            } else {
                JOptionPane.showMessageDialog(null, "Error! There are currently no products" +
                                " to buy, please come back after a seller has added one to the market.",
                        "Listing Page", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void customerListingPage(int chosenItem, ArrayList<Product> availableProducts)
            throws IOException, ClassNotFoundException {
        String buyAmt; //number of items the customer wants to buy, in string format
        int buyAmount = 0; //number of items the customer wants to buy

        if (chosenItem != -1) {
            String chosenItemString; //string to be formatted for the JOptionPane
            chosenItemString = "\nProduct #" + (chosenItem + 1) + ": " + availableProducts.get(chosenItem).getName() +
                    "\nDescription: " + availableProducts.get(chosenItem).getDescription() +
                    "\nQuantity available: " + availableProducts.get(chosenItem).getQuantityAvailable();

            JOptionPane.showMessageDialog(null, "Here are additional details about the " +
                    "selected item. " + chosenItemString, "Listing Page", JOptionPane.INFORMATION_MESSAGE);

            int quantityAvailable = availableProducts.get(chosenItem).getQuantityAvailable(); //available amt of chosen item
            do {
                try {
                    buyAmt = JOptionPane.showInputDialog(null,
                            quantityAvailable + " is/are available for purchase. How many would you like to " +
                                    "add to your cart?", "Listing Page", JOptionPane.QUESTION_MESSAGE);
                    if (buyAmt == null) {
                        break;
                    } else {
                        buyAmount = Integer.parseInt(buyAmt);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Error! Purchase amount must be an " +
                            "integer. Please try again.", "Listing Page", JOptionPane.ERROR_MESSAGE);
                    buyAmount = -1;
                }
                if (buyAmount < 0) {
                    JOptionPane.showMessageDialog(null, "Error! Purchase amount must be greater"
                            + " than or equal to 0. Please try again.", "Listing Page", JOptionPane.ERROR_MESSAGE);
                }
                if (buyAmount > quantityAvailable) {
                    JOptionPane.showMessageDialog(null, "Error! Purchase amount must be less " +
                                    "than or equal to what's currently available. Please try again.",
                            "Listing Page", JOptionPane.ERROR_MESSAGE);
                    buyAmount = -1;
                }
            } while (buyAmount < 0);

            buyAmount *= -1;
            ArrayList<Product> products = new ArrayList<>();
            oos.writeObject("1,2,2,0"); //updates quantity available of the product bought by customer
            products = (ArrayList<Product>) ois.readObject(); //items currently in customer's cart
            oos.writeObject(chosenItem);
            oos.writeObject(buyAmount);

            if (buyAmount < 0) {
                oos.writeObject("1,0,5"); //adds item to cart
                oos.writeObject(getAccountID());
                oos.writeObject(buyAmount * -1);
                oos.writeObject(availableProducts);
                oos.writeObject(chosenItem);
                JOptionPane.showMessageDialog(null, (-1 * buyAmount) + " " +
                                availableProducts.get(chosenItem).getName() + "s were added to your cart.",
                        "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void mainMenu(Login newLogin) throws IOException, ClassNotFoundException {
        String option; //main menu option choice selected by user
        int optionIndex; //index of the menu option selected

        do {
            do {
                try {
                    //main menu options to be used in JOptionPane
                    String[] mainMenu = new String[6];
                    mainMenu[0] = "View available products for purchase";
                    mainMenu[1] = "View, edit, or checkout your shopping cart";
                    mainMenu[2] = "View a dashboard with store and seller information";
                    mainMenu[3] = "Export your purchase history into a file";
                    mainMenu[4] = "Account settings";
                    mainMenu[5] = "Logout";

                    optionIndex = 6;
                    option = (String) JOptionPane.showInputDialog(null, "Welcome, " +
                                    getAccountName() + "!\nSelect one of the following dropdown options" +
                                    " to continue.", "Shopping Cart Menu", JOptionPane.QUESTION_MESSAGE,
                            null, mainMenu, mainMenu[0]);

                    for (int i = 0; i < mainMenu.length; i++) {
                        if (mainMenu[i].equals(option)) {
                            optionIndex = i + 1;
                            break;
                        }
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error! Please select a valid " +
                            "menu option.", "Client Menu", JOptionPane.ERROR_MESSAGE);
                    optionIndex = -1;
                }
            } while (optionIndex == -1);

            if (optionIndex == 1) {
                printListingPage(); //customer purchase page
            } else if (optionIndex == 2) {
                cartMenu(); //shopping cart menu that lets user choose to view, checkout, or remove item
            } else if (optionIndex == 3) {
                storeDashboard(); //dashboard displaying options on how to view store info
            } else if (optionIndex == 4) {
                purchaseHistory(getAccountName()); //exports customer history into .txt file
                JOptionPane.showMessageDialog(null, "Your purchase history has been " +
                                "successfully exported into File \"" + getAccountName() + "CustomerHistory.txt\".",
                        "Purchase History", JOptionPane.INFORMATION_MESSAGE);
                optionIndex = 10;
            } else if (optionIndex == 5) {
                if (newLogin.editAccount()) {
                    break;
                }
            } else {
                oos.writeObject("1,0,6"); //logs out, saves shopping cart
                oos.writeObject(getAccountID());
            }

        } while (optionIndex != 6);
    }
}