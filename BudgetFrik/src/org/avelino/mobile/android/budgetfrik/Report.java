package org.avelino.mobile.android.budgetfrik;

import java.util.Date;
import java.util.Map;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public interface Report {

	public abstract Map<Integer, String> getReportData(IconGridAdapter adapter, ProgressListener progressListener);

	public abstract CurrencyTO getCurrency();

	public abstract void changeCurrency(CurrencyTO dao);

	public abstract Date getStartDate();

	public abstract Date getEndDate();

	public abstract int[][] getHeadings();

}