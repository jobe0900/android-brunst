package com.example.android_projekt.event;

import java.io.Serializable;

import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.IdNr;

/**
 * A class for holding observations of Heat.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class HeatEvent extends Event
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Brunst: HeatEvent";
	
	public static final long UNSAVED_ID = -1;
	
	private long heatId = UNSAVED_ID;
	private int heatRound = 0;
	private Sign sign = null;
	private Strength stren= null;
	private String note = null;
	
	public static enum Sign {
		HEATSIGN_UNREST,	// "oro"
		HEATSIGN_DISCHARGE,	// "flytning"
		HEATSIGN_RIDING,	// "rida"
		HEATSIGN_SAG,		// "svanka"
		HEATSIGN_SWELLING,	// "svullnad"
		HEATSIGN_LOWMILK	// "lite mjolk"
	};
	
	public static enum Strength {
		HEATSTREN_1,
		HEATSTREN_2,
		HEATSTREN_3,
		HEATSTREN_4,
		HEATSTREN_5
	};
	
	/**
	 * Constructor
	 * @param idnr
	 */
	public HeatEvent(IdNr idnr) {
		super(Event.Type.EVENT_HEAT, idnr);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Construct a note from the underlying Event, fill in data after
	 * @param event
	 */
	public HeatEvent(Event event) {
		super(event);
	}
	
	@Override
	public String toString() {
		if(sign != null && stren != null) {
			return Utils.datetimeToString(getEventTime()) + ". Sign: " + 
				sign.toString() + ", Strength: " + stren.toString();
		}
		return null;
	}

	// Setters & getters -------------------------------------------------------
	public long getHeatId() {
		return heatId;
	}

	public void setHeatId(long heatId) {
		this.heatId = heatId;
	}

	public int getHeatRound() {
		return heatRound;
	}

	public void setHeatRound(int heatRound) {
		this.heatRound = heatRound;
	}

	public boolean hasSign() {
		return sign != null;
	}
	
	public Sign getSign() {
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
	}

	public boolean hasStrength() {
		return stren != null;
	}
	
	public Strength getStrength() {
		return stren;
	}

	public void setStrength(Strength strength) {
		this.stren = strength;
	}
	
	public boolean hasNote() {
		return note != null && note.length() > 0;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
