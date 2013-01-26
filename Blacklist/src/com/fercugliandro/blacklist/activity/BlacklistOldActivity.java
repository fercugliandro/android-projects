package com.fercugliandro.blacklist.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TabHost;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.activity.adapter.BlacklistAdapter;
import com.fercugliandro.blacklist.activity.adapter.HistoricoAdapter;
import com.fercugliandro.blacklist.database.BlacklistDataSource;
import com.fercugliandro.blacklist.database.HistoricoDataSource;
import com.fercugliandro.blacklist.datatype.Blacklist;
import com.fercugliandro.blacklist.datatype.Historico;
import com.fercugliandro.blacklist.util.CSVUtil;

@SuppressLint({ "ValidFragment", "NewApi" })
public class BlacklistOldActivity extends FragmentActivity {

	TabHost mTabHost;
    TabManager mTabManager;

    static BlacklistAdapter blackListAdapter;
    static HistoricoAdapter historicoAdapter;
    static NumerosBloqueadosOldFragment numerosBloqueadosFragment = new NumerosBloqueadosOldFragment();
    HistoricoNumerosBloqueadosFragment historicoNumerosBloqueadosFragment = new HistoricoNumerosBloqueadosFragment();
    
    ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_old);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

        mTabManager.addTab(mTabHost.newTabSpec("historico").setIndicator(getApplicationContext().getText(R.string.title_section1)), historicoNumerosBloqueadosFragment.getClass(), null);
        mTabManager.addTab(mTabHost.newTabSpec("bloqueados").setIndicator(getApplicationContext().getText(R.string.title_section2)), NumerosBloqueadosOldFragment.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        
        blackListAdapter = popularListaBlackList(getApplicationContext());
        historicoAdapter = popularListaHistorico(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    			new BlacklistActivity().removeNumerosDaBlackList();
    			
    			break;
    	
	    	case R.id.menu_action_share:    
	        	Intent intentCompartilhar = new Intent(android.content.Intent.ACTION_SEND);
	
	        	intentCompartilhar.setType("text/plain");
	        	intentCompartilhar.putExtra(Intent.EXTRA_SUBJECT, getApplicationContext().getString(R.string.compartilharSubject));
	        	intentCompartilhar.putExtra(Intent.EXTRA_TEXT, getApplicationContext().getString(R.string.compartilharTexto));
	
	        	startActivity(Intent.createChooser(intentCompartilhar, getApplicationContext().getString(R.string.compartilharTitulo)));
	        	break;
	    	case R.id.menu_settings:
	    		Intent intentSettings = new Intent(BlacklistOldActivity.this, PrefsActivity.class);
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
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        public void onTabChanged(String tabId) {
        	
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = ListFragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }

    
	public static class HistoricoNumerosBloqueadosFragment extends ListFragment {
        
    	public HistoricoNumerosBloqueadosFragment() { }

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

    		super.onActivityCreated(savedInstanceState);
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
    
    
    public static class NumerosBloqueadosOldFragment extends ListFragment {
    	
    	public NumerosBloqueadosOldFragment() { }
    	
    	@Override
		public void onActivityCreated(Bundle savedInstanceState) {

    		super.onActivityCreated(savedInstanceState);
    		
    		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					final Blacklist blacklist = (Blacklist) parent.getItemAtPosition(position);

					//Toast.makeText(getActivity(), blacklist.getNumero(), Toast.LENGTH_SHORT).show();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle(getActivity().getText(R.string.dialogNumberBlacklistTitle) + "Exclução")
						   .setMessage(getActivity().getText(R.string.dialogNumberBlacklistMessage1) + blacklist.getNumero() + getActivity().getText(R.string.dialogNumberBlacklistMessage2))
					       .setCancelable(false)
					       .setPositiveButton(getActivity().getText(R.string.dialogNumberBlacklistButtonYes), new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   		
					        	   removeNumeroBlacklist(getActivity(), blacklist.getId());
					        	   blackListAdapter = popularListaBlackList(getActivity());
					        	   setListAdapter(blackListAdapter);
					        	   
					               dialog.dismiss();
					           }
					       })
					       .setNegativeButton(getActivity().getText(R.string.dialogNumberBlacklistButtonNo), new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();	
					alert.show();
					
					return true;
				}
				
			});
    		
    		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    			
			setListAdapter(blackListAdapter);
		}
    }

    
    public static BlacklistAdapter popularListaBlackList(Context context) {
    	
    	// Inicializa DB e busca cartoes
    	BlacklistDataSource blackListDS = new BlacklistDataSource(context);
    	List<Blacklist> lista = new ArrayList<Blacklist>();
    	
		lista.addAll(blackListDS.getListaBlacklist());
      
        BlacklistAdapter adapter = new BlacklistAdapter(context, R.layout.list_number_blacklist, lista); 
		
    	return adapter ;
    }
    
    public static HistoricoAdapter popularListaHistorico(Context context) {
    	
    	// Inicializa DB e busca cartoes
    	HistoricoDataSource historicoDS = new HistoricoDataSource(context);
    	List<Historico> lista = new ArrayList<Historico>();
    	
		lista.addAll(historicoDS.getListHistoricoGroup());
      
        HistoricoAdapter adapter = new HistoricoAdapter(context, R.layout.list_history_blacklist, lista); 
		
    	return adapter ;
    }
    
    private static void removeNumeroBlacklist(Context context, int id) {
    	
    	BlacklistDataSource blackListDS = new BlacklistDataSource(context);
    	
   		blackListDS.deleteNumeroBloqueado(id);

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
	protected void onResumeFragments() {
    	blackListAdapter = popularListaBlackList(getApplicationContext());
    	historicoAdapter = popularListaHistorico(getApplicationContext());
    	historicoNumerosBloqueadosFragment.setListAdapter(null);
    	historicoNumerosBloqueadosFragment.setListAdapter(historicoAdapter);
    	
		super.onResumeFragments();
	}

	@Override
	protected void onStart() {
		
		blackListAdapter = popularListaBlackList(getApplicationContext());
    	historicoAdapter = popularListaHistorico(getApplicationContext());
    	historicoNumerosBloqueadosFragment.setListAdapter(null);
    	historicoNumerosBloqueadosFragment.setListAdapter(historicoAdapter);
    	
		super.onStart();
	}

	@Override
	protected void onRestart() {
		blackListAdapter = popularListaBlackList(getApplicationContext());
    	historicoAdapter = popularListaHistorico(getApplicationContext());
    	historicoNumerosBloqueadosFragment.setListAdapter(null);
    	historicoNumerosBloqueadosFragment.setListAdapter(historicoAdapter);
    	
		super.onRestart();
	}

	@Override
	protected void onResume() {

    	blackListAdapter = popularListaBlackList(getApplicationContext());
    	historicoAdapter = popularListaHistorico(getApplicationContext());
    	historicoNumerosBloqueadosFragment.setListAdapter(null);
    	historicoNumerosBloqueadosFragment.setListAdapter(historicoAdapter);
    	
		super.onResume();
	}
	
	private void backupBlacklist() {
    	//start the progress dialog
    	progressDialog = ProgressDialog.show(BlacklistOldActivity.this, getApplicationContext().getText(R.string.dialogBackupMessage1), getApplicationContext().getText(R.string.dialogBackupMessage2));

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
    	progressDialog = ProgressDialog.show(BlacklistOldActivity.this, getApplicationContext().getText(R.string.dialogRestoreMessage1), getApplicationContext().getText(R.string.dialogRestoreMessage2));    	

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
