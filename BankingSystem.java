import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			//con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }
	public static void closeConnection(){
		try {
			con.close();
			System.out.println(":: CONNECTION TO DATABASE CLOSED");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
		/* insert your code here */
		String initCustomer = String.format("INSERT INTO p1.customer(p1.customer.name, p1.customer.gender, p1.customer.age, p1.customer.pin) VALUES ('%1$s', '%2$s', %3$s, %4$s)", name, gender, age, pin);
		//System.out.println(initCustomer);
		
		try {
			/*create statement connection*/
			stmt = con.createStatement();

			/*insert data*/
			stmt.executeUpdate(initCustomer);

		
			/*return customer ID*/
			/*query*/
			String cusID = String.format("SELECT MAX(p1.customer.id) FROM p1.customer WHERE p1.customer.name = '%1$s'",name);
			/*run query and get resultset*/
			rs = stmt.executeQuery(cusID);
			/*parse through columns that were returned
			this case only 1 col should be returned so we can get data without for loop*/
			while(rs.next()){
				System.out.println("Your account ID: " + rs.getInt(1));
				System.out.println("Use this number upon login");
				System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID PIN");
		}
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		System.out.println(":: OPEN ACCOUNT - RUNNING");
				/* insert your code here */
		String initAccount = String.format("INSERT INTO p1.account(p1.account.id, p1.account.balance, p1.account.type, p1.account.status)"+
											"VALUES(%1$s, %2$s, '%3$s', 'A')", id, amount, type);
		/*execute INSERT*/
		try{
			stmt = con.createStatement();
			stmt.executeUpdate(initAccount);
		
			/*print account number*/
			String accNum = String.format("SELECT MAX(p1.account.number) FROM p1.account WHERE p1.account.id = %1$s", id);
			rs = stmt.executeQuery(accNum);
			/*return account number of ONLY the newly created account.*/
			while(rs.next()){
				int accID = rs.getInt(1);
				System.out.println("Your new account number: " + accID);
				System.out.println("Use this number for further account activities.");
			}
			rs.close();
			System.out.println(":: OPEN ACCOUNT - SUCCESS");
		}catch(SQLException e){
			System.out.println("Error in opening an account. Redirecting...");
		}
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
				/* insert your code here */
		String closeAcc = String.format("UPDATE p1.account SET p1.account.balance = 0, p1.account.status = 'I'"+
										"WHERE p1.account.number = %1$s", accNum);
		try{
			stmt = con.createStatement();
			stmt.executeUpdate(closeAcc);

		}catch(SQLException e){
			e.printStackTrace();

		}
		System.out.println(":: CLOSE ACCOUNT - SUCCESS");
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println(":: DEPOSIT - RUNNING");
				/* insert your code here */
		try{
			/*depositing*/
			String depositAmount = String.format("UPDATE p1.account SET p1.account.balance = p1.account.balance + %1$s "+
												"WHERE p1.account.number = %2$s", amount, accNum);
			stmt = con.createStatement();
			stmt.executeUpdate(depositAmount);
			System.out.println(":: DEPOSIT - SUCCESS");
		}catch(SQLException e){
	
			System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");

		}
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		System.out.println(":: WITHDRAW - RUNNING");
				/* insert your code here */
		try{
			/*check for withdraw overflow*/
			String amountRem;
			String checkAmount = String.format("SELECT p1.account.balance FROM p1.account WHERE p1.account.number = %1$s", accNum);
			stmt = con.createStatement();
			rs = stmt.executeQuery(checkAmount);
			while(rs.next()){
				/*get current balance*/
				amountRem = rs.getString("balance");
				try{
				if(Integer.valueOf(amountRem) - Integer.valueOf(amount) < 0){
					System.out.println("Inefficient funds for withdrawl.");
					System.out.println("Redirecting back...");
				}else {
					String withdrawBal = String.format("UPDATE p1.account SET p1.account.balance = p1.account.balance - %1$s "+
													   "WHERE p1.account.number = %2$s", amount, accNum);
					stmt = con.createStatement();
					stmt.executeUpdate(withdrawBal);
					System.out.println(":: WITHDRAW - SUCCESS");
				}
			}catch(NumberFormatException n){
				System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
			}
			}
			rs.close();
		}catch (SQLException e){
			System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
		}
	} 

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		System.out.println(":: TRANSFER - RUNNING");
				/* insert your code here */	
		/*withdraw*/
		try{
			/*check for withdraw overflow*/
			String amountRem;
			String checkAmount = String.format("SELECT p1.account.balance FROM p1.account WHERE p1.account.number = %1$s", srcAccNum);
			stmt = con.createStatement();
			rs = stmt.executeQuery(checkAmount);
			while(rs.next()){
				/*get current balance*/
				amountRem = rs.getString("balance");
				if(Integer.valueOf(amountRem) - Integer.valueOf(amount) < 0){
					System.out.println("Inefficient funds for withdrawl.");
					System.out.println("Redirecting back...");
				}else {
					String withdrawBal = String.format("UPDATE p1.account SET p1.account.balance = p1.account.balance - %1$s "+
													   "WHERE p1.account.number = %2$s", amount, srcAccNum);
					stmt = con.createStatement();
					stmt.executeUpdate(withdrawBal);
					System.out.println(":: TRANSFER - SUCCESS");
				}
			}
			rs.close();

			String depositAmount = String.format("UPDATE p1.account SET p1.account.balance = p1.account.balance + %1$s "+
												"WHERE p1.account.number = %2$s", amount, destAccNum);
			stmt = con.createStatement();
			stmt.executeUpdate(depositAmount);

		}catch(SQLException e){
			System.out.println(":: TRANSFER - ERROR");
		}
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
				/* insert your code here */		
		try{
			String accSum = String.format("SELECT p1.account.number, p1.account.balance FROM p1.account "+
										  "WHERE p1.account.status = 'A' AND p1.account.id = %1$s ", cusID);
			stmt = con.createStatement();
			rs = stmt.executeQuery(accSum);
			ResultSetMetaData rsmd = rs.getMetaData();
			int col = rsmd.getColumnCount();
			System.out.println("ACCOUNT NUMBER" + "\t" + "ACCOUNT BALANCE");
			System.out.println("-------------------------------");
			int totalBal = 0;
			/*each iteration of while is a row*/
			while(rs.next()){
				/*each iteration of for is a column*/
				for(int i = 1; i <=col; i++){
					System.out.print(rs.getString(i) + "\t\t\t");
				}
				System.out.println();
				/*balance is in 2nd column, so add those columns up*/
				totalBal += rs.getInt(2);
			}
			System.out.println("--------------------------------");
			System.out.println("ACCOUNT TOTAL:"+ "\t$" + totalBal);
			System.out.println("--------------------------------");
			System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
		}catch (SQLException e){
			System.out.println(":: ACCOUNT SUMMARY - ERROR");
		}
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
				/* insert your code here */	
		try{
			String report = "SELECT p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age, SUM(p1.account.balance) AS TOTAL "+
						    "FROM p1.customer JOIN p1.account ON p1.customer.id = p1.account.id "+
							"WHERE p1.account.status = 'A' "+
							"GROUP BY p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age "+
							"ORDER BY TOTAL DESC";
			stmt = con.createStatement();
			rs = stmt.executeQuery(report);
			ResultSetMetaData rsmd = rs.getMetaData();
			int col = rsmd.getColumnCount();
			System.out.println("ID"+ "\t" + "NAME" + "\t" + "GENDER"+ "\t" + "AGE"+ "\t" + "TOTAL BAL");
			System.out.println("------------------------------------------");
			/*each iteration of while is a row*/
			while(rs.next()){
				/*each iteration of for is a column*/
				for(int i = 1; i <=col; i++){
					System.out.print(rs.getString(i) + "\t");
				}
				System.out.println();
			}

			System.out.println(":: REPORT A - SUCCESS");
		}catch(SQLException e){
			System.out.println(":: REPORT A - ERROR");
		}
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
				/* insert your code here */	
		try{
			String avgBal = String.format("SELECT AVG(TOTAL) AS AVERAGE FROM " +
										  "(SELECT p1.customer.id, p1.customer.name , p1.customer.gender, p1.customer.age, SUM(p1.account.balance) AS TOTAL " +
			 							  "FROM p1.customer INNER JOIN p1.account ON p1.customer.id = p1.account.id " +
										  "GROUP BY p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age) " +
										  "WHERE age >= %1$S AND age <= %2$S", min, max);

			stmt = con.createStatement();
			rs = stmt.executeQuery(avgBal);
			int bal = 0;
			System.out.println("AVERAGE BALANCE");
			System.out.println("---------------");
			while (rs.next()) {
				bal = rs.getInt("AVERAGE");
				System.out.println(bal);
			}
			rs.close();
			System.out.println(":: REPORT B - SUCCESS");
		}catch(SQLException e){
			System.out.println(":: REPORT B - ERROR");
		}
	}


	public static void customerLogin(String customerID, String pin){
		String authenticate = String.format("SELECT p1.customer.pin FROM p1.customer WHERE p1.customer.id = %1$s", customerID);
			try {
				rs = stmt.executeQuery(authenticate);
				int pinNum = 0;
				while(rs.next()){
					pinNum = rs.getInt(1);
			}
			if(pinNum == Integer.valueOf(pin)){
				System.out.println("ID and PIN matches. Proceeding to Account");
				p1.customerMainMenu(customerID);

			}else{
				System.out.println("ID and/or PIN does not match our records. Try again");
				p1.menuScreen();
			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}


	public static boolean authenticate(String id, String accNum){
		boolean isOwner = false;
		String authAcc = String.format("SELECT p1.account.id FROM p1.account WHERE p1.account.number = %1$s", accNum);
		try{
			rs = stmt.executeQuery(authAcc);
			while(rs.next()){
				if(rs.getInt("id") == Integer.valueOf(id)){
					isOwner = true;
				}
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return isOwner;
	}
}
