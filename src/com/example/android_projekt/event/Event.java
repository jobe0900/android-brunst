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
public abstract class Event 
{
	private static final String TAG = "Brunst: Event";
	
	private EventType.Type type;
	private IdNr idnr;
	private Calendar eventTime;
	private Calendar regtime;
	
	/**
	 * Constructor
	 * @param	type	The type of event
	 * @param	idnr	The Individual's IdNr
	 */
	public Event(EventType.Type type, IdNr idnr) {
		this.type = type;
		this.idnr = idnr;
		eventTime = Calendar.getInstance();
		regtime = Calendar.getInstance();
	}
	
	@Override
	public String toString() {
		String str = regtime.toString() + ": " + idnr.toString() + " " + type + " at " + eventTime.toString();
		return str;
	}

	// GETTERS and SETTERS -----------------------------------------------------
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

	public Calendar getRegtime() {
		return regtime;
	}
	
}
