package com.example.android_projekt.event;

import java.io.Serializable;
import java.text.ParseException;

/**
 * The simple class to keep track of different types of Events
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class EventType 
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final String TAG = "Brunst: EventType";
	
//	public static final String EVENT_NOTE = "Note";
//	public static final String EVENT_HEAT = "HeatEvent";
//	public static final String EVENT_MATING = "Mating";
//	public static final String EVENT_PREGCHECK = "Pregcheck";
//	public static final String EVENT_BIRTH = "Birth";
	
//	public static enum Type {
//		EVENT_NOTE,
//		EVENT_HEAT,
//		EVENT_MATING,
//		EVENT_PREGCHECK,
//		EVENT_BIRTH
//	};
	
//	private Type type;
//	
//	/** Constructor. */
//	public EventType(EventType.Type type) {
//		this.type = type;
//	}
//	
//	public EventType.Type getType() {
//		return type;
//	}
//	
//	/**
//	 * Get a string representation of the Type
//	 * @param type
//	 * @return
//	 */
//	public static String getTypeAsString(EventType.Type type) {
//		String str = null;
//		switch(type) {
//		case EVENT_NOTE:
//			str = EventType.EVENT_NOTE;
//			break;
//		case EVENT_HEAT:
//			str = EventType.EVENT_HEAT;
//			break;
//		case EVENT_MATING:
//			str = EventType.EVENT_MATING;
//			break;
//		case EVENT_PREGCHECK:
//			str = EventType.EVENT_PREGCHECK;
//			break;
//		case EVENT_BIRTH:
//			str = EventType.EVENT_BIRTH;
//			break;
//		}
//		return str;
//	}
//	
//	/**
//	 * Try to parse the string to get the proper Type
//	 * @param typeString
//	 * @return
//	 * @throws ParseException
//	 */
//	public static EventType.Type parseString(String typeString) throws ParseException {
//		Type type = null;
//		if(typeString.equalsIgnoreCase(EVENT_NOTE)) 		type = Type.EVENT_NOTE;
//		else if(typeString.equalsIgnoreCase(EVENT_HEAT))	type = Type.EVENT_HEAT;
//		else if(typeString.equalsIgnoreCase(EVENT_MATING))	type = Type.EVENT_MATING;
//		else if(typeString.equalsIgnoreCase(EVENT_PREGCHECK)) type = Type.EVENT_PREGCHECK;
//		else if(typeString.equalsIgnoreCase(EVENT_BIRTH))	type = Type.EVENT_BIRTH;
//		
//		if(type == null) {
//			throw new ParseException("could not parse typeString", 0);
//		}
//		return type;
//	}
}
