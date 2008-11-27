package org.avelino.mobile.android.budgetfrik;

import static org.avelino.mobile.android.budgetfrik.EntryTO.CURRENCY_FORMAT;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.avelino.mobile.android.budgetfrik.DBHelper.CostQuery;

import android.util.Log;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class DefaultReport implements Report {

	private CurrencyTO currency;
	private CurrencyTO oldCurrency;
	private final Map<Integer,String> report;
	private boolean isReportCalculated = false;
	private boolean isCurrencyChanged = false;
	private static final String TAG = "DefaultReport";
	
	
	{
		report = new HashMap<Integer,String>();
		report.put(R.id.this_d_exp_val,"N/A");
		report.put(R.id.this_w_exp_val,"N/A");
		report.put(R.id.this_m_exp_val,"N/A");
		report.put(R.id.this_y_exp_val,"N/A");
		report.put(R.id.last_d_exp_val,"N/A");
		report.put(R.id.last_w_exp_val,"N/A");
		report.put(R.id.last_m_exp_val,"N/A");
		report.put(R.id.last_y_exp_val,"N/A");
		report.put(R.id.total_exp_val,"N/A");
		report.put(R.id.currency_val,"?");
		
	}
	/**
	 * @param daos ReportEntries sorted by Date
	 * @param currency
	 */
	public DefaultReport(CurrencyTO currency){
		this.currency = currency;
	}
	
	
	


	/* (non-Javadoc)
	 * @see org.avelino.mobile.android.budgetfrik.Report#getReportData()
	 */
	public Map<Integer,String> getReportData(IconGridAdapter adapter, ProgressListener progress){
		if (!isReportCalculated){
			List<CurrencyTO> curr = adapter.getCurrencies();
			//Today's expenses
			Calendar cal = Calendar.getInstance();
			ReportEntryDAO today = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_DATE, 
					new String []{CostQuery.SIMPLE_DATE.getFormat().format(cal.getTime())}), getCurrency(), curr);
			progress.setProgress(10);
			//This Week
			ReportEntryDAO thisWk = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_WEEK, 
					new String []{Utils.convertFromJavaWeekToSqliteWeek(CostQuery.SIMPLE_WEEK.getFormat().format(cal.getTime()))}), getCurrency(), curr);
			progress.setProgress(20);
			//This month
			ReportEntryDAO thisMh = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_MONTH, 
					new String []{CostQuery.SIMPLE_MONTH.getFormat().format(cal.getTime())}), getCurrency(), curr);
			progress.setProgress(30);
			//This year
			ReportEntryDAO thisYr = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_YEAR, 
					new String []{CostQuery.SIMPLE_YEAR.getFormat().format(cal.getTime())}), getCurrency(), curr);
			progress.setProgress(40);
			//Yesterday's expenses
			cal.add(Calendar.DAY_OF_YEAR, -1);
			ReportEntryDAO yester = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_DATE, 
					new String []{CostQuery.SIMPLE_DATE.getFormat().format(cal.getTime())}), getCurrency(), curr);
			progress.setProgress(50);
			//Last Week
			cal = Calendar.getInstance();
			cal.add(Calendar.WEEK_OF_YEAR, -1);
			ReportEntryDAO lastWk = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_WEEK, 
					new String []{Utils.convertFromJavaWeekToSqliteWeek(CostQuery.SIMPLE_WEEK.getFormat().format(cal.getTime()))}), getCurrency(), curr);
			progress.setProgress(60);
			//Last month
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			ReportEntryDAO lastMh = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_MONTH, 
					new String []{CostQuery.SIMPLE_MONTH.getFormat().format(cal.getTime())}), getCurrency(), curr);
			progress.setProgress(70);
			//Last year
			cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -1);
			ReportEntryDAO lastYr = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_YEAR, 
					new String []{CostQuery.SIMPLE_YEAR.getFormat().format(cal.getTime())}), getCurrency(), curr);
			progress.setProgress(80);
			//Normalize
			ReportEntryDAO total = adapter.sumarizeAndConvert(adapter.queryCostEntryDate(
					CostQuery.SIMPLE_ALL, 
					new String []{}), getCurrency(), curr);
			progress.setProgress(90);
			report.put(R.id.this_d_exp_val,  CURRENCY_FORMAT.format(today.getCost()));
			report.put(R.id.this_w_exp_val,  CURRENCY_FORMAT.format(thisWk.getCost()));
			report.put(R.id.this_m_exp_val,  CURRENCY_FORMAT.format(thisMh.getCost()));
			report.put(R.id.this_y_exp_val,  CURRENCY_FORMAT.format(thisYr.getCost()));
			report.put(R.id.last_d_exp_val,  CURRENCY_FORMAT.format(yester.getCost()));
			report.put(R.id.last_w_exp_val,  CURRENCY_FORMAT.format(lastWk.getCost()));
			report.put(R.id.last_m_exp_val,  CURRENCY_FORMAT.format(lastMh.getCost()));
			report.put(R.id.last_y_exp_val,  CURRENCY_FORMAT.format(lastYr.getCost()));
			report.put(R.id.total_exp_val,  CURRENCY_FORMAT.format(total.getCost()));
			report.put(R.id.currency_val,getCurrency().getMnemonic());
			isReportCalculated = true;
		} else if (isCurrencyChanged){
			progress.setProgress(10);
			List<CurrencyTO> curr = adapter.getCurrencies();
			try {
				report.put(R.id.this_d_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.this_d_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(20);
				report.put(R.id.this_w_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.this_w_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(30);
				report.put(R.id.this_m_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.this_m_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(40);
				report.put(R.id.this_y_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.this_y_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(50);
				report.put(R.id.last_d_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.last_d_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(60);
				report.put(R.id.last_w_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.last_w_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(70);
				report.put(R.id.last_m_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.last_m_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(80);
				report.put(R.id.last_y_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.last_y_exp_val)).floatValue(), oldCurrency, currency, curr)));
				progress.setProgress(90);
				report.put(R.id.total_exp_val,  CURRENCY_FORMAT.format(CurrencyTO.convertToCurrency(CURRENCY_FORMAT.parse(report.get(R.id.total_exp_val)).floatValue(), oldCurrency, currency, curr)));
			} catch (ParseException e) {
				Log.w(TAG,"Error while converting currencies from text to float",e);
			}
			report.put(R.id.currency_val,getCurrency().getMnemonic());
		}
		progress.setProgress(100);
		return report;
	}
	
	/* (non-Javadoc)
	 * @see org.avelino.mobile.android.budgetfrik.Report#getCurrency()
	 */
	public CurrencyTO getCurrency(){
		return currency;
	}
	
	/* (non-Javadoc)
	 * @see org.avelino.mobile.android.budgetfrik.Report#changeCurrency(org.avelino.mobile.android.budgetfrik.CurrencyDAO)
	 */
	public void changeCurrency(CurrencyTO dao){
		oldCurrency = currency;
		currency = dao;
		isCurrencyChanged = true;
		
	}
	
	/* (non-Javadoc)
	 * @see org.avelino.mobile.android.budgetfrik.Report#getStartDate()
	 */
	public Date getStartDate(){
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.avelino.mobile.android.budgetfrik.Report#getEndDate()
	 */
	public Date getEndDate(){
		return null;
	}




	private static final int[][] headings = {{
		R.string.this_d_exp,
		R.string.last_d_exp,
		R.string.this_w_exp,
		R.string.this_w_exp,
		R.string.this_m_exp,
		R.string.this_m_exp,
		R.string.this_y_exp,
		R.string.this_y_exp,
		R.string.total_exp,
		R.string.currency_txt
	},
	{   R.id.this_d_exp_val,
		R.id.last_d_exp_val,
		R.id.this_w_exp_val,
		R.id.last_w_exp_val,
		R.id.this_m_exp_val,
		R.id.last_m_exp_val,
		R.id.this_y_exp_val,
		R.id.last_y_exp_val,
		R.id.total_exp_val,
		R.id.currency_val}
	};
	public int[][] getHeadings() {
		
		return headings;
	}




}
