package org.avelino.mobile.android.budgetfrik;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import android.util.Log;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class CurrencyTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3153865913728848761L;
	private static final String TAG = "CurrencyDAO";
	private String symbol;
	private int id;
	private int base;
	private float exchange;
	private String mnemonic;
	
	public CurrencyTO(String symbol, int id, float exchange, int base, String mnemonic) {
		this.symbol = symbol;
		this.id = id;
		this.exchange = exchange;
		this.base = base;
		this.mnemonic = mnemonic;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString(){
		return mnemonic;
	}
	
	
	
	
	public int getBase() {
		return base;
	}
	public void setBase(int base) {
		this.base = base;
	}
	public float getExchange() {
		return exchange;
	}
	
	public void setExchange(float exchange) {
		this.exchange = exchange;
	}
	
	/**
	 * This thing is broken
	 * @param cost
	 * @param sourceCurr
	 * @param targetCurr
	 * @param exchLst
	 * @return
	 */
	public static float convertToCurrency(float cost, CurrencyTO sourceCurr,
			CurrencyTO targetCurr, List<CurrencyTO> exchLst) {
		Stack<Integer> processed = new Stack<Integer>();
		final CurrencyTO tmpCurr = new CurrencyTO("",0,0.0f,0,"");
		int rounds = 0;
		while(rounds < exchLst.size() +1){
			Log.v(TAG, "Converting $"+cost+" from " + sourceCurr.getId()  + "/" + sourceCurr.getBase() + " to " + targetCurr.getId()  + "/" + targetCurr.getBase()) ;
			//Same currency ... do nothing, money is ok
			if (sourceCurr.equals(targetCurr)){
				Log.v(TAG, "Same currency: " + cost);
				break;
			}
			//Target is the base, then convert to base
			if (sourceCurr.getBase() == targetCurr.getId()){
				cost /= sourceCurr.getExchange();
				Log.v(TAG, "Target is base: " + cost);
				break;
			}
			//Source is the base, then convert from base
			if (sourceCurr.getId() == targetCurr.getBase()){
				cost *= targetCurr.getExchange();
				Log.v(TAG, "Source is base: " + cost);
				break;
			} 
			// else conver to base and see if we have more luck next round.
			tmpCurr.setId(sourceCurr.getBase());
			if (processed.contains(tmpCurr.getId())){
				throw new RuntimeException("Cannot convert " + cost + " " + sourceCurr + " to " + targetCurr + " due to a cyclic reference in the currency database.");
			}
			//Conver to the base
			cost /= sourceCurr.getExchange();
			Log.v(TAG, "Convert to base and retry: " + cost);
			//Change the source
			tmpCurr.setId(sourceCurr.getBase());
			sourceCurr = exchLst.get(exchLst.indexOf(tmpCurr));
			
			processed.push(sourceCurr.getId());
			rounds++;
		}
		return cost;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyTO other = (CurrencyTO) obj;
		if (id != other.id)
			return false;
		return true;
	}
	public String getMnemonic() {
		return mnemonic;
	}
	public static CharSequence[] toStringArray(List<CurrencyTO> currencies) {
		CharSequence[] seq = new CharSequence[currencies.size()];
		
		for (int i = 0; i < seq.length; i++) {
			seq[i] = currencies.get(i).getMnemonic();
		}
		return seq;
	}
	public static CharSequence[] toStringValueArray(List<CurrencyTO> currencies) {
		CharSequence[] seq = new CharSequence[currencies.size()];
		
		for (int i = 0; i < seq.length; i++) {
			seq[i] = String.valueOf(currencies.get(i).getId());
		}
		return seq;
	}
}
