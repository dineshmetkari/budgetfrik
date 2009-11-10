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
		View.OnClickListener {
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

	public void onClick(View view) {
		final Spinner subcat = (Spinner) view.findViewById(R.id.ChooseSubCat);
		final Spinner currency = (Spinner) view.findViewById(R.id.ChooseCurrency);
		final EditText cost = (EditText) view.findViewById(R.id.Edit_Value);
		final EditText notes = (EditText) view.findViewById(R.id.Edit_Notes);
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
			Toast.makeText(view.getContext(), "Expense Saved", Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			Log.w("CastDetailListener", "Parsing cost value" ,e);
			//TODO Fix Toast.makeText(view).getContext(), "Invalid value in Expense Amount", Toast.LENGTH_LONG).show();
		}
	}

	private View view;
	private Button okBtn;
	public void setView(final View view){
		this.view = view;
	}
	public void	onPrepare() {
		final ViewGroup layoutSuperContainer = (ViewGroup) view.findViewById(R.id.ButtonsContainer).getRootView();
		
		okBtn = Utils.findViewInHierarchy(layoutSuperContainer, Button.class, new Clause<Button, Boolean>(){
		
		public Boolean evaluate(Button k) {
			return view.getContext().getText(R.string.positive_txt).equals(k.getText());
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