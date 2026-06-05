Test Cases:
* Clicking [Cancel] or [X] at any time during this process will greet you with a farewell message and exit you out of the program
* Clicking [OK] when the text field is empty should return a “Invalid Input” prompt and ask you for another input

**Sign up**
1. First account:
   1. Launch Server and Client in that order 
   2. When prompted to either [Login] or [Signup], select [Login]
      1. You should be greeted with a message stating that since there are currently no accounts, you cannot login and will be redirected to account sign up 
   3. When prompted for a username, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   4. When prompted for a password, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   5. When prompted for an email, please enter anything you would like and click “OK” 
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   6. When prompted for either [Customer] or [Merchant], select [Merchant].
      1. You should be greeted with a message stating that since there are currently no Merchant accounts and redirecting your account to that of a Merchant instead.
   7. When prompted for your additional actions, click [Cancel] or [X] to exit.
      1. You will be greeted with a farewell message 
2. Merchant Signup:
   1. Launch Server and Client in that order
   2. When prompted to either [Login] or [Signup], select [Signup]
   3. When prompted for a username, please enter the same username you used in test 1 you would like and click “OK”
      1. You will be greeted with a message stating that this username is unavailable and prompting you for another username. Please enter anything you would like and click “OK”
   4. When prompted for a password, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   5. When prompted for an email, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   6. When prompted for either [Customer] or [Merchant], select [Merchant].
   7. When prompted for additional actions, click “Cancel” or “X” to exit.
      1. You will be greeted with a farewell message

3. Customer Signup:
   1. Launch Server and Client in that order
   2. When prompted to either [Login] or [Signup], select [Signup]
   3. When prompted for a username, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   4. When prompted for a password, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   5. When prompted for an email, please enter anything you would like and click “OK”
      1. You will be greeted with a confirmation message, simply click any button to progress pass this
   6. When prompted for either [Customer] or [Merchant], select [Customer].
   7. When prompted for additional actions, click “Cancel” or “X” to exit.
   8. You will be greeted with a farewell message

**Expected Result: "Account.txt" should have the login information of all 3 accounts** 

**Login**
1. Customer Login
   1. Launch Server and Client in that order
   2. When prompted to either [Login] or [Signup], select [Login]
   3. When prompted for a username, please enter any username you have not used yet
      1. You should be greeted with a [Invalid Username] message and be prompted again
   4. Upon being prompted for username a second time, enter the username you used in the customer creation test
      1. You should be greeted with a confirmation message
   5. When prompted for a password, please enter anything that was not your password for the customer account
      1. You should be greeted with a [Invalid Password] message and be prompted again
   6. Upon being prompted for password a second time, enter the password you used in the customer creation test case
      1. You should be greeted with a confirmation message
   7. When prompted for additional actions, click “Cancel” or “X” to exit.
   8. You will be greeted with a farewell message
2. Merchant Login
   1. Launch Server and Client in that order
   2. When prompted to either [Login] or [Signup], select [Login]
   3. When prompted for a username, please enter any username you have not used yet
      1. You should be greeted with a [Invalid Username] message and be prompted again
   4. Upon being prompted for username a second time, enter the username you used in the merchant creation test
      1. You should be greeted with a confirmation message
   5. When prompted for a password, please enter anything that was not your password for the merchant account
      1. You should be greeted with a [Invalid Password] message and be prompted again
   6. Upon being prompted for password a second time, enter the password you used in the merchant creation test case
      1. You should be greeted with a confirmation message
   7. When prompted for additional actions, click “Cancel” or “X” to exit.
   8. You will be greeted with a farewell message

**Edit Account**
1. Repeat steps 1-6 of the Merchant Login test with the login info used in the "First Account" test
2. When prompted for additional actions, select Option 6 from the dropdown menu and click [OK]
3. You will be prompted for a confirmation to edit your account, select [OK]
4. You will be greeted with a confirmation for saving you data, select [OK]
5. When greeted with what part of your account you would like to edit, simply select anything you would like and follow the directions on screen.
6. Once you are done, simply click "Exit" or "X" 
7. You will be greeted with a farewell message

**Check "Accounts.txt"'s first line to make sure your changes are reflected**

**Seller**
1. Log in with the Seller as shown above
2. Select create store and create store with the name "nike"
3. It will say "Store successfully created"
4. Select edit store in the drop down menu and hit edit store name again
5. Select nike in the drop down menu
6. Change its name to be nike, it will say the store already exists
7. Hit cancel, you will be back to the main dashboard
8. Select create product in the drop down menu and select create new product again
9. Name it shoe and the description is black
10. Try to type "-1","a","","2b", you will see that none will work
11. Type 5 for the amount of product and type 21.1 for the price
12. Go to the drop down and put it in nike
13. You are now at main dashboard, select edit product
14. Select nike for the store and shoe for the product
15. Select Name of product for next dropdown
16. Type in "flip-flop", and hit yes
17. Main dashboard again, hit view statistics
18. Select store/product stats, and select nike and hit ok
19. Main dashboard again, hit export products, type in "nike products"
20. Hit yes and ok
21. Main dashboard again, hit the x and hit ok to log out
22. You should now have a flip-flop in a nike store in a file in the file named nike products.csv
23. You should also able to log in again and still see your stores and products