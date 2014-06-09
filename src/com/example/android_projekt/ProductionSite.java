package com.example.android_projekt;

/**
 * ProductionSite ("Produktionsplats") is a geographical site.
 * All production animals belongs to a Site (but can temporarilly be on another).
 * For the purpose of this App, the User has at least one Site = the home farm,
 * but can have several, e.g. milk cows on one site and young cattle on another.
 */
public class ProductionSite 
{
	public static final int UNSAVED_ID = -1;	
	private long _id;
	private ProductionSiteNr sitenr;
	private String name;
	private String address;
	private String postnr;
	private String postaddress;
	private String coordinates;
	
	/** Constructor. */
	ProductionSite(ProductionSiteNr ppnr) {
		_id = UNSAVED_ID;	// signal that this has not been saved
		sitenr = ppnr;
	}
	
	/** Constructor. */
	ProductionSite(String ppnrString) {
		this(new ProductionSiteNr(ppnrString));
//		sitenr = new ProductionSiteNr(ppnrString);
	}
	
	/** Constructor. */
	ProductionSite(String org, String ppnr) {
		this(new ProductionSiteNr(org, ppnr));
	}
	
	public String toString() {
		String str = sitenr.toString();
		if(name != null || !name.equals("")) {
			str += " : " + name;
		}
		return str;
	}
	
	/* What attributes are set? ***********************************************/
	public boolean hasName() {
		return name != null && !name.equals("");
	}
	
	public boolean hasAddress() {
		return address != null && !address.equals("");
	}
	
	public boolean hasPostnr() {
		return postnr != null && !postnr.equals("");
	}
	
	public boolean hasPostaddress() {
		return postaddress != null && !postaddress.equals("");
	}
	
	public boolean hasCoordinates() {
		return coordinates != null && !coordinates.equals("");
	}
	
	/* Setters and Getters. ***************************************************/
	
	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
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
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public ProductionSiteNr getSiteNr() {
		return sitenr;
	}	
}
