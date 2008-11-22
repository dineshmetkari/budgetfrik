package org.avelino.mobile.android.budgetfrik;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class CategoryTO {
	private String title;
	public CategoryTO(String title, int id, int parentId) {
		super();
		this.title = title;
		this.id = id;
		this.setParentId(parentId);
	}
	private int id;
	private int parentId;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String toString(){
		return title;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getParentId() {
		return parentId;
	}
}
