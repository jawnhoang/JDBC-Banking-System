import java.util.Scanner;


/**
 * command line UI
 */
public class p1 {
    public static void main(String[] args) {
        /*clears screen*/
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        /*clears screen*/

        /*connect to db*/
        System.out.println(":: PROGRAM START");
		
		if (args.length < 1) {
			System.out.println("Need database properties filename");
		} else {
			BankingSystem.init(args[0]);
			BankingSystem.testConnection();
			System.out.println();
			//BatchInputProcessor.run(args[0]);
		}
        /*db connection end*/
        menuScreen();
        

    }
    
    public static void menuScreen(){
        /*screen #1*/
        System.out.println("Welcome to the Self Services Banking System by John Hoang!");
        System.out.println("-----Main Menu-----");
        System.out.println("1. New Customer\n2. Customer Login\n3. Exit");
        
        /*handle user input*/
        Scanner in = new Scanner(System.in);
        int menuChoice = in.nextInt();
        while(menuChoice != 1 && menuChoice != 2 && menuChoice != 3){
            System.out.println("Invalid menu operation...");
            System.out.println("Please choose 1, 2, 3....");
            menuChoice = in.nextInt();
        }

        if(menuChoice == 1){

            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: NEW CUSTOMER ::");
            System.out.println("------------------");
            String name, gender, age, pin;
            System.out.println("Please Enter your first name:");
            name  = in.next();
            name = name.substring(0,1).toUpperCase() + name.substring(1);
            /*error handle for name constraint*/
           //todo
            
            System.out.println("What is your gender? M or F:");
            gender = in.next();
            gender = gender.substring(0).toUpperCase();
            /*error handle for gender constraints*/
            while(!gender.equals("M") && !gender.equals("F")){
                System.out.println("You have entered an invalid Gender. Please enter either M or F:");
                System.out.println("Capitalization negligable...");
                gender = in.next();
                gender = gender.substring(0).toUpperCase();
            }

            System.out.println("What is your age: ");
            age = in.next();
            /*error handle for age constraint*/
            while(Integer.valueOf(age) < 0){
                System.out.println("You entered an invalid age. Please try again...");
                age = in.next();
            }

            System.out.println("Please establish your PIN: ");
            pin = in.next();
            /*error handle for pin constraint*/
            while(Integer.valueOf(pin) < 0){
                System.out.println("You entered an invalid pin. Please try again...");
                pin = in.next();
            }
            /*new customer, init new account*/
            BankingSystem.newCustomer(name, gender, age, pin);
            menuScreen();
        }else if (menuChoice == 2){
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: CUSTOMER LOGIN ::");
            System.out.println("--------------------");

            /*Prompt for customer ID and Pin*/
            String custID, custPin;
            System.out.println("Please enter your customer ID received upon account creation:");
            custID = in.next();
            while(Integer.valueOf(custID) < 100 && Integer.valueOf(custID) != 0){
                System.out.println("You entered an ID that violates our constraints. Try Again");
                custID = in.next();
            }

            System.out.println("Please enter your PIN:");   
            custPin = in.next();
            while(Integer.valueOf(custPin) < 0){
                System.out.println("You entered a pin that violates our constraints. Please try again...");
                custPin = in.next();
            }
            
            if(Integer.valueOf(custID) == 0 && Integer.valueOf(custPin) == 0){
                adminScreen();

            }else{
                BankingSystem.customerLogin(custID, custPin);
                menuScreen();
            }
        }else{
            in.close();
            BankingSystem.closeConnection();
            System.out.println("Goodbye.");
            System.exit(0);
        }
    }

