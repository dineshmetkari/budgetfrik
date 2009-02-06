package org.avelino.mobile.android.budgetfrik;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 * This class helps open, create, and upgrade the database file.
 */
public class DBHelper extends SQLiteOpenHelper {

	 	private static final String TAG = "DBHelper";

	    private static final String DATABASE_NAME = "budgetfrik_1_0.db";
	    private static final int DATABASE_VERSION = 2;
	    private static final String CATEGORY_TABLE_NAME = "category";
	    private static final String CURRENCY_TABLE_NAME = "currency";
	    private static final String ENTRY_TABLE_NAME = "cost_entry";
	    private static final String AUTHORITY = "org.avelino.mobile.BudgetFrik";
	    /**
	     * LinkedHashMap preserves insertion order. We expect them to have the same order ALWAYS.
	     * @see LinkedHashMap
	     */
	    private static final Map<String,String> CATEGORY_MAP = new LinkedHashMap<String,String>();
	    private static final Map<String,String> CURRENCY_MAP = new LinkedHashMap<String,String>();
	    
	    private final Context ctxt;
	    
    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctxt = context;
        //Log.d(TAG, "Helper instantiated");
        //onUpgrade(getWritableDatabase(), 0, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d(TAG, "Creating Database");
    	Log.d(TAG, "Creating Category Table");
        db.execSQL("CREATE TABLE " + CATEGORY_TABLE_NAME + " ("
                + Categories._ID + " INTEGER PRIMARY KEY,"
                + Categories.TITLE + " TEXT NOT NULL,"
                + Categories.ICON + " TEXT NOT NULL,"
                + Categories.ICON_TYPE + " INTEGER,"
                + Categories.RANK + " INTEGER,"
                + Categories.PARENT_ID + " INTEGER,"
                + Categories.LAST_USED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
                + ");");
        Log.d(TAG, "Creating Currency Table");
        db.execSQL("CREATE TABLE " + CURRENCY_TABLE_NAME + " ("
                + Currencies._ID + " INTEGER PRIMARY KEY,"
                + Currencies.SYMBOL + " TEXT NOT NULL,"
                + Currencies.MNEMONIC + " TEXT NOT NULL,"
                + Currencies.EXCHANGE + " REAL,"
                + Currencies.BASE + " INTEGER REFERENCES " + CURRENCY_TABLE_NAME + "(" + Currencies._ID + ") ON DELETE RESTRICT"
                + ");");
        Log.d(TAG, "Creating Entry Table");
        db.execSQL("CREATE TABLE " + ENTRY_TABLE_NAME + " ("
                + Entries._ID + " INTEGER PRIMARY KEY,"
                + Entries.COST + " REAL,"
                + Entries.NOTES + " TEXT NOT NULL,"
                + Entries.DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + Entries.CATEGORY + " INTEGER REFERENCES " + CATEGORY_TABLE_NAME + "(" + Categories._ID + ") ON DELETE RESTRICT,"
                + Entries.CURRENCY + " INTEGER REFERENCES " + CURRENCY_TABLE_NAME + "(" + Currencies._ID + ") ON DELETE RESTRICT"
                + ");");

        Log.d(TAG, "Inserting Data");
        
