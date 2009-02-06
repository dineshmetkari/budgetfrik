package org.avelino.mobile.android.budgetfrik;

import static org.avelino.mobile.android.budgetfrik.EntryTO.SIMPLE_DATE_FORMAT;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;
import android.widget.ExpandableListView;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class DateHelper {
	public enum TimeUnits {
		Day(Calendar.DAY_OF_YEAR),
		Month(Calendar.MONTH),
		Week(Calendar.WEEK_OF_MONTH),
		Year(Calendar.YEAR);
		
		private int unit;
		TimeUnits(int unit){
			this.unit = unit;
		}
		public int getUnit(){
			return unit;
		}
	}


	private static final String TAG = "DateHelper";
	
	
	private Calendar earliest = Calendar.getInstance();
	private Calendar latest = Calendar.getInstance();
	private Calendar prev_earliest;
	private Calendar prev_latest;
	private TimeUnits timeUnit = TimeUnits.Day;
	
	private void adjustToDayView(TimeUnits oldUnit, final long selectedPosition, Date[] positions) {
		switch (oldUnit){
		//just the same date, here for orthogonality
		//Days are shown week by week
		case Day:
			earliest = prev_earliest;
			latest = prev_latest;
			break;
		//Previous was week, and moving to day.
		//Move the selected week, if not one is selected, then move to the same week of month as before
			//Weeks are shown month by month
		case Week:
			
			if (ExpandableListView.getPackedPositionType(selectedPosition) != ExpandableListView.PACKED_POSITION_TYPE_NULL){
				final Date date = positions[ExpandableListView.getPackedPositionGroup(selectedPosition)];
				//Move to week in month/year
				earliest.setTime(date);
				latest.setTime(date);
				spanToWeek();
			} else {
				//Move to previously remebered day of month
				earliest.set(Calendar.DAY_OF_MONTH, prev_earliest.get(Calendar.DAY_OF_MONTH));
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				//Span to week
				spanToWeek();
			}
			break;
			//Previous was month, and moving to day.
		    //Move the selected month, same week as before. if not one is selected, then move to the same week of month as before
				//Months are shown year by year
		case Month:
			if (ExpandableListView.getPackedPositionType(selectedPosition) != ExpandableListView.PACKED_POSITION_TYPE_NULL){
				final Date date = positions[ExpandableListView.getPackedPositionGroup(selectedPosition)];
				//Move to month in year
				earliest.setTime(date);
				latest.setTime(date);
				//Move to previously remebered week
				earliest.set(Calendar.WEEK_OF_MONTH, prev_earliest.get(Calendar.WEEK_OF_MONTH));
				latest.set(Calendar.WEEK_OF_MONTH, prev_earliest.get(Calendar.WEEK_OF_MONTH));
				spanToWeek();
			} else {
				//Move to previously remebered day of year
				earliest.set(Calendar.DAY_OF_YEAR, prev_earliest.get(Calendar.DAY_OF_YEAR));
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				spanToWeek();

			}
			break;
		//Previous was year, and moving to day.
		//Move the selected year, same day as before. if not one is selected, then move to the same date as before of the earliest displayed year
			//Years are shown by decade
		case Year:
			if (ExpandableListView.getPackedPositionType(selectedPosition) != ExpandableListView.PACKED_POSITION_TYPE_NULL){
				final Date date = positions[ExpandableListView.getPackedPositionGroup(selectedPosition)];
				//Move to year in decade
				earliest.setTime(date);
				latest.setTime(date);
				//Move to previously remebered day of year
				earliest.set(Calendar.DAY_OF_YEAR, prev_earliest.get(Calendar.DAY_OF_YEAR));
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				spanToWeek();
			} else {
				//Move to previous time
				earliest.setTime(prev_earliest.getTime());
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				spanToWeek();
			}
			break;
		}
	}
	private void adjustToMonthView(TimeUnits oldUnit, final long selectedPosition, Date[] positions) {
		switch (oldUnit){
		//Moving from Day or Week to Month (i.e. one year in view)
		case Day:
		case Week:
		case Month:
			spanToYear();
			break;
		//Moving from decade span to year span.
		//First get the selected year and span to year.
		//If none selected, get the previously active year and span to year
		case Year:
			if (ExpandableListView.getPackedPositionType(selectedPosition) != ExpandableListView.PACKED_POSITION_TYPE_NULL){
				final Date date = positions[ExpandableListView.getPackedPositionGroup(selectedPosition)];
				//Move to year in decade
				earliest.setTime(date);
				//Move to same year
				latest.setTime(date);
				spanToYear();
			} else {
				//Move to previously remebered year of the earliest date
				earliest.set(Calendar.YEAR, prev_earliest.get(Calendar.YEAR));
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				//Span Year
				spanToYear();
			}
			break;
			
		}
	}
	private void adjustToWeekView(TimeUnits oldUnit, final long selectedPosition, Date[] positions) {
		switch (oldUnit){
		//Moving from Day to week
		case Day:
		case Week:
			earliest.set(Calendar.MONTH,latest.get(Calendar.MONTH));
			spanToMonth();
			break;
		//Previous month, now is week
		//Move to the same week of the month as before in the selected month, if none selected then to the same month as before. Keep the year
			//Month is dispayed year by year
		case Month:
			if (ExpandableListView.getPackedPositionType(selectedPosition) != ExpandableListView.PACKED_POSITION_TYPE_NULL){
				final Date date = positions[ExpandableListView.getPackedPositionGroup(selectedPosition)];
				//Move to month in year
				earliest.setTime(date);
				latest.setTime(date);
				//Move to previously remebered week in month
				earliest.set(Calendar.WEEK_OF_MONTH, prev_earliest.get(Calendar.WEEK_OF_MONTH));
				latest.set(Calendar.WEEK_OF_MONTH, prev_earliest.get(Calendar.WEEK_OF_MONTH));
				spanToMonth();
			} else {
				//Move to previously remebered month of year of the earliest year
				earliest.set(Calendar.MONTH, prev_earliest.get(Calendar.MONTH));
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				spanToMonth();
			}
		//Previous was year, and moving to week.
		//Move the selected month, same week as before. if not one is selected, then move to the same week of month as before
			//Years are shown by decade
		case Year:
			if (ExpandableListView.getPackedPositionType(selectedPosition) != ExpandableListView.PACKED_POSITION_TYPE_NULL){
				final Date date = positions[ExpandableListView.getPackedPositionGroup(selectedPosition)];
				//Move to year in decade
				earliest.setTime(date);
				latest.setTime(date);
				//Move to previously remebered day of year
				earliest.set(Calendar.WEEK_OF_YEAR, prev_earliest.get(Calendar.WEEK_OF_YEAR));
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				spanToMonth();
			} else {
				//Move to previous time
				earliest.setTime(prev_earliest.getTime());
				//Move latest to same time as earliest
				latest.setTime(earliest.getTime());
				//Span a month
				spanToMonth();
			}
			break;

		}
	}
	public Calendar getEarliest() {
		return earliest;
	}
	

	public Calendar getLatest() {
		return latest;
	}

	TimeUnits getTimeUnit() {
		return timeUnit;
	}



	public void moveBackward(int length) {
		switch (timeUnit){
		case Day:
			moveCalendar(Calendar.WEEK_OF_YEAR, -1, length);
			break;
		case Week:
			moveCalendar(Calendar.MONTH,-1, length);
			break;
		case Month:
			moveCalendar(Calendar.YEAR,-1, length);
			break;
		case Year:
			moveCalendar(Calendar.YEAR,-5, length);
			break;
		}
	}

	private void moveCalendar(int unit, int amount, int length){
		Calendar cal = Calendar.getInstance();
		cal.setTime(latest.getTime());
		cal.add(unit, amount);
		moveTo(cal.getTime(), length);
		fixClock();
	}
	
	public void moveForward(int length) {
		switch (timeUnit){
		case Day:
			moveCalendar(Calendar.WEEK_OF_YEAR, 1, length);
			break;
		case Week:
			moveCalendar(Calendar.MONTH,1, length);
			break;
		case Month:
			moveCalendar(Calendar.YEAR,1, length);
			break;
		case Year:
			moveCalendar(Calendar.YEAR,5, length);
			break;
		}
	}
	
	private void moveTo(Date date, int length){
		try {
			latest.setTime(SIMPLE_DATE_FORMAT.parse(SIMPLE_DATE_FORMAT.format(date)));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		earliest.setTime(latest.getTime());
		earliest.add(timeUnit.unit, -1*(length-1));
		
	}

	//Month base 1, day base 1
	public Date[] setDate(int year, int month, int day, Date[] positions) {
		Calendar tmp = Calendar.getInstance();
		tmp.set(year, month, day);
		earliest.setTime(tmp.getTime());
		latest.setTime(tmp.getTime());
		switch(timeUnit){
		case Day:
			spanToWeek();
			positions = getDaysInAWeek();
			break;
		case Week:
			spanToMonth();
			positions = getWeeksInAMonth();
			break;
		case Month:
			spanToYear();
			positions = getMonthsInAYear();
			break;
		case Year:
			setYearPeriod(year);
			//Setup the internal dates array
			positions = getYears();
		}
		fixClock();
		return positions;
	}
	private void setYearPeriod(int year) {
		//Get the current latest, add 2 years (in the future) and move the earliest 2 years in the past
		//[+2][+1][LATEST][-1][-2]
		earliest.set(Calendar.YEAR, year);
		latest.add(Calendar.YEAR, 2);
		earliest.add(Calendar.YEAR, -2);
		//Span to year (on different years) to make sure we catch all
		spanToYear();
		
		
		
	}
	private Date[] getYears() {
		return new Date[5];
	}
	private Date[] getMonthsInAYear() {
		return new Date[latest.getMaximum(Calendar.MONTH)+1];
	}
	private Date[] getWeeksInAMonth() {
		final int weeks = latest.get(Calendar.WEEK_OF_YEAR) - earliest.get(Calendar.WEEK_OF_YEAR)+1;
		//If the number of weeks is negative, it means earliest is in the previous year
		return new Date[weeks + (weeks < 0?earliest.getActualMaximum(Calendar.WEEK_OF_YEAR):0)];
	}
	private Date[] getDaysInAWeek() {
		return new Date[latest.getMaximum(Calendar.DAY_OF_WEEK)];
	}



	public void setEarliest(Calendar earliest) {
		this.earliest = earliest;
	}

	public void setLatest(Calendar latest) {
		this.latest = latest;
	}

	/**
	 * @param timeUnit One of Day, Week, Month, Year
	 * @param selectedPosition PackedPosition of the ExpandableListView (what day is selected)
	 * @param positions Not used.
	 * @return The Date positions array filled
	 */
	public Date[] setTimeUnit(TimeUnits timeUnit, final long selectedPosition, Date[] positions) {
		
		//Avoid unneccesary recalculation
		if (this.timeUnit == timeUnit){
			return positions;
		}
		
		//Remeber where were we before the change, in case we need to come back
		
		switch(this.timeUnit){
		
		case Day:
			prev_earliest = earliest;
			prev_latest = latest;
			break;
		case Week:
			prev_earliest = earliest;
			prev_latest = latest;
			break;
		case Month:
			prev_earliest = earliest;
			prev_latest = latest;
			break;
		case Year:
			prev_earliest = earliest;
			prev_latest = latest;
			break;
		
		}
		TimeUnits oldUnit = this.timeUnit; 
		this.timeUnit = timeUnit;
		
		//Now we are in a new time unit
		switch(timeUnit){
		//Day will always have a previous date to remeber
		//Need to mix it with the previous unit
		case Day:
			adjustToDayView(oldUnit, selectedPosition, positions);
			//Setup the internal dates array
			positions = getDaysInAWeek();
			break;
		case Week:
			adjustToWeekView(oldUnit, selectedPosition, positions);
			positions = getWeeksInAMonth();
			break;
		case Month:
			adjustToMonthView(oldUnit, selectedPosition, positions);
			//Setup the internal dates array
			positions = getMonthsInAYear();
			break;
		case Year:
			setYearPeriod(latest.get(Calendar.YEAR));
			//Setup the internal dates array
			positions = getYears();
		}
		fixClock();
		return positions;
	}

	private void spanToMonth() {
		earliest.set(Calendar.DAY_OF_MONTH, 1);
		latest.set(Calendar.DAY_OF_MONTH, latest.getActualMaximum(Calendar.DAY_OF_MONTH));
		Log.i(TAG, "SpanToMonth: Earliest " + earliest.getTime());
		Log.i(TAG, "SpanToMonth: Latest " + latest.getTime());
		Log.d(TAG, "First Day of week:" + earliest.getFirstDayOfWeek());
		Log.d(TAG, "Min days in 1st week:" + earliest.getMinimalDaysInFirstWeek());
		Log.d(TAG, "Locale:" + Locale.getDefault());
		spanToWeek();
	}

	
	private void spanToWeek() {
 		earliest.add(Calendar.DAY_OF_YEAR, earliest.getFirstDayOfWeek()-earliest.get(Calendar.DAY_OF_WEEK));//Always moves back
		latest.add(Calendar.DAY_OF_YEAR, ((latest.getFirstDayOfWeek() + 6) % 8)-latest.get(Calendar.DAY_OF_WEEK));//Always moves forward
		Log.i(TAG, "spanToWeek: Earliest " + earliest.getTime());
		Log.i(TAG, "spanToWeek: Latest " + latest.getTime());
	}
	
	private void spanToYear() {
		earliest.set(Calendar.DAY_OF_YEAR, 1);
		latest.set(Calendar.DAY_OF_YEAR, latest.getActualMaximum(Calendar.DAY_OF_YEAR));
	}
	
	private void fixClock(){
		earliest.set(Calendar.HOUR_OF_DAY, 0);
		earliest.set(Calendar.MINUTE, 0);
		earliest.set(Calendar.SECOND, 0);
		earliest.set(Calendar.MILLISECOND, 0);
		
		latest.set(Calendar.HOUR_OF_DAY, 23);
		latest.set(Calendar.MINUTE, 59);
		latest.set(Calendar.SECOND, 59);
		latest.set(Calendar.MILLISECOND, 99);
	}
}
