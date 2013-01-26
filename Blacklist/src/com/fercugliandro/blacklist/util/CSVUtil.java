package com.fercugliandro.blacklist.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;

import com.fercugliandro.blacklist.datatype.Blacklist;

public class CSVUtil {

	public static boolean exportBlacklistToCSV(Context context, List<Blacklist> lista) throws IOException {
		
		File file;
		File dir;
		
		dir = new File(context.getExternalFilesDir(null) + "/");
		if (!dir.exists())
			dir.mkdir();
		
		file = new File(dir, "blacklist_bkp.csv");
		//file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		osw.append("ID;PHONE_NUMBER");
		osw.append("\n");
		
		for (Blacklist blacklist : lista) {
			osw.append(blacklist.getId() + ";" + blacklist.getNumero());
			osw.append("\n");
		}
		
		osw.flush();
		osw.close();
		fos.close();
		
		return true;
	}
	
	public static List<Blacklist> CSVToListBlacklist(Context context) throws IOException {
		
		List<Blacklist> lista = new ArrayList<Blacklist>();
		
		File dir = new File(context.getExternalFilesDir(null) + "/");
		File file = new File(dir, "blacklist_bkp.csv");
		
		if (file.exists()) {
		
			BufferedReader bfr = new BufferedReader(new FileReader(file));
			String line = null;
			int row = 1;
			int rowLine1 = 0;
			while ((line = bfr.readLine()) != null) {
				StringTokenizer stk = new StringTokenizer(line, ";");
				
				Blacklist blacklist = new Blacklist();
				
				while (stk.hasMoreTokens()) {
					
					if (rowLine1 == 2 ) {
						if (blacklist.getId() == 0)
							blacklist.setId(Integer.parseInt(stk.nextToken()));
						else 
							blacklist.setNumero(stk.nextToken());
					} else {
						rowLine1 += 1;
						stk.nextToken();
					}
					
				}
	
				if (row > 1)
					lista.add(blacklist);	
				
				row += 1;
				
			}
			
			
			bfr.close();
			return lista;
		} else {
			return null;
		}		
	}
	
}
