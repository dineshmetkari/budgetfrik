package org.avelino.mobile.android.budgetfrik;

import java.util.Calendar;
import java.util.Date;

/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class ReportEntryDAO {
	private float cost;
	private CurrencyTO currency;
	private int year;
	private int month;
	private int day;
	private int week;
	

	public ReportEntryDAO(float cost, CurrencyTO currency, Date date) {
		super();
		this.cost = cost;
		this.currency = currency;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.year = cal.get(Calendar.YEAR);
		this.month = cal.get(Calendar.MONTH);;
		this.day = cal.get(Calendar.DAY_OF_MONTH);
		this.week = cal.get(Calendar.WEEK_OF_YEAR);
	}
	
	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public CurrencyTO getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyTO currency) {
		this.currency = currency;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}
	
	public Date getDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal.getTime();
	}

	
}
