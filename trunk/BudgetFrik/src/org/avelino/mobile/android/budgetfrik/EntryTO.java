package org.avelino.mobile.android.budgetfrik;

import java.text.NumberFormat;
import java.text.ParseException;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class EntryTO {

	
	public EntryTO(CategoryTO cat, CurrencyTO curr, String cost, String notes) throws ParseException {
		this.cat = cat;
		this.curr = curr;
		this.cost = NumberFormat.getNumberInstance().parse(cost).floatValue();
		this.notes = notes;

	}

	
	public EntryTO(int id, CategoryTO cat, CurrencyTO curr, float cost, String notes) {
		this.id = id;
		this.cat = cat;
		this.curr = curr;
		this.cost = cost;
		this.notes = notes;
	}


	private CategoryTO cat;
	private CurrencyTO curr;
	private float cost;
	private String notes;
	private int id;
	
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
		return new StringBuilder(cat.toString()).append(':').append(curr.toString()).append(cost).toString();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
}
