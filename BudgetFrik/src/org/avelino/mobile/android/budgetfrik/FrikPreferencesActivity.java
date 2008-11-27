package org.avelino.mobile.android.budgetfrik;

import java.util.ArrayList;
import java.util.List;

import org.avelino.mobile.android.budgetfrik.DBHelper.Currencies;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class FrikPreferencesActivity extends PreferenceActivity {

	public static final String CURRENCIES = "org.avelino.mobile.android.budgetfrik.FrikPreferencesActivity.currency.list";
	protected static final String AUTOUPDATE = "org.avelino.mobile.android.budgetfrik.FrikPreferencesActivity.auto.update";;
	private RemoteCurrencyUpdater updater;

	/**
	 * What I'm I doing wrong with the progress here
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.frik_prefs);
		setProgressBarVisibility(false);
		List<CurrencyTO> curr = (List<CurrencyTO>) getIntent()
				.getSerializableExtra(CURRENCIES);
		setCurrencyCombo(curr);
		EditTextPreference csvPref = (EditTextPreference) getPreferenceScreen()
				.findPreference(getString(R.string.pref_csv_key));
		csvPref.setDefaultValue(PreferenceManager.getDefaultCSVSeparator());
		
		if (getIntent().getSerializableExtra(AUTOUPDATE)!= null){
			Toast.makeText(this, "Updating", Toast.LENGTH_LONG).show();
			new OnCurrencyUpdateListener(new ProgressListenerHandler(new Handler()){

				@Override
				public void setProgress(int p) {
					
					super.setProgress(p);
					if (p >=100){
						show(new AlertDialog.Builder(FrikPreferencesActivity.this)
						.setMessage("Press the back button to go the main screen"));
					}
				}
				
			}).onMenuItemClick(null);
			
		}
	}

	private void setCurrencyCombo(List<CurrencyTO> curr) {
		ListPreference curLst = (ListPreference) getPreferenceScreen()
				.findPreference(getString(R.string.pref_currency_key));
		curLst.setEntries(CurrencyTO.toStringArray(curr));
		curLst.setEntryValues(CurrencyTO.toStringValueArray(curr));
		curLst.setValueIndex(curr.indexOf(new CurrencyTO("", PreferenceManager
				.getDefaultCurrency(), 0.0f, 0, "")));
	}

	// protected void onResume() {
	// super.onResume();
	//
	// // Set up a listener whenever a key changes
	// getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	// }
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	//
	// // Unregister the listener whenever a key changes
	// getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	//	        
	// }

	private final class OnCurrencyUpdateListener implements
			OnMenuItemClickListener {
		
		private ProgressListenerHandler progressHandler;
		public OnCurrencyUpdateListener(){
		}
		public OnCurrencyUpdateListener(ProgressListenerHandler progressHandler) {
			this.progressHandler = progressHandler;
			
		}

		public boolean onMenuItemClick(MenuItem arg0) {
							// dialog.show();
			
			new Thread() {

				final Handler mHandler = new Handler();

				@Override
				public void run() {
					final DBHelper dbhelper = new DBHelper(
							FrikPreferencesActivity.this);
					updater = new RemoteCurrencyUpdater(dbhelper);
					try {
						setProgressBarVisibility(true);
						if (progressHandler == null){
							progressHandler = new ProgressListenerHandler(mHandler);
						}
						if (updater
								.updateCurrencies(progressHandler)) {
							Cursor c = dbhelper.getAllCurrencies(dbhelper.getReadableDatabase());
							List<CurrencyTO> currList = new ArrayList<CurrencyTO>();
							c.moveToFirst();
							while (!c.isAfterLast()) {
								currList.add(new CurrencyTO(c
										.getString(Currencies.SYMBOL.dbIndex()), 
										c.getInt(Currencies._ID.dbIndex()),
										c.getFloat(Currencies.EXCHANGE.dbIndex()), 
										c.getInt(Currencies.BASE.dbIndex()),
										c.getString(Currencies.MNEMONIC.dbIndex())));
								c.moveToNext();
							}
							c.close();
							setCurrencyCombo(currList);
							progressHandler.showToast("Currencies Updated", FrikPreferencesActivity.this, Toast.LENGTH_SHORT);
						} else {
							progressHandler.show(
									new AlertDialog.Builder(
										FrikPreferencesActivity.this)
											.setMessage(	
												"Failed to update exhchange rates, try again.\nReason: "
													+ updater.getErrorReason()));
						}
					} finally {
						setProgressBarVisibility(false);

						dbhelper.close();
					}
				}

			}.start();

			return false;
		}
	}

	private class ProgressListenerHandler implements ProgressListener{
		private static final int PROG_RATE = 1000 / 100;
		private int progress;
		private final Handler handler; 
		public ProgressListenerHandler(Handler handler) {
			this.handler = handler;
		}

		public void showToast(final String toast, final Context context, final int duration) {
			handler.post(new Runnable(){

				public void run() {
					Toast.makeText(context, toast, duration).show();
				}
				
			});
		}

		public void setProgress(int p) {
			progress = p * PROG_RATE;
			handler.post(new Runnable(){
				public void run() {
					getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress);
				}

			});
		}


		public void show(final Builder builder) {
			handler.post(new Runnable(){

				public void run() {
					builder.show();
				}
				
			});
			
		}
	}

	public static class PreferenceManager {
		private PreferenceManager() {
		}

		private static final String DEFAULT_CURRENCY = "default.currency";
		private static final String DEFAULT_CSV_SEP = "default.csv.separator";
		private static final String FIRST_RUN = "first.run";

		public static int getDefaultCurrency() {
			return Integer.parseInt(prefs.getString(DEFAULT_CURRENCY, "1"));
		}

		public static String getDefaultCSVSeparator() {
			return prefs.getString(DEFAULT_CSV_SEP, ",");
		}

		private static SharedPreferences prefs;

		public static void init(SharedPreferences sharedPreferences) {
			prefs = sharedPreferences;
			if (!prefs.contains(DEFAULT_CURRENCY)) {
				prefs.edit().putString(DEFAULT_CURRENCY, "1").commit();
			}
			if (!prefs.contains(DEFAULT_CSV_SEP)) {
				prefs.edit().putString(DEFAULT_CSV_SEP, ",").commit();
			}
		}

		public static boolean firstRun() {
			return !prefs.contains(FIRST_RUN);
		}

		public static void unsetFirstRun() {
			prefs.edit().putString(FIRST_RUN, String.valueOf(System.currentTimeMillis())).commit();
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.prefs_menu, menu);
		MenuItem item = menu.findItem(R.id.men_pref_clear);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem arg0) {
				final AlertDialog dialog = new AlertDialog.Builder(
						FrikPreferencesActivity.this).setIcon(
						android.R.drawable.ic_dialog_alert).setTitle(
						R.string.txt_pref_confirm).setMessage(
						R.string.txt_pref_clear_warn).setNegativeButton(
						android.R.string.cancel, null).setPositiveButton(
						android.R.string.ok, new OnClickListener() {

							public void onClick(DialogInterface dlg, int which) {
								new DBHelper(
										FrikPreferencesActivity.this)
										.clearEntries();
								dlg.dismiss();
								new AlertDialog.Builder(
										FrikPreferencesActivity.this)
										.setMessage("Expenses Cleared").show();
							}
						}).create();
				dialog.show();

				return false;
			}

		});

		item = menu.findItem(R.id.menu_pref_update);
		item.setOnMenuItemClickListener(new OnCurrencyUpdateListener());
		return super.onCreateOptionsMenu(menu);
	}

}
