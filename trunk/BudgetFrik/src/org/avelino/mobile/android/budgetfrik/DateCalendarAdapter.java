/**
 * 
 */
package org.avelino.mobile.android.budgetfrik;

import static org.avelino.mobile.android.budgetfrik.EntryTO.SIMPLE_DATE_FORMAT;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.avelino.mobile.android.budgetfrik.DBHelper.Categories;
import org.avelino.mobile.android.budgetfrik.DBHelper.CostQuery;
import org.avelino.mobile.android.budgetfrik.DBHelper.ReportEntry;
import org.avelino.mobile.android.budgetfrik.DateHelper.TimeUnits;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
class DateCalendarAdapter extends BaseExpandableListAdapter implements IEntryEditor{
	




	/**
	 * 
	 */
	private final CalendarActivity activity;

	private static final String TAG = "DateCalendarAdapter";
	
	private DateHelper dateHelper = new DateHelper();
	private DBHelper helper;
	private Map<Date, List<Integer>> groups = new HashMap<Date, List<Integer>>();
	private Map<Integer, EntryTO> children = new HashMap<Integer, EntryTO>();
	private Date[] positions;


	private EntryTO activeEntry;

	private final SQLiteDatabase db;
	
	public  DateCalendarAdapter(CalendarActivity calendarActivity, Date date, int back, DBHelper helper){
		this.activity = calendarActivity;
		try {
			dateHelper.getLatest().setTime(SIMPLE_DATE_FORMAT.parse(SIMPLE_DATE_FORMAT.format(date)));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		dateHelper.getEarliest().setTime(dateHelper.getLatest().getTime());
		dateHelper.getEarliest().add(dateHelper.getTimeUnit().getUnit(), -1*back);
		this.helper = helper;
		positions = new Date[back];
		db = helper.getReadableDatabase();
		setDays();
		
	}
	
	private void setDays() {
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(dateHelper.getLatest().getTime());
		groups.clear();
		children.clear();
		for (int i = positions.length-1; i >= 0; i--) {
			positions[i] = tmp.getTime();
			groups.put(positions[i], new ArrayList<Integer>());
			tmp.add(dateHelper.getTimeUnit().getUnit(), -1);
		}
		query();
	}

	
	
	/**
	 * Bug in the query, is doing a strange comparision of date, probably due the string nature of them
	 */
	private void query() {
		Cursor cursor = helper.queryCostEntryDate(db, CostQuery.BETWEEN_DATES, new String[]{
							CostQuery.BETWEEN_DATES.getFormat().format(dateHelper.getLatest().getTime()),
							CostQuery.BETWEEN_DATES.getFormat().format(dateHelper.getEarliest().getTime())}, 
						false);
		Date parsedDate = new Date();
		Log.d(TAG+".query", "Returned Rows:" + cursor.getCount() );
		if (cursor.getCount() > 0 ){
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				try {
					parsedDate = SIMPLE_DATE_FORMAT.parse(cursor.getString(ReportEntry.DATE.dbIndex()));
				} catch (ParseException e) {
					Log.w(TAG+".query", "Error while parsing date, using NOW", e);
				}
				final CurrencyTO entryCurr = new CurrencyTO(
						cursor.getString(ReportEntry.SYMBOL.dbIndex()),
						cursor.getInt(ReportEntry.CURRENCY_ID.dbIndex()),
						cursor.getFloat(ReportEntry.EXCHANGE.dbIndex()),
						cursor.getInt(ReportEntry.BASE.dbIndex()),
						cursor.getString(ReportEntry.MNEMONIC.dbIndex()));
				final CategoryTO cat = new CategoryTO(
						cursor.getString(ReportEntry.TITLE.dbIndex()),
						cursor.getInt(ReportEntry.CATEGORY_ID.dbIndex()),
						cursor.getInt(ReportEntry.PARENT_CATEGORY_ID.dbIndex()),
						cursor.getString(ReportEntry.ICON.dbIndex()),
						cursor.getInt(ReportEntry.ICON_TYPE.dbIndex())
						);
				final EntryTO entry = new EntryTO(
						cursor.getInt(ReportEntry.ENTRY_ID.dbIndex()),
						cat, entryCurr,
						cursor.getFloat(ReportEntry.COST.dbIndex()),
						cursor.getString(ReportEntry.NOTES.dbIndex())
						, parsedDate);
				children.put(entry.getId(), entry);
				
				getGroupInPeriod(parsedDate).add(entry.getId());
				
			}
		}
		cursor.close();
	}

