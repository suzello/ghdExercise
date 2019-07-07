package com.ghd_test.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Multimap;

public class TaxCalculation {

	/**
	 * cannot differentiate between books/food/medical products, so added a few in a
	 * List, If need more exempt products add them to the list
	 * Ideally products might be classified under different types and we can check if each item belongs to a type
	 */
	static List<String> expemptItems = Arrays.asList("book", "chocolate bar", "chocolates", "packet of headache pills");
	static String imported = "imported";

	private static final BigDecimal ROUND_FACTOR = new BigDecimal("0.05");

	static BigDecimal finalPrice;
	static BigDecimal finalRoundedPrice;
	static BigDecimal finalTotalAmount;
	static BigDecimal finalTotalTax;
	static BigDecimal initialTotalAmount = new BigDecimal("0");
	static BigDecimal initialTotalTax = new BigDecimal("0");
	private static BigDecimal basicTaxPrice;
	static BigDecimal importTaxPrice;

	public static BigDecimal getBasicTaxPrice() {
		if(basicTaxPrice != null) {
			return basicTaxPrice;
		}
		return new BigDecimal("0");
	}

	public static void setBasicTaxPrice(BigDecimal basicTaxPrice) {
		TaxCalculation.basicTaxPrice = basicTaxPrice;
	}


	public static BigDecimal getImportTaxPrice() {
		if(importTaxPrice != null) {
			return importTaxPrice;
		}
		return new BigDecimal("0");
	}

	public static void setImportTaxPrice(BigDecimal importTaxRate) {
		TaxCalculation.importTaxPrice = importTaxRate;
	}

	
	//printing appropriate text based on different combinations
	public void calcualateTaxandPrint() {

		ReadAndParse readandparse = new ReadAndParse();

		Multimap<String, String> parsedItems = readandparse.readFromFileAndParse();
		
		for (String key : parsedItems.keySet()) {
			List<String> values = (List<String>) parsedItems.get(key);

			HashMap<String, String> priceAndQuantity = getPriceAndQuantity(values);
			BigDecimal initialPrice = new BigDecimal(priceAndQuantity.get("price"));
			BigDecimal quantity = new BigDecimal(priceAndQuantity.get("quantity"));

			if (!expemptItems.parallelStream().anyMatch(key::contains) && !key.contains(imported)) {
				calculateNonExempt(initialPrice, basicTaxPrice, quantity);
				System.out.println(quantity + " " + key + " " + ": " + finalRoundedPrice);
			}
			if (key.contains(imported) && !expemptItems.parallelStream().anyMatch(key::contains)) {
				calculateImportedAndNonExempt(initialPrice, basicTaxPrice, quantity, importTaxPrice);
				System.out.println(quantity + " " + key + " " + ": " + finalRoundedPrice);
			}

			if (key.contains(imported) && expemptItems.parallelStream().anyMatch(key::contains)) {
				calculateImported(initialPrice, quantity, importTaxPrice);
				System.out.println(quantity + " " + key + " " + ": " + finalRoundedPrice);
			}
			if (expemptItems.parallelStream().anyMatch(key::contains) && !key.contains(imported)) {
				finalRoundedPrice = initialPrice;
				System.out.println(quantity + " " + key + " " + ": " + finalRoundedPrice);
			}
			
			//logic to calculate total sales tax and total amount
			finalTotalAmount = initialTotalAmount.add(finalRoundedPrice);
			initialTotalAmount = finalTotalAmount;
			finalTotalTax = initialTotalTax.add(getImportTaxPrice()).add(TaxCalculation.getBasicTaxPrice());
			initialTotalTax = finalTotalTax;
			setBasicTaxPrice(new BigDecimal("0"));
			setImportTaxPrice(new BigDecimal("0"));
		}
		System.out.println("Sales Taxes:" + finalTotalTax);
		System.out.println("Total:" + finalTotalAmount);
	}

	//logic to calculate sales tax for NonExempt goods
	public static void calculateNonExempt(BigDecimal initialPrice, BigDecimal basicTaxPrice, BigDecimal quantity) {
		BigDecimal baicTaxRate = initialPrice.multiply(Constants.basicSalesTax).divide(new BigDecimal("100"));
		baicTaxRate = roundOff(baicTaxRate);
		setBasicTaxPrice(baicTaxRate);
		finalRoundedPrice = initialPrice.multiply(quantity).add(baicTaxRate);
		
	}

	//logic to calculate sales tax for imported goods
	private static void calculateImported(BigDecimal initialPrice, BigDecimal quantity, BigDecimal importTaxPrice) {
		BigDecimal importTaxRate = initialPrice.multiply(Constants.importSalesTax).divide(new BigDecimal("100"));
		importTaxRate = roundOff(importTaxRate);
		setImportTaxPrice(importTaxRate);
		finalRoundedPrice = initialPrice.multiply(quantity).add(importTaxRate);
	}

	//logic to calculate sales tax for imported and NonExempt goods
	private static void calculateImportedAndNonExempt(BigDecimal initialPrice, BigDecimal basicTaxPrice,
			BigDecimal quantity, BigDecimal importTaxPrice) {
		calculateNonExempt(initialPrice, basicTaxPrice, quantity);
		BigDecimal basicTaxAmount = getBasicTaxPrice();
		calculateImported(initialPrice, quantity, importTaxPrice);
		BigDecimal importTaxAmount = getImportTaxPrice();
		finalRoundedPrice = initialPrice.multiply(quantity).add(importTaxAmount).add(basicTaxAmount);
	}

	
	//parsing each key to get their price and quantity
	private static HashMap<String, String> getPriceAndQuantity(List<String> values) {

		HashMap<String, String> priceAndQuantity = new HashMap<String, String>();

		for (String value : values) {

			if (value.contains("quantity")) {
				int index = value.lastIndexOf(":");
				String quantity = value.substring(index + 1);
				priceAndQuantity.put("quantity", quantity);
			}
			if (value.contains("price")) {
				int index = value.lastIndexOf(":");
				String price = value.substring(index + 1);
				priceAndQuantity.put("price", price);
			}
		}
		return priceAndQuantity;
	}

	private static BigDecimal roundOff(BigDecimal value) {
		value = value.divide(ROUND_FACTOR);
		value = new BigDecimal(Math.ceil(value.doubleValue()));
		value = value.multiply(ROUND_FACTOR);
		return value;
	}
}
