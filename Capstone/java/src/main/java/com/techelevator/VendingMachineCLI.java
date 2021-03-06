package com.techelevator;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.techelevator.view.Menu;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };
	private static final String [] PURCHESE_MENU = {"Feed Money", "Select Product", "Finish Transaction", "Back"};
	private static final String [] MONEY_MENU = {"$1.00", "$2.00", "$5.00", "$10.00", "Back"};
	private static final String [] SELECT_PRODUCT = {"Chip", "Candy", "Drink", "Gum", "Back"};
	private static  Object[] SELECT_CHIP;
	private static  Object[] SELECT_CANDY;
	private static  Object[] SELECT_DRINK;
	private static  Object[] SELECT_GUM;
	private Menu menu;
	private CustomerBalance customer = new CustomerBalance();
	private List <String> selectedItems = new ArrayList <String> ();
	private List <String> perchesedItems = new ArrayList <String> ();
	static List <MasterItemType> inventory = new ArrayList <MasterItemType> ();
	static SelectedItems item = new SelectedItems();
	public BigDecimal money = new BigDecimal(0.00);
	LogFile log = new LogFile();
	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() {
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				readProducts();
				System.out.println("Current Money Provided: $" + customer.getCurrentBalance());
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				System.out.println("Current Money Provided: $" + customer.getCurrentBalance());
				processPurchaseMenuOption();
			} else {
				for (String itemsPrint : perchesedItems) {
				System.out.println(itemsPrint);
				}
				makeChange();
				System.exit(0);
			}
		}
	}

	public void makeChange () {
		System.out.println("Your change is " + customer.getCurrentBalance());
		double tempCoins = Double.valueOf(customer.getCurrentBalance().toString())*100;
		int coins = (int) tempCoins;
		int quarters = coins/25;
		int dimes = coins%25/10;
		int nickels = coins%25%10/5;
		System.out.println("quarters: " + quarters + " dimes: " + dimes + " nickels: " + nickels);
		log.logWriter("GIVE CHANGE: $" + customer.getCurrentBalance().toString() + " $0.00");
	}
	
	private void processPurchaseMenuOption() {
		String purchaseMenuOption = "";
		while (!purchaseMenuOption.equals("Back")) {
			purchaseMenuOption = (String) menu.getChoiceFromOptions(PURCHESE_MENU);
			if (purchaseMenuOption.equals("Feed Money")) {
				processMoneyFeed();
			} else if (purchaseMenuOption.equals("Select Product")) {
				selectProduct();
			} else if (purchaseMenuOption.equals("Finish Transaction")) {
				finishTransaction();
			}
		}
		System.out.println("Current Money Provided: $" + customer.getCurrentBalance());
	}

	private void selectProduct() {
		System.out.println("Current Money Provided: $" + customer.getCurrentBalance());
		System.out.println("Please enter the code for the item you wish to purchase");
		String itemSelected = "";
		String chipSelected = "";
		String candySelected = "";
		String drinkSelected = "";
		String gumSelected = "";
		while (!itemSelected.equals("Back")) {
			itemSelected = (String) menu.getChoiceFromOptions(SELECT_PRODUCT);
			if (itemSelected.equals("Chip")) {
				chipSelected = (String) menu.getChoiceFromOptions(SELECT_CHIP);
				if (!chipSelected.contentEquals("Back")) {
					selectedItems.add(item.addItemToPerchase(chipSelected));
				}
			} else if (itemSelected.equals("Candy")) {
				candySelected = (String) menu.getChoiceFromOptions(SELECT_CANDY);
				if (!candySelected.contentEquals("Back")) {
					selectedItems.add(item.addItemToPerchase(candySelected));
				}
			} else if (itemSelected.equals("Drink")) {
				drinkSelected = (String) menu.getChoiceFromOptions(SELECT_DRINK);
				if (!drinkSelected.contentEquals("Back")) {
					selectedItems.add(item.addItemToPerchase(drinkSelected));
				}
			} else if (itemSelected.equals("Gum")) {
				gumSelected = (String) menu.getChoiceFromOptions(SELECT_GUM);
				if (!gumSelected.contentEquals("Back")) {
					selectedItems.add(item.addItemToPerchase(gumSelected));
				}
			}
		}
	}

	private void finishTransaction() {
		System.out.flush();  
		if (customer.getCurrentBalance().compareTo(item.getTotal()) == -1) {
			System.out.println("Sorry you do not have enough money to finish this transaction. Please add more money.");
			processPurchaseMenuOption();
		} else {
			String oldBalance = customer.getCurrentBalance().toString();
			customer.subFromCurrentBalance(item.getTotal());
			perchesedItems.addAll(selectedItems);
			perchesedItems.removeAll(Arrays.asList("", null));
			selectedItems.clear();
			System.out.println(customer);
			log.logWriter("Items perchesed: " + perchesedItems + " $" + oldBalance + " $" + customer.getCurrentBalance().toString());
			run();
		}
	}


	public void processMoneyFeed() {
		String feedOptions = "";
		while (!feedOptions.equals("Back")) {
			feedOptions = (String) menu.getChoiceFromOptions(MONEY_MENU);	
			if (!feedOptions.equals("Back")) {
				money = BigDecimal.valueOf(Double.parseDouble(feedOptions.replace("$", "")));
				customer.addToCurrentBalance(money.setScale(2));
				System.out.println("Current Money Provided: $" + customer.getCurrentBalance());		
				log.logWriter("FEED MONEY: " + feedOptions + " $" + customer.getCurrentBalance().toString());
			}
		}
		System.out.println("Current Money Provided: $" + customer.getCurrentBalance());
		
	}

	public static void main(String[] args) throws FileNotFoundException {
		item.getInventory();
		SELECT_CHIP = item.getChipList();
		SELECT_CANDY = item.getCandyList();
		SELECT_DRINK = item.getDrinkList();
		SELECT_GUM = item.getGumList();
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
	
	public static void readProducts() {
		File products = new File("vendingmachine.csv");
		List <String> readList = new ArrayList <String> ();
		if (products.exists()) {
			try (Scanner fileScanner = new Scanner(products)) {
				while (fileScanner.hasNextLine()) {
				String items = fileScanner.nextLine();
				readList.add(items);
				}
			} catch (FileNotFoundException e) {
			}
		}
		for (String i : readList) {
			System.out.println(i);
		}
	}
	
}