	private List<Integer> getGroupInPeriod(Date parsedDate) {
		List<Integer> result = null;
		Calendar target = Calendar.getInstance();
		target.setTime(parsedDate);
		switch (dateHelper.getTimeUnit()){
		case Day:
			result = searchDate(target, Calendar.DAY_OF_YEAR);
			break;
		case Week:
			result = searchDate(target, Calendar.WEEK_OF_YEAR);
			break;
		case Month:
			result = searchDate(target, Calendar.MONTH);
			break;
		case Year:
			result = searchDate(target, Calendar.YEAR);
			break;

		}
		
		return result;
	}

	private List<Integer> searchDate(Calendar target, int datefield) {
		List<Integer> result = new ArrayList<Integer>();
		Calendar tmp = Calendar.getInstance();
		for (Date weekDate : groups.keySet()) {
			tmp.setTime(weekDate);
			if (target.get(datefield) == tmp.get(datefield)){
				result = groups.get(weekDate);
				break;
			}
		}
		return result;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groups.get(positions[groupPosition]).get(childPosition));
	}

	public long getChildId(int groupPosition, int childPosition) {
		return groups.get(positions[groupPosition]).get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return groups.get(positions[groupPosition]).size();
	}

	public Object getGroup(int groupPosition) {
		final CurrencyTO defaultCurrency = getDefaultCurrency();
		return getDateString(groupPosition) + " - " +
				EntryTO.CURRENCY_FORMAT.format(sumarizeAndConvert(groups.get(positions[groupPosition]), defaultCurrency)) + " " +
					defaultCurrency.getMnemonic() ;
	}

	private String getDateString(int groupPosition) {
		String dateStr = "Unknown";
		switch (dateHelper.getTimeUnit()){
		case Day:
			dateStr = CalendarActivity.DATE_INSTANCE.format(positions[groupPosition]);
			break;
		case Week:
			Calendar targetDate = Calendar.getInstance();
			targetDate.setTime(positions[groupPosition]);
			targetDate.set(Calendar.DAY_OF_WEEK, targetDate.getMinimum(Calendar.DAY_OF_WEEK));
			dateStr = CalendarActivity.SHORT_DATE_INSTANCE.format(targetDate.getTime());
			targetDate.set(Calendar.DAY_OF_WEEK, targetDate.getMaximum(Calendar.DAY_OF_WEEK));
			dateStr = dateStr + " - " + CalendarActivity.SHORT_DATE_INSTANCE.format(targetDate.getTime());
			break;
		case Month:
			targetDate = Calendar.getInstance();
			targetDate.setTime(positions[groupPosition]);
			dateStr = CalendarActivity.MONTH_YEAR_INSTANCE.format(targetDate.getTime());
			break;
		case Year:
			targetDate = Calendar.getInstance();
			targetDate.setTime(positions[groupPosition]);
			dateStr = CalendarActivity.YEAR_INSTANCE.format(targetDate.getTime());
			break;
			
		}
		return dateStr;
	}

	private float sumarizeAndConvert(List<Integer> list,
										CurrencyTO defaultCurrency) {
		float total = 0.0f;
		final List<CurrencyTO> allCurrencies = this.activity.getCurrencies();
		for (Integer childId : list) {
			EntryTO entry = children.get(childId);
			total += CurrencyTO.convertToCurrency(entry.getCost(), entry.getCurr(), defaultCurrency, allCurrencies);
		}
		return total;
	}

	private CurrencyTO getDefaultCurrency() {
		int id = FrikPreferencesActivity.PreferenceManager.getDefaultCurrency();
		return this.activity.getCurrencies().get(this.activity.getCurrencies().indexOf(new CurrencyTO(null, id, id, id, null)));
	}

	public int getGroupCount() {
		return positions.length;
	}

	public long getGroupId(int groupPosition) {
		return positions[groupPosition].getTime();
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, 48);

        TextView textView = new TextView(this.activity);
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        textView.setPadding(36, 0, 0, 0);
        return textView;
	}

	 
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
    	TextView textView;
		if (convertView == null){
			textView = getGenericView();
			this.activity.registerForContextMenu(textView);
		} else {
			textView = (TextView) convertView;
		}
        final EntryTO child = (EntryTO) getChild(groupPosition, childPosition);
		textView.setText(child.toString());
		textView.setTag(child);
        textView.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				setActiveEntry(child);
				DateCalendarAdapter.this.activity.showDialog(CalendarActivity.EDIT_EXPENSE_DIALOG);
			}});

        return textView;
    }
    
    

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
		TextView textView;
		if (convertView == null){
			textView = getGenericView();
		} else {
			textView = (TextView) convertView;
		}
        textView.setText(getGroup(groupPosition).toString());
        if (SIMPLE_DATE_FORMAT.format(positions[groupPosition]).equals(SIMPLE_DATE_FORMAT.format(new Date()))){
        	Log.w(TAG,"Group is today:" + groupPosition);
        	textView.setTextAppearance(this.activity, android.R.style.TextAppearance_Medium);
        } else {
        	textView.setTextAppearance(this.activity, android.R.style.TextAppearance_Theme);
        }
        return textView;
    }

	void setActiveEntry(EntryTO activeEnty) {
		this.activeEntry = activeEnty;
	}

	EntryTO getActiveEntry() {
		return activeEntry;
	}

	public void insertEntry(EntryTO entryDAO) {
		EntryEditorImpl.insertEntry(entryDAO, helper);
		
	}

	public void updateEntry(EntryTO entry) {
		EntryEditorImpl.updateEntry(entry, helper);
		
	}

	public List<CategoryTO> getSubCategories() {			
		final CategoryTO category = getActiveEntry().getCat();
		final int parentId = category.getParentId();
		Cursor c = helper.getAllSubCategories(db, parentId != -1?parentId:category.getId());
		List<CategoryTO> l = new ArrayList<CategoryTO>();
		if (parentId == -1){
			l.add(category);
		}
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


	public int getCategoryIcon(CategoryTO category ){
		int icon = android.R.drawable.star_off;
		if (category.getIconType() == Categories.ICON_RES_DRAWABLE){
			try{ 
				icon = Integer.parseInt(category.getIcon()); 
			} catch (RuntimeException e){
				Log.w(TAG, "Unexpected error for resource icon: " + category.getIcon() + " for category:" + category.getId(),e);
			}
		} else if (category.getParentId() != -1 ){
			Cursor c = helper.getCategory(category.getParentId(), db);
			if (c.getCount() > 0){
				c.moveToFirst();
				if (c.getInt(Categories.ICON_TYPE.dbIndex()) == Categories.ICON_RES_DRAWABLE){
					icon = c.getInt(Categories.ICON.dbIndex());
				}
			}
			c.close();
		}
		return icon;
	}
	
	public int getActiveEntryIcon() {
		return getCategoryIcon(getActiveEntry().getCat());
	}

	public void reset() {
		setDays();
	}
	List<CategoryTO> categoryCache = null;

	
	
	public List<CategoryTO> getCategories() {
		if (categoryCache == null){
			Cursor c = helper.getAllCategories(db);
			categoryCache = new ArrayList<CategoryTO>();
			
			c.moveToFirst();
			while (!c.isAfterLast()){
				categoryCache.add(new CategoryTO(c.getString(Categories.TITLE.dbIndex()), 
										c.getInt(Categories._ID.dbIndex()), 
										c.getInt(Categories.PARENT_ID.dbIndex())));
				c.moveToNext();
			}
			c.close();
		}
		return categoryCache;
	}

	public List<CategoryTO> getSubCategories(CategoryTO selectedCat) {
		int targetCategory = selectedCat.getParentId() != -1? selectedCat.getParentId():selectedCat.getId();
		final Cursor c = helper.getAllSubCategories(db,targetCategory);
		List<CategoryTO> l = new ArrayList<CategoryTO>();
		if (selectedCat.getParentId() == -1){
			l.add(selectedCat);
		}
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

	public void deleteEntry(EntryTO tag) {
		helper.deleteEntry(tag.getId(), db);
		
	}

	public void setTimeUnit(TimeUnits tu) {
		positions = dateHelper.setTimeUnit(tu, activity.getSelectedPosition(), positions);
		Log.i(TAG, "Positions:" + positions.length);
		setDays();
		
	}

	public void moveBackward() {
		dateHelper.moveBackward(positions.length);
		setDays();
	}

	public void moveForward() {
		dateHelper.moveForward(positions.length);
		setDays();
	}

	public void gotoDate(Calendar date) {
		positions = dateHelper.setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), positions);
		Log.i(TAG, "Positions:" + positions.length);
		setDays();
	}

	

	
}