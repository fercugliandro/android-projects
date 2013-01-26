package com.fercugliandro.blacklist.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fercugliandro.blacklist.R;
import com.fercugliandro.blacklist.datatype.Blacklist;

public class BlacklistAdapter extends ArrayAdapter<Blacklist> {

	private List<Blacklist> listaBlackList;
	
	public BlacklistAdapter(Context context, int textViewResourceId,
			List<Blacklist> objects) {

		super(context, textViewResourceId, objects);
	
		this.listaBlackList = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View viewLocal = convertView;

		Blacklist blacklist = null;
		
		if (viewLocal == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewLocal = vi.inflate(R.layout.list_number_blacklist, null);
		}
				
		blacklist = listaBlackList.get(position);
		TextView numero = (TextView) viewLocal.findViewById(R.id.labelNumeroTelefone);
		numero.setText(blacklist.getNumero());
		
		return viewLocal;
		
	}

}
