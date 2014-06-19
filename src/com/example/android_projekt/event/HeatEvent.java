package com.example.android_projekt.event;

import java.io.Serializable;

public class HeatEvent
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Brunst: HeatEvent";
	
	public static enum Sign {
		HEATSIGN_UNREST,	// "oro"
		HEATSIGN_DISCHARGE,	// "flytning"
		HEATSIGN_RIDING,	// "rida"
		HEATSIGN_SAG,		// "svanka"
		HEATSIGN_SWELLING,	// "svullnad"
		HEATSIGN_LOWMILK	// "lite mjolk"
	};
	
	public static enum Strength {
		HEATSTREN_1,
		HEATSTREN_2,
		HEATSTREN_3,
		HEATSTREN_4,
		HEATSTREN_5
	};
}
