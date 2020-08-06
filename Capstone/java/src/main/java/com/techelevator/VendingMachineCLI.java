package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.techelevator.view.Menu;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";

	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };

	private static final String [] PURCHESE_MENU = {"Feed Money", "Select Product", "Back"};
	private static final String [] MONEY_MENU = {"$1.00", "$2.00", "$5.00", "$10.00", "Back"};
//	private static final List <String> SELECT_PRODUCT = readProducts();
	private Menu menu;
	CustomerBalance myBalance = new CustomerBalance();
	CustomerBalance balanceForLog = new CustomerBalance();
	String dateTime = (LocalDate.now() + " " + LocalTime.now());

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() throws FileNotFoundException {
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				readProducts();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				processPurchaseMenuOption();
			} else {
				System.exit(0);
			}
		}
	}

	private void processPurchaseMenuOption() {
		String purchaseMenuOption = "";
		while (!purchaseMenuOption.equals("Back")) {
			purchaseMenuOption = (String) menu.getChoiceFromOptions(PURCHESE_MENU);
			if (purchaseMenuOption.equals("Feed Money")) {
				processMoneyFeed();
			} 
		}
	}

	private void processMoneyFeed() {
		String feedOptions = "";
		while (!feedOptions.equals("Back")) {
			feedOptions = (String) menu.getChoiceFromOptions(MONEY_MENU);					
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
	
	public static void readProducts() throws FileNotFoundException {
		File products = new File("vendingmachine.csv");
		List <String> readList = new ArrayList <String> ();
		if (products.exists()) {
			try (Scanner fileScanner = new Scanner(products)) {
				while (fileScanner.hasNextLine()) {
				String items = fileScanner.nextLine();
				readList.add(items);
				}
			} 
		}
		for (String i : readList) {
			System.out.println(i);
		}
		
	}
	
	public class CustomerBalance {

		private BigDecimal currentBalance =  new BigDecimal(0);
		private BigDecimal zeroBalance = new BigDecimal(0);

		public BigDecimal getCurrentBalance() {
			return currentBalance;
		}

		public void addToCurrentBalance(BigDecimal addAmount) {
			currentBalance = currentBalance.add(addAmount);
		}

		public void subFromCurrentBalance(BigDecimal subAmount) {
			if (subAmount.doubleValue() <= currentBalance.doubleValue()) {
				currentBalance = currentBalance.subtract(subAmount);
			}
		}

		public void returnToZero() {
		currentBalance = zeroBalance;
		}

		public String toString() {
			return "$" + getCurrentBalance() + " remaining";
		}


	}
}
