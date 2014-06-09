package com.example.android_projekt;

import java.io.Serializable;

/**
 * A ProductionSiteNr ("Produktionsplatsnummer") consists of two parts:
 * - 2 letters for organisation, mostly "SE" for Sweden, but in some cases it
 *   can also be a number for a "husdjursförening" (zero-padded)
 * - 6 digits with a unique number in the organisation (zero-padded).
 * Can be combined to a single string, delimited with a '-', like: "SE-012345".
 */
public class ProductionSiteNr implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String org;
	private String ppnr;

	/**
	 * Constructor.
	 * @param org
	 * @param ppnr
	 */
	public ProductionSiteNr(String org, String ppnr) {
		this.org = org;
		this.ppnr =  ppnr;
	}
	
	/**
	 * Constructor. If the ppnrString is malformed the ProductionSiteNr is all null.
	 * @param	ppnrString	String like "SE-012345".
	 */
	public ProductionSiteNr(String ppnrString) {
		this.org = ProductionSiteNr.getOrgFromString(ppnrString);
		this.ppnr = ProductionSiteNr.getPpnrFromString(ppnrString);
	}
	
	/**
	 * Get the organisation part of the ProductionSiteNr.
	 * @return	the organisation string
	 */
	public String getOrg() {
		return org;
	}
	
	/**
	 * Get the unique nr part of the ProductionSiteNr.
	 * @return the unique nr
	 */
	public String getPpnr() {
		return ppnr;
	}
	
	/**
	 * Return a string like "SE-012345", or null if something is wrong.
	 */
	@Override
	public String toString() {
		if(org != null && ppnr != null) {
			return org + "-" + ppnr;
		}
		return null;
	}

	/**
	 * Get the first part of ppnrString "SE-012345".
	 * @param 	ppnrString	
	 * @return	the org-part of the string or null if invalid string
	 */
	public static String getOrgFromString(String ppnrString) {
		if(validString(ppnrString)) {
			return ppnrString.substring(0, 2);
		}
		return null;
	}
	
	/**
	 * Get the second part of ppnrString "SE-012345".
	 * @param 	ppnrString	
	 * @return	the org-part of the string or null if invalid string
	 */
	public static String getPpnrFromString(String ppnrString) {
		return ppnrString.substring(3);
	}
	
	/**
	 * Simple validation of a string, shoud be like "SE-012345"
	 * @param 	ppnrString
	 * @return	true if valid, false if not
	 */
	private static boolean validString(String ppnrString) {
		if(ppnrString.length() == 9 && ppnrString.charAt(2) == '-') {
			return true;
		}
		return false;
	}
}
