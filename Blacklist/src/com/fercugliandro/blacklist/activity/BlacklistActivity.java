package com.fercugliandro.blacklist.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.activity.adapter.BlacklistAdapter;
import com.fercugliandro.blacklist.activity.adapter.HistoricoAdapter;
import com.fercugliandro.blacklist.database.BlacklistDataSource;
import com.fercugliandro.blacklist.database.HistoricoDataSource;
import com.fercugliandro.blacklist.datatype.Blacklist;
import com.fercugliandro.blacklist.datatype.Historico;
import com.fercugliandro.blacklist.util.CSVUtil;

@SuppressLint({ "ValidFragment", "NewApi"})
public class BlacklistActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    BlacklistAdapter blacklistAdapter;
    
    HistoricoAdapter historicoAdapter;
    NumerosBloqueadosFragment numerosBloqueadosFragment = new NumerosBloqueadosFragment();
    HistoricoNumerosBloqueadosFragment historicoNumerosBloqueadosFragment = new HistoricoNumerosBloqueadosFragment();
    ProgressDialog progressDialog;
    private List<Blacklist> listaNumerosSelecionados = new ArrayList<Blacklist>();
    
    private Menu mnuTopMenuActionBar; 
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped);
//        bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//        actionBar.setBackgroundDrawable(bg);
//
//        BitmapDrawable bgSplit = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped_split_img);
//        bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//        actionBar.setSplitBackgroundDrawable(bgSplit);
        
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(new ActionBar.TabListener() {

								public void onTabReselected(Tab tab,
										FragmentTransaction arg1) {
									
									if (tab.getPosition() == 0) {
							        	corrigirMenu(false);
							        }
									
								}

								public void onTabSelected(Tab tab,
										FragmentTransaction fragmentTransaction) {
									mViewPager.setCurrentItem(tab.getPosition());
								}

								public void onTabUnselected(Tab arg0,
										FragmentTransaction arg1) {
								}
                            	
                            })
            		);
        }
        
        blacklistAdapter = popularListaBlackList(this);
        historicoAdapter = popularListaHistorico(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mnuTopMenuActionBar = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
    	
    		case R.id.menu_action_new:
    			Intent intent = new Intent(getApplicationContext(), AddNumeroBlackListActivity.class);
    	    	startActivityForResult(intent, 0);
    			break;
    		case R.id.menu_actionbar_remove:
    			removeNumerosDaBlackList();
    			break;
    	
	    	case R.id.menu_action_share:    
	        	Intent intentCompartilhar = new Intent(android.content.Intent.ACTION_SEND);
	
	        	intentCompartilhar.setType("text/plain");
	        	intentCompartilhar.putExtra(Intent.EXTRA_SUBJECT, getApplicationContext().getString(R.string.compartilharSubject));
	        	intentCompartilhar.putExtra(Intent.EXTRA_TEXT, getApplicationContext().getString(R.string.compartilharTexto));
	
	        	startActivity(Intent.createChooser(intentCompartilhar, getApplicationContext().getString(R.string.compartilharTitulo)));
	        	break;
	    	case R.id.menu_settings:
	    		Intent intentSettings = new Intent(BlacklistActivity.this, PrefsActivity.class);
	    		startActivityForResult(intentSettings, 1);
		        break;
		        
	    	case R.id.menu_historico:
	    		
	    		//Toast.makeText(getActivity(), blacklist.getNumero(), Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getApplicationContext().getText(R.string.dialogHistoryTitle))
					   .setMessage(getApplicationContext().getText(R.string.dialogHistoryMessage))
				       .setCancelable(false)
				       .setPositiveButton(getApplicationContext().getText(R.string.dialogHistoryButtonYes), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   		
				        	   limparHistorico(getApplicationContext());
				        	   historicoAdapter = popularListaHistorico(getApplicationContext());
				        	   historicoNumerosBloqueadosFragment.setListAdapter(historicoAdapter);
				        	   
				               dialog.dismiss();
				           }
				       })
				       .setNegativeButton(getApplicationContext().getText(R.string.dialogHistoryButtonNo), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();	
				alert.show();

		        break;
		        
	    	case R.id.menu_action_backup:
	    		
	    		backupBlacklist();
	    		break;
	    		
	    	case R.id.menu_action_restore:
	    		restoreBlacklist();
	    		break;
	        default:
	            return super.onOptionsItemSelected(item);
    	
    	}
    	
    	return super.onOptionsItemSelected(item);
    	
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	
        	if (i == 0) {
        		return historicoNumerosBloqueadosFragment;
        	} else {
        		return numerosBloqueadosFragment;	
        	}
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();                
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class HistoricoNumerosBloqueadosFragment extends ListFragment {
        
    	public HistoricoNumerosBloqueadosFragment() { }

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

    		super.onActivityCreated(savedInstanceState);
    		
    		getListView().setVerticalScrollBarEnabled(true);
    		setListAdapter(historicoAdapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			Historico historico = (Historico) l.getItemAtPosition(position);
			Intent intent = new Intent(getActivity(), DetalheHistoricoActivity.class);
			intent.putExtra("phoneNumber", historico.getNumeroTelefone());
			
			startActivity(intent);
		}
		
    }
    
    
    public class NumerosBloqueadosFragment extends ListFragment {
    	
    	public NumerosBloqueadosFragment() { }
    	
    	@Override
		public void onActivityCreated(Bundle savedInstanceState) {

    		super.onActivityCreated(savedInstanceState);
    		
    		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    			
			setListAdapter(blacklistAdapter);
		}

    	
    	
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			if (l.isItemChecked(position)) {
				v.setBackgroundColor(Color.rgb(190, 222, 252));
				l.setItemChecked(position, true);
			
				listaNumerosSelecionados.add((Blacklist)l.getItemAtPosition(position));
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
				l.setItemChecked(position, false);
				
				if (listaNumerosSelecionados.size() == 1 && position != 0) {
					listaNumerosSelecionados.remove(0);
				} else {
					listaNumerosSelecionados.remove(position);
				}	
				
			}			

			
			if (l.getCheckedItemCount() == 0 ) {
				corrigirMenu(false);
			} else {
				corrigirMenu(true);
			}
		}
		
		
    }
    
    public void removeNumerosDaBlackList() {
    	
    	BlacklistDataSource blackListDS = new BlacklistDataSource(getApplicationContext());
    	
    	for (Blacklist blacklist : listaNumerosSelecionados) {
    		
    		blackListDS.deleteNumeroBloqueado(blacklist.getId());
    	}

    	blacklistAdapter = popularListaBlackList(this);
    	
    	if (mViewPager.getCurrentItem() == 1) {
    		numerosBloqueadosFragment.setListAdapter(blacklistAdapter);
    		
    		//Corrigindo o menu
    		corrigirMenu(false);
    		
    	}
    	
    }
    
    private BlacklistAdapter popularListaBlackList(Context context) {
    	
    	// Inicializa DB e busca cartoes
    	BlacklistDataSource blackListDS = new BlacklistDataSource(context);
    	List<Blacklist> lista = new ArrayList<Blacklist>();
    	
		lista.addAll(blackListDS.getListaBlacklist());
      
        BlacklistAdapter adapter = new BlacklistAdapter(context, R.layout.list_number_blacklist, lista); 
		
    	return adapter ;
    }
    
    private HistoricoAdapter popularListaHistorico(Context context) {
    	
    	// Inicializa DB e busca cartoes
    	HistoricoDataSource historicoDS = new HistoricoDataSource(context);
    	List<Historico> lista = new ArrayList<Historico>();
    	
		lista.addAll(historicoDS.getListHistoricoGroup());
      
        HistoricoAdapter adapter = new HistoricoAdapter(context, R.layout.list_history_blacklist, lista); 
		
    	return adapter ;
    }
    
    private void corrigirMenu(boolean hasItemSelected) {
    	
    	mnuTopMenuActionBar.clear();
    	
    	if (hasItemSelected) 
    		getMenuInflater().inflate(R.menu.menu_remove_numbers_blacklist, mnuTopMenuActionBar);
    	else	
    		getMenuInflater().inflate(R.menu.main, mnuTopMenuActionBar);
    }
    

    private static void limparHistorico(Context context) {
    	
    	HistoricoDataSource historicotDS = new HistoricoDataSource(context);
    	
   		historicotDS.deleteAllHistory();

    }
    
    @Override
    public void onBackPressed() {
    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_HOME);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        
        blacklistAdapter = popularListaBlackList(getApplicationContext());
        numerosBloqueadosFragment.setListAdapter(blacklistAdapter);
        
        historicoAdapter = popularListaHistorico(getApplicationContext());
        historicoNumerosBloqueadosFragment.setListAdapter(historicoAdapter);
    }

    
    private void backupBlacklist() {
    	//start the progress dialog
    	progressDialog = ProgressDialog.show(BlacklistActivity.this, getApplicationContext().getText(R.string.dialogBackupMessage1), getApplicationContext().getText(R.string.dialogBackupMessage2));

    	new Thread() {

    		public void run() {

    			try{

    				BlacklistDataSource blackListDS = new BlacklistDataSource(getApplicationContext());
    		    	List<Blacklist> lista = new ArrayList<Blacklist>();
    		    	
    				lista.addAll(blackListDS.getListaBlacklist());
    				
    				CSVUtil.exportBlacklistToCSV(getApplicationContext(), lista);
    				
    				sleep(5000);    			
    				
    			} catch (Exception e) {
    				e.printStackTrace();
    			}	

    			progressDialog.dismiss();

    		}

    	}.start();	
    			
    }
    
    private void restoreBlacklist () {
    	
    	//start the progress dialog
    	progressDialog = ProgressDialog.show(BlacklistActivity.this, getApplicationContext().getText(R.string.dialogRestoreMessage1), getApplicationContext().getText(R.string.dialogRestoreMessage2));    	

    	new Thread() {
    		
    		public void run() {
    			
    			try {
    				
    				BlacklistDataSource blackListDS = new BlacklistDataSource(getApplicationContext());
    				List<Blacklist> lista = CSVUtil.CSVToListBlacklist(getApplicationContext());
    				
    				if (!lista.isEmpty()) {    					
    					blackListDS.restoreBlacklist(lista);    					
    				}
    				
    				
    			} catch (Exception e) {
    				e.printStackTrace();
    			}

    			progressDialog.dismiss();
        
    		}

    	}.start();

    	Intent main = getIntent();
    	this.finish();
    	startActivity(main);
    	
    }    
}
