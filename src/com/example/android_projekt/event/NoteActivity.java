package com.example.android_projekt.event;

import com.example.android_projekt.MainActivity;
import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.individ.IdNr;
import com.example.android_projekt.individ.IndividualEventsActivity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.os.Build;

/**
 * An Activity for displaying and editing Notes
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class NoteActivity extends ActionBarActivity 
{
	private static final String TAG = "Brunst: NoteActivity";
	
	// updating an existing Note (as object)
	public static final String EXTRA_NOTE = "brunst.extra.NoteActivity.Note";
	// new note for idnr (as string)
	public static final String EXTRA_IDNR = "brunst.extra.NoteActivity.IdNr";
	
	// WIDGETS
	private EditText etText;
	private TextView tvIdnr;
	
	private NoteDB noteDB;
	private boolean updating = false;
	
	private Note note;
	private IdNr idnr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_NOTE)) {
			note = (Note) intent.getSerializableExtra(EXTRA_NOTE);
			updating = true;
			Log.d(TAG, "received Note: " + note.toString());
			idnr = note.getIdnr();
		}
		if(intent.hasExtra(EXTRA_IDNR)) {
			String idnrStr = intent.getStringExtra(EXTRA_IDNR);
			Log.d(TAG, "edit individual: " + idnrStr);
			try {
				idnr = new IdNr(idnrStr);
				note = new Note(idnr);
			} catch (Exception ex) {
				Log.d(TAG, "Cannot parse the idnr: " + idnrStr);
			}
		}
		
		// only continue if valid idnr
		if(idnr != null) {
			noteDB = new NoteDB(this);
			noteDB.open();
			
			findViews();
			prepareViews();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_activity_actions, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here.
		switch(item.getItemId()) {
		case R.id.note_action_erase:
			showDialogErase();
			return true;
		case R.id.note_action_save:
			showDialogSave();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// Get the context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_delete, menu); 
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_menu_delete:
			Log.d(TAG, "Pick context Delete");
			etText.setText("");
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		noteDB.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		noteDB.close();
		super.onPause();
	}
	
	/**
	 * Find the views in the layout
	 */
	private void findViews() {
		etText = (EditText) findViewById(R.id.note_entry_text);
		tvIdnr = (TextView) findViewById(R.id.note_label_idnr);
		
	}

	/**
	 * Fill the views with the contents they should have
	 */
	private void prepareViews() {
		String idText = getString(R.string.note_label_idnr) + " " + idnr.toString();
		tvIdnr.setText(idText);
		
		if(updating && note.hasText()) {
			etText.setText(note.getText());
		}
		
		registerForContextMenu(etText);
	}
	
	private void showDialogErase() {
		if(etText.length() > 0) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_ask_erase_text);
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					etText.setText("");	
				}
			});
			// NO button
			builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// nothing?
				}
			});

			builder.show();
		}
	}
	
	private void showDialogDelete() {
		if(etText.length() == 0) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_ask_delete_note);
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteNote();
				}
			});
			// NO button
			builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// nothing?
				}
			});

			builder.show();
		}
	}

	

	private void showDialogSave() {
		if(etText.length() > 0) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.dialog_ask_save) + "?");
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveNote();
				}
			});
			// NO button
			builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// nothing?
				}
			});
			builder.show();
		}
		// if no text: ask to delete note
		else {
			if(updating) {
				showDialogDelete();
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.toast_nothing_to_save, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Perform the save
	 */
	protected void saveNote() {
		note.setText(etText.getText().toString());
		
		noteDB.saveNote(note);
		
		// back to Events
		Intent intent = new Intent(this, IndividualEventsActivity.class);
		intent.putExtra(IndividualEventsActivity.EXTRA_IDNR, note.getIdnr().toString());
		startActivity(intent);
	}
	
	/**
	 * Perform the deletion of the Note
	 */
	protected void deleteNote() {
		noteDB.deleteNote(note.getNoteId());

		// back to Events
		Intent intent = new Intent(this, IndividualEventsActivity.class);
		intent.putExtra(IndividualEventsActivity.EXTRA_IDNR, note.getIdnr().toString());
		startActivity(intent);
	}

}
