package com.example.android_projekt.individ;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.android_projekt.productionsite.ProductionSiteNr;

/**
 * An Individual is an individual animal, identified by its IdNr. It must also
 * always have a HomeSite, which is the ProductionPlaceNr for the current owning
 * ProductionPlace.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class Individual implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final int UNSAVED_ID = -1;
	public static final int DEFAULT_HEATCYCLUS = 21;	// 21 days
	
	
	public static enum Sex {
		M,		// Male
		F		// Female
	};
	
	private long _id;
	private IdNr idnr;
	private int shortnr;
	private Calendar birthdate;
	private String name;
	private Sex sex;
	private boolean active;
	private Calendar lastbirth;
	private int lactationnr;
	private int heatcyclus;
	private ProductionSiteNr homesitenr;
	private IdNr motheridnr;
	private IdNr fatheridnr;
	private String imageUri;

	/**
	 * Constructor. Defaults to a Female Individual.
	 * @param	idNr		ID of Individual to be created
	 * @param 	homeSitenr	The production site nr for the site to which this individ belongs
	 */
	public Individual(IdNr idNr, ProductionSiteNr homeSitenr) {
		this(idNr, homeSitenr, Sex.F);
	}
	
	/**
	 * Constructor.
	 * @param	idNr		ID of Individual to be created
	 * @param 	homeSitenr	The production site nr for the site to which this individ belongs
	 * @param	sex			Set the Sex of this Individual M or F.
	 */
	public Individual(IdNr idNr, ProductionSiteNr homeSitenr, Sex sex) {
		_id = UNSAVED_ID;
		idnr = idNr;
		
		// some defaults
		shortnr = idnr.generateShortNr();
		this.sex = sex;
		active = true;
		lactationnr = 0;
		heatcyclus = DEFAULT_HEATCYCLUS;
		homesitenr = homeSitenr;
	}
	
	/**
	 * Get a string representation of this Individual, like
	 * "123 Rosa (SE-012345-0123-9)"
	 */
	@Override
	public String toString() {
		String str = shortnr + "";
		if(name != null && !name.equals("")) {
			str += " " + name;
		}
		str += " (" + idnr.toString() + ")";
		return str;
	}
	
	// GETTERS and SETTERS -----------------------------------------------------
	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public IdNr getIdNr() {
		return new IdNr(idnr);
	}

	public int getShortNr() {
		return shortnr;
	}

	public void setShortNr(int shortnr) {
		this.shortnr = shortnr;
	}

	public boolean hasBirthdate() {
		return birthdate != null;
	}
	
	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}
	
	public boolean hasName() {
		return name != null && !name.equals("");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sex getSex() {
		return sex;
	}
	
	public String getSexAsString() {
		if(sex == Sex.F)
			return "F";
		else
			return "M";
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}
	
	public void setSex(String sex) {
		if(sex.equalsIgnoreCase("M"))
			this.sex = Sex.M;
		else
			this.sex = Sex.F;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean hasLastBirth() {
		return lastbirth != null && !lastbirth.equals("");
	}

	public Calendar getLastBirth() {
		return lastbirth;
	}

	public void setLastBirth(Calendar lastbirth) {
		this.lastbirth = lastbirth;
	}

	public int getLactationNr() {
		return lactationnr;
	}

	public void setLactationNr(int lactationnr) {
		this.lactationnr = lactationnr;
	}

	public int getHeatcyclus() {
		return heatcyclus;
	}

	public void setHeatcyclus(int heatcyclus) {
		this.heatcyclus = heatcyclus;
	}

	public ProductionSiteNr getHomesiteNr() {
		return homesitenr;
	}

	public void setHomesiteNr(ProductionSiteNr homesitenr) {
		if(homesitenr != null)
			this.homesitenr = homesitenr;
	}

	public boolean hasMotherIdNr() {
		return motheridnr != null && !motheridnr.equals("");
	}
	
	public IdNr getMotherIdNr() {
		return new IdNr(motheridnr);
	}

	public void setMotherIdNr(IdNr motheridnr) {
		this.motheridnr = motheridnr;
	}
	
	public boolean hasFatherIdNr() {
		return fatheridnr != null && !fatheridnr.equals("");
	}

	public IdNr getFatherIdNr() {
		return new IdNr(fatheridnr);
	}

	public void setFatherIdNr(IdNr fatheridnr) {
		this.fatheridnr = fatheridnr;
	}
	
	public boolean hasImageUri() {
		return imageUri != null && !imageUri.equals("");
	}

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
}
