package com.example.android_projekt;

public class ProductionSite 
{
	private int _id;
	private ProductionSiteNr nr;
	private String name;
	private String address;
	private String postnr;
	private String postaddress;
	
	/** Constructor. */
	ProductionSite(ProductionSiteNr ppnr) {
		nr = ppnr;
	}
	
	/** Constructor. */
	ProductionSite(String ppnrString) {
		nr = new ProductionSiteNr(ppnrString);
	}
	
	/** Constructor. */
	ProductionSite(String org, String ppnr) {
		nr = new ProductionSiteNr(org, ppnr);
	}
	
	/* Setters and Getters. ***************************************************/
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostnr() {
		return postnr;
	}

	public void setPostnr(String postnr) {
		this.postnr = postnr;
	}

	public String getPostaddress() {
		return postaddress;
	}

	public void setPostaddress(String postaddress) {
		this.postaddress = postaddress;
	}

	public ProductionSiteNr getNr() {
		return nr;
	}
	
	
}
