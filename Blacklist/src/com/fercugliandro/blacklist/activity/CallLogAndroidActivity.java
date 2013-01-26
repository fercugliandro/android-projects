package com.fercugliandro.blacklist.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.activity.adapter.CallLogAdapter;
import com.fercugliandro.blacklist.datatype.CallLog;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class CallLogAndroidActivity extends Activity {

	CallLogAdapter adapter;
	ListView listaCallLog;

	String[] projection = new String[] { android.provider.CallLog.Calls.NUMBER,	android.provider.CallLog.Calls.CACHED_NAME, android.provider.CallLog.Calls.TYPE };
	Uri contacts = android.provider.CallLog.Calls.CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_call_log);

		listaCallLog = (ListView) findViewById(R.id.listViewCallLog);
		adapter = popularAdapterCallLog(this);
		listaCallLog.setAdapter(adapter);
		
		listaCallLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				final CallLog callLog = (CallLog) parent.getItemAtPosition(position);
								
				Intent intent = new Intent(getApplicationContext(), AddNumeroBlackListActivity.class);
				intent.putExtra("phoneNumber", callLog.getNumeroTelefone());
				
				startActivity(intent);
			}
		});
		
		if (Build.VERSION.SDK_INT > 10 ) {
			modificarActionBar(getActionBar());
		}
		
		
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
	    	case android.R.id.home:
	    		NavUtils.navigateUpFromSameTask(this);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
        return super.onOptionsItemSelected(item);
    }

	private void modificarActionBar(ActionBar actionBar) {
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	
	private CallLogAdapter popularAdapterCallLog(Context context) {

		List<CallLog> lista = new ArrayList<CallLog>();

		Cursor managedCursor = managedQuery(contacts, projection, null, null, null);
		lista = getColumnData(managedCursor);

		lista = corrigirListaCallLog(lista);
		
		CallLogAdapter adapter = new CallLogAdapter(context,
				R.layout.list_call_log, lista);

		return adapter;

	}

	private List<CallLog> getColumnData(Cursor cur) {

		List<CallLog> list = new ArrayList<CallLog>();

		try {
			if (cur.moveToFirst()) {
				String name;
				String number;
				
				int nameColumn = cur.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
				int numberColumn = cur.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
				int typeColumn = cur.getColumnIndex(android.provider.CallLog.Calls.TYPE);

				do {
					name = cur.getString(nameColumn);
					number = cur.getString(numberColumn);
					int type = cur.getInt(typeColumn);
					
					if (type == 1 || type == 3) {
						CallLog callLog = new CallLog();
						callLog.setNome(name);
						callLog.setNumeroTelefone(number);
	
						list.add(callLog);
					}	
				} while (cur.moveToNext());
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error :( ", Toast.LENGTH_SHORT).show();
		}

		return list;

	}

	private List<CallLog> corrigirListaCallLog(List<CallLog> listaAdapter) {
		
		List<CallLog> listaCorrigida = new ArrayList<CallLog>();
		HashMap<String, CallLog> list = new HashMap<String, CallLog>();
		
		for (CallLog callLog : listaAdapter) {
			
			if (callLog.getNumeroTelefone() != null && !"".equals(callLog.getNumeroTelefone()) && !"-2".equals(callLog.getNumeroTelefone()))
				list.put(callLog.getNumeroTelefone(), callLog);
		}
		
		listaCorrigida = new ArrayList<CallLog>(list.values());
		
		return listaCorrigida;
		
	}
	
}
