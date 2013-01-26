package com.fercugliandro.blacklist.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.fercugliandro.blacklist.R;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class NotificationUtil {

	public static Notification sendBigPictureStyleNotification(Context context, Intent intent, PendingIntent pendingIntent, String...args) {
		
		Builder builder = new Notification.Builder(context);
		builder.setContentTitle("Telefone desconhecido")
				.setSmallIcon(R.drawable.ic_access_secure)
				.setContentIntent(pendingIntent)
				.addAction(R.drawable.ic_actionbar_accept, "Bloquear ?", pendingIntent);
				

		Notification notification = new Notification.BigTextStyle(builder).bigText(args[0]).build();
		notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;;
		
		return notification;
		
	}

	/**
	 * Notificacoes para Android < 4.1
	 * @param context
	 * @param intent
	 * @param pendingIntent
	 * @param args
	 * @return
	 */
	public static Notification normalStyleNotification(Context context, Intent intent, PendingIntent pendingIntent, String...args) {
		
		    
		Notification notification = new Notification(R.drawable.ic_access_secure, "Telefone desconhecido. Bloquear? ", System.currentTimeMillis());
	    // Hide the notification after its selected
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(context, "Telefone desconhecido. Bloquear?", args[0], pendingIntent);
		    
		return notification;
	}
	
}
