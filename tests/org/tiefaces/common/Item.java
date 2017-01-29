/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.common;

/**
 * The Class Item.
 */
public class Item {

	/** The code. */
	private String code;
	
	/** The name. */
	private String name;
	
	/** The description. */
	private String description;
	
	/** The price. */
	private Double price;
	
	/** The quantity. */
	private Double quantity;

	/**
	 * Instantiates a new item.
	 */
	public Item() {
		super();

	}
	
	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code
	 *            the new code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 *
	 * @param price
	 *            the new price
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public final Double getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 *
	 * @param quantity
	 *            the quantity to set
	 */
	public final void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * Instantiates a new item.
	 *
	 * @param code
	 *            the code
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param priceStr
	 *            the price str
	 */
	public Item(String code, String name, String description,
			String priceStr) {
		super();
		this.code = code;
		this.name = name;
		this.description = description;
		this.price = Double.valueOf(priceStr);
	}



}
