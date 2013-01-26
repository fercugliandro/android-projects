package com.fercugliandro.blacklist.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;

@SuppressLint("NewApi")
public class CalendarUtil {

	public static boolean readCalendar(Context context) {

		ContentResolver contentResolver = context.getContentResolver();

		// Fetch a list of all calendars synced with the device, their display
		// names and whether the
		// user has them selected for display.

		Uri uriCalendar = Calendars.CONTENT_URI;
		
		final Cursor cursor = contentResolver.query(uriCalendar, (new String[] {
						Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME, Calendars.OWNER_ACCOUNT }), null, null, null);
		// For a full list of available columns see http://tinyurl.com/yfbg76w

		List<String> calendarIds = new ArrayList<String>();

		while (cursor.moveToNext()) {

			final String _id = cursor.getString(0);

			calendarIds.add(_id);
		}

		// For each calendar, display all the events from the previous week to
		// the end of next week.
		boolean hasEvent = false;
		for (String id : calendarIds) {
			
			Uri.Builder builder = Uri
					.parse("content://com.android.calendar/instances/when").buildUpon();
			long now = new Date().getTime();
			ContentUris.appendId(builder, now);
			ContentUris.appendId(builder, now + 60*60*1000);

			Cursor eventCursor = contentResolver.query(builder.build(),
					new String[] { "title", "begin", "end", "allDay" },
					" calendar_id=" + id, null,
					"startDay ASC, startMinute ASC");
		
			while (eventCursor.moveToNext()) {
		
				hasEvent = true;
				break;
			}
			
			if (hasEvent) 
				break;
		}
		
		return hasEvent;
	}

}
