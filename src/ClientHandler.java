import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Project - Project 5  -  ClientHandler
 * This class is the class that handles all of the threads made by the server and handles the client by receiving
 * and writing objects back and forth between this class and the client.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    public static final Object obj = new Object(); //ensures there will be no racing between the threads
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientHandler(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        this.clientSocket = socket;
        this.oos = oos;
        this.ois = ois;
    }

    public void run() {
        Login loginServer = new Login(null, null, null, null);


        String response = ""; //option choice that the seller or customer is using to edit a file
        String[] strResponse; //response split by commas to be parsed for integers
        int[] responses = new int[4]; //split response that stores array of option choices
        Arrays.fill(responses, -1);
        FileManager shoppingCart; //shopping cart of current customer being worked on
        int productChoice = -1; //product choice of customer that is being worked with
        ArrayList<Integer> buyAmounts; //amount of each product in a customer cart that they are buying

        do {
            try {
                response = (String) ois.readObject();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error! A problem occurred when sending " +
                        "a message to the server. Please try again.", "Server", JOptionPane.ERROR_MESSAGE);
            }

            strResponse = response.split(",");
            responses[0] = Integer.parseInt(strResponse[0]);
            responses[1] = Integer.parseInt(strResponse[1]);


            switch (responses[0]) {
                // Here logic is written to handle all account related calls at Server end and call appropriate method of LoginServer.
                // LoginServer is having logic to edit accounts.txt file at server end.
                case CommandConstants.LOGIN_COMMAND_PREFIX:
                    if (CommandConstants.UPDATE_USERNAME == responses[1]) {
                        loginServer.editUsername(strResponse[2], strResponse[3]);
                    } else if (CommandConstants.UPDATE_PASSWORD == responses[1]) {
                        loginServer.editPassword(strResponse[2], strResponse[3]);
                    } else if (CommandConstants.UPDATE_EMAIL == responses[1]) {
                        loginServer.editEmail(strResponse[2], strResponse[3]);
                    } else if (CommandConstants.DELETE_ACCOUNT == responses[1]) {
                        loginServer.deleteAccount(strResponse[2]);
                    } else if (CommandConstants.CHECK_USERNAME == responses[1]) {
                        int userIndex = loginServer.checkUsername(strResponse[2]);
                        try {
                            oos.writeObject(userIndex);
                            oos.flush();
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                            "when trying to send an object to the client. Please try again.",
                                    "Server", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (CommandConstants.CHECK_PASSWORD == responses[1]) {
                        boolean result = loginServer.checkPassword(strResponse[2], Integer.parseInt(strResponse[3]));
                        try {
                            oos.writeObject(result);
                            oos.flush();
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                            "when trying to send an object to the client. Please try again.",
                                    "Server", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (CommandConstants.GET_ACCOUNT == responses[1]) {
                        String[] accountInfo = loginServer.getAccount(Integer.parseInt(strResponse[2]));
                        try {
                            oos.writeObject(accountInfo);
                            oos.flush();
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                            "when trying to send an object to the client. Please try again.",
                                    "Server", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (CommandConstants.CREATE_ACCOUNT == responses[1]) {
                        try {
                            loginServer.createAccount(new String[]{strResponse[2], strResponse[3], strResponse[4],
                                    strResponse[5], strResponse[6]});
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                            "when trying to create a file. Please try again.",
                                    "Server", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (CommandConstants.GET_ACCOUNTS == responses[1]) {
                        ArrayList<String[]> allAccounts = loginServer.getAccountsServer();
                        try {
                            oos.writeObject(allAccounts);
                            oos.flush();
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                            "when trying to send an object to the client. Please try again.",
                                    "Server", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case 1:
                    ArrayList<Seller> sellers = new ArrayList<>();
                    Store[] stores;
                    ArrayList<Product> products = new ArrayList<>(); //list of products all currently on the market
                    ArrayList<String[]> allAccounts = new ArrayList<>();
                    try {
                        // creating new file, file reader, buffered reader
                        File f = new File("accounts.txt"); // "accounts.txt" is a csv file where account login info is stored
                        FileReader fr = new FileReader(f);
                        BufferedReader bfr = new BufferedReader(fr);

                        synchronized (obj) {
                            String line = bfr.readLine();
                            while (line != null) {
                                allAccounts.add(line.split(","));
                                line = bfr.readLine();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    synchronized (obj) {
                        for (String[] allAccount : allAccounts) {
                            if (Boolean.parseBoolean(allAccount[3])) { //checks to see if seller
                                sellers.add(new Seller(allAccount[0], allAccount[1], allAccount[2],
                                        Boolean.parseBoolean(allAccount[3]), allAccount[4], false, oos, ois));
                            }
                        }
                        ArrayList<Store> allStores = new ArrayList<>(); //Initializes stores
                        for (int i = 0; i < sellers.size(); i++) {
                            for (int j = 0; j < sellers.get(i).getAllStores().size(); j++) { //iterates through #stores seller i has
                                allStores.add(sellers.get(i).getAllStores().get(j));//The customer makes sure that if there is a no products in the store, it accounts for that
                            }
                        }
                        stores = new Store[allStores.size()];
                        for (int i = 0; i < allStores.size(); i++) {
                            stores[i] = allStores.get(i);
                        }

                        for (Store allStore : allStores) { //initializing products
                            ArrayList<Product> tempList = allStore.getProducts();
                            for (Product product : tempList) {
                                products.add(product);
                            }
                        }
                    }
                    if (responses[1] == 0) { //customer initialization. opens shopping cart file and buyAmounts
                        String accountID = "";
                        synchronized (obj) {
                            try {
                                accountID = (String) ois.readObject(); //acct name of customer that is being initialized
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                                "when trying to send an object to the server. Please try again.",
                                        "Server", JOptionPane.ERROR_MESSAGE);
                            }
                            shoppingCart = new FileManager(accountID + ".txt");
                            if (strResponse.length > 2) {
                                responses[2] = Integer.parseInt(strResponse[2]);
                                if (responses[2] == 0) { //query products from shoppingCart
                                    try {
                                        oos.writeObject(shoppingCart.getProducts());
                                        oos.flush();
                                        oos.writeObject(shoppingCart.readFile());//queries buyAmounts from ShoppingCart
                                        oos.flush();
                                    } catch (IOException e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem " +
                                                "occurred when trying to send an object to the client. Please " +
                                                "try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                } else if (responses[2] == 1) {//query products from shoppingCart
                                    try {
                                        oos.writeObject(shoppingCart.getProducts());
                                        oos.flush();
                                        oos.writeObject(shoppingCart.readFile());//queries buyAmounts from ShoppingCart
                                        oos.flush();
                                        writeTransactionCustomer(shoppingCart.getProducts(), shoppingCart.readFile(), accountID);
                                    } catch (IOException e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem " +
                                                "occurred when trying to send an object to the client. Please " +
                                                "try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                } else if (responses[2] == 2) { //check out shopping cart, set it to empty
                                    shoppingCart.setProducts(null);
                                    shoppingCart.setBuyAmounts(null);
                                } else if (responses[2] == 3) { //remove product from shopping list
                                    try {
                                        productChoice = (int) ois.readObject();
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem " +
                                                "occurred when trying to send an object to the server. Please " +
                                                "try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                    shoppingCart.removeProduct(productChoice);
                                    buyAmounts = shoppingCart.getBuyAmounts();
                                    buyAmounts.remove(productChoice);
                                    shoppingCart.setBuyAmounts(buyAmounts);
                                } else if (responses[2] == 4) { //certain amt of product removed, but not whole product
                                    int productsRemoved = -1; //# of products being removed from cart
                                    try {
                                        productChoice = (int) ois.readObject();
                                        productsRemoved = (int) ois.readObject();
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem " +
                                                "occurred when trying to send an object to the server. Please " +
                                                "try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                    buyAmounts = shoppingCart.getBuyAmounts();
                                    buyAmounts.set(productChoice, (buyAmounts.get(productChoice) - productsRemoved));
                                    shoppingCart.setBuyAmounts(buyAmounts);
                                } else if (responses[2] == 5) {
                                    try {
                                        int productsAdded = (int) ois.readObject();
                                        ArrayList<Product> availableProducts = (ArrayList<Product>) ois.readObject();
                                        int chosenItem = (int) ois.readObject();
                                        buyAmounts = shoppingCart.getBuyAmounts();
                                        if (buyAmounts == null) {
                                            buyAmounts = new ArrayList<>();
                                        }
                                        buyAmounts.add(productsAdded);
                                        shoppingCart.setBuyAmounts(buyAmounts);
                                        shoppingCart.addProduct(availableProducts.get(chosenItem), productsAdded);
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem " +
                                                "occurred when trying to send an object to the server. Please " +
                                                "try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                } else if (responses[2] == 6) { //logout, saves shopping cart
                                    try {
                                        shoppingCart.logout(accountID);
                                    } catch (IOException e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem" +
                                                        " occurred when trying to logout. Please try again.",
                                                "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                } else if (responses[2] == 7) { //runs historyFile in customer
                                    try {
                                        ArrayList<Product> purchases = (ArrayList<Product>) ois.readObject();
                                        buyAmounts = (ArrayList<Integer>) ois.readObject();
                                        String name = (String) ois.readObject();

                                        Customer customerServer = new Customer(accountID, "no", "no",
                                                false, "no", oos, ois);

                                        customerServer.historyFile(purchases, buyAmounts, name);
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, "Error! A problem" +
                                                " occurred when trying to send an object to the server. " +
                                                "Please try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                    }
                                    break;
                                }
                            }
                            try {
                                shoppingCart.logout(accountID); //updates shopping cart file after any change was made
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "Error! A problem" +
                                                " occurred when trying to logout. Please try again.",
                                        "Server", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else if (responses[1] == 2) {
                        responses[2] = Integer.parseInt(strResponse[2]);

                        synchronized (obj) {
                            if (responses[2] == 0) { //SENDS CUSTOMER THE FULL ARRAYLIST OF SELLER SELLERS
                                try {
                                    oos.writeObject(sellers);
                                    oos.flush();
                                } catch (IOException e) {
                                    JOptionPane.showMessageDialog(null, "Error! A problem " +
                                            "occurred when trying to send an object to the client. Please " +
                                            "try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                }
                            } else if (responses[2] == 1) { //SENDS CUSTOMER THE FULL ARRAYLIST OF STORE ARRAYS
                                try {
                                    oos.writeObject(stores);
                                    oos.flush();
                                } catch (IOException e) {
                                    JOptionPane.showMessageDialog(null, "Error! A problem " +
                                            "occurred when trying to send an object to the client. Please try" +
                                            " again.", "Server", JOptionPane.ERROR_MESSAGE);
                                }
                            } else if (responses[2] == 2) { //SENDS CUSTOMER THE FULL PRODUCT ARRAYLIST OF PRODUCTS
                                try {
                                    oos.writeObject(products);
                                    oos.flush();
                                } catch (IOException e) {
                                    JOptionPane.showMessageDialog(null, "Error! A problem " +
                                            "occurred when trying to send an object to the client. Please try" +
                                            " again.", "Server", JOptionPane.ERROR_MESSAGE);
                                }
                                if (strResponse.length > 3) {
                                    responses[3] = Integer.parseInt(strResponse[3]);
                                    if (responses[3] == 0) { //updates buyAmount of specific product
                                        int productsRemoved = -1; //items subtracted from buyAmount
                                        try {
                                            productChoice = (int) ois.readObject();
                                            productsRemoved = (int) ois.readObject();
                                        } catch (Exception e) {
                                            JOptionPane.showMessageDialog(null, "Error! A " +
                                                    "problem occurred when trying to send an object to the server. " +
                                                    "Please try again.", "Server", JOptionPane.ERROR_MESSAGE);
                                        }

                                        updateQuantityAvailable(products, sellers, productChoice, productsRemoved);

                                    }
                                }
                            }
                        }
                    }
                    break; //since everything is updated simultaneously, no updates need to be made when logging out

                case 2:
                    synchronized (obj) {
                        if (responses[1] == 0) { //query products from shoppingCart
                            exportProducts(strResponse[2], strResponse[3]);
                        } else if (responses[1] == 1) {
                            ArrayList<Store> updatedStores = new ArrayList<>();
                            ArrayList<Product> updatedProducts;
                            String[] updatedStoreNames;
                            try {
                                updatedStoreNames = (String[]) ois.readObject();
                                updatedProducts = (ArrayList<Product>) ois.readObject();
                                for (int i = 0; i < updatedStoreNames.length; i++) {
                                    updatedStores.add(new Store(strResponse[2], updatedStoreNames[i]));
                                }
                                putProductsInStores(updatedStores, updatedProducts);

                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                                "when trying to send an object to the server. Please try again.",
                                        "Server", JOptionPane.ERROR_MESSAGE);
                            }
                            //update Stores in respective account file
                            save(updatedStores, strResponse[2]);
                            try {
                                oos.writeObject("");
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "Error! A problem occurred " +
                                                "when trying to send an object to the client",
                                        "Server", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    break;
                case 3:
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Error! A problem occurred when " +
                                        "trying to close the client connection. Please try again.",
                                "Server", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }

            try {
                oos.flush();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error! A problem occurred when trying to " +
                        "close the client connection. Please try again.", "Server", JOptionPane.ERROR_MESSAGE);
            }

        } while (responses[0] != 3);

        try {
            ois.close();
            oos.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error! A problem occurred when trying to " +
                    "close the client connection. Please try again.", "Server", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void updateQuantityAvailable(ArrayList<Product> products, ArrayList<Seller> sellers,
                                               int productChoice, int productsRemoved) {
        Seller seller;
        for (Seller value : sellers) {
            if (value.getAccountID().equals(products.get(productChoice).getAccountID())) {
                seller = value;
                for (int k = 0; k < seller.getAllStores().size(); k++) {
                    if (products.get(productChoice).getStore().equals(seller.getAllStores().get(k).getName())) {
                        for (int l = 0; l < seller.getAllStores().get(k).getProducts().size(); l++) {
                            if (seller.getAllStores().get(k).getProducts().get(l).getName().equals(products.
                                    get(productChoice).getName())) {
                                seller.getAllStores().get(k).getProducts().get(l).setQuantityAvailable(products.
                                        get(productChoice).getQuantityAvailable() + productsRemoved);
                                save(seller.getAllStores(), seller.getAccountID());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void putProductsInStores(ArrayList<Store> updatedStores, ArrayList<Product> updatedProducts) {
        for (int i = 0; i < updatedProducts.size(); i++) {
            int index = 0;
            for (int j = 0; j < updatedStores.size(); j++) {
                if (updatedProducts.get(i).getStore().equals(updatedStores.get(j).getName())) index = j;
            }
            updatedStores.get(index).addProductObject(updatedProducts.get(i));
        }
    }

    public static void exportProducts(String exportedFileName, String accountID) {
        File f = new File(exportedFileName + ".csv");
        try { //creates a new file if there isn't one already
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);
            try {
                // creating new file, file reader, buffered reader
                File f2 = new File(accountID + ".txt");
                f2.createNewFile();
                FileReader fr = new FileReader(f2);
                BufferedReader bfr = new BufferedReader(fr);

                String line = bfr.readLine();
                while (line != null) {
                    pw.println(line);
                    line = bfr.readLine();
                }
                bfr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(ArrayList<Store> updatedStores, String accountID) {
        if (updatedStores.size() != 0) {
            File f = new File(accountID + ".txt");
            try {//creates a new file if there isn't one already
                f.createNewFile();
                //reads through whole class and prints to appropriate sellerID file
                FileOutputStream fos = new FileOutputStream(f, false);
                PrintWriter pw = new PrintWriter(fos);
                for (Store updatedStore : updatedStores) {
                    if (updatedStore.getProducts().size() == 0) {
                        pw.printf("%s,null,null,-1,-1%n", updatedStore.getName());
                    } else {
                        for (int j = 0; j < updatedStore.getProducts().size(); j++) {
                            pw.printf("%s,%s,%s,%d,%.2f%n", updatedStore.getName(),
                                    updatedStore.getProducts().get(j).getName(),
                                    updatedStore.getProducts().get(j).getDescription(),
                                    updatedStore.getProducts().get(j).getQuantityAvailable(),
                                    updatedStore.getProducts().get(j).getPrice());
                        }
                    }
                }
                pw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //seller
        }
    }

    public static void writeTransactionCustomer(ArrayList<Product> purchases, ArrayList<Integer> buyAmounts, String accountID) {
        File f = new File("transactions.txt");
        try { //creates a new file if there isn't one already
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f, true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        PrintWriter pw = new PrintWriter(fos);

        for (int i = 0; i < purchases.size(); i++) {
            pw.print("" + accountID + "," + purchases.get(i).getAccountID());
            pw.print("," + purchases.get(i).getStore() + "," + purchases.get(i).getName());
            pw.print("," + purchases.get(i).getDescription() + "," + buyAmounts.get(i));
            pw.printf(",%.2f%n", purchases.get(i).getPrice());
        }
        pw.close();
    }

}
