/**
 * 
 */
package org.avelino.mobile.android.budgetfrik;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.avelino.mobile.android.budgetfrik.DBHelper.Categories;
import org.avelino.mobile.android.budgetfrik.DBHelper.CostQuery;
import org.avelino.mobile.android.budgetfrik.DBHelper.Currencies;
import org.avelino.mobile.android.budgetfrik.DBHelper.ReportEntry;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class IconGridAdapter extends BaseAdapter implements OnClickListener, OnFocusChangeListener, IEntryEditor {
    
    
    private static final LayoutParams IMG_BTN_LAYOUT_PARAMS = new GridView.LayoutParams(80, 80);
	private static final String TAG = "IconGridAdapter";
	/**
	 * 
	 */
	private final Activity gridActivity;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private View activeObj;
	private List<CurrencyTO> currencyCache;

	/**
	 * @param test
	 */
	IconGridAdapter(Activity act) {
		gridActivity = act;
	}

	public void open(){
		dbHelper = new DBHelper(gridActivity);
		db = dbHelper.getReadableDatabase();
	}

	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
    	ImageButton ivew;
    	parent.setFocusable(false);
        if (convertView == null) {
            ivew = new ImageButton(gridActivity){
            	{
            		setTag(new LinkedHashMap<Categories,Object>());
            		setLayoutParams(IMG_BTN_LAYOUT_PARAMS);
            		setScaleType(ImageView.ScaleType.FIT_CENTER);//CENTER_INSIDE, FIT_CENTER
            		}

            	
            	
            };
            
        } else {
            ivew = (ImageButton) convertView;
        }

  //      ResolveInfo info = mApps.get(position);
        //i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
        Cursor cx = fetchData();
        cx.moveToPosition(position);
        Map<Categories,Object> map = (Map<Categories, Object>) ivew.getTag();
        ivew.setImageResource(
        					cx.getInt(
        							Categories.ICON.dbIndex()));
        map.put(Categories._ID, cx.getInt(Categories._ID.dbIndex()));
        map.put(Categories.TITLE, cx.getString(Categories.TITLE.dbIndex()));
        map.put(Categories.ICON, cx.getString(Categories.ICON.dbIndex()));
        map.put(Categories.ICON_TYPE, cx.getInt(Categories.ICON_TYPE.dbIndex()));
        map.put(Categories.PARENT_ID, cx.getInt(Categories.PARENT_ID.dbIndex()));
        ivew.setOnClickListener(this);
        ivew.setOnFocusChangeListener(this);
        if (position == 0){
        	ivew.requestFocusFromTouch();
        	ivew.requestFocus();
        }
        return ivew;
    }


    public final int getCount() {
        return fetchData().getCount();
    }

    public final Object getItem(int position) {
    	//ImageViews created in getView
    	Cursor itm = fetchData();
    	itm.moveToPosition(position);
        return itm.getString(Categories.TITLE.dbIndex());
    }

    public final long getItemId(int position) {
    	Cursor itm = fetchData();
    	itm.moveToPosition(position);
        return itm.getLong(Categories._ID.dbIndex());
    }


	public void onClick(View iview) {
		//iview.startAnimation(iview.getAnimation());
		ImageButton view = (ImageButton) iview;
		this.activeObj = view;
		gridActivity.showDialog(BudgetFrikActivity.ENTRY_DETAILS_DIALOG);
	}


	@SuppressWarnings("unchecked")
	public void onFocusChange(View iview, boolean hasFocus) {
		Log.d("FocusChange",iview + ":" + hasFocus);
		if (hasFocus){
			ImageButton view = (ImageButton) iview;
			TextView mLblGrid = (TextView) gridActivity.findViewById(R.id.myGridLabel);
			mLblGrid.setText(((Map <Categories,Object>)view.getTag()).get(Categories.TITLE).toString());
		} else {
			TextView mLblGrid = (TextView) gridActivity.findViewById(R.id.myGridLabel);
			mLblGrid.setText("");
		}
		
	}
	
	private Cursor fetchData(){
		if (cursor == null){
			cursor = dbHelper.getAllCategories(db);
		}
		return cursor;
	}

	public View getActiveIcon() {
		return activeObj;
	}

	@SuppressWarnings("unchecked")
	public List<CategoryTO> getSubCategories() {
		if (activeObj == null){
			return null;
		}
		final Cursor c = dbHelper.getAllSubCategories(db,(Integer)((Map <Categories,Object>)activeObj.getTag()).get(Categories._ID));
		List<CategoryTO> l = new ArrayList<CategoryTO>();
		l.add(new CategoryTO(((Map <Categories,Object>)activeObj.getTag()).get(Categories.TITLE).toString(), 
								(Integer)((Map <Categories,Object>)activeObj.getTag()).get(Categories._ID), 
								(Integer)((Map <Categories,Object>)activeObj.getTag()).get(Categories.PARENT_ID)));
		c.moveToFirst();
		while (!c.isAfterLast()){
			l.add(new CategoryTO(c.getString(Categories.TITLE.dbIndex()), 
									c.getInt(Categories._ID.dbIndex()), 
									c.getInt(Categories.PARENT_ID.dbIndex())));
			c.moveToNext();
		}
		c.close();
		return l;
	}

	public List<CurrencyTO> getCurrencies() {
		if (currencyCache == null){
			Cursor c = dbHelper.getAllCurrencies(db);
			List<CurrencyTO> l = new ArrayList<CurrencyTO>();
			c.moveToFirst();
			while (!c.isAfterLast()){
				l.add(new CurrencyTO(c.getString(Currencies.SYMBOL.dbIndex()), 
						c.getInt(Currencies._ID.dbIndex()),
						c.getFloat(Currencies.EXCHANGE.dbIndex()),
						c.getInt(Currencies.BASE.dbIndex()),
					    c.getString(Currencies.MNEMONIC.dbIndex())));
				c.moveToNext();
			}
			c.close();
			currencyCache = l;
		}
		return currencyCache;
	}

	public void close() {
		if (cursor != null ){
			cursor.close();
		}
		if (db != null ){
			db.close();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.avelino.mobile.android.budgetfrik.IEntryEditor#insertEntry(org.avelino.mobile.android.budgetfrik.EntryTO)
	 */
	public void insertEntry(EntryTO entryDAO) {
		EntryEditorImpl.insertEntry(entryDAO, dbHelper);
	}

	
	public void updateEntry(EntryTO entry){
		EntryEditorImpl.updateEntry(entry, dbHelper);
	}
	
	public Report getDefaultReport(int defaultCurrency) {
		CurrencyTO defCurr = null;
		List<CurrencyTO> currs = getCurrencies();
		for (CurrencyTO curr : currs) {
			if (curr.getId() == defaultCurrency){
				defCurr = curr;
				break;
			}
		}
		if (defCurr == null){
			throw new IllegalArgumentException("Currency with ID:" + defaultCurrency + " not found.");
		}
		Report rep = new DefaultReport(defCurr);
		
		return rep;
	}

	public Cursor queryCostEntryDate(CostQuery costQuery, String[] date) {
		
		return dbHelper.queryCostEntryDate(db,costQuery, date, true);
	}

	public ReportEntryDAO sumarizeAndConvert(Cursor cursor,
								CurrencyTO targetCurr, List<CurrencyTO> currs) {
		float totalCost = 0.0f;
		Date parsedDate = new Date();
		Log.d(TAG+".sumarizeAndConvert", "Returned Rows:" + cursor.getCount() );
		if (cursor.getCount() > 0 ){
			try {
				cursor.moveToFirst();
				parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(ReportEntry.DATE.dbIndex()));
					
			} catch (ParseException e) {
				Log.w(TAG+".sumarizeAndConvert", "Error while parsing date, using NOW", e);
			}
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				final CurrencyTO entryCurr = new CurrencyTO(
						cursor.getString(ReportEntry.SYMBOL.dbIndex()),
						cursor.getInt(ReportEntry.CURRENCY_ID.dbIndex()),
						cursor.getFloat(ReportEntry.EXCHANGE.dbIndex()),
						cursor.getInt(ReportEntry.BASE.dbIndex()),
						cursor.getString(ReportEntry.MNEMONIC.dbIndex()));
				final float orgCost = cursor.getFloat(ReportEntry.COST.dbIndex());
				Log.d(TAG+".sumarizeAndConvert", "Row:" + cursor.getPosition() + ", cost:" + orgCost + " Curr:" + entryCurr.getId());
				totalCost  += CurrencyTO.convertToCurrency(orgCost, entryCurr, targetCurr, currs);
			}
		}
		cursor.close();
		return new ReportEntryDAO(totalCost,targetCurr,parsedDate);
	}

	public void clearCache() {
		currencyCache = null;
		
	}

	
}