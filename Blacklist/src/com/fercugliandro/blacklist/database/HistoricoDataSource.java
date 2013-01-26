package com.fercugliandro.blacklist.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fercugliandro.blacklist.datatype.Historico;

public class HistoricoDataSource {

	private static final String TABLE_HISTORICO = "tbhistorico";
	private static final String HISTORICO_COLUMN_ID = "_id";
	private static final String HISTORICO_COLUMN_NUM_TEL_BLACKLIST = "num_tel_blacklist";
	private static final String HISTORICO_COLUMN_DT_ULTIMA_LIGACAO_REC = "dt_ultima_ligacao_rec";
	private static final String HISTORICO_COLUMN_QTDE_LIG_BLOCKED = "count(*) as QTDE_LIG_BLOQ";
	public static final String HISTORICO_COLUMN_MOTIVO_CANCEL = "mot_lig_cancel";
	
	private SQLiteDatabase db;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { 	
			HISTORICO_COLUMN_ID,
			HISTORICO_COLUMN_NUM_TEL_BLACKLIST,
			HISTORICO_COLUMN_DT_ULTIMA_LIGACAO_REC,
			HISTORICO_COLUMN_MOTIVO_CANCEL
			};
	
	private String[] allColumnsGroup = { 	
			HISTORICO_COLUMN_ID,
			HISTORICO_COLUMN_NUM_TEL_BLACKLIST,
			HISTORICO_COLUMN_DT_ULTIMA_LIGACAO_REC,
			HISTORICO_COLUMN_QTDE_LIG_BLOCKED
			};
	
	public HistoricoDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	private void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	private void close() {
		db.close();
	}
	
	public synchronized Boolean deleteAllHistory() {

		this.open();
		int result = db.delete(TABLE_HISTORICO, null, null);
		this.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public synchronized Long inserirHistorico(Historico historico) {
		this.open();
		
		ContentValues values = new ContentValues();		
		values.put(HISTORICO_COLUMN_NUM_TEL_BLACKLIST, historico.getNumeroTelefone());		
		values.put(HISTORICO_COLUMN_DT_ULTIMA_LIGACAO_REC, historico.getUltimaLigacao());
		values.put(HISTORICO_COLUMN_MOTIVO_CANCEL, historico.getMotivo());
		
		long insertId = db.insert(TABLE_HISTORICO, null, values);
		this.close();
		return insertId;
	}
	
	public synchronized List<Historico> obterHistoricoByPhoneNumber(String phoneNumber) {
		
		this.open();
		List<Historico> listaHistorico = new ArrayList<Historico>();
		//Cursor cursor = db.rawQuery("SELECT _id, num_tel_blacklist, dt_ultima_ligacao_rec, mot_lig_cancel FROM tbhistorico WHERE num_tel_blacklist like ? ", new String[] {phoneNumber});//;; query(TABLE_HISTORICO, allColumns, HISTORICO_COLUMN_NUM_TEL_BLACKLIST + " = '" + phoneNumber + "'" , null, null, null, null);
		Cursor cursor = db.query(TABLE_HISTORICO, allColumns, HISTORICO_COLUMN_NUM_TEL_BLACKLIST + " like ? "  , new String[] {phoneNumber}, null, null, null);
		cursor.moveToFirst();
		
		do {
			Historico historico = cursorToObject(cursor, true);
			listaHistorico.add(historico);
		} while (cursor.moveToNext());
		
		cursor.close();
		this.close();
		
		return listaHistorico;
	}
	
	public synchronized List<Historico> getListHistoricoGroup() {
		this.open();
		List<Historico> listHistorico = new ArrayList<Historico>();
		Cursor cursor = db.query(TABLE_HISTORICO, allColumnsGroup, null, null, HISTORICO_COLUMN_NUM_TEL_BLACKLIST, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Historico historico = cursorToObject(cursor, false);
			listHistorico.add(historico);
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		
		return listHistorico;
	}
	
	private Historico cursorToObject(Cursor cursor, boolean isDetalhe) {
	
		Historico historico = new Historico();
		historico.setId(cursor.getInt(0));
		historico.setNumeroTelefone(cursor.getString(1));
		historico.setUltimaLigacao(cursor.getString(2));
		if(isDetalhe)
			historico.setMotivo(cursor.getString(3));
		else
			historico.setQtdeLigacao(cursor.getInt(3));
		return historico;
	}
	
}
