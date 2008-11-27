package org.avelino.mobile.android.budgetfrik;

public interface IEntryEditor {

	public abstract void insertEntry(EntryTO entryDAO);

	public abstract void updateEntry(EntryTO entry);

}