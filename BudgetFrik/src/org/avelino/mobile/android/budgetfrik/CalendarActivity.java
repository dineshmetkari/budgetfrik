package org.avelino.mobile.android.budgetfrik;

import static org.avelino.mobile.android.budgetfrik.EntryTO.SIMPLE_DATE_FORMAT;

import java.text.DateFormat;
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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class CalendarActivity extends ExpandableListActivity {

	private static final DateFormat DATE_INSTANCE = DateFormat.getDateInstance(DateFormat.MEDIUM);
	protected static final String CURRENCIES = "org.avelino.mobile.android.budgetfrik.CalendarActivity.currency.list";
	protected static final int EDIT_EXPENSE_DIALOG = 1;
	protected static final String TAG = "CalendarActivity";
	private static final int MOVE_CATEGORY_DIALOG = 2;
	private static final int CHANGE_DATE_DIALOG = 3;
	private static final int CONFIRM_DELETE_DIALOG = 4;
	
	private DBHelper helper;
	private List<CurrencyTO> currencies;
	private DateCalendarAdapter adapter;
	private CostDetailsListener costDetailsListener;
	private Calendar weekstart;
	private View selectedView;
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new DBHelper(this);
		currencies = ((List<CurrencyTO>) getIntent().getSerializableExtra(CURRENCIES));
		weekstart = Calendar.getInstance();
		final int maxWeekDays = weekstart.getMaximum(Calendar.DAY_OF_WEEK);
		weekstart.set(Calendar.DAY_OF_WEEK, maxWeekDays);
		adapter = new DateCalendarAdapter(weekstart.getTime(), maxWeekDays , helper);
		final Button nwk = new Button(this);
		nwk.setText("Next Week>");
		nwk.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				weekstart.add(Calendar.WEEK_OF_YEAR, 1);
				adapter.moveTo(weekstart.getTime());
				getExpandableListView().invalidate();
				setListAdapter(adapter);
			}});
		final Button pwk = new Button(this);
		pwk.setText("<Previous Week");
		pwk.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				weekstart.add(Calendar.WEEK_OF_YEAR, -1);
				adapter.moveTo(weekstart.getTime());
				getExpandableListView().invalidate();
				setListAdapter(adapter);
			}});
		getExpandableListView().addHeaderView(nwk);
		getExpandableListView().addFooterView(pwk);
		setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Modify Expense:");
        //menu.add(0, 0, 0, R.string.expandable_list_sample_action);
        menu.add(Menu.NONE, 1, Menu.NONE, "Edit");
        menu.add(Menu.NONE, 2, Menu.NONE, "Move Category");
        menu.add(Menu.NONE, 3, Menu.NONE, "Change Date");
        menu.add(Menu.NONE, 4, Menu.NONE, "Delete");
        setContextMenuView(v);
    }
    
    private void setContextMenuView(View v) {
		selectedView = v; 
		
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	Log.w(TAG, "onContextItemSelected.item:" + item);
    	ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
    	Log.w(TAG, "onContextItemSelected.info:" + info);
//    	int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
//        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
//    	Log.w(TAG, "onContextItemSelected.view:" + info.targetView);
//    	Log.w(TAG, "onContextItemSelected.group:" + groupPos);
//    	Log.w(TAG, "onContextItemSelected.child:" + childPos);
    	
    	switch(item.getItemId()){
    	//Edit
    	case 1:
    		adapter.setActiveEntry((EntryTO)getContextMenuView().getTag());
			showDialog(EDIT_EXPENSE_DIALOG);
			break;
			//Move Category
    	case 2:
    		adapter.setActiveEntry((EntryTO)getContextMenuView().getTag());
    		showDialog(MOVE_CATEGORY_DIALOG);
    		break;
    		//ShowDialog
    	case 3:
    		adapter.setActiveEntry((EntryTO)getContextMenuView().getTag());
    		showDialog(CHANGE_DATE_DIALOG);
    		break;
    		//Delete
    	case 4:
    		adapter.setActiveEntry((EntryTO)getContextMenuView().getTag());
    		showDialog(CONFIRM_DELETE_DIALOG);
    		break;    		
    	}
    	
    	return super.onContextItemSelected(item);
    }

    @Override
	protected void onDestroy() {
		helper.close();
		super.onDestroy();
	}
	
	
	private List<CurrencyTO> getCurrencies() {
		return currencies;
	}

	
	private class DateCalendarAdapter extends BaseExpandableListAdapter implements IEntryEditor{
		

		

		private static final String TAG = "DateCalendarAdapter";
		
		private Calendar latest = Calendar.getInstance();
		private Calendar earliest = Calendar.getInstance();
		private DBHelper helper;
		private Map<Date, List<Integer>> groups = new HashMap<Date, List<Integer>>();
		private Map<Integer, EntryTO> children = new HashMap<Integer, EntryTO>();
		private Date[] positions;


		private EntryTO activeEntry;

		private final SQLiteDatabase db;
		
		public  DateCalendarAdapter(Date date, int back, DBHelper helper){
			try {
				latest.setTime(SIMPLE_DATE_FORMAT.parse(SIMPLE_DATE_FORMAT.format(date)));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			earliest.setTime(latest.getTime());
			earliest.add(Calendar.DAY_OF_YEAR, -1*back);
			this.helper = helper;
			positions = new Date[back];
			db = helper.getReadableDatabase();
			setDays();
			
		}
		
		private void setDays() {
			Calendar tmp = Calendar.getInstance();
			tmp.setTime(latest.getTime());
			groups.clear();
			children.clear();
			for (int i = 0; i < positions.length; i++) {
				positions[i] = tmp.getTime();
				groups.put(positions[i], new ArrayList<Integer>());
				tmp.add(Calendar.DAY_OF_YEAR, -1);
			}
			query();
		}

		public void moveTo(Date date){
			try {
				latest.setTime(SIMPLE_DATE_FORMAT.parse(SIMPLE_DATE_FORMAT.format(date)));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			earliest.setTime(latest.getTime());
			earliest.add(Calendar.DAY_OF_YEAR, -1*(positions.length-1));
			setDays();
		}
		
		private void query() {
			Cursor cursor = helper.queryCostEntryDate(db, CostQuery.BETWEEN_DATES, new String[]{
								CostQuery.BETWEEN_DATES.getFormat().format(latest.getTime()),
								CostQuery.BETWEEN_DATES.getFormat().format(earliest.getTime())}, 
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
					if (groups.containsKey(parsedDate)){
						groups.get(parsedDate).add(entry.getId());
					} else {
						groups.put(parsedDate, new ArrayList<Integer>());
					}
				}
			}
			cursor.close();
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
			return DATE_INSTANCE.format(positions[groupPosition]) + " - " +
					EntryTO.CURRENCY_FORMAT.format(sumarizeAndConvert(groups.get(positions[groupPosition]), defaultCurrency)) + " " +
						defaultCurrency.getMnemonic() ;
		}

		private float sumarizeAndConvert(List<Integer> list,
											CurrencyTO defaultCurrency) {
			float total = 0.0f;
			final List<CurrencyTO> allCurrencies = getCurrencies();
			for (Integer childId : list) {
				EntryTO entry = children.get(childId);
				total += CurrencyTO.convertToCurrency(entry.getCost(), entry.getCurr(), defaultCurrency, allCurrencies);
			}
			return total;
		}

		private CurrencyTO getDefaultCurrency() {
			int id = FrikPreferencesActivity.PreferenceManager.getDefaultCurrency();
			return getCurrencies().get(getCurrencies().indexOf(new CurrencyTO(null, id, id, id, null)));
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

            TextView textView = new TextView(CalendarActivity.this);
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
				registerForContextMenu(textView);
			} else {
				textView = (TextView) convertView;
			}
            final EntryTO child = (EntryTO) getChild(groupPosition, childPosition);
			textView.setText(child.toString());
			textView.setTag(child);
            textView.setOnClickListener(new OnClickListener(){

				public void onClick(View arg0) {
					setActiveEntry(child);
					showDialog(EDIT_EXPENSE_DIALOG);
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
            	textView.setTextAppearance(CalendarActivity.this, android.R.style.TextAppearance_Medium);
            } else {
            	textView.setTextAppearance(CalendarActivity.this, android.R.style.TextAppearance_Theme);
            }
            return textView;
        }

		private void setActiveEntry(EntryTO activeEnty) {
			this.activeEntry = activeEnty;
		}

		private EntryTO getActiveEntry() {
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
		

		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id){
			case EDIT_EXPENSE_DIALOG:
				costDetailsListener = new CostDetailsListener(adapter);
				dialog = DialogHelper.getDataEntryDialog(getCurrencies(), costDetailsListener, this, new CalcButtonListener(costDetailsListener));
				break;
			case CHANGE_DATE_DIALOG:
				final Calendar entry = Calendar.getInstance(); 
				dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Log.w(TAG,"Date Changed:" + year + "/" + monthOfYear + "/" +dayOfMonth);
						final Calendar date = Calendar.getInstance();
						date.set(year, monthOfYear, dayOfMonth);
						if (getContextMenuView() != null){
							final EntryTO entry = (EntryTO) getContextMenuView().getTag();
							entry.setDate(date.getTime());
							adapter.setActiveEntry(entry);
							adapter.updateEntry(entry);
							Toast.makeText(CalendarActivity.this, "Expense Updated", Toast.LENGTH_SHORT);
							adapter.reset();
							getExpandableListView().invalidate();
							setListAdapter(adapter);
						}
						
					}}, entry.get(Calendar.YEAR), entry.get(Calendar.MONTH), entry.get(Calendar.DAY_OF_MONTH));
				break;
			case MOVE_CATEGORY_DIALOG:
				final ArrayAdapter<CategoryTO> catAdapter = new ArrayAdapter<CategoryTO>(
						this, android.R.layout.simple_spinner_item, adapter.getCategories());
				catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				final EntryTO entryTO = (EntryTO)getContextMenuView().getTag();
				final CategoryTO oriCategory = entryTO.getCat();
				adapter.setActiveEntry(entryTO);
				if (catAdapter.getPosition(oriCategory) > 0 ){
					catAdapter.remove(oriCategory);
					catAdapter.insert(oriCategory, 0);
				} else if (catAdapter.getPosition(oriCategory) == -1 ){
					catAdapter.insert(oriCategory, 0);
				}

//				final ListView listView = new ListView(this);
//				listView.setAdapter(catAdapter);
//				listView.setSelection(catAdapter.getPosition(oriCategory));
//				listView.setOnItemClickListener( new AdapterView.OnItemClickListener(){
//					public void onItemClick(AdapterView parent, View view, int position, long id){
//			);

				dialog = new AlertDialog.Builder(this).setTitle("Select Category"). 
								setSingleChoiceItems(catAdapter, catAdapter.getPosition(oriCategory), new DialogInterface.OnClickListener(){

									public void onClick(DialogInterface dialog, int which) {
										
										final CategoryTO selectedCat = catAdapter.getItem(which);
										adapter.getActiveEntry().setCat(selectedCat);
										dialog.dismiss();
										final ArrayAdapter<CategoryTO> subcatAdapter = new ArrayAdapter<CategoryTO>(
												CalendarActivity.this, android.R.layout.simple_spinner_item, adapter.getSubCategories(selectedCat));
										subcatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										
										new AlertDialog.Builder(CalendarActivity.this).setTitle("Select SubCategory").setSingleChoiceItems(subcatAdapter, subcatAdapter.getPosition(selectedCat), new DialogInterface.OnClickListener(){
											//Select and go
											public void onClick(DialogInterface dialog,
													int which) {
												adapter.getActiveEntry().setCat(subcatAdapter.getItem(which));
												adapter.updateEntry(adapter.getActiveEntry());
												Toast.makeText(CalendarActivity.this, "Expense Updated", Toast.LENGTH_SHORT);
												getExpandableListView().invalidateViews();
												dialog.dismiss();
											}})
											.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

												public void onClick(DialogInterface dialog,
														int which) {
													//Keep the selection from previous screen
													adapter.updateEntry(adapter.getActiveEntry());
													Toast.makeText(CalendarActivity.this, "Expense Updated", Toast.LENGTH_SHORT);
													getExpandableListView().invalidateViews();
												}})
												//No changes...
											.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

												public void onClick(DialogInterface dialog,
														int which) {
													adapter.getActiveEntry().setCat(oriCategory);
												}})
												//Restart
											.setNeutralButton("Back", new DialogInterface.OnClickListener(){

												public void onClick(DialogInterface dialog,
														int which) {
													dialog.dismiss();
													adapter.getActiveEntry().setCat(oriCategory);
													showDialog(MOVE_CATEGORY_DIALOG);
												}}).show(); //Subcategory Dialog
										
									}})						
								.create(); //Category Dialog
				break;
			case CONFIRM_DELETE_DIALOG:
				dialog = new AlertDialog.Builder(this)
								.setTitle("Delete Expense?")
								.setMessage("Are you sure you want to delete this expense?\nThis action is not undoable.")
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface dialog,
											int which) {
										adapter.deleteEntry((EntryTO)getContextMenuView().getTag());
										Toast.makeText(CalendarActivity.this, "Expense Deleted", Toast.LENGTH_SHORT);
										adapter.reset();
										getExpandableListView().invalidateViews();
										setListAdapter(adapter);
									}})
								.setNegativeButton(android.R.string.cancel, DialogHelper.EMPTY_CLICK_LISTENER)
								.create(); 
				break;
		  default:
			  dialog = super.onCreateDialog(id);
		  break;
		}
		
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch (id){
		case EDIT_EXPENSE_DIALOG:
			final EntryTO activeEntry = adapter.getActiveEntry();
			final ArrayAdapter<CategoryTO> arrayAdapter = new ArrayAdapter<CategoryTO>(
					this, android.R.layout.simple_spinner_item, adapter
							.getSubCategories());
			arrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			final CategoryTO defCategory = activeEntry.getCat();
			if (arrayAdapter.getPosition(defCategory) != 0 ){
				arrayAdapter.remove(defCategory);
				arrayAdapter.insert(defCategory, 0);
			}
			
			((Spinner) dialog.findViewById(R.id.ChooseSubCat))
					.setAdapter(arrayAdapter);

			// final Drawable drawable =
			// getResources().getDrawable(Integer.parseInt(
			// adapter.getActiveMetaData().getEntry(
			// Categories.ICON).toString()));
			// drawable.setBounds(0, 0, 32, 32);
			((ImageView) dialog.findViewById(R.id.DialogIcon)).setImageResource(adapter.getActiveEntryIcon());
			
				((EditText) dialog.findViewById(R.id.Edit_Notes)).setText(activeEntry.getNotes());
			((EditText) dialog.findViewById(R.id.Edit_Value)).setText(String.valueOf(activeEntry.getCost()));
			final ArrayAdapter<CurrencyTO> currAdapter = new ArrayAdapter<CurrencyTO>(
					this, android.R.layout.simple_spinner_item, currencies);
			final CurrencyTO defCurrency = activeEntry.getCurr();
			if (currAdapter.getPosition(defCurrency) != 0 ){
				currAdapter.remove(defCurrency);
				currAdapter.insert(defCurrency, 0);
			}
			
			Spinner currLst =  (Spinner)dialog.findViewById(R.id.LLayout2).findViewById(R.id.ChooseCurrency);
			currLst.setAdapter(currAdapter);
			currAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			costDetailsListener.setEntry(activeEntry);
			costDetailsListener.onPrepare();
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){

				public void onDismiss(DialogInterface dialog) {
					CalendarActivity.this.getExpandableListView().invalidateViews();
				}}); 
			break;
		case CHANGE_DATE_DIALOG:
			Calendar entry = Calendar.getInstance(); 
			entry.setTime(adapter.getActiveEntry().getDate());
			((DatePickerDialog)dialog).updateDate(entry.get(Calendar.YEAR), entry.get(Calendar.MONTH), entry.get(Calendar.DAY_OF_MONTH));
			break;
		case MOVE_CATEGORY_DIALOG:
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private View getContextMenuView() {
		return selectedView;
	}
	
}
