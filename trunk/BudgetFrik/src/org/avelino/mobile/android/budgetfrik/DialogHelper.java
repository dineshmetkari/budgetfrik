package org.avelino.mobile.android.budgetfrik;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DialogHelper {
	static final EmptyClickListener EMPTY_CLICK_LISTENER = new EmptyClickListener();
	
	public static AlertDialog getDataEntryDialog(final List<CurrencyTO> currencies, 
			CostDetailsListener costDetailsListener, 
			Context context,
			CalcButtonListener calcButtonListener) {
		AlertDialog dialog;
		final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View textEntryView = layoutInflater.inflate(R.layout.enterdialog, null);
		dialog = new AlertDialog.Builder(context)
				.setCustomTitle(layoutInflater.inflate(R.layout.dialogtitle, null))
				.setPositiveButton(android.R.string.ok, costDetailsListener)
				.setNegativeButton(android.R.string.cancel,EMPTY_CLICK_LISTENER)
				.setView(textEntryView).create();
		costDetailsListener.setAlertDialog(dialog);
		// Button 00,0-9,. just append the face value
		for (int btnId : CalcButtonListener.CALC_BTNS) {
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

	
	private static final class EmptyClickListener implements
	DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog,
				int whichButton) {
		}
}
}
