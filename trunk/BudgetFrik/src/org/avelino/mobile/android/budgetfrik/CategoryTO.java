package org.avelino.mobile.android.budgetfrik;
/**
 * License http://creativecommons.org/licenses/by-nc-sa/2.5/se/deed.en_US
 * See assets/license.html
 * @author Avelino Benavides
 *
 */
public class CategoryTO {
	private String title;
	private int iconType;
	private String icon;
	public CategoryTO(String title, int id, int parentId) {
		this.title = title;
		this.id = id;
		this.setParentId(parentId);
	}
	public CategoryTO(String title, int id, int parentId, String icon,
			int iconType) {
		this(title,id,parentId);
		this.icon = icon;
		this.iconType = iconType;
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
	public int getIconType() {
		return iconType;
	}
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
