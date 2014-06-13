package com.example.android_projekt.individ;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;

import com.example.android_projekt.productionsite.ProductionSiteNr;

/**
 * ID number for a Individual, made up of 4 parts, like "SE-012345-0123-9".
 * The first two part is the ProductionSiteNr where the Individual was born, 
 * the third part is a unique running number for that site and
 * the fourth part is a checknr.
 * 
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class IdNr implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private ProductionSiteNr birthSite;
	private String individNr;
	private String checkNr;
	
	/**
	 * Constructor.
	 * @param	idNrString		String like "SE-012345-0123-9"
	 * @throws	ParseException	If malformed string
	 */
	public IdNr(String idNrString) throws ParseException {
		String arr[] = IdNr.getIdNrStringAsArray(idNrString);
		birthSite = IdNr.getBirthSiteNrFromString(idNrString);
		individNr = arr[2];
		checkNr = arr[3];
	}
	
	/**
	 * Copy constructor.
	 * @param orig
	 */
	public IdNr(IdNr orig) {
		this.birthSite = new ProductionSiteNr(orig.birthSite);
		this.individNr = new String(orig.individNr);
		this.checkNr = new String(orig.checkNr);
	}
	
	/**
	 * Get the ProductionSiteNr part of the IdNr, i.e. the birth site.
	 */
	public ProductionSiteNr getBirthSiteNr() {
		return new ProductionSiteNr(birthSite.toString());
	}
	
	/**
	 * Get the individNr part of the IdNr, as a 4 char string.
	 */
	public String getIndividNr() {
		return individNr;
	}
	
	/**
	 * Get the check nr part of the IdNr, as a 1 char string.
	 * @return
	 */
	public String getCheckNr() {
		return checkNr;
	}
	
	/**
	 * Return a string like "SE-012345-0123-9"
	 */
	@Override
	public String toString() {
		return birthSite.toString() + "-" + individNr + "-" + checkNr;
	}
	
	/**
	 * Generate a short nr like 123 from the IdNr "SE-012345-0123-9"
	 * @return
	 * @throws NumberFormatException
	 */
	public int generateShortNr() throws NumberFormatException {
		return Integer.parseInt(individNr);
	}
	
	/**
	 * Try to parse a String containing an IdNr, like "SE-012345-0123-9"
	 * and extract the ProductionSiteNr part, i.e. "SE-012345"
	 * @param idNrString
	 * @return
	 * @throws ParseException	If malformed string.
	 */
	public static ProductionSiteNr getBirthSiteNrFromString(String idNrString) throws ParseException {
		String[] arr = IdNr.getIdNrStringAsArray(idNrString);
		return new ProductionSiteNr(arr[0], arr[1]);
	}
	
	/**
	 * Try to parse a String containing an IdNr, like "SE-012345-0123-9"
	 * and extract the ProductionSiteNr part, i.e. "0123"
	 * @param idNrString
	 * @return
	 * @throws ParseException
	 */
	public static String getIndividNrFromString(String idNrString) throws ParseException {
		String[] arr = IdNr.getIdNrStringAsArray(idNrString);
		return arr[2];
	}
	
	/**
	 * Try to parse a String containing an IdNr, like "SE-012345-0123-9"
	 * and extract the ProductionSiteNr part, i.e. "9"
	 * @param idNrString
	 * @return
	 * @throws ParseException
	 */
	public static String getCheckNrFromString(String idNrString) throws ParseException {
		String[] arr = IdNr.getIdNrStringAsArray(idNrString);
		return arr[3];
	}
	
	/**
	 * Split an IdNr string like "SE-012345-0123-9" into an array with the parts
	 * as elements, i.e. {"SE", "012345", "0123", "9"}
	 * @param idNrString
	 * @return
	 * @throws ParseException	If malformed String
	 */
	public static String[] getIdNrStringAsArray(String idNrString) throws ParseException {
		if(idNrString.length() != 16) {
			throw new ParseException("Malformed IdNrString", 0);
		}
		String[] arr = idNrString.split("-");
		if( arr.length != 4 && 
			arr[0].length() != 2 && 
			arr[1].length() != 6 &&
			arr[2].length() != 4 &&
			arr[3].length() != 1
		) {
			throw new ParseException("Malformed IdNrString", 0);
		}
		return arr;
	}
	
	/**
	 * Generate a shortnr like "123" from idNrString like "SE-012345-0123-9"
	 * @param idNrString
	 * @return
	 * @throws ParseException
	 */
	public static int generateShortNr(String idNrString) throws ParseException {
		return Integer.parseInt(IdNr.getIndividNrFromString(idNrString));
	}
}
