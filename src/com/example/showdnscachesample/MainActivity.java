package com.example.showdnscachesample;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button web = (Button)findViewById(R.id.button1);
		web.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText ed = (EditText)findViewById(R.id.editText1);
				new queryDNS().execute(ed.getEditableText().toString());
			}
			
		});
		
		Button cache = (Button)findViewById(R.id.button2);
		cache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					showDNSCache();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
	}
	
	private void showDNSCache() throws NoSuchFieldException, IllegalAccessException {
		TextView view = (TextView)findViewById(R.id.textView1);
		
		  Class<InetAddress> cInetAddress = InetAddress.class;
		  Field fAddressCache = cInetAddress.getDeclaredField("addressCache");
		  fAddressCache.setAccessible(true);
		  Object addressCache = fAddressCache.get(null);

		  Class cAddressCache = addressCache.getClass();
		  Field fcache = cAddressCache.getDeclaredField("cache");
		  fcache.setAccessible(true);
		  Object cache = fcache.get(addressCache);
		  
		  Class cBasicLruCache = cache.getClass();
		  Field fmap = cBasicLruCache.getDeclaredField("map");
		  fmap.setAccessible(true);
		  Map<String, Object> map = (Map<String, Object>)fmap.get(cache);

		  StringBuffer sb = new StringBuffer();
		  for (Map.Entry<String, Object> entry : map.entrySet()) {
			  Object value = entry.getValue();
			  
			  Class cAddressCacheEntry = value.getClass();
			  Field fvalue = cAddressCacheEntry.getDeclaredField("value");
			  fvalue.setAccessible(true);
			  InetAddress[] addresses = (InetAddress[])fvalue.get(value);
			  
			  sb.append(entry.getKey() + "\t");
			  for (InetAddress address : addresses) {
				 sb.append(address.getHostAddress() + " ");
			  }
			  sb.append("\n");
		  }
		  
		  view.setText(sb.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class queryDNS extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				InetAddress address =InetAddress.getByName(params[0]);
				return address.getHostAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
		}
		
		
	}
}
