package com.tpvision.ambilib;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class Connection {
	
	public static boolean setAmbilights(String ip, int[] lights){
		try {
			makePostRequest("http://" + ip + ":1925/1/ambilight/cached", Parser.parse(lights));
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}
	
	public static boolean setAmbilightsMode(String ip, String mode){
		try {
			makePostRequest("http://" + ip + ":1925/1/ambilight/mode", "{	\"current\": \""+ mode +"\"}");
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}
	
	
	private static void makePostRequest(String uris, String json) throws ClientProtocolException, IOException, URISyntaxException {
		URI uri = null;
		uri = new URI(uris);
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setEntity(new StringEntity(json));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		new DefaultHttpClient().execute(httpPost);
	}



}
