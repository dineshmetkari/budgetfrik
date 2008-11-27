package org.avelino.mobile.android.budgetfrik;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.avelino.mobile.android.budgetfrik.DBHelper.Currencies;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class RemoteCurrencyUpdater {

	private static final String TAG = "RemoteCurrencyUpdater";
	private final DBHelper dbhelper;
	private boolean cancelled;
	private String reason = "N/A";

	public RemoteCurrencyUpdater(DBHelper dbhelper){
		this.dbhelper = dbhelper;
	}
	
	public void cancel(){
		cancelled = true;
	}
	
	public boolean isCanceled(){
		return cancelled;
	}
	
	public String getErrorReason(){
		return reason;
	}
	
	//http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
	//http://www.ny.frb.org/markets/pilotfx.html
	public boolean updateCurrencies(ProgressListener listener){
		cancelled = false;
//		ConnectivityManager conMan = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo[] ntwkrs = conMan.getAllNetworkInfo();
//		for (NetworkInfo info : ntwkrs) {
//			Log.v(TAG, info.toString());
//		}
		listener.setProgress(10);
//		DefaultHttpClient client = new DefaultHttpClient();
//		HttpParams params = new BasicHttpParams();
//		client.setParams(params);
//		HttpUriRequest request = new HttpGet(ECBCurrencyContentHandler.CURR_URL);
		
//		URL url;
//		HttpURLConnection httpConn = null;
//		try {
//			url = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
//			httpConn = (HttpURLConnection) url.openConnection();
//			int response = -1;
//
//	        httpConn.setAllowUserInteraction(false);
//	        httpConn.setInstanceFollowRedirects(true);
//	        httpConn.setRequestMethod("GET");
//	        httpConn.connect(); 
//
//	        response = httpConn.getResponseCode();                 
//	        if (response != HttpURLConnection.HTTP_OK) {
//	        	reason = httpConn.getResponseMessage();
//	            return false;                                 
//	        }
//		} catch (MalformedURLException e) {
//			Log.w(TAG,e);
//			reason = e.getLocalizedMessage();
//			return false;
//		} catch (IOException e) {
//			Log.w(TAG,e);
//			reason = e.getLocalizedMessage();
//			Log.v(TAG,System.getProperties().toString());
//			return false;
//		}
		
//		System.setProperty("http.proxyHost","alvwebcache1.bastion.europe.hp.com");
//		System.setProperty("http.proxyPort","8080");
//		HttpResponse response = null;
//		try {
//			response = client.execute(request);
//			dialog.setProgress(20);
//			if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK){
//				throw new ClientProtocolException(response.getStatusLine().getReasonPhrase());
//			}
//		} catch (ClientProtocolException e) {
//			Log.w(TAG,e);
//			reason = e.getLocalizedMessage();
//			return false;
//		} catch (IOException e) {
//			Log.w(TAG,e);
//			reason = e.getLocalizedMessage();
//			return false;
//		}
		final ECBCurrencyContentHandler handler = new ECBCurrencyContentHandler();
		if (!cancelled){
			try {
				SAXParserFactory
						.newInstance()
							.newSAXParser()
								.parse(ECBCurrencyContentHandler.CURR_URL,
										handler);//httpConn.getInputStream()));
				listener.setProgress(40);
			} catch (SAXException e) {
				Log.w(TAG,e);
				reason = e.getLocalizedMessage();
				return false;
			} catch (IllegalStateException e) {
				Log.w(TAG,e);
				reason = e.getLocalizedMessage();
				return false;
			} catch (IOException e) {
				Log.w(TAG,e);
				reason = e.getLocalizedMessage();
				return false;
			} catch (ParserConfigurationException e) {
				Log.w(TAG,e);
				reason = e.getLocalizedMessage();
				return false;
			}
		}
		if (!cancelled){
			List<CurrencyTO> targetCurr = new ArrayList<CurrencyTO>(); 
			Map<String,CurrencyTO> newCurr = handler.getCurrencies();
			String defCurr = handler.getDefaultMnemonic();
			CurrencyTO defaultCurr = null;
			final SQLiteDatabase database = dbhelper.getWritableDatabase();
			Cursor c = dbhelper.getAllCurrencies(database);
			listener.setProgress(50);
			c.moveToFirst();
			while (!c.isAfterLast()){
				final String mnemonic = c.getString(Currencies.MNEMONIC.dbIndex());
				if (defCurr.equals(mnemonic)){
					defaultCurr = new CurrencyTO(mnemonic, 
							c.getInt(Currencies._ID.dbIndex()),
							1.0f,
							c.getInt(Currencies._ID.dbIndex()),
						    c.getString(Currencies.MNEMONIC.dbIndex()));
					targetCurr.add(defaultCurr);
				} else if (newCurr.containsKey(mnemonic)){
					targetCurr.add(new CurrencyTO(mnemonic, 
						c.getInt(Currencies._ID.dbIndex()),
						newCurr.get(mnemonic).getExchange(),
						c.getInt(Currencies.BASE.dbIndex()),//Base not set here because we dont know where it is.
					    c.getString(Currencies.MNEMONIC.dbIndex())));
					newCurr.remove(mnemonic);
				}
				c.moveToNext();
			}
			//TODO What if the base currency is not on the list :)
			for (CurrencyTO curr : targetCurr) {
				curr.setBase(defaultCurr.getId());
			}
			listener.setProgress(60);
			int prog = 60;
			float rate = 20/(newCurr.size()+1);
			for (String mnemonic : newCurr.keySet()) {
				final CurrencyTO curr = newCurr.get(mnemonic);
				curr.setSymbol(curr.getMnemonic());
				curr.setBase(defaultCurr.getId());
				targetCurr.add(curr);
				prog+=rate;
				listener.setProgress(prog);
			}
			listener.setProgress(85);
			dbhelper.inserOrUpdateCurrencies(targetCurr,database);
			c.close();
			dbhelper.close();
		}
		listener.setProgress(100);
		return true;
	}
	

/**
 * 
 * @author benaviav
 *
 *<gesmes:Envelope>
<gesmes:subject>Reference rates</gesmes:subject>

<gesmes:Sender>
<gesmes:name>European Central Bank</gesmes:name>
</gesmes:Sender>
<Cube>
<Cube time="2008-11-20">
<Cube currency="USD" rate="1.2542"/>
<Cube currency="JPY" rate="119.87"/>
<Cube currency="BGN" rate="1.9558"/>
<Cube currency="CZK" rate="25.635"/>
<Cube currency="DKK" rate="7.4524"/>
<Cube currency="EEK" rate="15.6466"/>
<Cube currency="GBP" rate="0.84210"/>
<Cube currency="HUF" rate="269.94"/>
<Cube currency="LTL" rate="3.4528"/>
<Cube currency="LVL" rate="0.7093"/>
<Cube currency="PLN" rate="3.8500"/>
<Cube currency="RON" rate="3.8170"/>
<Cube currency="SEK" rate="10.2395"/>
<Cube currency="SKK" rate="30.380"/>
<Cube currency="CHF" rate="1.5290"/>
<Cube currency="ISK" rate="245.00"/>
<Cube currency="NOK" rate="8.8700"/>
<Cube currency="HRK" rate="7.1350"/>
<Cube currency="RUB" rate="34.5555"/>
<Cube currency="TRY" rate="2.1354"/>
<Cube currency="AUD" rate="1.9927"/>
<Cube currency="BRL" rate="3.0198"/>
<Cube currency="CAD" rate="1.5814"/>
<Cube currency="CNY" rate="8.5718"/>
<Cube currency="HKD" rate="9.7202"/>
<Cube currency="IDR" rate="15363.95"/>
<Cube currency="KRW" rate="1892.24"/>
<Cube currency="MXN" rate="16.9003"/>
<Cube currency="MYR" rate="4.5446"/>
<Cube currency="NZD" rate="2.3209"/>
<Cube currency="PHP" rate="62.760"/>
<Cube currency="SGD" rate="1.9176"/>
<Cube currency="THB" rate="44.079"/>
<Cube currency="ZAR" rate="13.2315"/>
</Cube>
</Cube>
</gesmes:Envelope>
 *
 */
	public class ECBCurrencyContentHandler extends DefaultHandler{
		private static final String EUR = "EUR";
		private static final String TAG = "ECBCurrencyContentHandler";
		private static final String CURR_URL = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
		private Map<String,CurrencyTO> currencies = new HashMap<String,CurrencyTO>();
		
		public Map<String,CurrencyTO> getCurrencies(){
			return currencies;
		}

		public String getDefaultMnemonic() {
			return EUR;
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			if ("Cube".equals(localName) && attributes.getValue("currency") != null
					&& attributes.getValue("rate") != null){
				try {
					currencies.put(attributes.getValue("currency"),
							new CurrencyTO("",-1, 
									DecimalFormat.getNumberInstance().parse(
											attributes.getValue("rate")).floatValue(),
									-1,attributes.getValue("currency")));
				} catch (ParseException e) {
					Log.w(TAG, "Skipping:" + attributes.getValue("currency") + ", Rate:" + attributes.getValue("rate"),e);
				}
			}
		}
		
	}
	
}
