package com.fercugliandro.blacklist.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtil {

	public static boolean getUtilizaDadosCalendario(Context context) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getBoolean("utilizaCalendario", false);
	}
	
	public static String getOpcoesCalendario(Context context) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getString("listpref", "-1");
	}

	public static boolean getMostrarNotificacoes(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getBoolean("mostrarNotificacoes", false);
	}
}
