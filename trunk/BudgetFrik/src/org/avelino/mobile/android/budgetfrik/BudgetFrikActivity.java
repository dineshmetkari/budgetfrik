package org.avelino.mobile.android.budgetfrik;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.avelino.mobile.android.budgetfrik.CategoryDBHelper.Categories;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class BudgetFrikActivity extends Activity {
	private static final EmptyClickListener EMPTY_CLICK_LISTENER = new EmptyClickListener();
	private static final int[] CALC_BTNS = { R.id.CalcButton0,
		R.id.CalcButton00, R.id.CalcButton01, R.id.CalcButton02,
		R.id.CalcButton03, R.id.CalcButton04, R.id.CalcButton05,
		R.id.CalcButton06, R.id.CalcButton07, R.id.CalcButton08,
		R.id.CalcButton09, R.id.CalcButtonDot };
	private static final int CHANGE_CURRENCY_DIALOG = 4;	
	
	private static final int FILENAME_DIALOG = 2;
	
	public static final int ENTRY_DETAILS_DIALOG = 1;
	
	private static final int UPDATE_CURRENCIES_DIALOG = 8;
	
	private static final int MAIN_CATEGORY_VIEW = 0;
	
	private static final int DEFAULT_REPORT_VIEW = 1;
	
	public static final String PREFS_NAME = "org.avelino.mobile.android.budgetfrik_preferences";
	
	protected static final String TAG = "BudgetFrikActivity";
	
	private final IconGridAdapter adapter = new IconGridAdapter(this);
	private ViewFlipper flipper;
	private LayoutInflater layoutFactory;
	private GridView mGrid;
	private Report report;
	private final ReportMenuListener reportMenuListener = new ReportMenuListener();


	private static final class EmptyClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog,
				int whichButton) {
		}
	}
	private final class CSVReporter implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog,
				int which) {
			String filename = ((EditText) ((Dialog) dialog)
								.findViewById(R.id.Edit_Value))
								.getText().toString();
			int[][] heads = report.getHeadings();
			final boolean canceled[] = {false};
			Map<Integer, String> data = report.getReportData(adapter,new ProgressListener(){
				public void setProgress(int p) {
					}});
			ProgressDialog mProgressDialog = new ProgressDialog(BudgetFrikActivity.this);
			mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
            mProgressDialog.setTitle(R.string.dialog_progress);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(heads[0].length);
            mProgressDialog.setOnCancelListener(new OnCancelListener(){

				public void onCancel(DialogInterface dialog) {
					canceled[0] = true;
					
				}});
			try {
				FileOutputStream fos = getApplicationContext()
						.openFileOutput(filename,
								MODE_WORLD_WRITEABLE);
				mProgressDialog.show();
				PrintStream out = new PrintStream(fos);
				out.println("Heading;Value");
				
				for (int j = 0; j < heads[0].length;j++) {
					if (canceled[0]){
						mProgressDialog.setProgress(0);
						break;
					}
					mProgressDialog.setProgress(j);
					String heading = "N/A";
					String value = "N/A";
					heading = getApplicationContext().getString(heads[0][j]);
					value = data.get(heads[1][j]);
					out.printf("\"%s\";%s\n",
							heading, value);
				}
				out.flush();
				fos.close();
				if (canceled[0]){
					getApplicationContext().deleteFile(filename);
				}
			} catch (IOException e) {
				Log.w(TAG,
						"Error while exporting file:"
								+ filename, e);
				Dialog error = new AlertDialog.Builder(
						BudgetFrikActivity.this)
						.setMessage(
								"Unable to export report as "
										+ filename
										+ "\nTry again.")
						.setTitle("Export Error")
						.setIcon(
								android.R.drawable.ic_dialog_alert)
						.create();
				error.show();
			} finally{
				mProgressDialog.dismiss();
			}

		}
	}
	private static final class CalcButtonListener implements OnClickListener {

		public void onClick(View v) {
			final Button btn = (Button) v;
			EditText value = (EditText) v.getRootView().findViewById(
					R.id.Edit_Value);
			value.append(btn.getText());
		}
	}
	private final class ReportMenuListener implements OnMenuItemClickListener {

		public boolean onMenuItemClick(MenuItem item) {
			flipper.setDisplayedChild(DEFAULT_REPORT_VIEW);
			report = adapter.getDefaultReport(FrikPreferencesActivity.PreferenceManager
					.getDefaultCurrency());
			displayReport();
			return false;
		}
	}
	private void displayReport() {
		final ProgressDialog dialog = new ProgressDialog(this);
		try{
			dialog.setTitle("Report in progress...");
			dialog.setMax(100);
			dialog.show();
			Map<Integer, String> reportData = report.getReportData(adapter, new ProgressListener(){
				final static float PROGRESS_RATE = 80/100;
				public void setProgress(int p) {
					dialog.setProgress((int)(p*PROGRESS_RATE));
					
				}});
			dialog.setProgress(80);
			int increment = 20/(1+reportData.size());
			int progress = 80;
			for (int keyid : reportData.keySet()) {
				TextView tv = (TextView) flipper.findViewById(keyid);
				tv.setText(reportData.get(keyid));
				progress += increment;
				dialog.setProgress(progress);
			}
		} finally {
			dialog.dismiss();
		}
	}

	// private List<ResolveInfo> mApps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutFactory = LayoutInflater.from(this);
		setContentView(R.layout.grid_1);
		flipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
		flipper.addView(layoutFactory.inflate(R.layout.defaultreport, null),
				DEFAULT_REPORT_VIEW);
		flipper.setInAnimation(this, android.R.anim.slide_in_left);
		flipper.setOutAnimation(this, android.R.anim.slide_out_right);
		mGrid = (GridView) findViewById(R.id.myGrid);
		adapter.open();
		mGrid.setAdapter(adapter);
		FrikPreferencesActivity.PreferenceManager.init(getSharedPreferences(PREFS_NAME, 0));
		if (FrikPreferencesActivity.PreferenceManager.firstRun()){
			FrikPreferencesActivity.PreferenceManager.unsetFirstRun();
			showDialog(UPDATE_CURRENCIES_DIALOG);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		final List<CurrencyTO> currencies = adapter.getCurrencies();
		switch (id) {
		case ENTRY_DETAILS_DIALOG:
			dialog = getDataEntryDialog(currencies);
			break;
		case FILENAME_DIALOG:
			dialog = getFilenameDialog();
			break;
		case CHANGE_CURRENCY_DIALOG:
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.menu_curr)
					.setItems(CurrencyTO.toStringArray(currencies),
						new DialogInterface.OnClickListener() {
	
							public void onClick(DialogInterface dialog, int which) {
								Log.v(TAG, "Chosen index:" + which);
								Log.v(TAG, "Old currency:" + report.getCurrency());
								report.changeCurrency(currencies
										.get(which));
								Log.v(TAG, "Changed currency:"
										+ report.getCurrency());
								displayReport();
							}
						})
					.create();
			break;
		case UPDATE_CURRENCIES_DIALOG:
			dialog = new AlertDialog.Builder(this)
			.setTitle("Welcome!")
			.setMessage("Do you want to download the latest currency exchanges?\nInternet connection required.")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent();
					intent.putExtra(FrikPreferencesActivity.CURRENCIES, (Serializable)adapter.getCurrencies());
					intent.putExtra(FrikPreferencesActivity.AUTOUPDATE, (Serializable)Boolean.TRUE);
					intent.setClass(BudgetFrikActivity.this, FrikPreferencesActivity.class);
					startActivity(intent);
					adapter.clearCache();
				}

				})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					new AlertDialog.Builder(BudgetFrikActivity.this)
					.setMessage("You can always download the lates exchage rates from the Preferences menu.\nEnjoy your visit.")
					.show();
				}

				})
			.create();
		}
		return dialog;
	}

	private AlertDialog getDataEntryDialog(final List<CurrencyTO> currencies) {
		AlertDialog dialog;
		final View textEntryView = layoutFactory.inflate(R.layout.enterdialog, null);
		final CostDetailsListener costDetailsListener = new CostDetailsListener(
				adapter);
		dialog = new AlertDialog.Builder(this)
				.setCustomTitle(layoutFactory.inflate(R.layout.dialogtitle, null))
				.setPositiveButton(android.R.string.ok, costDetailsListener)
				.setNegativeButton(android.R.string.cancel,EMPTY_CLICK_LISTENER)
				.setView(textEntryView).create();
		costDetailsListener.setAlertDialog(dialog);
		// Button 00,0-9,. just append the face value
		final CalcButtonListener calcButtonListener = new CalcButtonListener();
		for (int btnId : CALC_BTNS) {
			((Button) textEntryView.findViewById(btnId))
					.setOnClickListener(calcButtonListener);
		}
		// Button c, sends a delete keypress event
		((Button) textEntryView.findViewById(R.id.CalcButtonC))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						v.getRootView().findViewById(R.id.Edit_Value)
								.dispatchKeyEvent(
										new KeyEvent(KeyEvent.ACTION_DOWN,
												KeyEvent.KEYCODE_DEL));
						v.getRootView().findViewById(R.id.Edit_Value)
								.dispatchKeyEvent(
										new KeyEvent(KeyEvent.ACTION_UP,
												KeyEvent.KEYCODE_DEL));
					}
				});
		return dialog;
	}

	private AlertDialog getFilenameDialog() {
		final EditText textEntryView = new EditText(this);
		textEntryView.setMaxLines(1);
		textEntryView.setId(R.id.Edit_Value);
		return new AlertDialog.Builder(this)
					.setMessage(R.string.file_dialog_message)
					.setTitle(R.string.file_dialog_title)
					.setView(textEntryView)
					.setPositiveButton(android.R.string.ok,
							new CSVReporter())
					.setNegativeButton(android.R.string.cancel,
							EMPTY_CLICK_LISTENER)
					.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Dunno why, all the examples do this.
		super.onCreateOptionsMenu(menu);
		
		return true;
	}

	private void buildMainMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		MenuItem item = menu.findItem(R.id.reports_menuitem);
		item.setOnMenuItemClickListener(reportMenuListener);
		item = menu.findItem(R.id.prefs_menuitem);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.putExtra(FrikPreferencesActivity.CURRENCIES, (Serializable)adapter.getCurrencies());
				intent.setClass(BudgetFrikActivity.this, FrikPreferencesActivity.class);
				startActivity(intent);
				adapter.clearCache();
				return false;
			}});
	}
	
	
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		boolean display = false;
		switch (flipper.getDisplayedChild()){
			case MAIN_CATEGORY_VIEW:
				menu.clear();
				buildMainMenu(menu);
				display = true;
				break;
			case DEFAULT_REPORT_VIEW:
				menu.clear();
				buildReportMenu(menu);
				display = true;
				break;
		}
		return display;
	}

	private void buildReportMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reports_menu, menu);
		MenuItem item = menu.findItem(R.id.back2main_menuitem);
		item.setIcon(android.R.drawable.ic_menu_gallery);//R.drawable.cal_icon);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				flipper.setDisplayedChild(MAIN_CATEGORY_VIEW);
				return false;
			}
		});
		
		item = menu.findItem(R.id.reports_menuitem);
		item.setIcon(android.R.drawable.ic_menu_report_image );//R.drawable.budgetfrik);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(CHANGE_CURRENCY_DIALOG);
				return false;
			}
		});
		
		item = menu.findItem(R.id.save_menuitem);
		item.setIcon(android.R.drawable.ic_menu_save);//R.drawable.view_icon);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(FILENAME_DIALOG);
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		adapter.close();
		super.onDestroy();
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		final List<CurrencyTO> currencies = adapter.getCurrencies();
		switch (id) {
		case ENTRY_DETAILS_DIALOG:
			final ArrayAdapter<CategoryTO> arrayAdapter = new ArrayAdapter<CategoryTO>(
					this, android.R.layout.simple_spinner_item, adapter
							.getSubCategories());
			arrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			((Spinner) dialog.findViewById(R.id.ChooseSubCat))
					.setAdapter(arrayAdapter);

			// final Drawable drawable =
			// getResources().getDrawable(Integer.parseInt(
			// adapter.getActiveMetaData().getEntry(
			// Categories.ICON).toString()));
			// drawable.setBounds(0, 0, 32, 32);
			((ImageView) dialog.findViewById(R.id.DialogIcon))
					.setImageResource(Integer
							.parseInt(((Map<Categories, Object>) adapter
									.getActiveIcon().getTag()).get(
									Categories.ICON).toString()));
			((EditText) dialog.findViewById(R.id.Edit_Notes)).setText("");
			((EditText) dialog.findViewById(R.id.Edit_Value)).setText("");
			
			final ArrayAdapter<CurrencyTO> currAdapter = new ArrayAdapter<CurrencyTO>(
					this, android.R.layout.simple_spinner_item, currencies);
			final CurrencyTO defCurrency = currencies.get(
						currencies.indexOf(new CurrencyTO("",
						FrikPreferencesActivity
						.PreferenceManager
						.getDefaultCurrency(),0.0f,0,"")));
			int pos = currAdapter.getPosition(defCurrency);
			if (pos != 0 ){
				currAdapter.remove(defCurrency);
				currAdapter.insert(defCurrency, 0);
			}
			
			Spinner currLst =  (Spinner)dialog.findViewById(R.id.LLayout2).findViewById(R.id.ChooseCurrency);
			currLst.setAdapter(currAdapter);
			currAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			break;
		case FILENAME_DIALOG:
			((EditText) dialog.findViewById(R.id.Edit_Value))
					.setText(new SimpleDateFormat("yyyy_MM_dd")
							.format(new Date())
							+ ".csv");

			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * ACTION = UP (Finger UP?) EDGE = (Left - Right)
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

}
