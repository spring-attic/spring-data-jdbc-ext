package org.springframework.data.jdbc.core.domain;

import java.util.HashSet;
import java.util.Set;

public class Customer {

	private Integer id;

	private String name;

	private Set<Address> addresses = new HashSet<Address>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public void addAddress(Address address) {
		this.addresses.add(address);
	}

	@Override
	public String toString() {
		return "Customer: " + " " + name + " " + addresses;
	}
}