        Map<Integer,Long> catIds = new HashMap<Integer,Long>();
        InsertHelper ihlp = new InsertHelper(db,CATEGORY_TABLE_NAME);
        ContentValues cvs = new ContentValues();
        Resources sysres = ctxt.getResources();//Resources.getSystem();
        int rank = 0;
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.arts));
        cvs.put(Categories.ICON.columnName(), R.drawable.arts);
        cvs.put(Categories.ICON_TYPE.columnName(), Categories.ICON_RES_DRAWABLE);
        cvs.put(Categories.RANK.columnName(), rank++);
        cvs.put(Categories.PARENT_ID.columnName(), -1);
        catIds.put(R.array.arts, ihlp.insert(cvs));
        
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.bills));
        cvs.put(Categories.ICON.columnName(), R.drawable.bills);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.bills, ihlp.insert(cvs));
		
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.coffee));
        cvs.put(Categories.ICON.columnName(), R.drawable.coffee);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.coffee, ihlp.insert(cvs));
        
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.games));
        cvs.put(Categories.ICON.columnName(), R.drawable.games);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.games, ihlp.insert(cvs));
        
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.news));
        cvs.put(Categories.ICON.columnName(), R.drawable.news);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.news, ihlp.insert(cvs));
        
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.tools));
        cvs.put(Categories.ICON.columnName(), R.drawable.tools);
        cvs.put(Categories.RANK.columnName(), rank++);
        cvs.put(Categories.PARENT_ID.columnName(), -1);	
        catIds.put(R.array.tools, ihlp.insert(cvs));
        
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.tv));
        cvs.put(Categories.ICON.columnName(), R.drawable.tv);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.tv, ihlp.insert(cvs));
        
		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.groceries));
		cvs.put(Categories.ICON.columnName(), R.drawable.groceries);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.groceries, ihlp.insert(cvs));

		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.eatout));
        cvs.put(Categories.ICON.columnName(), R.drawable.eatout);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.eatout, ihlp.insert(cvs));

		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.clothing));
        cvs.put(Categories.ICON.columnName(), R.drawable.clothing);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.clothing, ihlp.insert(cvs));

		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.electronics));
        cvs.put(Categories.ICON.columnName(), R.drawable.electronics);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.electronics, ihlp.insert(cvs));

		cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.mobility));
        cvs.put(Categories.ICON.columnName(), R.drawable.mobility);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.mobility, ihlp.insert(cvs));

        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.gifts));
        cvs.put(Categories.ICON.columnName(), R.drawable.gifts);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.gifts, ihlp.insert(cvs));
        
        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.publict));
        cvs.put(Categories.ICON.columnName(), R.drawable.publict);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.publict, ihlp.insert(cvs));

        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.car));
        cvs.put(Categories.ICON.columnName(), R.drawable.car);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.car, ihlp.insert(cvs));

        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.air));
        cvs.put(Categories.ICON.columnName(), R.drawable.airicon);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.air, ihlp.insert(cvs));

		
        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.home));
        cvs.put(Categories.ICON.columnName(), R.drawable.home);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.home, ihlp.insert(cvs));

        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.sports));
        cvs.put(Categories.ICON.columnName(), R.drawable.sports);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.sports, ihlp.insert(cvs));

        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.hotel));
        cvs.put(Categories.ICON.columnName(), R.drawable.hotel);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.hotel, ihlp.insert(cvs));
        
        cvs.put(Categories.TITLE.columnName(),sysres.getString(R.string.office));
        cvs.put(Categories.ICON.columnName(), R.drawable.office);
        cvs.put(Categories.RANK.columnName(), rank++);
        catIds.put(R.array.office, ihlp.insert(cvs));
        
        ihlp.close();
        //=========Sub Categories
        cvs.put(Categories.ICON_TYPE.columnName(), Categories.ICON_NONE);
        cvs.put(Categories.RANK.columnName(), 1000);
        cvs.put(Categories.ICON.columnName(), "");
        
        String [] subcats;
        ihlp = new InsertHelper(db,CATEGORY_TABLE_NAME);
        for (int arrId : catIds.keySet()) {
			subcats = ctxt.getResources().getStringArray(arrId);
			cvs.put(Categories.PARENT_ID.columnName(), catIds.get(arrId));
			for (String scat : subcats) {
				cvs.put(Categories.TITLE.columnName(), scat);
				ihlp.insert(cvs);
			}
		}
        ihlp.close();
        
        //==== Currencies
        ihlp = new InsertHelper(db,CURRENCY_TABLE_NAME);
        cvs = new ContentValues();
        cvs.put(Currencies.SYMBOL.columnName(),"$");
        cvs.put(Currencies.MNEMONIC.columnName(),"USD");
        cvs.put(Currencies.BASE.columnName(),1);
        cvs.put(Currencies.EXCHANGE.columnName(),1.0);
        long base = ihlp.insert(cvs);
        
        cvs.put(Currencies.SYMBOL.columnName(),"€");
        cvs.put(Currencies.MNEMONIC.columnName(),"EUR");
        cvs.put(Currencies.BASE.columnName(),base);
        cvs.put(Currencies.EXCHANGE.columnName(),1.27292);
        ihlp.insert(cvs);
        
        cvs.put(Currencies.SYMBOL.columnName(),"£");
        cvs.put(Currencies.MNEMONIC.columnName(),"GBP");
        cvs.put(Currencies.BASE.columnName(),base);
        cvs.put(Currencies.EXCHANGE.columnName(),1.55622);
        ihlp.insert(cvs);
                
        cvs.put(Currencies.SYMBOL.columnName(),"SEK");
        cvs.put(Currencies.MNEMONIC.columnName(),"SEK");
        cvs.put(Currencies.BASE.columnName(),base);
        cvs.put(Currencies.EXCHANGE.columnName(),0.127009);
        ihlp.insert(cvs);
        
        cvs.put(Currencies.SYMBOL.columnName(),"$");
        cvs.put(Currencies.MNEMONIC.columnName(),"MXN");
        cvs.put(Currencies.BASE.columnName(),base);
        cvs.put(Currencies.EXCHANGE.columnName(),1.0777166);
        ihlp.insert(cvs);
        
        ihlp.close();
        
        Log.d(TAG, "Data Inserted");
        Log.d(TAG, "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CURRENCY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ENTRY_TABLE_NAME);
        onCreate(db);
    }
    
    public enum Categories implements BaseColumns {
        _ID("_ID",0),
    	TITLE("title",1),
        ICON("icon",2),
        ICON_TYPE("icontype",3),
        RANK("rank",4),
        PARENT_ID("parent_id",5), 
        LAST_USED("last_used",6);
    	
    	private final int index;
    	private final String colname;
        Categories(String col, int ix) {
        	index = ix;
        	colname = col;
        }
        
        public String columnName(){
        	return colname;
        }

        public int dbIndex(){
        	return index;
        }
        
        public String toString(){
        	return columnName();
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/categories");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of catergories.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.avelino.categories";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single category.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.avelino.category";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = " " + LAST_USED + " desc, " + RANK + " asc ";
        public static final String DEFAULT_SUB_CAT_SORT_ORDER = " " + LAST_USED + " desc, " + TITLE + " asc ";
        public static final int ICON_RES_DRAWABLE = 0x0;
        public static final int ICON_EXTERN =0x00000001;
        public static final int ICON_NONE =0x00000002;
        
    }
    
    static{
    	CATEGORY_MAP.put(Categories._ID.columnName(), Categories._ID.columnName());
    	CATEGORY_MAP.put(Categories.TITLE.columnName(), Categories.TITLE.columnName());
    	CATEGORY_MAP.put(Categories.ICON.columnName(), Categories.ICON.columnName());
    	CATEGORY_MAP.put(Categories.ICON_TYPE.columnName(), Categories.ICON_TYPE.columnName());
    	CATEGORY_MAP.put(Categories.RANK.columnName(), Categories.RANK.columnName());
    	CATEGORY_MAP.put(Categories.PARENT_ID.columnName(), Categories.PARENT_ID.columnName());
    	CATEGORY_MAP.put(Categories.LAST_USED.columnName(), Categories.LAST_USED.columnName());
    	
    	CURRENCY_MAP.put(Currencies._ID.columnName(), Currencies._ID.columnName());
    	CURRENCY_MAP.put(Currencies.SYMBOL.columnName(), Currencies.SYMBOL.columnName());
    	CURRENCY_MAP.put(Currencies.MNEMONIC.columnName(), Currencies.MNEMONIC.columnName());
    	CURRENCY_MAP.put(Currencies.EXCHANGE.columnName(), Currencies.EXCHANGE.columnName());
    	CURRENCY_MAP.put(Currencies.BASE.columnName(), Currencies.BASE.columnName());
    }

	public Cursor getAllCategories(SQLiteDatabase db) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CATEGORY_TABLE_NAME);
		qb.setProjectionMap(CATEGORY_MAP);
		qb.appendWhere(" " + Categories.PARENT_ID.columnName() + "= -1 ");
		return qb.query(db, null, null, null, null, null, Categories.DEFAULT_SORT_ORDER);
	}

	public Cursor getAllSubCategories(SQLiteDatabase db, int parent) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CATEGORY_TABLE_NAME);
		qb.setProjectionMap(CATEGORY_MAP);
		qb.appendWhere(" " + Categories.PARENT_ID.columnName() + "= " + parent);
		return qb.query(db, null, null, null, null, null, Categories.DEFAULT_SUB_CAT_SORT_ORDER);
	}

	public Cursor getAllCurrencies(SQLiteDatabase db) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CURRENCY_TABLE_NAME);
		qb.setProjectionMap(CURRENCY_MAP);
		return qb.query(db, null, null, null, null, null, null);
	}
	
	
	public enum Currencies implements BaseColumns{
		_ID("_ID",0),
    	SYMBOL("symbol",1),
        MNEMONIC("mnemonic",2),
        EXCHANGE("exchange",3),
        BASE("base_id",4);
    	
    	private final int index;
    	private final String colname;
    	Currencies(String col, int ix) {
        	index = ix;
        	colname = col;
        }
        
        public String columnName(){
        	return colname;
        }

        public int dbIndex(){
        	return index;
        }
        
        public String toString(){
        	return columnName();
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/currencies");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of catergories.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.avelino.currencies";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single category.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.avelino.currency";
	}
	
	
	public enum Entries implements BaseColumns{
		_ID("_ID",0),
    	COST("cost",1),
        NOTES("notes",2),
        DATE("date_created",3),
        CATEGORY("category_id",4),
        CURRENCY("currency_id",3);
    	
    	private final int index;
    	private final String colname;
    	Entries(String col, int ix) {
        	index = ix;
        	colname = col;
        }
        
        public String columnName(){
        	return colname;
        }

        public int dbIndex(){
        	return index;
        }
        
        public String toString(){
        	return columnName();
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/cost_entries");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of catergories.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.avelino.cost_entries";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single category.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.avelino.cost_entry";

        

	}


	public void insertEntry(float cost, String notes, int catid, int currid) {
		final SQLiteDatabase wdb = getWritableDatabase();
		final InsertHelper ins = new InsertHelper(wdb, ENTRY_TABLE_NAME);
		ContentValues cvs = new ContentValues();
        cvs.put(Entries.COST.columnName(),cost);
        cvs.put(Entries.NOTES.columnName(),notes);
        cvs.put(Entries.CATEGORY.columnName(),catid);
        cvs.put(Entries.CURRENCY.columnName(),currid);
        Log.d(TAG,"Inserting entry...");
        ins.insert(cvs);
        ins.close();
        Log.d(TAG,"Entry inserted");
	}

	public void updateCategoryHit(int catid) {
		final SQLiteDatabase wdb = getWritableDatabase();
//		ContentValues cvs = new ContentValues();
//        cvs.put(Categories.LAST_USED.columnName(),"datetime('now')");
//        
//		wdb.update(CATEGORY_TABLE_NAME, cvs, " " + Categories._ID.columnName() + " = ?" , new String[]{String.valueOf(catid)});
		wdb.execSQL("UPDATE " + CATEGORY_TABLE_NAME + 
					" SET " + Categories.LAST_USED + 
					" = datetime('now') WHERE " + Categories._ID + " = ?", new Object[]{catid});
		Log.d(TAG,"Category " + catid + " updated");
	}

	
//	sqlite> select sum(COST), strftime("%Y/%W-%m-%d",date_created), currency_id, sym
//	bol, mnemonic, exchange, base_id  from cost_entry, currency on currency._ID = cu
//	rrency_id group by date(date_created), currency_id order by date_created desc;
//	select sum(COST), strftime("%Y/%W-%m-%d",date_created), currency_id, symbol, mne
//	monic, exchange, base_id  from cost_entry, currency on currency._ID = currency_i
//	d group by date(date_created), currency_id order by date_created desc;	


	public enum CostQuery{
		SIMPLE_DATE ("strftime(\"%Y-%m-%d\",date_created) = ? ", new SimpleDateFormat("yyyy-MM-dd"),"currency_id"),
		SIMPLE_WEEK ("strftime(\"%Y-%W\",date_created) = ? ", new SimpleDateFormat("yyyy-ww"),"currency_id"),
		SIMPLE_MONTH ("strftime(\"%Y-%m\",date_created) = ? ", new SimpleDateFormat("yyyy-MM"),"currency_id"),
		SIMPLE_YEAR ("strftime(\"%Y\",date_created) = ? ", new SimpleDateFormat("yyyy"),"currency_id"),
		SIMPLE_ALL(null,null,"currency_id"),
		//Old "date(?) <= date(date_created) or date(date_created) >= date(?)"
		BETWEEN_DATES (" julianday(?) >= julianday(date_created) and julianday(?) <= julianday(date_created) ", new SimpleDateFormat("yyyy-MM-dd"),null);
		
		private final String query;
		private final String group;
		private final DateFormat format;
		CostQuery(String str, DateFormat frmt, String grp){
			query = str;
			format = frmt;
			group = grp;
		}
		
		public DateFormat getFormat(){
			return format;
		}
		public String getQuery(){
			return query;
		}
		
		public String getGrouping(){
			return group;
		}
	}
	
	
	public Cursor queryCostEntryDate(SQLiteDatabase db, CostQuery costQuery, String[] dates, boolean lumpsum) {
		String query = SQLiteQueryBuilder.buildQueryString(false, 
				ENTRY_TABLE_NAME +", " + 
				CURRENCY_TABLE_NAME + " on "+CURRENCY_TABLE_NAME+"." + Currencies._ID.columnName()+" = " + Entries.CURRENCY.columnName() + ", " +
				CATEGORY_TABLE_NAME + " on "+CATEGORY_TABLE_NAME+"." + Categories._ID.columnName()+" = " + Entries.CATEGORY.columnName(), 
				new String[]{
								ENTRY_TABLE_NAME+"."+Entries._ID + " as " +ReportEntry.ENTRY_ID,
								(lumpsum ? "sum("+ReportEntry.COST+") as "+ReportEntry.COST : ReportEntry.COST.columnName()),
								ReportEntry.DATE.columnName(), 
								ReportEntry.CURRENCY_ID.columnName(), 
								ReportEntry.SYMBOL.columnName(), 
								ReportEntry.MNEMONIC.columnName(), 
								ReportEntry.EXCHANGE.columnName(), 
								ReportEntry.BASE.columnName(),
								ReportEntry.TITLE.columnName(),
								ReportEntry.CATEGORY_ID.columnName(),
								ReportEntry.PARENT_CATEGORY_ID.columnName(),
								ReportEntry.ICON.columnName(),
								ReportEntry.ICON_TYPE.columnName(),
								ReportEntry.NOTES.columnName()
								},
				costQuery.getQuery(), costQuery.getGrouping(), null, Entries.DATE + " desc", null);
		Log.i(TAG + ".queryCostEntryDate:query", query);
		Log.i(TAG + ".queryCostEntryDate:args", Arrays.asList(dates).toString());
		return db.rawQuery(query, dates);
	}
	public enum ReportEntry {
		ENTRY_ID(		"entry_id",		0),
        COST(			"cost",			1),
    	DATE(			"date_created",	2),
        CURRENCY_ID(	"currency_id",	3),
        SYMBOL(			"symbol",		4),
        MNEMONIC(		"mnemonic",		5),
        EXCHANGE(		"exchange",		6), 
        BASE(			"base_id",		7),
        TITLE(			"title",		8),
        CATEGORY_ID(	"category_id",	9),
    PARENT_CATEGORY_ID(	"parent_id",	10),
        ICON(			"icon",			11),
        ICON_TYPE(		"icontype",		12),
		NOTES(			"notes",		13);
    	
    	private final int index;
    	private final String colname;
    	ReportEntry(String col, int ix) {
        	index = ix;
        	colname = col;
        }
        
        public String columnName(){
        	return colname;
        }

        public int dbIndex(){
        	return index;
        }
        
        public String toString(){
        	return columnName();
        }
	}


	public void clearEntries() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM "+ ENTRY_TABLE_NAME);
		db.close();
	}

	public void inserOrUpdateCurrencies(List<CurrencyTO> targetCurr,
			SQLiteDatabase db) {
		InsertHelper ihlp = new InsertHelper(db,CURRENCY_TABLE_NAME);
		ContentValues cvs = null;
		for (CurrencyTO curr : targetCurr) {
	        if (curr.getId() == -1){
	        	cvs = new ContentValues();
		        cvs.put(Currencies.SYMBOL.columnName(),curr.getSymbol());
		        cvs.put(Currencies.MNEMONIC.columnName(),curr.getMnemonic());
		        cvs.put(Currencies.BASE.columnName(),curr.getBase());
		        cvs.put(Currencies.EXCHANGE.columnName(),curr.getExchange());
	        	ihlp.insert(cvs);
	        	Log.d(TAG, "Inserted CUrr:"+curr);
	        } else {
	        	db.execSQL("UPDATE " + CURRENCY_TABLE_NAME + " SET " + Currencies.EXCHANGE.columnName() + " = " + curr.getExchange() +
	        			", " +Currencies.BASE.columnName() + " = " + curr.getBase() + " WHERE " + Currencies._ID.columnName() + " = " + curr.getId());
	        	Log.d(TAG, "Updated Curr:"+curr);
	        }
		}
		
		
	}

	public void updateEntry(int id, float cost, String notes, int catid, int currid, String date) {
		final SQLiteDatabase wdb = getWritableDatabase();
			wdb.execSQL("UPDATE " + ENTRY_TABLE_NAME + " SET " + Entries.COST + " = " + cost + ", " +
					Entries.NOTES.columnName() + " = " + DatabaseUtils.sqlEscapeString(notes) + ", " +
					Entries.CATEGORY.columnName() + " = " + catid + ", " + Entries.CURRENCY.columnName() + " = " + currid + ", " +
					Entries.DATE.columnName() + " = " + DatabaseUtils.sqlEscapeString(date) + " WHERE " +
					Entries._ID.columnName() + " = " + id);
			Log.d(TAG, "Updated Entry:"+id);
	}

	public Cursor getCategory(int id, SQLiteDatabase db) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CATEGORY_TABLE_NAME);
		qb.setProjectionMap(CATEGORY_MAP);
		qb.appendWhere(" " + Categories._ID.columnName() + " = " + id);
		return qb.query(db, null, null, null, null, null, Categories.DEFAULT_SUB_CAT_SORT_ORDER);
	}

	public void deleteEntry(int id, SQLiteDatabase db) {
		db.execSQL("DELETE FROM "+ ENTRY_TABLE_NAME + " WHERE " + Entries._ID.columnName() + " = " + id);
	}

}
