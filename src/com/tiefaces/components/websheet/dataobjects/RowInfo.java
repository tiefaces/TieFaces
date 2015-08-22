/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tiefaces.components.websheet.dataobjects;


/**
 * hold row control information for the entire row. Add as the first object of the row for control object.
 * 
 * @author Jason Jiang
 */
public class RowInfo {

	private boolean debug = false;

	private void debug(String msg) {
		if (debug) {
			System.out.println("debug: " + msg);
		}
	}

	private int rowIndex;
	
	private boolean rendered;
	
	private float rowheight; 

	private boolean repeatZone;

	public RowInfo(int rowIndex) {
		super();
		this.rowIndex = rowIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public float getRowheight() {
		return rowheight;
	}

	public void setRowheight(float rowheight) {
		this.rowheight = rowheight;
	}

	public boolean isRepeatZone() {
		return repeatZone;
	}

	public void setRepeatZone(boolean repeatZone) {
		this.repeatZone = repeatZone;
	}
	
	
	
	
	
}
