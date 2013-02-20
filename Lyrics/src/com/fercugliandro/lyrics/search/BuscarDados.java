package com.fercugliandro.lyrics.search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.fercugliandro.lyrics.datatype.Artista;

public class BuscarDados {

	private static final String URI_CIFRA_CLUB = "http://www.cifraclub.com.br/suggest/";

	private static final String URI_LETRAS_MUS = "http://letras.mus.br/suggest/";

	private String tipoBusca;

	public BuscarDados(String tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public List<Artista> buscarDados(String charSequence) throws JSONException {

		HttpResponse response = null;
		try {
			response = pesquisarArtista(charSequence);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String responseString = out.toString();

			Log.i("Lyrics", responseString);
			
			return jsonToObject(responseString);
			
		} else {
			// Closes the connection.
			try {
				response.getEntity().getContent().close();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				throw new IOException(statusLine.getReasonPhrase());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	private HttpResponse pesquisarArtista(String charSequence) throws URISyntaxException,
			ClientProtocolException, IOException {

		URI uri = null;
		if (tipoBusca.equalsIgnoreCase("C"))
			uri = new URI(URI_CIFRA_CLUB+charSequence.charAt(0)+"/"+charSequence.substring(0, 3)+".json");
		else
			uri = new URI(URI_LETRAS_MUS+charSequence.charAt(0)+"/"+charSequence.substring(0, 3)+".json");
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(uri));

		return response;
	}
	
	private List<Artista> jsonToObject(String response) throws JSONException {
		
		ArrayList<Artista> artistas = new ArrayList<Artista>();
		JSONObject json = new JSONObject(response);
		
		try {
			// Get the element that holds the earthquakes ( JSONArray )
			JSONArray artistasJson = json.getJSONArray("artistas");

			// Loop the Array
			for (int i = 0; i < artistasJson.length(); i++) {

				JSONObject e = artistasJson.getJSONObject(i);

				final Artista artista = new Artista();
				artista.setDns(e.getString("dns"));
				artista.setNomeArtista(e.getString("artista"));
			
				artistas.add(artista);
			}
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		
		return artistas;
	}
}
