package org.tiefaces.components.websheet.dataobjects;
/**
 * Class object hold anchor.
 * @author Jason Jiang
 *
 */
public class AnchorSize {
	/** anchor's left. */
	private int left;
	/** anchor's top. */
	private int top;
	/** anchor's width. */
	private int width;
	/** anchor's height. */
	private int height;
	
	public final int getTop() {
		return top;
	}
	public final void setTop(final int top) {
		this.top = top;
	}
	public final int getLeft() {
		return left;
	}
	public final void setLeft(final int left) {
		this.left = left;
	}
	public final int getWidth() {
		return width;
	}
	public final void setWidth(final int width) {
		this.width = width;
	}
	public final int getHeight() {
		return height;
	}
	public final void setHeight(final int height) {
		this.height = height;
	}
	/**
	 * Constructor.
	 * @param left anchor's left.
	 * @param top anchor's top.
	 * @param width anchor's width.
	 * @param height anchor's height.
	 */
	public AnchorSize(final int left, final int top, final int width, final int height) {
		super();
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	/**
	 * show human readable message.
	 */
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
