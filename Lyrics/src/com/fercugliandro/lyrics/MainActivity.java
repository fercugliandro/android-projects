package com.fercugliandro.lyrics;

import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fercugliandro.lyrics.datatype.Artista;
import com.fercugliandro.lyrics.search.BuscarDados;

public class MainActivity extends Activity {

	private BuscarDados buscarDados = new BuscarDados("C");
	private List<Artista> artistas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.enableDefaults();
		
		final EditText txtArtistName = (EditText) findViewById(R.id.nameArtist);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = txtArtistName.getText().toString(); 
				try {
					artistas = buscarDados.buscarDados(name);
					
					System.out.println(artistas.size());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
