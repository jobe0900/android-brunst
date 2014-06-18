package com.example.android_projekt.event;

import com.example.android_projekt.individ.IdNr;

/**
 * A small class holding a Note for an individual
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class Note extends Event
{
	private static final String TAG = "Brunst: Note";
	public static final long UNSAVED_ID = -1;
	
	private long noteId = UNSAVED_ID;
//	private Event event;
//	private long eventId;
	private String text = null;
	
	/**
	 * Constructor
	 * @param	eventId		The _id for the Event
	 */
	public Note(IdNr idNr) {
		super(EventType.Type.EVENT_NOTE, idNr);
	}
	
	public Note(Event event) {
		super(event);
	}
	
	public String toString() {
		boolean isTextLong = text.length() > 10;
		String shortText = isTextLong ? (text.substring(0, 10) + "...") : text;
		return getRegTime().toString() + ": " + shortText;
	}
	
//	/** Constructor */
//	public Note() {
//		_id = UNSAVED_ID;
//		event = null;
//		text = null;
//	}

	public long getNoteId() {
		return noteId;
	}

	public void setNoteId(long id) {
		noteId = id;
	}
	
//	public long getEventId() {
//		return eventId;
//	}
	
//	public void setEventId(long eventId) {
//		this.eventId = eventId;
//	}

//	public boolean hasEvent() {
//		return event != null;
//	}
//	
//	public Event getEvent() {
//		return event;
//	}
//
//	public void setEvent(Event event) {
//		this.event = event;
//	}
	
	public boolean hasText() {
		return text != null && text.length() > 0;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
