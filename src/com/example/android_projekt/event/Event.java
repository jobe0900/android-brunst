package com.example.android_projekt.event;

import java.io.Serializable;
import java.util.Calendar;

import com.example.android_projekt.individ.IdNr;

/**
 * Base class for the different kinds of events to register:
 * 0. Note
 * 1. HeatEvent
 * 2. Mating
 * 3. Pregnancy check
 * 4. Birth
 * 
 * Keeps track of which individual it concerns, time of event and time of registration.
 *  
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class Event 
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Brunst: Event";
	
	public static final long UNSAVED_ID = -1;
	
	public static enum Type {
		EVENT_NOTE,
		EVENT_HEAT,
		EVENT_MATING,
		EVENT_PREGCHECK,
		EVENT_BIRTH
	};
	
	private long eventId;
	private Type type;
	private IdNr idnr;
	private Calendar eventTime;
	private Calendar regTime;
	
	/**
	 * Constructor
	 * @param	type	The type of event
	 * @param	idnr	The Individual's IdNr
	 */
	public Event(Type type, IdNr idnr) {
		eventId = UNSAVED_ID;
		this.type = type;
		this.idnr = idnr;
		eventTime = Calendar.getInstance();
		regTime = Calendar.getInstance();
	}
	
	/**
	 * Copy constructor
	 * @param orig
	 */
	public Event(Event orig) {
		eventId = orig.eventId;
		type = orig.type;
		idnr = new IdNr(orig.idnr);
		eventTime = orig.eventTime;		// is this safe?
		regTime = orig.regTime;
	}
	
	@Override
	public String toString() {
		String str = regTime.toString() + ": " + idnr.toString() + " " + type + " at " + eventTime.toString();
		return str;
	}

	// GETTERS and SETTERS -----------------------------------------------------
	public long getEventId() {
		return eventId;
	}
	
	public void setEventId(long id) {
		eventId = id;
	}
	
	public Type getType() {
		return type;
	}

	public IdNr getIdnr() {
		return idnr;
	}

	public Calendar getEventTime() {
		return eventTime;
	}

	public void setEventTime(Calendar eventTime) {
		this.eventTime = eventTime;
	}

	public Calendar getRegTime() {
		return regTime;
	}
	
	public void setRegTime(Calendar regTime) {
		this.regTime = regTime;
	}
	
}
