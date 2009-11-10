package org.avelino.mobile.android.budgetfrik;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.avelino.mobile.android.budgetfrik.DBHelper.Categories;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Process for changing screen layout: (Portrait to Landscape)
02-11 13:08:34.486: INFO/WindowManager(49): Input configuration changed: { scale=1.0 imsi=0/0 locale=en_US touch=3 key=2/1 nav=3 orien=1 }
02-11 13:08:34.534: INFO/BudgetFrikActivity(1860): SaveInstanceState:10
02-11 13:08:34.544: INFO/BudgetFrikActivity(1860): Pause
02-11 13:08:34.544: INFO/BudgetFrikActivity(1860): Stop
02-11 13:08:34.553: INFO/BudgetFrikActivity(1860): Destroy
02-11 13:08:34.572: INFO/BudgetFrikActivity(1860): Create
02-11 13:08:34.722: INFO/BudgetFrikActivity(1860): Start
02-11 13:08:34.722: INFO/BudgetFrikActivity(1860): RestoreInstanceState:10
02-11 13:08:34.732: INFO/BudgetFrikActivity(1860): Resume
02-11 13:08:34.732: INFO/BudgetFrikActivity(1860): PostResume

 * 
 * Making a call and coming back: 
 * 
02-11 12:33:56.962: INFO/ActivityManager(49): Starting activity: Intent { action=android.intent.action.CALL_BUTTON flags=0x10000000 comp={com.android.contacts/com.android.contacts.DialtactsActivity} }
02-11 12:33:57.023: INFO/BudgetFrikActivity(609): SaveInstanceState
02-11 12:33:57.032: INFO/BudgetFrikActivity(609): Pause
02-11 12:33:57.234: INFO/BudgetFrikActivity(609): Stop
02-11 12:33:57.703: DEBUG/dalvikvm(88): GC freed 1166 objects / 63696 bytes in 61ms
02-11 12:33:57.993: INFO/ActivityManager(49): Displayed activity com.android.contacts/.DialtactsActivity: 1021 ms
02-11 12:34:03.033: WARN/KeyCharacterMap(88): No keyboard for id 0
02-11 12:34:03.033: WARN/KeyCharacterMap(88): Using default keymap: /system/usr/keychars/qwerty.kcm.bin
02-11 12:34:03.132: INFO/BudgetFrikActivity(609): Restart
02-11 12:34:03.132: INFO/BudgetFrikActivity(609): Start
02-11 12:34:03.142: INFO/BudgetFrikActivity(609): Resume
02-11 12:34:03.142: INFO/BudgetFrikActivity(609): PostResume

 * 
 * 
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class BudgetFrikActivity extends Activity {
	
	
	//private static final int CHANGE_CURRENCY_DIALOG = 4;	
	
	//private static final int FILENAME_DIALOG = 2;
	
	public static final int ENTRY_DETAILS_DIALOG = 1;
	
	private static final int UPDATE_CURRENCIES_DIALOG = 8;
	
	//private static final int MAIN_CATEGORY_VIEW = 0;
	
	//private static final int DEFAULT_REPORT_VIEW = 1;
	
	public static final String PREFS_NAME = "org.avelino.mobile.android.budgetfrik_preferences";
	
	protected static final String TAG = "BudgetFrikActivity";
	
	private final IconGridAdapter adapter = new IconGridAdapter(this);
	//// private ViewFlipper flipper;
	//// private GridView mGrid;
	private final ReportMenuListener reportMenuListener = new ReportMenuListener();
	private CostDetailsListener costDetailsListener;

	private Gallery mGallery;

	//private TextView catLabel;


//	private static final class EmptyClickListener implements
//			DialogInterface.OnClickListener {
//		public void onClick(DialogInterface dialog,
//				int whichButton) {
//		}
//	}
//	private final class CSVReporter implements DialogInterface.OnClickListener {
//		public void onClick(DialogInterface dialog,
//				int which) {
//			String filename = ((EditText) ((Dialog) dialog)
//								.findViewById(R.id.Edit_Value))
//								.getText().toString();
//			int[][] heads = report.getHeadings();
//			final boolean canceled[] = {false};
//			Map<Integer, String> data = report.getReportData(adapter,new ProgressListener(){
//				public void setProgress(int p) {
//					}});
//			ProgressDialog mProgressDialog = new ProgressDialog(BudgetFrikActivity.this);
//			mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
//            mProgressDialog.setTitle(R.string.dialog_progress);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            mProgressDialog.setMax(heads[0].length);
//            mProgressDialog.setOnCancelListener(new OnCancelListener(){
//
//				public void onCancel(DialogInterface dialog) {
//					canceled[0] = true;
//					
//				}});
//			try {
//				FileOutputStream fos = getApplicationContext()
//						.openFileOutput(filename,
//								MODE_WORLD_WRITEABLE);
//				mProgressDialog.show();
//				PrintStream out = new PrintStream(fos);
//				out.println("Heading;Value");
//				
//				for (int j = 0; j < heads[0].length;j++) {
//					if (canceled[0]){
//						mProgressDialog.setProgress(0);
//						break;
//					}
//					mProgressDialog.setProgress(j);
//					String heading = "N/A";
//					String value = "N/A";
//					heading = getApplicationContext().getString(heads[0][j]);
//					value = data.get(heads[1][j]);
//					out.printf("\"%s\";%s\n",
//							heading, value);
//				}
//				out.flush();
//				fos.close();
//				if (canceled[0]){
//					getApplicationContext().deleteFile(filename);
//				}
//			} catch (IOException e) {
//				Log.w(TAG,
//						"Error while exporting file:"
//								+ filename, e);
//				Dialog error = new AlertDialog.Builder(
//						BudgetFrikActivity.this)
//						.setMessage(
//								"Unable to export report as "
//										+ filename
//										+ "\nTry again.")
//						.setTitle("Export Error")
//						.setIcon(
//								android.R.drawable.ic_dialog_alert)
//						.create();
//				error.show();
//			} finally{
//				mProgressDialog.dismiss();
//			}
//
//		}
//	}
	private final class ReportMenuListener implements OnMenuItemClickListener {

		public boolean onMenuItemClick(MenuItem item) {
//			flipper.setDisplayedChild(DEFAULT_REPORT_VIEW);
//			report = adapter.getDefaultReport(FrikPreferencesActivity.PreferenceManager
//					.getDefaultCurrency());
//			displayReport();
//			return false;
			
			Intent intent = new Intent();
			intent.putExtra(ReportActivity.REPORT, 
								adapter.getDefaultReport(
									FrikPreferencesActivity.PreferenceManager.getDefaultCurrency()));
			intent.setClass(BudgetFrikActivity.this, ReportActivity.class);
			startActivity(intent);
			adapter.clearCache();
			return false;
		}
	}
//	private void displayReport() {
//		final ProgressDialog dialog = new ProgressDialog(this);
//		try{
//			dialog.setTitle("Report in progress...");
//			dialog.setMax(100);
//			dialog.show();
//			Map<Integer, String> reportData = report.getReportData(adapter, new ProgressListener(){
//				final static float PROGRESS_RATE = 80/100;
//				public void setProgress(int p) {
//					dialog.setProgress((int)(p*PROGRESS_RATE));
//					
//				}});
//			dialog.setProgress(80);
//			int increment = 20/(1+reportData.size());
//			int progress = 80;
//			for (int keyid : reportData.keySet()) {
//				TextView tv = (TextView) flipper.findViewById(keyid);
//				tv.setText(reportData.get(keyid));
//				progress += increment;
//				dialog.setProgress(progress);
//			}
//		} finally {
//			dialog.dismiss();
//		}
//	}

	// private List<ResolveInfo> mApps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Create");
//		layoutFactory = LayoutInflater.from(this);
		//// setContentView(R.layout.grid_1);
		setContentView(R.layout.main);
		//// flipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
		
//		flipper.addView(layoutFactory.inflate(R.layout.defaultreport, null),
//				DEFAULT_REPORT_VIEW);
		///// flipper.setInAnimation(this, android.R.anim.slide_in_left);
		///// flipper.setOutAnimation(this, android.R.anim.slide_out_right);
		///// mGrid = (GridView) findViewById(R.id.myGrid);
		mGallery = (Gallery) findViewById(R.id.Category_Gallery); 
		adapter.open();
		///// mGrid.setAdapter(adapter);
		mGallery.setAdapter(adapter);
		FrikPreferencesActivity.PreferenceManager.init(getSharedPreferences(PREFS_NAME, 0));
		if (FrikPreferencesActivity.PreferenceManager.firstRun()){
			FrikPreferencesActivity.PreferenceManager.unsetFirstRun();
			showDialog(UPDATE_CURRENCIES_DIALOG);
		}
		//catLabel = (TextView) getLayoutInflater().inflate(R.layout.textlabel, null);
		//catLabel.setShadowLayer(0.5f, 2, 5, android.R.color.black);
		//catLabel.setTextColor(android.R.color.white);
		//catLabel.setBackgroundColor(android.R.color.background_dark);
		//addContentView(catLabel, new AbsoluteLayout.LayoutParams(400,100,0,0));
		final List<CurrencyTO> currencies = adapter.getCurrencies();
		final CurrencyTO defCurrency = currencies.get(
				currencies.indexOf(new CurrencyTO("",
				FrikPreferencesActivity
				.PreferenceManager
				.getDefaultCurrency(),0.0f,0,"")));
		final ArrayAdapter<CurrencyTO> currAdapter = new ArrayAdapter<CurrencyTO>(
				this, android.R.layout.simple_spinner_item, currencies);
	int pos = currAdapter.getPosition(defCurrency);
	if (pos != 0 ){
		currAdapter.remove(defCurrency);
		currAdapter.insert(defCurrency, 0);
	}
	
	Spinner currLst =  (Spinner)this.findViewById(R.id.LLayout2).findViewById(R.id.ChooseCurrency);
	currLst.setAdapter(currAdapter);
	currAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
	//Buttons
	
	costDetailsListener = new CostDetailsListener(adapter);
	((Button)this.findViewById(R.id.ButtonsContainer).findViewById(R.id.positive)).setOnClickListener(costDetailsListener);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		final List<CurrencyTO> currencies = adapter.getCurrencies();
		switch (id) {
		case ENTRY_DETAILS_DIALOG:
				costDetailsListener = new CostDetailsListener(adapter);
				dialog = DialogHelper.getDataEntryDialog(currencies, costDetailsListener, this, new CalcButtonListener(costDetailsListener));
			break;
//		case FILENAME_DIALOG:
//			dialog = getFilenameDialog();
//			break;
//		case CHANGE_CURRENCY_DIALOG:
//			dialog = new AlertDialog.Builder(this)
//					.setTitle(R.string.menu_curr)
//					.setItems(CurrencyTO.toStringArray(currencies),
//						new DialogInterface.OnClickListener() {
//	
//							public void onClick(DialogInterface dialog, int which) {
//								Log.v(TAG, "Chosen index:" + which);
//								Log.v(TAG, "Old currency:" + report.getCurrency());
//								report.changeCurrency(currencies
//										.get(which));
//								Log.v(TAG, "Changed currency:"
//										+ report.getCurrency());
//								displayReport();
//							}
//						})
//					.create();
//			break;
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


//	private AlertDialog getFilenameDialog() {
//		final EditText textEntryView = new EditText(this);
//		textEntryView.setMaxLines(1);
//		textEntryView.setId(R.id.Edit_Value);
//		return new AlertDialog.Builder(this)
//					.setMessage(R.string.file_dialog_message)
//					.setTitle(R.string.file_dialog_title)
//					.setView(textEntryView)
//					.setPositiveButton(android.R.string.ok,
//							new CSVReporter())
//					.setNegativeButton(android.R.string.cancel,
//							EMPTY_CLICK_LISTENER)
//					.create();
//	}

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
		item = menu.findItem(R.id.calendar_menuitem);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.putExtra(CalendarActivity.CURRENCIES, (Serializable)adapter.getCurrencies());
				intent.setClass(BudgetFrikActivity.this, CalendarActivity.class);
				startActivity(intent);
				return false;
			}});
	}
	
	
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		boolean display = false;
//		switch (flipper.getDisplayedChild()){
//			case MAIN_CATEGORY_VIEW:
				menu.clear();
				buildMainMenu(menu);
				display = true;
