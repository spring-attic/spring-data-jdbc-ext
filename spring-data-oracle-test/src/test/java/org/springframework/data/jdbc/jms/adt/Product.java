package org.springframework.data.jdbc.jms.adt;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class Product implements Serializable {

	private Long id;
	
	private String description;
	
	private BigDecimal price;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Description: " + description + ";");
		buffer.append("Price: " + price);
		return buffer.toString();
	}
}
