package org.avelino.mobile.android.budgetfrik;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class EntryTO {

	
	public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public EntryTO(CategoryTO cat, CurrencyTO curr, String cost, String notes) throws ParseException {
		this.cat = cat;
		this.curr = curr;
		this.cost = CURRENCY_FORMAT.parse(cost).floatValue();
		this.notes = notes;

	}

	public EntryTO(CategoryTO cat, CurrencyTO curr, float cost, String notes) throws ParseException {
		this.cat = cat;
		this.curr = curr;
		this.cost = cost;
		this.notes = notes;

	}
	
	public EntryTO(int id, CategoryTO cat, CurrencyTO curr, float cost, String notes) {
		this.id = id;
		this.cat = cat;
		this.curr = curr;
		this.cost = cost;
		this.notes = notes;
	}

	public EntryTO(int id, CategoryTO cat, CurrencyTO curr, float cost, String notes, Date date) {
		this(id,cat,curr,cost,notes);
		this.date = date;
	}

	
	private CategoryTO cat;
	private CurrencyTO curr;
	private float cost;
	private String notes;
	private int id;
	private Date date;
	
	public CategoryTO getCat() {
		return cat;
	}


	public void setCat(CategoryTO cat) {
		this.cat = cat;
	}


	public CurrencyTO getCurr() {
		return curr;
	}


	public void setCurr(CurrencyTO curr) {
		this.curr = curr;
	}


	public float getCost() {
		return cost;
	}


	public void setCost(float cost) {
		this.cost = cost;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String toString(){
		return new StringBuilder(cat.toString()).append(" - ").append(CURRENCY_FORMAT.format(cost)).append(' ').append(curr.getMnemonic()).toString();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}
}