//				break;
//			case DEFAULT_REPORT_VIEW:
//				menu.clear();
//				buildReportMenu(menu);
//				display = true;
//				break;
//		}
		return display;
	}

//	private void buildReportMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.reports_menu, menu);
//		MenuItem item = menu.findItem(R.id.back2main_menuitem);
//		item.setIcon(android.R.drawable.ic_menu_gallery);//R.drawable.cal_icon);
//		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			public boolean onMenuItemClick(MenuItem item) {
//				flipper.setDisplayedChild(MAIN_CATEGORY_VIEW);
//				return false;
//			}
//		});
//		
//		item = menu.findItem(R.id.reports_menuitem);
//		item.setIcon(android.R.drawable.ic_menu_report_image );//R.drawable.budgetfrik);
//		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			public boolean onMenuItemClick(MenuItem item) {
//				showDialog(CHANGE_CURRENCY_DIALOG);
//				return false;
//			}
//		});
//		
//		item = menu.findItem(R.id.save_menuitem);
//		item.setIcon(android.R.drawable.ic_menu_save);//R.drawable.view_icon);
//		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			public boolean onMenuItemClick(MenuItem item) {
//				showDialog(FILENAME_DIALOG);
//				return false;
//			}
//		});
//	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Destroy");
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
			costDetailsListener.onPrepare();
			break;
