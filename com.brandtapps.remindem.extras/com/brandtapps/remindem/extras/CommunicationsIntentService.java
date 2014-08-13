package com.brandtapps.remindem.extras;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;
import android.app.IntentService;
import android.content.Intent;


public class CommunicationsIntentService extends IntentService {
	
	public static final String PARAM_ACTION = "ACTION";
	public static final String PARAM_IN_JSON = "JSON_IN";
	public static final String PARAM_OUT_JSON = "JSON_OUT";
	public static final String PARAM_RECEIVER = "RECEIVER";
	public static final String PARAM_OUT_ERROR_MESSAGE = "ERROR_MESSAGE";


	public CommunicationsIntentService() {
		super("CommunicationsIntentService");
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		InputStream is = null;
		HttpURLConnection conn = null;

		String errorMessage = "";
		int len = 1024;
		String response = null;
		String Receiver = "";
		OutputStream os = null;

		try {
			String Action = intent.getStringExtra(PARAM_ACTION);
			String JSON = intent.getStringExtra(PARAM_IN_JSON);
			Receiver = intent.getStringExtra(PARAM_RECEIVER);


			URL url = new URL("http://www.williamsportsda.com/apps/" + Action);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(20000);
			conn.setConnectTimeout(20000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoInput(true);

			os = conn.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
			if (JSON != null)
				writer.write(JSON);
			writer.flush();
			writer.close();


			conn.connect();
			int respCd = conn.getResponseCode();

			if (respCd == 200) {
				is = conn.getInputStream();

				// handle redirect
				if (!url.getHost().equals(conn.getURL().getHost())) {
					throw new Exception("Url Redirected.");
				}

				response = slurp(is, len);
			}

		} catch (Exception e) {
			errorMessage = "Internet Connection Failure.";
			e.printStackTrace();
		} finally {

			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}

			if (conn != null)
				conn.disconnect();

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {					
				}
			}

		}

		if (Receiver != null && Receiver != "") {
			
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(Receiver);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(PARAM_OUT_JSON, response);
			broadcastIntent.putExtra(PARAM_OUT_ERROR_MESSAGE, errorMessage);
			sendBroadcast(broadcastIntent);
		}

	}

	public static String slurp(final InputStream is, final int bufferSize) {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException e) {
			
		} catch (IOException e) {
			
		}
		return out.toString();
	}

}

