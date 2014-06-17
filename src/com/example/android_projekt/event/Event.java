package com.example.android_projekt.event;

import java.util.Calendar;

import com.example.android_projekt.individ.IdNr;

/**
 * Base class for the different kinds of events to register:
 * 0. Note
 * 1. Heat
 * 2. Mating
 * 3. Pregnancy check
 * 4. Birth
 * 
 * Keeps track of which individual it concerns, time of event and time of registration.
 *  
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class Event 
{
	private static final String TAG = "Brunst: Event";
	public static final long UNSAVED_ID = -1;
	
	private long _id;
	private EventType.Type type;
	private IdNr idnr;
	private Calendar eventTime;
	private Calendar regTime;
	
	/**
	 * Constructor
	 * @param	type	The type of event
	 * @param	idnr	The Individual's IdNr
	 */
	public Event(EventType.Type type, IdNr idnr) {
		_id = UNSAVED_ID;
		this.type = type;
		this.idnr = idnr;
		eventTime = Calendar.getInstance();
		regTime = Calendar.getInstance();
	}
	
	@Override
	public String toString() {
		String str = regTime.toString() + ": " + idnr.toString() + " " + type + " at " + eventTime.toString();
		return str;
	}

	// GETTERS and SETTERS -----------------------------------------------------
	public long get_id() {
		return _id;
	}
	
	public void set_id(long id) {
		this._id = id;
	}
	
	public EventType.Type getType() {
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
