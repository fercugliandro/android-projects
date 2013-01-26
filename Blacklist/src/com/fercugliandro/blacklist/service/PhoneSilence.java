package com.fercugliandro.blacklist.service;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fercugliandro.blacklist.activity.AddNumeroBlackListActivity;
import com.fercugliandro.blacklist.database.BlacklistDataSource;
import com.fercugliandro.blacklist.database.HistoricoDataSource;
import com.fercugliandro.blacklist.datatype.Blacklist;
import com.fercugliandro.blacklist.datatype.Historico;
import com.fercugliandro.blacklist.util.CalendarUtil;
import com.fercugliandro.blacklist.util.ContactUtil;
import com.fercugliandro.blacklist.util.NotificationUtil;
import com.fercugliandro.blacklist.util.PreferencesUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PhoneSilence extends BroadcastReceiver {

	private Context mContext;
	private AudioManager audioManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		mContext = context;
		
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int ringtoneDefault = audioManager.getRingerMode();
		
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);

			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

				BlacklistDataSource blacklistDS = new BlacklistDataSource(context);
				
				String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				
				Blacklist blacklist = blacklistDS.getItemBlacklist(phoneNumber);
				
				boolean utilizaCalendario = PreferencesUtil.getUtilizaDadosCalendario(context);
				boolean mostrarNotificacoes = PreferencesUtil.getMostrarNotificacoes(context);
				
				String list = PreferencesUtil.getOpcoesCalendario(context);
				
				if (utilizaCalendario && buscarDadosCalendario()) {
					if ("S".equalsIgnoreCase(list)) {
						audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					} else {
						endCall(context);
					}
					//Gravar historico
					gravarHistorico(context, phoneNumber, "2");
				} else if (blacklist != null) {
					
					//Gravar historico
					gravarHistorico(context, blacklist.getNumero(), "1");
					
					//Finaliza ligação
					endCall(context);
				} else {
					
					if (mostrarNotificacoes) {
					
						if (!ContactUtil.verificarContatoExistente(context, phoneNumber, obterPaisSimCar(context))) {
							
							if (Build.VERSION.SDK_INT == 16) {
							
								Notification notification = NotificationUtil.sendBigPictureStyleNotification(context, intent, getPendingIntent(context, phoneNumber), phoneNumber);
								NotificationManager notificationManager = getNotificationManager(context);
								notificationManager.notify(0, notification);
							} else { 	
								Notification notification = NotificationUtil.normalStyleNotification(context, intent, getPendingIntent(context, phoneNumber), phoneNumber);
								NotificationManager notificationManager = getNotificationManager(context);
								notificationManager.notify(0, notification);
							}
						}
					}	
				}

				audioManager.setRingerMode(ringtoneDefault);
			}
		}

	}

	private boolean endCall(Context context) {

		try {
			// Get the boring old TelephonyManager
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// Get the getITelephony() method
			Class classTelephony = Class.forName(telephonyManager.getClass()
					.getName());
			Method methodGetITelephony = classTelephony
					.getDeclaredMethod("getITelephony");

			// Ignore that the method is supposed to be private
			methodGetITelephony.setAccessible(true);

			// Invoke getITelephony() to get the ITelephony interface
			Object telephonyInterface = methodGetITelephony
					.invoke(telephonyManager);

			// Get the endCall method from ITelephony
			Class telephonyInterfaceClass = Class.forName(telephonyInterface
					.getClass().getName());
			Method methodEndCall = telephonyInterfaceClass
					.getDeclaredMethod("endCall");

			// Invoke endCall()
			methodEndCall.invoke(telephonyInterface);

		} catch (Exception ex) { // Many things can go wrong with reflection
									// calls
			Log.e("PhoneStateReceiver **", ex.toString());
			return false;
		}
		return true;
	}

	public PendingIntent getPendingIntent(Context context, String... args) {

		Intent intent = new Intent(context, AddNumeroBlackListActivity.class);
		intent.putExtra("phoneNumber", args[0]);

		return PendingIntent.getActivity(context.getApplicationContext(), 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private boolean buscarDadosCalendario() {
		return CalendarUtil.readCalendar(mContext);
	}

	
	private void gravarHistorico(Context context, String phoneNumber, String tipoMotivo) {
		HistoricoDataSource historicoDS = new HistoricoDataSource(context);
		
		final String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		
		String motivo;
		if ("1".equals(tipoMotivo)) {
			motivo = "Número na blacklist";
		} else {
			motivo = "Em reunião";
		}
		
		Historico historico = new Historico();
		historico.setNumeroTelefone(phoneNumber);
		historico.setUltimaLigacao(data);
		historico.setMotivo(motivo);
		
		historicoDS.inserirHistorico(historico);
		
	}
	
	public String obterPaisSimCar(Context context) {
		
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
	}
}
