package org.avelino.mobile.android.budgetfrik;

public class EntryEditorImpl  {

	public static void insertEntry(EntryTO entryDAO, DBHelper helper) {
		helper.insertEntry(entryDAO.getCost(),entryDAO.getNotes(),entryDAO.getCat().getId(),entryDAO.getCurr().getId());
		helper.updateCategoryHit(entryDAO.getCat().getId());
		if (entryDAO.getCat().getParentId() >=0 ){ //-1 means no parent
			helper.updateCategoryHit(entryDAO.getCat().getParentId());
		}
		
	}

	public static void updateEntry(EntryTO entry, DBHelper helper) {
		helper.updateEntry(entry.getId(), entry.getCost(), entry.getNotes(),entry.getCat().getId(),entry.getCurr().getId(), EntryTO.SIMPLE_DATE_FORMAT.format( entry.getDate()));
	}

}
