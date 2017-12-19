package techflavors.tools.smc.generators.source.data;

public class Address {
	public String street;
	public String city;

	@Override
	public String toString() {
		return street + "," + city;
	}
	
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
}
