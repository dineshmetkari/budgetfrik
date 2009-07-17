package org.avelino.mobile.android.budgetfrik;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US See
 * assets/license.html
 * 
 * @author Avelino Benavides
 * 
 */
public class ReportActivity extends Activity {

	private static final class EmptyClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int whichButton) {
		}
	}

	public static final String REPORT = "org.avelino.mobile.android.budgetfrik.ReportActivity.report";
	private static final int FILENAME_DIALOG = 0;
	private static final int CHANGE_CURRENCY_DIALOG = 1;
	protected static final String TAG = "ReportActivity";
	private static final OnClickListener EMPTY_CLICK_LISTENER = new EmptyClickListener();
	private Report report;
	private ReportAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defaultreport);
		final ProgressDialog dialog = new ProgressDialog(this);
		report = (Report) getIntent().getSerializableExtra(REPORT);
		adapter = new ReportAdapter(this);
		try {
			dialog.setTitle("Report in progress...");
			dialog.setMax(100);
			dialog.show();
			Map<Integer, String> reportData = report.getReportData(adapter,
					new ProgressListener() {
						final static float PROGRESS_RATE = 80 / 100;

						public void setProgress(int p) {
							dialog.setProgress((int) (p * PROGRESS_RATE));

						}
					});
			dialog.setProgress(80);
			int increment = 20 / (1 + reportData.size());
			int progress = 80;
			for (int keyid : reportData.keySet()) {
				TextView tv = (TextView) findViewById(keyid);
				tv.setText(reportData.get(keyid));
				progress += increment;
				dialog.setProgress(progress);
			}
		} finally {
			dialog.dismiss();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		final List<CurrencyTO> currencies = adapter.getCurrencies();
		switch (id) {
		case FILENAME_DIALOG:
			dialog = getFilenameDialog();
			break;
		case CHANGE_CURRENCY_DIALOG:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.menu_curr)
					.setItems(CurrencyTO.toStringArray(currencies),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Log.v(TAG, "Chosen index:" + which);
									Log.v(TAG, "Old currency:"
											+ report.getCurrency());
									report
											.changeCurrency(currencies
													.get(which));
									Log.v(TAG, "Changed currency:"
											+ report.getCurrency());
									displayReport();
								}
							}).create();
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reports_menu, menu);
		MenuItem item = menu.findItem(R.id.reports_menuitem);
		item.setIcon(android.R.drawable.ic_menu_report_image);// R.drawable.budgetfrik);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(CHANGE_CURRENCY_DIALOG);
				return false;
			}
		});

		item = menu.findItem(R.id.save_menuitem);
		item.setIcon(android.R.drawable.ic_menu_save);// R.drawable.view_icon);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(FILENAME_DIALOG);
				return false;
			}
		});
		return true;
	}

	private void displayReport() {
		final ProgressDialog dialog = new ProgressDialog(this);
		try {
			dialog.setTitle("Report in progress...");
			dialog.setMax(100);
			dialog.show();
			Map<Integer, String> reportData = report.getReportData(adapter,
					new ProgressListener() {
						final static float PROGRESS_RATE = 80 / 100;

						public void setProgress(int p) {
							dialog.setProgress((int) (p * PROGRESS_RATE));

						}
					});
			dialog.setProgress(80);
			int increment = 20 / (1 + reportData.size());
			int progress = 80;
			for (int keyid : reportData.keySet()) {
				TextView tv = (TextView) findViewById(keyid);
				tv.setText(reportData.get(keyid));
				progress += increment;
				dialog.setProgress(progress);
			}
		} finally {
			dialog.dismiss();
		}
	}

	private AlertDialog getFilenameDialog() {
		final EditText textEntryView = new EditText(this);
		textEntryView.setMaxLines(1);
		textEntryView.setId(R.id.Edit_Value);
		return new AlertDialog.Builder(this).setMessage(
				R.string.file_dialog_message).setTitle(
				R.string.file_dialog_title).setView(textEntryView)
				.setPositiveButton(android.R.string.ok, new CSVReporter())
				.setNegativeButton(android.R.string.cancel,
						EMPTY_CLICK_LISTENER).create();
	}

	private final class CSVReporter implements DialogInterface.OnClickListener {
		
		public void onClick(DialogInterface dialog, int which) {
			String filename = ((EditText) ((Dialog) dialog)
					.findViewById(R.id.Edit_Value)).getText().toString();
			int[][] heads = report.getHeadings();
			final boolean canceled[] = { false };
			final String separator = FrikPreferencesActivity.PreferenceManager.getDefaultCSVSeparator();
			
			Map<Integer, String> data = report.getReportData(adapter,
					new ProgressListener() {
						public void setProgress(int p) {
							Log.i(TAG,"Progress:" + p);
						}
					});
			ProgressDialog mProgressDialog = new ProgressDialog(
					ReportActivity.this);
			mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
			mProgressDialog.setTitle(R.string.dialog_progress);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(heads[0].length);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					canceled[0] = true;

				}
			});
			try {
				FileOutputStream fos = getApplicationContext().openFileOutput(
						filename, MODE_WORLD_WRITEABLE);
				mProgressDialog.show();
				PrintStream out = new PrintStream(fos);
				out.printf("Heading%sValue\n", separator);

				for (int j = 0; j < heads[0].length; j++) {
					if (canceled[0]) {
						mProgressDialog.setProgress(0);
						break;
					}
					mProgressDialog.setProgress(j);
					String heading = "N/A";
					String value = "N/A";
					heading = getApplicationContext().getString(heads[0][j]);
					value = data.get(heads[1][j]);
					out.printf("\"%s\"%s%s\n", heading, separator, value);
				}
				out.flush();
				fos.close();
				if (canceled[0]) {
					getApplicationContext().deleteFile(filename);
				}
				Toast.makeText(ReportActivity.this, "Report Saved", Toast.LENGTH_SHORT);
			} catch (IOException e) {
				Log.w(TAG, "Error while exporting file:" + filename, e);
				Dialog error = new AlertDialog.Builder(ReportActivity.this)
						.setMessage(
								"Unable to export report as " + filename
										+ "\nTry again.").setTitle(
								"Export Error").setIcon(
								android.R.drawable.ic_dialog_alert).create();
				error.show();
			} finally {
				mProgressDialog.dismiss();
				
			}

		}
	}

	@Override
	protected void onDestroy() {
		if (adapter != null){
			adapter.close();
		}
		super.onDestroy();
	}

}
