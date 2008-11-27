/**
 * 
 */
package org.avelino.mobile.android.budgetfrik;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.avelino.mobile.android.budgetfrik.Utils.Clause;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public final class CostDetailsListener implements
		DialogInterface.OnClickListener {
	private static final NumberFormat NUMBER_INSTANCE = DecimalFormat.getNumberInstance();
	@SuppressWarnings("unused")
	private static final String TAG = "CostDetailsListener";
	/**
	 * 
	 */
	private final IEntryEditor adapter;
	private EntryTO entry;

	/**
	 * @param adapter
	 */
	CostDetailsListener(IEntryEditor adapter) {
		this.adapter = adapter;
	}
	
	
	CostDetailsListener(IEntryEditor adapter, EntryTO entry) {
		this.adapter = adapter;
		this.entry = entry;
	}

	public void onClick(DialogInterface di,
			int whichButton) {
		final Spinner subcat = (Spinner) dialog.findViewById(R.id.ChooseSubCat);
		final Spinner currency = (Spinner) dialog.findViewById(R.id.ChooseCurrency);
		final EditText cost = (EditText) dialog.findViewById(R.id.Edit_Value);
		final EditText notes = (EditText) dialog.findViewById(R.id.Edit_Notes);
		final CategoryTO catTo = (CategoryTO) subcat.getAdapter().getItem(subcat.getSelectedItemPosition());
		final CurrencyTO currTo = (CurrencyTO)currency.getAdapter().getItem(currency.getSelectedItemPosition());
		final String noteVal = notes.getText().toString();
		try {
			final float costVal = NUMBER_INSTANCE.parse(cost.getText().toString()).floatValue();	
			if (entry == null){
			adapter.insertEntry(new EntryTO(
					catTo, 
					currTo,
					costVal, 
					noteVal));
			} else {
				entry.setCat(catTo);
				entry.setCurr(currTo);
				entry.setCost(costVal);
				entry.setNotes(noteVal);
				adapter.updateEntry(entry);
			}
			Toast.makeText(dialog.getContext(), "Expense Saved", Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			Log.w("CastDetailListener", "Parsing cost value" ,e);
			Toast.makeText(((Dialog)di).getContext(), "Invalid value in Expense Amount", Toast.LENGTH_LONG).show();
		}
	}

	private AlertDialog dialog;
	private Button okBtn;
	public void setAlertDialog(final AlertDialog dialog){
		this.dialog = dialog;
	}
	public void	onPrepare() {
		final ViewGroup layoutSuperContainer = (ViewGroup) dialog.findViewById(R.id.enterdetailsLayout).getRootView();
		okBtn = Utils.findViewInHierarchy(layoutSuperContainer, Button.class, new Clause<Button, Boolean>(){
		
		public Boolean evaluate(Button k) {
		return dialog.getContext().getText(android.R.string.ok).equals(k.getText());
		}});
		final EditText textView = (EditText)layoutSuperContainer.findViewById(R.id.Edit_Value);
		if (okBtn != null){
			okBtn.setEnabled(isContentAmountParseable(textView.getText()));
		}
		
		textView.setKeyListener( new DigitsKeyListener(false,true){

			@Override
			public boolean onKeyUp(View view, Editable content, int keyCode,
					KeyEvent event) {
				
				final boolean onKeyUp = super.onKeyUp(view, content, keyCode, event);
				notifyCostTextChange(content);
				return onKeyUp;
			}
			
		});
		
	}

	private static boolean isContentAmountParseable(String text) {
		try {
			NUMBER_INSTANCE.parse(text);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}


	public void notifyCostTextChange(Editable content) {
		okBtn.setEnabled(isContentAmountParseable(content));
	}

	private static boolean isContentAmountParseable(Editable content){
		return isContentAmountParseable(content.toString());
	}
	
	public void setEntry(EntryTO entry) {
		this.entry = entry;
	}
}