package com.fercugliandro.blacklist.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.activity.adapter.DetalheHistoricoAdapter;
import com.fercugliandro.blacklist.database.HistoricoDataSource;
import com.fercugliandro.blacklist.datatype.Historico;

@SuppressLint("NewApi")
public class DetalheHistoricoActivity extends Activity {

	DetalheHistoricoAdapter adapter;
	ListView listaDetailHistorico;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.detail_historico);
		
		String phoneNumber = getIntent().getExtras().getString("phoneNumber");
		
		listaDetailHistorico = (ListView) findViewById(R.id.listViewDetail);
		adapter = popularAdapterDetalheHistorico(this, phoneNumber);
		listaDetailHistorico.setAdapter(adapter);
		
		//Modificando a ActionBar
		if (Build.VERSION.SDK_INT > 10 ) {
			modificarActionBar(getActionBar(), phoneNumber);
		}
	}


	private void modificarActionBar(ActionBar actionBar, String phoneNumber) {
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getApplicationContext().getText(R.string.actionBarHistory) + phoneNumber);
		
	}

	private DetalheHistoricoAdapter popularAdapterDetalheHistorico(Context context, String phoneNumber) {

    	// Inicializa DB e busca cartoes
    	HistoricoDataSource historicoDS = new HistoricoDataSource(context);
    	List<Historico> lista = new ArrayList<Historico>();
    	
		lista.addAll(historicoDS.obterHistoricoByPhoneNumber(phoneNumber));
      
        DetalheHistoricoAdapter adapter = new DetalheHistoricoAdapter(context, R.layout.list_detail_history_blacklist, lista); 
		
    	return adapter ;

		
	}
}
