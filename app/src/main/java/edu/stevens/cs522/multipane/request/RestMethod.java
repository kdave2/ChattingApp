package edu.stevens.cs522.multipane.request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.apache.http.HttpResponse;

import android.net.Uri;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.Log;

public class RestMethod {

	public static final String TAG = RestMethod.class.getCanonicalName();

	public Response perform(Register request) {
		long clientId;
		Response response = new Response();
        HttpURLConnection conn = null;
		// Send data
		try {
            URL url = new URL(request.getRequestUri().toString());
            Log.i(TAG, "RestMethod, URI="+ url.toString() +" ,id= "+request.id);

            // Send POST data request
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1500);
            conn.setConnectTimeout(2000);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-latitude", "54.725488");
            conn.setRequestProperty("X-longitude", "53.785742");
            conn.setRequestMethod("POST");

			response.status = conn.getResponseCode();
            Log.i(TAG, "Response Code = " + String.valueOf(response.status));

			if (response.status == 201) {
				response.headers = conn.getHeaderFields();
				InputStream in = conn.getInputStream();
				JsonReader jr = new JsonReader(new InputStreamReader(in, "UTF-8"));
				jr.beginObject();
				if (jr != null) {
					jr.nextName();
					clientId = jr.nextLong();
					response.body = String.valueOf(clientId);
                    Log.i(TAG, "clientIdResponse =" + String.valueOf(response.body));
				}
				jr.close();
			}else if(response.status == 400){
                response = null;
                Log.i(TAG, "Response set to null - as client name already present");
            }
		}catch (Exception ex) {
            Log.e(TAG, "RestMethod: "+ex.getMessage());
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
		return response;
	}

	public StreamingResponse perform(Synchronize request) {
		HttpURLConnection conn;
		URI uri;
		StreamingResponse response = null;
		URL url;
		try {
			uri = new URI(request.getRequestUri().toString());
			url = uri.toURL();
		    conn =(HttpURLConnection) url.openConnection();
		    JsonReader rd = null;
		    response =  request.getResponse(conn, rd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public Response perform(Unregister request) {
        Log.i(TAG, "preforming Unregister in RestMethod.java");

		long clientId;
		Response response = new Response();
		try {
            URL url = new URL(request.getRequestUri().toString());
            Log.i(TAG, "RestMethod, URI="+ url.toString() +" ,id= "+request.id);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1500);
            conn.setConnectTimeout(2000);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("DELETE");

            response.status = conn.getResponseCode();
			Log.i(TAG, "Response Code= "+String.valueOf(response.status));
//			if (response.status<400||response.status>600) {
//				response.headers = conn.getHeaderFields();
////				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
////				wr.writeBytes(urlParameters);
////				wr.flush();
////				wr.close();
//				InputStream in = conn.getInputStream();
//				JsonReader jr = new JsonReader(new InputStreamReader(in,"UTF-8"));
//				jr.beginObject();
//				if (jr != null) {
//					jr.nextName();
//					clientId = jr.nextLong();
//					response.body = String.valueOf(clientId);
//					//Log.d("clientIdResponse",String.valueOf(response.body));
//				}
//				jr.close();
//			}
		} catch (Exception ex) {
			Log.e("RestMethod", ex.toString());
		}
		return response;
	}
}
