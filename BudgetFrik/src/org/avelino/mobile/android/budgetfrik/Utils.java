package org.avelino.mobile.android.budgetfrik;

import java.text.NumberFormat;

/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class Utils {
	private static final NumberFormat NUMBER_WEEK = NumberFormat.getNumberInstance();
	static{
		NUMBER_WEEK.setMinimumIntegerDigits(2);
		NUMBER_WEEK.setMaximumIntegerDigits(2);
	}
	
	private Utils(){}
	
	public static String convertFromJavaWeekToSqliteWeek(String yyyy_ww){
		String [] split = yyyy_ww.split("-");
		return split[0] + "-" + NUMBER_WEEK.format(Integer.parseInt(split[1])-1);
	}
}
