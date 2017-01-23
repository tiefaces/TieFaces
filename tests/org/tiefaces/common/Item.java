package org.tiefaces.common;

public class Item {

	private String code;
	private String name;
	private String description;
	private Double price;
	private Double quantity;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	
	/**
	 * @return the quantity
	 */
	public final Double getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public final void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Item(String code, String name, String description,
			String priceStr) {
		super();
		this.code = code;
		this.name = name;
		this.description = description;
		this.price = Double.valueOf(priceStr)
				;
	}
	public Item() {
		super();
		
	}
	
	

}
