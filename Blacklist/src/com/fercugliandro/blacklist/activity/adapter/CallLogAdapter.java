package com.fercugliandro.blacklist.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.datatype.CallLog;

public class CallLogAdapter extends ArrayAdapter<CallLog>{

	private List<CallLog> listaCallLog;
	
	public CallLogAdapter(Context context, int textViewResourceId,
			List<CallLog> objects) {

		super(context, textViewResourceId, objects);
	
		this.listaCallLog = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View viewLocal = convertView;

		CallLog callLog = null;
		
		if (viewLocal == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewLocal = vi.inflate(R.layout.list_call_log, null);
		}
				
		callLog = listaCallLog.get(position);
		
		TextView numero = (TextView) viewLocal.findViewById(R.id.labelNroContato);
		TextView nome = (TextView) viewLocal.findViewById(R.id.labelNomeContato);
		numero.setText(callLog.getNumeroTelefone());		
		
		if (callLog.getNome() != null && !"".equals(callLog.getNome())) {
			nome.setText(callLog.getNome());
		} else {
			nome.setText("");
		}
		
		return viewLocal;
		
	}
	
}
