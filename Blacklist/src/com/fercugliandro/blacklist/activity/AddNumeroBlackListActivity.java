package com.fercugliandro.blacklist.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.database.BlacklistDataSource;
import com.fercugliandro.blacklist.datatype.Blacklist;

@SuppressLint("NewApi")
public class AddNumeroBlackListActivity extends Activity {

	private BlacklistDataSource blacklistDataSource;
	private boolean isOldVersion = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_numberblacklist_activity);

		//Inicializando o DataSource
		blacklistDataSource = new BlacklistDataSource(getApplicationContext());
		
		//Modificando a ActionBar
		if (Build.VERSION.SDK_INT > 10 ) {
			modificarActionBar(getActionBar());
		} else {
			isOldVersion = true;
		}
		
		//Caso venha da Notificacao
		if (getIntent().getExtras() != null) {
		
			String numeroTelefone = getIntent().getExtras().getString("phoneNumber");
			
			EditText txtNumeroTelefone = (EditText) findViewById(R.id.txtNumeroTelefone);
			txtNumeroTelefone.setText(numeroTelefone);
			txtNumeroTelefone.setEnabled(false);
			clearNotification();
		}
		
		Button btnLerHistorico = (Button) findViewById(R.id.btnReadCallLog);
		btnLerHistorico.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), CallLogAndroidActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
	}
	
	
	
	private void clearNotification() {
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        getMenuInflater().inflate(R.menu.menu_add_number_blacklist, menu);
        return true;
    }
    
    private void modificarActionBar(ActionBar actionBar) {
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
	    	case android.R.id.home:
	    		NavUtils.navigateUpFromSameTask(this);
				break;
			case R.id.menu_actionbar_accept:
				saveBlacklist();
				break;
			default:
				return super.onOptionsItemSelected(item);
				
    	
    	}
        return super.onOptionsItemSelected(item);
    }
    
    private void saveBlacklist() {
    	
    	//boolean retorno = CalendarUtil.readCalendar(this);
    	
    	EditText numero = (EditText) findViewById(R.id.txtNumeroTelefone);
    	Blacklist blacklist = new Blacklist();
    	blacklist.setNumero(numero.getText().toString());
    	
   		long retorno = blacklistDataSource.insereBlacklist(blacklist);
   		if (retorno < 0) {
   			Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.numberExistsOnBlacklist), Toast.LENGTH_SHORT).show();
   		}
    	
   		Intent intent = null;
   		if (isOldVersion) {
   			intent = new Intent(getApplicationContext(), BlacklistOldActivity.class);
   		} else {
   			intent = new Intent(getApplicationContext(), BlacklistActivity.class);
   		}
   		
    	startActivity(intent);
    }
}