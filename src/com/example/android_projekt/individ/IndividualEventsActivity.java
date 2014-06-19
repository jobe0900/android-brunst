package com.example.android_projekt.individ;

import java.util.List;

import com.example.android_projekt.R;
import com.example.android_projekt.Utils;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.event.EventDB;
import com.example.android_projekt.event.Note;
import com.example.android_projekt.event.NoteActivity;
import com.example.android_projekt.event.NoteAdapter;
import com.example.android_projekt.event.NoteDB;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Build;
import android.provider.MediaStore;

/**
 * The purpose of this activity is to enable an oversight of events for an
 * Individual. You can decide which type of event to display, and add new.
 * You can also go on to edit the Individual.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class IndividualEventsActivity extends ActionBarActivity 
{
	private static final String TAG = "Brunst: IndividualEventsActivity";
	
	public static final String EXTRA_IDNR = "brunst.extra.IndividualEventsActivity.IdNr";
	
	// WIDGETS
//	private EditText etShortnr;
//	private EditText etName;
	private ImageButton ibThumb;
	private ImageButton ibEditIndividual;
	private ImageButton ibAddEvent;
	private ListView lvEvents;
	private Spinner spinEvents;
	private TextView tvShortnr;
	private TextView tvName;
	private TextView tvIdnr;
	
	private ArrayAdapter<CharSequence> eventTypeAdapter;
	private String selectedEventType;
//	private ArrayAdapter<CharSequence> listAdapter;
	
	private IndividualDB individualDB;
	private EventDB eventDB;
	
	private Individual individual;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_events);
		
		individualDB = new IndividualDB(this);
		individualDB.open();
		eventDB = new EventDB(this);
		eventDB.open();
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_IDNR)) {
			String idnrStr = intent.getStringExtra(EXTRA_IDNR);
			Log.d(TAG, "edit individual: " + idnrStr);
			IdNr idnr = null;
			try {
				idnr = new IdNr(idnrStr);
			} catch (Exception ex) {
				Log.d(TAG, "Cannot parse the idnr: " + idnrStr);
			}
			if(idnr != null) {
				individual = individualDB.getIndividual(idnr);
				if(individual != null) {
					Log.d(TAG, "received Individual: " + individual.toString());
				}
				else {
					String text = getString(R.string.toast_could_not_fetch) + " " + idnrStr;
					Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		// make sure we constructed a valid individual before continuing;
		if(individual != null) {
			findViews();
			prepareViews();
			setupListeners();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.individual_events, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		individualDB.open();
		eventDB.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		individualDB.close();
		eventDB.close();
		super.onPause();
	}
	
	/**
	 * Get references to the widgets
	 */
	private void findViews() {
//		etShortnr = (EditText) findViewById(R.id.individual_events_entry_shortnr);
//		etName = (EditText) findViewById(R.id.individual_events_entry_name);
		ibThumb = (ImageButton) findViewById(R.id.individual_events_imgbutton_thumb);
		ibEditIndividual = (ImageButton) findViewById(R.id.individual_events_imgbutton_edit_individual);
		ibAddEvent = (ImageButton) findViewById(R.id.individual_events_imgbutton_events_add);
		lvEvents = (ListView) findViewById(R.id.individual_events_listview_events);
		spinEvents = (Spinner) findViewById(R.id.individual_events_spinner_events);
		tvShortnr = (TextView) findViewById(R.id.individual_events_text_shortnr);
		tvName = (TextView) findViewById(R.id.individual_events_text_name);
		tvIdnr = (TextView) findViewById(R.id.individual_events_text_idnr);
	}

	/**
	 * Set the state of the widgets, and populate them
	 */
	private void prepareViews() {
		// disable entries
//		Utils.disableEntry(etShortnr);
//		Utils.disableEntry(etName);
		
		// populate
		tvShortnr.setText(individual.getShortNr() + "");
		if(individual.hasName()) {
			tvName.setText(individual.getName());
		}
		tvIdnr.setText(individual.getIdNr().toString());
		setThumbnail();
		
		setupSpinners();
		
	}
	

	/**
	 * Set the contents of the Thumbnail image button
	 */
	private void setThumbnail() {
		if(individual.hasImageUri()) {
			Uri imageUri = Uri.parse(individual.getImageUri());
			Bitmap thumb = getThumbnail(imageUri);
			Log.d(TAG, "setting thumbnail : " + thumb);
			if(thumb != null) {
				ibThumb.setImageBitmap(thumb);
			}
		}	
	}
	
	/**
	 * Get a micro (96x96) thumbnail from the image at the Uri.
	 * The method is a mix of solutions from this thread on SO:
	 * http://stackoverflow.com/questions/5548645/get-thumbnail-uri-path-of-the-image-stored-in-sd-card-android
	 * @param	uri		Uri to the image
	 * @return	Bitmap or null
	 */
	private Bitmap getThumbnail(Uri uri) {
	    String[] projection = { MediaStore.Images.Media._ID };
	    String result = null;
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    
	    Log.d(TAG, "cursor: " + cursor);
	    
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
	    
	    Log.d(TAG, "column: " + column_index);

	    cursor.moveToFirst();
	    long imageId = cursor.getLong(column_index);
//	    cursor.close();		// can't close or else it crashes on a second attempt at changing pic
	    Log.d(TAG, "imageID: " + imageId);

	    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
	            getContentResolver(), imageId,
	            MediaStore.Images.Thumbnails.MICRO_KIND,
	            null);
	    
	    Log.d(TAG, "bitmap: " + bitmap);
	    return bitmap;
	}

	/**
	 * Have widgets listen for events
	 */
	private void setupListeners() {
		setupOnClickListeners();		// click on buttons
		setupOnItemSelectedListeners();	// selection in spinners
		
	}

	/**
	 * Set up OnClickListeners for buttons
	 */
	private void setupOnClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.individual_events_imgbutton_thumb:
					showImage();
					break;
				case R.id.individual_events_imgbutton_edit_individual:
					editIndividual();
					break;
				case R.id.individual_events_imgbutton_events_add:
					addEvent();
					break;
				}
				
			}
		};
		ibThumb.setOnClickListener(clickListener);
		ibEditIndividual.setOnClickListener(clickListener);
		ibAddEvent.setOnClickListener(clickListener);
	}

	/**
	 * Open the thumbnail in full size in image viewer.
	 */
	protected void showImage() {
		if(individual.hasImageUri()) {
			Uri uri = Uri.parse(individual.getImageUri());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	/**
	 * Start the IndividualEdit activity.
	 */
	protected void editIndividual() {
		Intent intent = new Intent(this, IndividualEditActivity.class);
		intent.putExtra(IndividualEditActivity.EXTRA_INDIVIDUAL_UPDATE, individual.getIdNr().toString());
		startActivity(intent);
	}

	/**
	 * Add a new Event.
	 */
	protected void addEvent() {
		// TODO Auto-generated method stub
		Log.d(TAG, "should launch Event activity here");
		// NOTE
		if(selectedEventType.equals(getString(R.string.event_type_note))) {
			Intent intent = new Intent(this, NoteActivity.class);
			intent.putExtra(NoteActivity.EXTRA_IDNR, individual.getIdNr().toString());
			startActivity(intent);
		}
		
	}
	
	/**
	 * Populate spinners from array.
	 */
	private void setupSpinners() {
		// Event types
		eventTypeAdapter = ArrayAdapter.createFromResource(this, R.array.event_types, R.layout.simple_spinner_item);
		spinEvents.setAdapter(eventTypeAdapter);
	}

	/**
	 * Listen for selection in spinners.
	 */
	private void setupOnItemSelectedListeners() {
		spinEvents.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectedEventType = (String) parent.getItemAtPosition(position);
				// Notes
				if(selectedEventType.equals(getString(R.string.event_type_note))) {
					showNotesList();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// NOTHING
			}
		});
	}

	/**
	 * Display the notes in the list view
	 */
	protected void showNotesList() {
		// Get the notes
		NoteDB ndb = new NoteDB(this);
		ndb.open();
		List<Note> notes = ndb.getAllNotesForIndividual(individual.getIdNr());
		ndb.close();
		
		Log.d(TAG, "nr notes for individual: " + notes.size());
//		listAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, notes);
		
		NoteAdapter adapter = new NoteAdapter(this, notes);
		lvEvents.setAdapter(adapter);
		lvEvents.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Note note = (Note) parent.getItemAtPosition(position);
				// TODO open note for edit
				Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
				intent.putExtra(NoteActivity.EXTRA_NOTE, note);
				startActivity(intent);
//				Toast.makeText(getApplicationContext(), note.getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
