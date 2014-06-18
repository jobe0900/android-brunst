package com.example.android_projekt.event;

import java.util.List;

import com.example.android_projekt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * An Adapter to display Notes in a List
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class NoteAdapter extends ArrayAdapter<Note>
{
	private final Context context;
	private final List<Note> notes;
	
	public NoteAdapter(Context context, List<Note> notes) {
		super(context, R.layout.list_row_note, notes);
		this.context = context;
		this.notes = notes;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.list_row_note, parent);
		TextView line1 = (TextView) rowView.findViewById(R.id.list_row_note_line1);
		TextView line2 = (TextView) rowView.findViewById(R.id.list_row_note_line2);
		
		// fill in the text
		Note note = notes.get(position);
		line1.setText(note.getRegTime().toString());
		line2.setText(note.getText());
		
		return rowView;
	}
}
