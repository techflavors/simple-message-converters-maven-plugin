package techflavors.tools.smc.generators.source.data;

import java.util.List;
import java.util.Set;

public class Person {
	public String firstname;
	public String lastname;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastName) {
		this.lastname = lastName;
	}

	public List<Address> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<Address> address) {
		this.addressList = address;
	}

	public Set<Address> addressSet;
	public Address[] addressArray;

	public Set<Address> getAddressSet() {
		return addressSet;
	}

	public void setAddressSet(Set<Address> addressSet) {
		this.addressSet = addressSet;
	}

	public Address[] getAddressArray() {
		return addressArray;
	}

	public void setAddressArray(Address[] addressArray) {
		this.addressArray = addressArray;
	}

	public List<Address> addressList;

	public String toString() {
		return firstname + "," + lastname + "," + addressList;
	}

	public Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<?> anything;

	public List<?> getAnything() {
		return anything;
	}

	public void setAnything(List<?> anything) {
		this.anything = anything;
	}
}
