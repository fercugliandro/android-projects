package com.fercugliandro.blacklist.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactUtil {

	public static boolean verificarContatoExistente(Context context, String numeroTelefone, String country) {
		
		Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while(c.moveToNext()){
            String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));            
            String hasPhone =c.getString(
                    c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if(Integer.parseInt(hasPhone) == 1){
                Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"='"+contactID+"'",
                        null, null);
                while(phoneCursor.moveToNext()){
                	phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("(", "").replace(")", "").replace("-", "").replace(" ", "");

                    if ("br".equals(country)) {
                    
	                    String newNumero = "";
	                    String newNumeroContato = "";
	                    if (numeroTelefone.length() > 12) {
	            			//Fora do estado
	                    	 newNumero = numeroTelefone.substring(5);
	            		} else if (numeroTelefone.length() == 12) {
	            			//SP
	            			newNumero = numeroTelefone.substring(3);
	            		} else if (numeroTelefone.length() == 11) {
	            			//Demais regioes
	            			newNumero = numeroTelefone.substring(2);
	            		} else {
	            			//Sem ddd em outras regioes
	            			newNumero = numeroTelefone;
	            		}
	                    
	                    if (number.length() > 12) {
	            			//Fora do estado
	                    	 newNumeroContato = number.substring(5);
	            		} else if (number.length() == 12) {
	            			//SP
	            			newNumeroContato = number.substring(3);
	            		} else if (number.length() == 11) {
	            			//Demais regioes
	            			newNumeroContato = number.substring(2);
	            		} else {
	            			//Sem ddd em outras regioes
	            			newNumeroContato = number;
	            		}
	                    
	                    if (newNumeroContato.equalsIgnoreCase(newNumero)) {
	                    	return true;
	                    }
                    } else {
                    	if (numeroTelefone.equals(number)) {
                    		return true;
                    	}
                    }
	                   
                }
                phoneCursor.close();
            }
 
        }
		
        return false;
	}
	
}
