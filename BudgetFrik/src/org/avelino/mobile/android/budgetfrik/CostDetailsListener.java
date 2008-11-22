/**
 * 
 */
package org.avelino.mobile.android.budgetfrik;

import java.text.ParseException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public final class CostDetailsListener implements
		DialogInterface.OnClickListener {
	/**
	 * 
	 */
	private final IconGridAdapter adapter;

	/**
	 * @param adapter
	 */
	CostDetailsListener(IconGridAdapter adapter) {
		this.adapter = adapter;
	}

	public void onClick(DialogInterface di,
			int whichButton) {
		Log.w("CostDetailsListener", "OK");
		Spinner subcat = (Spinner) dialog.findViewById(R.id.ChooseSubCat);
		Spinner currency = (Spinner) dialog.findViewById(R.id.ChooseCurrency);
		EditText cost = (EditText) dialog.findViewById(R.id.Edit_Value);
		EditText notes = (EditText) dialog.findViewById(R.id.Edit_Notes);
		final int subcatId = subcat.getSelectedItemPosition();
		Log.w("CostDetailsListener","SubcategoryValue:"+subcat.getAdapter().getItem(subcatId));
		Log.w("CostDetailsListener","CurrencyValue:"+currency.getAdapter().getItem(currency.getSelectedItemPosition()));
		Log.w("CostDetailsListener","Cost:"+cost.getText().toString());
		Log.w("CostDetailsListener","Notes:"+notes.getText().toString());
		try {
			adapter.insertEntry(new EntryTO(
					(CategoryTO)subcat.getAdapter().getItem(
											subcatId), 
					(CurrencyTO)currency.getAdapter().getItem(
											currency.getSelectedItemPosition()),
					cost.getText().toString(), 
					notes.getText().toString()));
			
		} catch (ParseException e) {
			Log.w("CastDetailListener", "Parsing cost value" ,e);
		}
	}

	private AlertDialog dialog;
	public void setAlertDialog(AlertDialog dialog) {
		this.dialog = dialog;
	}
}