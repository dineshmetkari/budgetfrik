/**
 * 
 */
package org.avelino.mobile.android.budgetfrik;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

final class CalcButtonListener implements OnClickListener {

	public static final int[] CALC_BTNS = { R.id.CalcButton0,
		R.id.CalcButton00, R.id.CalcButton01, R.id.CalcButton02,
		R.id.CalcButton03, R.id.CalcButton04, R.id.CalcButton05,
		R.id.CalcButton06, R.id.CalcButton07, R.id.CalcButton08,
		R.id.CalcButton09, R.id.CalcButtonDot };
	
	private CostDetailsListener costDetailsListener;

	public CalcButtonListener(CostDetailsListener costDetailsListener) {
		this.costDetailsListener= costDetailsListener;
	}

	public void onClick(View v) {
		final Button btn = (Button) v;
		EditText value = (EditText) v.getRootView().findViewById(
				R.id.Edit_Value);
		value.append(btn.getText());
		costDetailsListener.notifyCostTextChange(value.getText());
	}
}