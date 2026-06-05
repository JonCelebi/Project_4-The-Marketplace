Project 4 - A Guide

Both Report and Vocareum submissions were made by **Shenggang Liu**

How to run the program:

1. Go to the Server class and press the green play button
2. Go to the Client class and press the green play button
3. Be sure to only interact with the console when prompted
4. Test/interact with code as desired

If testing with multiple profiles: 
1. Go to the terminal at the bottom of Intellij
2. open >2 terminal windows
3. Compile the Server in one of the terminal windows
4. Compile the Client in two or more of the terminals windows
5. Run the Server
6. Run all of the Clients

Classes and their functions:

1. Account:
    1. The "Account" class is used to declare an object that stores the login information of the user during each
       instance where the code is run
    2. It is also exclusively used by the "Login" class but extended by 2 other object classes,
       the "Customer" class and the "Seller" class
    3. As this is an object, we tested simultaneously with the "Login" class by simply using some example inputs
2. Customer:
    1. An extension of the Account class designed to store information specific to customer accounts
    2. Accessed in login by the "createCustomer" method to create a customer object. "createCustomer" is then accessed
       in "Client" to create said new customer class which is then used by the code for further processing
    3. This further processing includes displaying a customer dashboard with 6 options to perform necessary function
       associated with the customer. It does this by calling methods from "FileManager" as well as "Login"
    4. We tested this code by running several sample inputs through it and testing each and every function to ensure
       that the code works
3. FileManager:
    1. "FileManager" is a class to store and allow access to all methods associated with the "ShoppingCart" variable of
       the "Customer" class
    2. The class is heavily and also only accessed by the "Customer" class to achieve the necessary functions of
       customer accounts
    3. Since this largely stores methods used by the "Customer" class, we simultaneously tested both classes to ensure
       they return the appropriate responses
4. Login:
    1. The "Login" class is used by "Client" to prompt the user for input to either log into or sign up for an account
    2. Once an account has either been signed or logged into, it will create a new "Account" object to store information
       associated with the account
    3. Depending on the type of account being used here, it will then create a new "Customer" or "Seller" object for use
       by other classes
    4. We tested this class simply by running it out of "Client" and ensuring each method achieves their intended results
5. Client:
    1. The "Client" class is used to run the program and to interact with any and all other classes. It functions as
   the main method that allows for multiple threads to run at once
    2. Specifically, "Client" will instantiate and call methods from "Login" to login the user to an account, it will then
       pass this information associated with this account to either Customer or Seller code to achieve the necessary
       functions with each type of account
    3. Since this was our essentially our Main class, we tested it by running and adding some user input to make sure
       that it indeed did pass information and run through our general logic correctly
6. Server:
   1. Serves as our database, where all client threads access pertinent object information to ensure updates can be 
       seen from all users on the market
   2. All files are maintained and updated within this class
   3. This class was tested by running a Server class in one terminal and making edits with 2 different client users in two
      other terminal windows. This ensured updates made with one user could be seen by the other active profile
7. ClientHandler:
   1. The ClientHandler class is responsible for maintaining all active Server threads
   2. This ensures that multiple profiles can be maintained and run at the same time in the terminal
   3. This class was tested at the same time as the Server. See point 3 under the Server class to see how ClientHandler was tested
8. Product:
    1. Creates "Product" objects to store information pertaining to listings
    2. Accessed by the customer, store and seller classes as well as marketplace to present information regarding
       listings
    3. Tested by simply running the code associated with this class and ensuring it worked
9. Seller:
    1. An extension of the Account class designed to store information specific to seller accounts,
    2. Create instances of "Store" to store information regarding a seller's stores and listings
    3. Create instances of "Product" to store information in the store that will show up in the listings
    4. It also has features of editing and deleting a store, editing and deleting a product, importing and exporting 
   products and statistics of its stores and products.
    5. We tested this class simply by running it out of "Client" and ensuring each method achieves their intended results
10. Store:
    1. The "Store" class is used to declare "Store" objects which store the information of products associated with a
       stores
    2. Is called by "Sellers" to allow both the creation of new shops and the passing of information onto
       the customer class
    3. Tested by simply running the code associated with this class and ensuring it worked
11. CommandConstants: 
    1. This class was used to maintain organization within the Server class
    2. It contains all needed if/else constants that are required to determine which Login logic will be run
    3. No testing was required, as no logic is contained in this class