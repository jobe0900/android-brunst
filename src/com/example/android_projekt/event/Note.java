package com.example.android_projekt.event;

/**
 * A small class holding a Note for an individual
 * @author Jonas
 *
 */
public class Note 
{
	private static final String TAG = "Brunst: Note";
	public static final long UNSAVED_ID = -1;
	
	private long _id;
//	private Event event;
	private long eventId;
	private String text;
	
	/**
	 * Constructor
	 * @param	eventId		The _id for the Event
	 */
	public Note(long eventId) {
		_id = UNSAVED_ID;
		this.eventId = eventId;
		text = null;
	}
	
//	/** Constructor */
//	public Note() {
//		_id = UNSAVED_ID;
//		event = null;
//		text = null;
//	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}
	
	public long getEventId() {
		return eventId;
	}
	
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
