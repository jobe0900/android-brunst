package com.example.android_projekt.event;

import java.io.Serializable;
import java.util.Calendar;

import com.example.android_projekt.Utils;

public class Reminder 
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Brunst: Reminder";
	
	public static final long UNSAVED_ID = -1;
	
	public static enum Type {
		REMINDER_NORMAL,
		REMINDER_ALERT,
		REMINDER_SERIOUS
	};
	
	private long reminderId = UNSAVED_ID;
	private Type type = Type.REMINDER_NORMAL;
	private Event event = null;
	private Event.Type eventType = null;
	private String description = "";
	private Calendar eventTime = null;
	private Calendar reminderTime = null;
	private int reminderIntervalHour = 0;
	private int reminderIntervalDay = 1;
	private boolean active = true;
	
	/**
	 * Constructor
	 * @param	event		The Event generating this reminder
	 * @param	eventType	The type of event to remind about
	 */
	public Reminder(Event event, Event.Type eventType) {
		this.event = event;
		this.eventType = eventType;
	}
	
	@Override
	public String toString() {
		String str = null;
		if(eventTime != null) {
			str = type + " : " + eventType + " : " + Utils.datetimeToString(eventTime);
		}
		return str;
	}

	// SETTERS & GETTERS -------------------------------------------------------
	public long getReminderId() {
		return reminderId;
	}

	public void setReminderId(long reminderId) {
		this.reminderId = reminderId;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public Event.Type getEventType() {
		return eventType;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getEventTime() {
		return eventTime;
	}

	public void setEventTime(Calendar eventTime) {
		this.eventTime = eventTime;
	}

	public Calendar getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(Calendar reminderTime) {
		this.reminderTime = reminderTime;
	}
	
	public int getReminderIntervalHour() {
		return reminderIntervalHour;
	}
	
	public void setReminderIntervalHour(int hours) {
		reminderIntervalHour = hours;
	}
	
	public int getReminderIntervalDay() {
		return reminderIntervalDay;
	}
	
	public void setReminderIntervalDay(int days) {
		reminderIntervalDay = days;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
