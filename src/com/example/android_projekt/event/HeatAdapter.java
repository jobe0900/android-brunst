package com.example.android_projekt.event;

import java.util.List;

import com.example.android_projekt.R;
import com.example.android_projekt.Utils;
import com.example.android_projekt.event.HeatEvent.Sign;
import com.example.android_projekt.event.HeatEvent.Strength;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An adapter to display HeatEvents in a ListView
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class HeatAdapter extends ArrayAdapter<HeatEvent>
{
	private static final String TAG = "Brunst: HeatAdapter";
	
	private final Context context;
	private final List<HeatEvent> heats;
	
	public HeatAdapter(Context context, List<HeatEvent> heats) {
		super(context, R.layout.list_row_heat, heats);
		this.context = context;
		this.heats = heats;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.list_row_heat, parent, false);
		ImageView img = (ImageView) rowView.findViewById(R.id.list_row_heat_img);
		TextView line1 = (TextView) rowView.findViewById(R.id.list_row_heat_line1);
		TextView line2 = (TextView) rowView.findViewById(R.id.list_row_heat_line2);
		
		HeatEvent heat = heats.get(position);
		line1.setText(Utils.datetimeToString(heat.getEventTime()));
		// text for line 2
		String line2Str = getStringFromSign(heat.getSign()) +
				" (" + context.getString(R.string.heat_label_heatround) + 
				": " + heat.getHeatRound() + ")";
		line2.setText(line2Str);
		
		Log.d(TAG, "line 1: " + line1.getText().toString());
		Log.d(TAG, "line 2: " + line2Str);
		
		// image for img
		HeatEvent.Strength stren = heat.getStrength();
		int imgId = getImgIdFromStrength(stren);
		img.setImageDrawable(context.getResources().getDrawable(imgId));
		
		return rowView;
	}

	
	/** Get the string corresponding to a Sign */
	private String getStringFromSign(Sign sign) {
		String signStr = null;
		
		switch(sign) {
		case HEATSIGN_UNREST:
			signStr = context.getString(R.string.heat_sign_unrest);
			break;
		case HEATSIGN_DISCHARGE:
			signStr = context.getString(R.string.heat_sign_discharge);
			break;
		case HEATSIGN_RIDING:
			signStr = context.getString(R.string.heat_sign_riding);
			break;
		case HEATSIGN_SAG:
			signStr = context.getString(R.string.heat_sign_sag);
			break;
		case HEATSIGN_SWELLING:
			signStr = context.getString(R.string.heat_sign_swelling);
			break;
		case HEATSIGN_LOWMILK:
			signStr = context.getString(R.string.heat_sign_lowmilk);
			break;
		}
		
		return signStr;
	}
	
	/** Get the id to a Drawable corresponding to a Strength. */
	private int getImgIdFromStrength(Strength stren) {
		int id = 0;
		
		switch(stren) {
		case HEATSTREN_1:
			id = R.drawable.ic_brunst1;
			break;
		case HEATSTREN_2:
			id = R.drawable.ic_brunst2;
			break;
		case HEATSTREN_3:
			id = R.drawable.ic_brunst3;
			break;
		case HEATSTREN_4:
			id = R.drawable.ic_brunst4;
			break;
		case HEATSTREN_5:
			id = R.drawable.ic_brunst5;
			break;
		}
		
		return id;
	}

}
