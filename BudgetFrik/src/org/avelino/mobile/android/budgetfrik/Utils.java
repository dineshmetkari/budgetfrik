package org.avelino.mobile.android.budgetfrik;

import java.text.NumberFormat;
import java.util.Stack;

import android.view.View;
import android.view.ViewGroup;

/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class Utils {
	private static final NumberFormat NUMBER_WEEK = NumberFormat.getNumberInstance();
	private static final String TAG = "BudgetFrik.Utils";
	static{
		NUMBER_WEEK.setMinimumIntegerDigits(2);
		NUMBER_WEEK.setMaximumIntegerDigits(2);
	}
	
	private Utils(){}
	
	public static String convertFromJavaWeekToSqliteWeek(String yyyy_ww){
		String [] split = yyyy_ww.split("-");
		return split[0] + "-" + NUMBER_WEEK.format(Integer.parseInt(split[1])-1);
	}
	
	
	public interface Clause<K,V>{
		public V evaluate(K k);
	}
	
	public static <K extends View>  K findViewInHierarchy(ViewGroup parent, final Class<K> claz,
																final Clause<K, Boolean> clause){
		
		K result = null;
		Stack<ViewGroup> parents = new Stack<ViewGroup>();
		while (result == null ){
			int childCount = parent.getChildCount();
			for (int i = 0; i< childCount; i++){
				final View childAt = parent.getChildAt(i);
				if (claz.isInstance(childAt) && 
					clause.evaluate(claz.cast(childAt))){
					result =  claz.cast(childAt);
					break;
				}
				if (childAt instanceof ViewGroup){
					parents.push((ViewGroup) childAt);
				}
			}
			if (!parents.isEmpty()){
				parent = parents.pop();
			} else {
				break;
			}
		}
		return result;
	}
	
}