    public static void customerMainMenu(String customerID){
        /*screen 2*/
        System.out.println(":: CUSTOMER MAIN MENU ::");
        System.out.println("------------------------");
        System.out.println("1. Open Account\n2. Close Account\n3. Deposit\n4. Withdraw\n5. Transfer\n6. Account Summary\n7. Exit");
        System.out.println("------------------------");

        /*handle user input*/
        Scanner in1 = new Scanner(System.in);
        int menuChoice = in1.nextInt();
        while(menuChoice > 7 || menuChoice < 1){
            System.out.println("Invalid menu operation...");
            System.out.println("Please choose from selections available....");
            menuChoice = in1.nextInt();
        }
        if(menuChoice == 1){ /*open account*/
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/
            
            System.out.println(":: OPEN ACCOUNT ::");
            System.out.println("----------------------");

            /*prompt for cusID, account type, and bal (init deposit)*/
            String cusID, type, amount;
            System.out.println("Please enter your customer ID to proceed: ");
            cusID = in1.next();

            System.out.println("What type of account? Checking (C) or Savings (S): ");
            type = in1.next();
            type = type.substring(0).toUpperCase();
            /*error handle for gender constraints*/
            while(!type.equals("C") && !type.equals("S")){
                System.out.println("You have entered an invalid account Type.");
                System.out.println("Please enter either C for checkings or S for savings.");
                System.out.println("Capitalization negligable...");
                type = in1.next();
                type = type.substring(0).toUpperCase();
            }


            System.out.println("Please enter your initial deposit:");
            amount = in1.next();
            while(Integer.valueOf(amount) < 0){
                System.out.println("You entered an invalid balance. Cannot be negative. Try again.");
                amount = in1.next();
            }

            BankingSystem.openAccount(cusID, type, amount);
            customerMainMenu(customerID);

        }else if (menuChoice == 2){ /*close acc*/
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: CLOSE ACCOUNT ::");
            System.out.println("----------------------");


            /*prompt for account #*/
            System.out.println("Please enter the account number you want to close: ");
            String accNum;
            accNum = in1.next();
            while(Integer.valueOf(accNum) < 1000){
                System.out.println("That is an invalid account number. Try again:"); 
                accNum = in1.next();
            }
            System.out.println("Authenticating...");
            if(BankingSystem.authenticate(customerID , accNum)){
                System.out.println("Proceeding to account closure...");
                BankingSystem.closeAccount(accNum);
                customerMainMenu(customerID);
            }else{
                System.out.println("You are not the owner of this account. Cannot close.");
                customerMainMenu(customerID);
            }
        }else if(menuChoice == 3){ /*deposit*/
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: DEPOSIT ::");
            System.out.println("-------------");

            /*prompt for account #*/
            System.out.println("Please enter the account number you want to deposit into:");
            String accNumDep;
            accNumDep = in1.next();
            while(Integer.valueOf(accNumDep) < 1000){
                System.out.println("That is an invalid account number. Try again:"); 
                accNumDep = in1.next();
            }

            System.out.println("How much do you want to deposit?:");
            String depAmount;
            depAmount = in1.next();
            while(Integer.valueOf(depAmount) < 0){
                System.out.println("You entered an invalid deposit amount. Please establish a positive amount.");
                depAmount = in1.next();
            }
            BankingSystem.deposit(accNumDep, depAmount);
            customerMainMenu(customerID);
        }else if(menuChoice == 4){ /*withdraw*/
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: WITHDRAW ::");
            System.out.println("--------------");

            /*prompt for account #*/
            System.out.println("Please enter the account number you want to withdraw from:");
            String accNumWith, amountWithd;
            accNumWith = in1.next();
            while(Integer.valueOf(accNumWith) < 1000){
                System.out.println("That is an invalid account number. Try again:"); 
                accNumWith = in1.next();
            }

            System.out.println("How much would you like to withdraw?");
            amountWithd = in1.next();
            while(Integer.valueOf(amountWithd) < 0){
                System.out.println("You entered an invalid withdrawl amount. Please establish a positive amount.");
                amountWithd = in1.next();
            }
            System.out.println("Authenticating...");
            if(BankingSystem.authenticate(customerID , accNumWith)){
                System.out.println("Proceeding to account withdrawl...");
                BankingSystem.withdraw(accNumWith, amountWithd);
                System.out.println("You have withdrawn $" + amountWithd);
                customerMainMenu(customerID);
            }else{
                System.out.println("You are not the owner of this account. Cannot withdraw.");
                customerMainMenu(customerID);
            }

        }else if(menuChoice == 5){/*transfer funds*/
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: TRANSFER ::");
            System.out.println("--------------");

            System.out.println("Please enter your account number: ");
            String accNumSrc, accNumDest, amountTransf;
            accNumSrc = in1.next();
            while(Integer.valueOf(accNumSrc) < 1000){
                System.out.println("That is an invalid account number. Try again:"); 
                accNumSrc = in1.next();
            }

            System.out.println("Please enter the account number to transfer into: ");
            accNumDest = in1.next();
            while(Integer.valueOf(accNumDest) < 1000){
                System.out.println("That is an invalid account number. Try again:"); 
                accNumDest = in1.next();
            }

            System.out.println("How much do you wish to transfer: ");
            amountTransf = in1.next();
            while(Integer.valueOf(amountTransf) < 0 ){
                System.out.println("Invalid amount, please enter a positive amount.");
                amountTransf = in1.next();
            }

            System.out.println("Authenticating...");
            if(BankingSystem.authenticate(customerID , accNumSrc)){
                System.out.println("Proceeding to account transfer...");
                BankingSystem.transfer(accNumSrc, accNumDest, amountTransf);
                System.out.println("You have transfered $" + amountTransf);
                customerMainMenu(customerID);
            }else{
                System.out.println("You are not the owner of this account. Cannot transfer.");
                customerMainMenu(customerID);
            }
        }else if(menuChoice == 6){
            /*clears screen*/
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            /*clears screen*/

            System.out.println(":: ACCOUNT SUMMARY ::");
            System.out.println("---------------------");
            BankingSystem.accountSummary(customerID);
            customerMainMenu(customerID);
           
        }else if(menuChoice == 7){
            menuScreen();
        }
    }


    public static void adminScreen(){
        /*screen #1*/
        System.out.println("-----ADMINISTRATOR MENU-----");
        System.out.println("1. Account Summary for a Customer\n2. Report A :: Customer Information with Total Balance in Decreasing Order "+
                           "\n3. Report B :: Find the Average Total Balance Between Age Groups\n " +
                           "4. Exit");
        
        /*handle user input*/
        Scanner in2 = new Scanner(System.in);
        int menuChoice = in2.nextInt();
        while(menuChoice < 1 && menuChoice > 4){
            System.out.println("Invalid menu operation...");
            System.out.println("Please choose 1, 2, 3, 4....");
            menuChoice = in2.nextInt();
        }

        if(menuChoice == 1){
            System.out.println("Please enter the customer ID to view summary: ");
            String cusID = in2.next();
            BankingSystem.accountSummary(cusID);
            adminScreen();
        }else if (menuChoice == 2){
            BankingSystem.reportA();
            adminScreen();
        }else if (menuChoice == 3){
            System.out.println("Please enter minimum age: ");
            String minAge = in2.next();
            System.out.println("Please enter maximum age: ");
            String maxAge = in2.next();
            while(Integer.valueOf(maxAge) < Integer.valueOf(minAge)){
                System.out.println("The maximum age you establish is smaller than minimum age.");
                System.out.println("Please enter a larger age: ");
                maxAge = in2.next();
            }
            BankingSystem.reportB(minAge, maxAge);
            adminScreen();
        }else if (menuChoice == 4){
            menuScreen();
        }

    }


}
