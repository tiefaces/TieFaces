package com.tiefaces.components.websheet.dataobjects;

public class AnchorSize {
	
	int left;
	int top;
	int width;
	int height;
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public AnchorSize(int left, int top, int width, int height) {
		super();
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("left = " + left);
		sb.append(",");
		sb.append("top = " + top);
		sb.append(",");
		sb.append("width = " + width);
		sb.append(",");
		sb.append("height = " + height);
		sb.append("}");
		return sb.toString();
	}	
	

}
