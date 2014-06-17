package com.example.android_projekt.event;

/**
 * The simple class to keep track of different types of Events
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class EventType 
{
	private static final String TAG = "Brunst: EventType";
	
	public static final String EVENT_NOTE = "Note";
	public static final String EVENT_HEAT = "Heat";
	public static final String EVENT_MATING = "Mating";
	public static final String EVENT_PREGCHECK = "Pregcheck";
	public static final String EVENT_BIRTH = "Birth";
	
	public static enum Type {
		EVENT_NOTE,
		EVENT_HEAT,
		EVENT_MATING,
		EVENT_PREGCHECK,
		EVENT_BIRTH
	};
}