//		case FILENAME_DIALOG:
//			((EditText) dialog.findViewById(R.id.Edit_Value))
//					.setText(new SimpleDateFormat("yyyy_MM_dd")
//							.format(new Date())
//							+ ".csv");
//
//			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * ACTION = UP (Finger UP?) EDGE = (Left - Right)
	 * 
	 */
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//
//		return super.onTouchEvent(event);
//	}

	@Override
	protected void onPause() {
		Log.i(TAG, "Pause");
		super.onPause();
	}

	@Override
	protected void onPostResume() {
		//// Log.i(TAG, "PostResume:" + mGrid.getChildCount());
		super.onPostResume();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "Restart");
		super.onRestart();
	}



	@Override
	protected void onResume() {
		Log.i(TAG, "Resume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		Log.i(TAG, "SaveInstanceState:focus:" + getWindow().getCurrentFocus());
		//Save which menu item had the focus.
////		if (mGrid != null && mGrid.getFocusedChild() != null){
////			Log.i(TAG, "SaveInstanceState:" + mGrid.getFocusedChild().getId());
////			outState.putInt("SELECTED_ITEM", mGrid.getFocusedChild().getId());
////		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "RestoreInstanceState:" + savedInstanceState.getInt("SELECTED_ITEM"));
		//Restore Selected Item
		//adapter.focusButton(savedInstanceState.getInt("SELECTED_ITEM"));
////		final View childAt = mGrid.getChildAt(savedInstanceState.getInt("SELECTED_ITEM"));
////		Log.i(TAG,"RestoreInstanceState: count" + mGrid.getCount());
		////		if (childAt != null){
		////	childAt.requestFocus();
		////	Log.i(TAG,"RestoreInstanceState: View Found");
		////childAt.requestFocusFromTouch();
		////}
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	
	@Override
	protected void onStart() {
		Log.i(TAG, "Start");
		WindowManager winMan = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Log.i(TAG, "Start:Orientation:" + winMan.getDefaultDisplay().getOrientation());
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "Stop");
		super.onStop();
	}

	/**
	 *  Map<Categories,Object> map = (Map<Categories, Object>) ivew.getTag();
        ivew.setImageResource(
        					cx.getInt(
        							Categories.ICON.dbIndex()));
        map.put(Categories._ID, cx.getInt(Categories._ID.dbIndex()));
        map.put(Categories.TITLE, cx.getString(Categories.TITLE.dbIndex()));
        map.put(Categories.ICON, cx.getString(Categories.ICON.dbIndex()));
        map.put(Categories.ICON_TYPE, cx.getInt(Categories.ICON_TYPE.dbIndex()));
        map.put(Categories.PARENT_ID, cx.getInt(Categories.PARENT_ID.dbIndex()));
        ivew.setOnClickListener(this);
        ivew.setOnFocusChangeListener(this);
        if (position == 0){
        	ivew.requestFocusFromTouch();
        	ivew.requestFocus();
        }
	 * @param objectTag
	 * 
	 */
	public void onIconFocusChange(Map<Categories, Object> map) {
		//First, display the Name
		Log.i(TAG, "onIconFocusChange");
		//catLabel.setText(map.get(Categories.TITLE).toString());
		
		//Load the CategoryCombo
		
		final ArrayAdapter<CategoryTO> arrayAdapter = new ArrayAdapter<CategoryTO>(
				this, android.R.layout.simple_spinner_item, 
				adapter.getSubCategories());
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) this.findViewById(R.id.ChooseSubCat)).setAdapter(arrayAdapter);
		
		//Toast.makeText(this.getApplication(), map.get(Categories.TITLE).toString(), Toast.LENGTH_SHORT).show();
		//Load the Subcategory list on the combo
	}

	

}
