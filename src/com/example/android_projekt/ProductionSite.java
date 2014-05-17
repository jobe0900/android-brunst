package com.example.android_projekt;

/**
 * ProductionSite ("Produktionsplats") is a geographical site.
 * All production animals belongs to a Site (but can temporarilly be on another).
 * For the purpose of this App, the User has at least one Site = the home farm,
 * but can have several, e.g. milk cows on one site and young cattle on another.
 */
public class ProductionSite 
{
	private int _id;
	private ProductionSiteNr nr;
	private String name;
	private String address;
	private String postnr;
	private String postaddress;
	private String coord;
	
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
	
	public String getCoordinates() {
		return coord;
	}

	public void setCoordinates(String coord) {
		this.coord = coord;
	}

	public ProductionSiteNr getNr() {
		return nr;
	}
	
	
}
