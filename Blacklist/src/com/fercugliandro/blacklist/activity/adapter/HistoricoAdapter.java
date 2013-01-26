package com.fercugliandro.blacklist.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.datatype.Historico;

public class HistoricoAdapter extends ArrayAdapter<Historico> {

	private List<Historico> listaHistorico;
	
	public HistoricoAdapter(Context context, int textViewResourceId,
			List<Historico> objects) {

		super(context, textViewResourceId, objects);
	
		this.listaHistorico = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View viewLocal = convertView;

		Historico historico = null;
		
		if (viewLocal == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewLocal = vi.inflate(R.layout.list_history_blacklist, null);
		}
				
		historico = listaHistorico.get(position);
		TextView numero = (TextView) viewLocal.findViewById(R.id.labelNumeroTelefone);
		numero.setText(historico.getNumeroTelefone());
		
		TextView qtdeLigacoes = (TextView) viewLocal.findViewById(R.id.labelQtdeLigCanceladas);
		qtdeLigacoes.setText(String.valueOf(historico.getQtdeLigacao()));
		
		TextView dataUltimaLigacao = (TextView) viewLocal.findViewById(R.id.labelDataUltimaLigacao);
		dataUltimaLigacao.setText(historico.getUltimaLigacao());

		return viewLocal;
		
	}


}
