package com.ghd_test.business;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class ReadAndParse {


	/**
	 * Read data from the filePath supplied and create a 
	 * multimap of item -> price,quantity
	 * 
	 * */
	public Multimap<String, String> readFromFileAndParse() {
		LinkedHashSet<String> listOfItems = new LinkedHashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(Constants.filePath));
			String str;

			while ((str = in.readLine()) != null) {
				listOfItems.add(str);
			}
			in.close();
			if (listOfItems.isEmpty()) {
				System.out.println("input is empty");
				return null;
			}
		} catch (IOException e) {
			System.out.println("exception occured reading the file");
			e.printStackTrace();
		}
		return parse(listOfItems);
	}

	private static Multimap<String, String> parse(LinkedHashSet<String> listOfItems) {

		Multimap<String, String> map = LinkedListMultimap.create();

		
		//iterating through each entry and parsing
		for (String item : listOfItems) {
			String quantity = item.substring(0, 1);
			int index = item.lastIndexOf("at");
			String itemName = item.substring(2, index - 1);
			String price = item.substring(index + 3);
			map.put(itemName, "quantity:" + quantity);
			map.put(itemName, "price:" + price);
		}
		return map;
	}

}
