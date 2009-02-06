package org.avelino.mobile.android.budgetfrik;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.avelino.mobile.android.budgetfrik.DateHelper.TimeUnits;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class CalendarActivity extends ExpandableListActivity {

	static final DateFormat DATE_INSTANCE = DateFormat.getDateInstance(DateFormat.MEDIUM);
	static final DateFormat SHORT_DATE_INSTANCE = DateFormat.getDateInstance(DateFormat.SHORT);
	static final DateFormat MONTH_YEAR_INSTANCE = new SimpleDateFormat("MMMM yyyy");
	static final DateFormat YEAR_INSTANCE = new SimpleDateFormat("yyyy");
	protected static final String CURRENCIES = "org.avelino.mobile.android.budgetfrik.CalendarActivity.currency.list";
	protected static final int EDIT_EXPENSE_DIALOG = 1;
	protected static final String TAG = "CalendarActivity";
	private static final int MOVE_CATEGORY_DIALOG = 2;
	private static final int CHANGE_DATE_DIALOG = 3;
	private static final int CONFIRM_DELETE_DIALOG = 4;
	protected static final int DISPLAY_DATE_DIALOG = 5;
	
	
	private DBHelper helper;
	private List<CurrencyTO> currencies;
	private DateCalendarAdapter adapter;
	private CostDetailsListener costDetailsListener;
	private View selectedView;
	private Button nextBtn;
	private Button prevBtn;
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
		Calendar weekstart = Calendar.getInstance();
		final int maxWeekDays = weekstart.getMaximum(Calendar.DAY_OF_WEEK);
		weekstart.set(Calendar.DAY_OF_WEEK, maxWeekDays);
		adapter = new DateCalendarAdapter(this, weekstart.getTime(), maxWeekDays , helper);
		final LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.calendarhdr, null);
		//header.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		nextBtn = (Button) header.findViewById(R.id.btn_next);
		nextBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				adapter.moveForward();
				getExpandableListView().invalidate();
				setListAdapter(adapter);
			}});
		prevBtn = (Button) header.findViewById(R.id.btn_prev);
		prevBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				adapter.moveBackward();
				getExpandableListView().invalidate();
				setListAdapter(adapter);
			}});
		getExpandableListView().addHeaderView(header);
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
	
	
	List<CurrencyTO> getCurrencies() {
		return currencies;
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
			case DISPLAY_DATE_DIALOG:
				Calendar entry1 = Calendar.getInstance();
				dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Log.w(TAG,"Date Go to:" + year + "/" + monthOfYear + "/" +dayOfMonth);
						final Calendar date = Calendar.getInstance();
						date.set(year, monthOfYear, dayOfMonth);
						adapter.gotoDate(date);
						getExpandableListView().invalidateViews();
						setListAdapter(adapter);
					}
				}, entry1.get(Calendar.YEAR), entry1.get(Calendar.MONTH), entry1.get(Calendar.DAY_OF_MONTH));
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
			//Needed, don't delete :)
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private View getContextMenuView() {
		return selectedView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.calendar_menu, menu);
		MenuItem item = menu.findItem(R.id.menu_day);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				adapter.setTimeUnit(TimeUnits.Day);
				getExpandableListView().invalidateViews();
				setListAdapter(adapter);
				nextBtn.setText("Next Week");
				prevBtn.setText("Previous Week");
				return false;
			}});
		
		item = menu.findItem(R.id.menu_week);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				adapter.setTimeUnit(TimeUnits.Week);
				getExpandableListView().invalidateViews();
				setListAdapter(adapter);
				nextBtn.setText("Next Month");
				prevBtn.setText("Previous Month");
				return false;
			}});
		

		item = menu.findItem(R.id.menu_month);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				adapter.setTimeUnit(TimeUnits.Month);
				getExpandableListView().invalidateViews();
				setListAdapter(adapter);
				nextBtn.setText("Next Year");
				prevBtn.setText("Previous Year");
				return false;
			}});

		item = menu.findItem(R.id.menu_year);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				adapter.setTimeUnit(TimeUnits.Year);
				getExpandableListView().invalidateViews();
				setListAdapter(adapter);
				nextBtn.setText("Next 5 Years");
				prevBtn.setText("Previous 5 Years");
				return false;
			}});
		
		item = menu.findItem(R.id.menu_gotodate);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				showDialog(DISPLAY_DATE_DIALOG);
				return false;
			}});
		return true;
	}
	
}
