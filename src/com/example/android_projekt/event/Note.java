package com.example.android_projekt.event;

import java.io.Serializable;

import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.IdNr;

/**
 * A small class holding a Note for an individual
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class Note extends Event
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Brunst: Note";
	
	public static final long UNSAVED_ID = -1;
	
	private long noteId = UNSAVED_ID;
	private String text = null;
	
	/**
	 * Constructor
	 * @param	idNr		IdNr for the Individual the Notes is concerning
	 */
	public Note(IdNr idNr) {
		super(Event.Type.EVENT_NOTE, idNr);
	}
	
	/**
	 * Create a Note from an Event.
	 * @param event
	 */
	public Note(Event event) {
		super(event);
	}
	
	@Override
	public String toString() {
		boolean isTextLong = text.length() > 10;
		String shortText = isTextLong ? (text.substring(0, 10) + "...") : text;
		return Utils.datetimeToString(getRegTime()) + ": " + shortText;
	}
	

	// SETTERS & GETTERS
	public long getNoteId() {
		return noteId;
	}

	public void setNoteId(long id) {
		noteId = id;
	}
	
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
