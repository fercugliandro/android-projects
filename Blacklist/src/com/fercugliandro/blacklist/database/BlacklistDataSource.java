package com.fercugliandro.blacklist.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fercugliandro.blacklist.datatype.Blacklist;

public class BlacklistDataSource {

	private SQLiteDatabase db;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { 	
			MySQLiteHelper.BLACKLIST_COLUMN_ID,
			MySQLiteHelper.BLACKLIST_COLUMN_NUMERO,
			};
	
	public BlacklistDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	private void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	private void close() {
		db.close();
	}
	
	public synchronized Boolean deleteNumeroBloqueado(Integer id) {

		this.open();
		int result = db.delete(MySQLiteHelper.TABLE_BLACKLIST, MySQLiteHelper.BLACKLIST_COLUMN_ID + " = " + id, null);
		this.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public synchronized Long insereBlacklist(Blacklist blacklist) {
		this.open();
		Cursor cursor = db.query(MySQLiteHelper.TABLE_BLACKLIST,
				allColumns, MySQLiteHelper.BLACKLIST_COLUMN_NUMERO + " like '%" + blacklist.getNumero() + "'", null,
				null, null, null);
		
		if (cursor.getCount() > 0) {
			cursor.close();
			this.close();
			return -2L;
		}	
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.BLACKLIST_COLUMN_NUMERO, blacklist.getNumero());
		
		long insertId = db.insert(MySQLiteHelper.TABLE_BLACKLIST, null, values);
		this.close();
		return insertId;
	}
	
	public synchronized void restoreBlacklist(List<Blacklist> lista) {
		
		this.open();
		
		for (Blacklist blacklist : lista) {
			Cursor cursor = db.query(MySQLiteHelper.TABLE_BLACKLIST,
					allColumns, MySQLiteHelper.BLACKLIST_COLUMN_NUMERO + " like '%" + blacklist.getNumero() + "'", null,
					null, null, null);
			
			if (cursor.getCount() > 0) {
				cursor.close();
				
				break;
			}	
			
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.BLACKLIST_COLUMN_NUMERO, blacklist.getNumero());
			
			db.insert(MySQLiteHelper.TABLE_BLACKLIST, null, values);
		}
		
		this.close();
	}
	
	public synchronized Blacklist getItemBlacklist(String phoneNumber) {
		
		this.open();
		Blacklist blacklist = null;
		Cursor cursor = db.query(MySQLiteHelper.TABLE_BLACKLIST, allColumns, MySQLiteHelper.BLACKLIST_COLUMN_NUMERO + " like '%" + phoneNumber + "'", null, null, null, null); 
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			blacklist = cursorToObject(cursor);
			break;
		}
		cursor.close();
		this.close();
		
		return blacklist;
	}
	
	public synchronized List<Blacklist> getListaBlacklist() {
		
		this.open();
		List<Blacklist> listBlackList = new ArrayList<Blacklist>();
		Cursor cursor = db.query(MySQLiteHelper.TABLE_BLACKLIST, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Blacklist blacklist = cursorToObject(cursor);
			listBlackList.add(blacklist);
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		
		return listBlackList;
	}
	
	private Blacklist cursorToObject(Cursor cursor) {
	
		Blacklist blacklist = new Blacklist();
		blacklist.setId(cursor.getInt(0));
		blacklist.setNumero(cursor.getString(1));

		return blacklist;
	}
	
}
