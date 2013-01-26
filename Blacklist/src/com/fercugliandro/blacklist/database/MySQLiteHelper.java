package com.fercugliandro.blacklist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	private static String DATABASE_NAME = "blacklist.db";
	private static Integer DATABASE_VERSION = 1;
	
	public static final String TABLE_BLACKLIST = "tbblacklist";
	public static final String TABLE_HISTORICO = "tbhistorico";
	public static final String BLACKLIST_COLUMN_ID = "_id"; 
	public static final String BLACKLIST_COLUMN_NUMERO = "numero";
	
	public static final String HISTORICO_COLUMN_ID = "_id";
	public static final String HISTORICO_COLUMN_NUM_TEL_BLACKLIST = "num_tel_blacklist";
	public static final String HISTORICO_COLUMN_DT_ULTIMA_LIGACAO_REC = "dt_ultima_ligacao_rec";
	public static final String HISTORICO_COLUMN_MOTIVO_CANCEL = "mot_lig_cancel";
	
	private static String DATABASE_CREATE_BLACKLIST = "create table "
			+ TABLE_BLACKLIST + "(" +
			BLACKLIST_COLUMN_ID + " integer primary key autoincrement, " +
			BLACKLIST_COLUMN_NUMERO + " text not null ) ";
			
	private static String DATABASE_CREATE_HISTORICO = "create table "
			+ TABLE_HISTORICO + "(" +
			HISTORICO_COLUMN_ID + " integer primary key autoincrement, " +
			HISTORICO_COLUMN_NUM_TEL_BLACKLIST + " text , " +
			HISTORICO_COLUMN_DT_ULTIMA_LIGACAO_REC + " text, " +
			HISTORICO_COLUMN_MOTIVO_CANCEL + " text) ";
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(DATABASE_CREATE_BLACKLIST);
		db.execSQL(DATABASE_CREATE_HISTORICO);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
//		if(newVersion == 2 && oldVersion == 1) {
//			
//			String[] oldColumns = {
//					MySQLiteHelper.COLUMN_NUMERO
//					};
//			
//			List<Cartao> listaCartao = new ArrayList<Cartao>();
//			Cursor cursor = db.query(MySQLiteHelper.TABLE_CARTAO, oldColumns, null, null, null, null, null);
//			cursor.moveToFirst();
//			
//			while (!cursor.isAfterLast()) {
//				Cartao cartao = new Cartao();
//				cartao.setNumero(cursor.getLong(0));
//				listaCartao.add(cartao);
//				cursor.moveToNext();
//			}
//			cursor.close();		
//			
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTAO);
//			onCreate(db);
//			
//			for(Cartao cartao : listaCartao) {
//				
//				if(cartao.getNumeroAsString().startsWith(Constantes.PREFIXO_TICKET_REFEICAO)) {
//					cartao.setNome(Constantes.EMISSOR_TICKET + " Almoço");
//				} else if (cartao.getNumeroAsString().startsWith(Constantes.PREFIXO_TICKET_ALIMENTACAO)) {
//					cartao.setNome(Constantes.EMISSOR_TICKET + " Mercado");
//				} else {
//					cartao.setNome(Constantes.EMISSOR_TICKET);
//				}
//				
//				ContentValues values = new ContentValues();
//				values.put(MySQLiteHelper.COLUMN_NUMERO, cartao.getNumero().toString());
//				values.put(MySQLiteHelper.COLUMN_NOME, cartao.getNome());
//				values.put(MySQLiteHelper.COLUMN_CPF, cartao.getCpf());
//				values.put(MySQLiteHelper.COLUMN_EMISSOR, Constantes.EMISSOR_TICKET);	
//				values.put(MySQLiteHelper.COLUMN_DATA_ATUALIZACAO, 1L);
//				values.put(MySQLiteHelper.COLUMN_SALDO, 0F);
//				values.put(MySQLiteHelper.COLUMN_LANCAMENTOS, "");
//				values.put(MySQLiteHelper.COLUMN_SALDO_DETALHADO, "");
//				
//				db.insert(MySQLiteHelper.TABLE_CARTAO, null, values);				
//				
//			}
//
//		} else {
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTAO);
//			onCreate(db);			
//		}

	}

}
